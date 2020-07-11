package com.lisz.es;

import com.lisz.es.entity.Product;
import com.lisz.es.service.ProductService;
import lombok.SneakyThrows;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.profile.ProfileShardResult;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESApplicationTest {
    @Autowired
    private ProductService service;

    private TransportClient client;

    @Before
    @SneakyThrows
    public void init() {
        // 默认的集群名字就是elasticsearch，这里可以不写. 但是如果cluster.name对不上就会报错:None of the configured nodes are available
        // es集群的elasticsearch.yml中就有cluster.name的配置，这里要查一下，要对得上
        Settings settings = Settings.builder()
                .put("cluster.name", "my-es")
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9300))  // 节点之间的通讯IP
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9301)); // 节点之间的通讯IP
    }

    @After
    public void end() {
        client.close();
        System.out.println(client);
    }

    @Test
    @SneakyThrows
    public void esCRUD() {
        //create(client);
        //get(client);
        getAll(client);
    }

    @SneakyThrows
    private void getAll(TransportClient client) {
        SearchResponse response = client.prepareSearch("product2").get();
        SearchHit hits[] = response.getHits().getHits();
        // 直接打印hits[]，跟Kibana上的hits[]查询结果一样, 数组类型
        Arrays.stream(hits).forEach(System.out::println);
        System.out.println("\n================================\n");
        // 只打印Kibana查询结果中各个_source里面的那个JSON
        Arrays.stream(hits).forEach(h->{
            String res = h.getSourceAsString();
            System.out.println(res);
        });
    }

    @SneakyThrows
    private void create(TransportClient client) {
        List<Product> list = service.list();
        for (Product product : list) {
            System.out.println(product.getCreateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            IndexResponse response = client.prepareIndex("product2", "_doc", product.getId().toString())
                                           .setSource(XContentFactory.jsonBuilder()
                                               .startObject()
                                                    .field("name", product.getName())
                                                    .field("desc", product.getDesc())
                                                    .field("price", product.getPrice())
                                                    .field("date", product.getCreateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                                    .field("tags", product.getTags().split(","))
                                                .endObject()
                                           )
                                       .get();
            System.out.println(response.getResult());
        }
    }

    @SneakyThrows
    private void get(TransportClient client) {
        GetResponse response = client.prepareGet("product2", "_doc", "1").get();
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        Map<String, Object> source = response.getSource();
        System.out.println("index: " + index);
        System.out.println("type: " + type);
        System.out.println("id: " + id);
        System.out.println("source: " + source);
    }
}
