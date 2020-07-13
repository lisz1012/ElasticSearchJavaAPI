package com.lisz.es;

import com.google.gson.Gson;
import com.lisz.es.entity.Product;
import com.lisz.es.service.ProductService;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESHighLevelAPITest {
	@Autowired
	private ProductService service;

	private Gson gson = new Gson();

	private RestHighLevelClient client;

	@Before
	public void init () {
		client = new RestHighLevelClient(
				RestClient.builder(
						new HttpHost("192.168.1.3", 9200, "http"), // 服务端口
						new HttpHost("192.168.1.3", 9201, "http")  // 服务端口
				)
		);
	}

	@After
	public void close () {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@SneakyThrows
	public void createIndex() {
		CreateIndexRequest request = new CreateIndexRequest("test_index");
		request.settings(Settings.builder()
				.put("index.number_of_shards", 3)
				.put("index.number_of_replicas", 2));
		CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
		if (response.isAcknowledged()) {
			System.out.println("创建 index 成功");
		} else {
			System.out.println("创建 index 失败");
		}
	}

	@Test
	@SneakyThrows
	public void deleteIndex() {
		DeleteIndexRequest request = new DeleteIndexRequest("test_index");
		AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
		if (response.isAcknowledged()) {
			System.out.println("删除 index 成功");
		} else {
			System.out.println("删除 index 失败");
		}
	}

	@Test
	@SneakyThrows
	public void getIndex() {
		GetIndexRequest request = new GetIndexRequest("product*");
		GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
		String[] indices = response.getIndices();
		Arrays.stream(indices).forEach(System.out::println);
	}

	@Test
	@SneakyThrows
	public void insert() {
		List<Product> list = service.list();
		IndexRequest request = new IndexRequest("test_index");
		list.stream().forEach(p->{
			String source = gson.toJson(p);
			request.id(p.getId().toString())
				   .source(source, XContentType.JSON);
			IndexResponse response = null;
			try {
				response = client.index(request, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(response);
		});
	}

	@Test
	@SneakyThrows
	// 对象转JSON -> 作为JSON 设到各个IndexRequest的source中去 -> 把各个IndexRequest add 到 BulkRequest中
	public void bulkInsert() {
		BulkRequest request = new BulkRequest("test_index");
		Product product = Product.builder().build();
		product.setPrice(3999.00);
		product.setDesc("xiaomi");
		for (int i = 0; i < 10; i++) {
			String name = "name_" + i;
			product.setName(name);
			String source = gson.toJson(product);
			request.add(new IndexRequest().source(source, XContentType.JSON));
		}
		BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
		System.out.println("数量： " + responses.getItems().length);
	}

	@Test
	@SneakyThrows
	public void getById() {
		GetRequest request = new GetRequest("test_index");
		request.id("1");
		GetResponse response = client.get(request, RequestOptions.DEFAULT);
		System.out.println(response.getSourceAsString());
	}

	@Test
	@SneakyThrows
	// 只选出指定的字段, 某个字段在includes和excludes两边都用，则excludes
	public void getById2() {
		GetRequest request = new GetRequest("test_index", "1");
		String includes[] = {"name", "price"};
		String excludes[] = {"desc"};
		request.fetchSourceContext(new FetchSourceContext(true, includes, excludes));
		GetResponse response = client.get(request, RequestOptions.DEFAULT);
		System.out.println(response.getSourceAsString());
	}

	@Test
	@SneakyThrows
	// mget中传入request的id，甚至可以跨index
	public void multiGet() {
		MultiGetRequest request = new MultiGetRequest();
		request.add("test_index", "YshyRnMBj-Hr8vYViZ5U")
			   .add("product", "1")
		       .add(new MultiGetRequest.Item("product", "2")); //两种写法
		MultiGetResponse responses = client.mget(request, RequestOptions.DEFAULT);
		Arrays.stream(responses.getResponses()).forEach(r->{
			System.out.println(r.getResponse().getSource());
		});
	}

	@Test
	@SneakyThrows
	public void deleteById() {
		DeleteRequest request = new DeleteRequest("test_index", "achyRnMBj-Hr8vYViZ5W");
		DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
		System.out.println(response.status());
	}

	@Test
	@SneakyThrows
	public void updateByQuery(){
		UpdateByQueryRequest request = new UpdateByQueryRequest("test_index");
		request.setQuery(QueryBuilders.matchQuery("name", "name_1 name_2"))
			   .setMaxDocs(10) // 限制更新条数，一般不用
			   .setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.desc+='#';", Collections.emptyMap()));
		BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
		System.out.println(response.getStatus());
	}
}
