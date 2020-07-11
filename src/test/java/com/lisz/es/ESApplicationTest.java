package com.lisz.es;

import com.lisz.es.entity.Product;
import com.lisz.es.service.ProductService;
import lombok.SneakyThrows;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESApplicationTest {
    @Autowired
    private ProductService service;

    @Test
    @SneakyThrows
    public void esCRUD() {
        // 默认的集群名字就是elasticsearch，这里可以不写. 但是如果cluster.name对不上就会报错:None of the configured nodes are available
        // es集群的elasticsearch.yml中就有cluster.name的配置，这里要查一下，要对得上
        Settings settings = Settings.builder()
                                    .put("cluster.name", "my-es")
                                    .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9300))  // 节点之间的通讯IP
                                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9301)); // 节点之间的通讯IP
        create(client);

        client.close();
        System.out.println(client);
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
}
