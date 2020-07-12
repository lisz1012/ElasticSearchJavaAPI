package com.lisz.es;

import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ESHighLevelAPITest {
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
}
