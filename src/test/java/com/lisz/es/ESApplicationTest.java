package com.lisz.es;

import com.lisz.es.entity.Product;
import com.lisz.es.service.ProductService;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.Avg;
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
import java.io.IOException;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
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
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9300))  // 节点之间的通讯端口
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.3"), 9301)); // 节点之间的通讯端口
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
        //getAll(client);
        //update(client);
        //delete(client);
        //multiSearch(client);
        //updatePrice(2, 4999);
        //aggsSearch(client);
    }

    @SneakyThrows
    @Test
    public void getAll() {
        // SearchResponse就是 GET /product2/_search 所拿到的完整的东西，想要什么去get...
        SearchResponse response = client.prepareSearch("product2").get(); //可以设置为"product*"
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
    public void create(TransportClient client) {
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
    @Test
    public void get() {
        GetResponse response = client.prepareGet("product2", "_doc", "1").get();
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        Map<String, Object> source = response.getSource();
        System.out.println("index: " + index);
        System.out.println("type: " + type);
        System.out.println("id: " + id);
        System.out.println("source: " + source);
        System.out.println(response.getSourceAsString());
    }

    @SneakyThrows
    public void update() {
        // 只更改指定的field，其余的保持不变
        UpdateResponse response = client.prepareUpdate("product2", "_doc", "5")
                                      .setDoc(XContentFactory.jsonBuilder()
                                          .startObject()
                                              .field("name", "hongmi erji") // updated name
                                          .endObject()
                                      )
                                  .get();
        System.out.println("Rest status: " + response.status());
        System.out.println(response.getResult());
    }

    public void updatePrice(Integer id, double price) {
        UpdateResponse response = null;
        try {
            response = client.prepareUpdate("product2", "_doc", id.toString())
                    .setDoc(XContentFactory.jsonBuilder()
                    .startObject()
                        .field("price", price)
                    .endObject())
                .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.status());
        System.out.println(response.getGetResult()); // null
    }

    @SneakyThrows
    public void delete() {
        DeleteResponse response = client.prepareDelete("product2", "_doc", "1").get();
        System.out.println(response.getResult());
    }

    @SneakyThrows
    public void multiSearch() {
        // 查询所有name中包含“小米”，且价格小于等于4000的
        SearchResponse response = client.prepareSearch("product2")
                .setTypes("_doc") //可以删掉
                .setQuery(QueryBuilders.termQuery("name", "xiaomi"))
                .setPostFilter(QueryBuilders.rangeQuery("price").from(0).to(4000)) // 链式编程
                .setFrom(0).setSize(2) //从第几条（而不是第几页）开始显示，总共显示几条数据
                .get();
        SearchHit hits[] = response.getHits().getHits();
        Arrays.stream(hits).forEach(System.out::println);
        Arrays.stream(hits).forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    @SneakyThrows
    @Test
    public void aggsSearch() {
        // 除了subAggregation都是自己摸索出来的，高兴
        SearchResponse response = client.prepareSearch("product2")
                .addAggregation(
                        AggregationBuilders.dateHistogram("group_by_month")
                            .field("date")
                            .calendarInterval(DateHistogramInterval.MONTH)
                        .subAggregation(
                            AggregationBuilders
                                .terms("by_tag")
                                    .field("tags.keyword") // 聚合查询需要正排索引需要keyword
                            .subAggregation(
                                AggregationBuilders
                                    .avg("avg_price")
                                        .field("price")
                            )
                        )
                )
                .setSize(0)
            .execute().actionGet();

        // 拿Aggregation、转Map -> 取值 -> 强转 -> 拿buckets、遍历 -> 拿bucket的Aggregation、转Map ...
        Map<String, Aggregation> map = response.getAggregations().asMap();
        Aggregation groupByMonth = map.get("group_by_month");
        Histogram dates = (Histogram)groupByMonth;
        Iterator<Histogram.Bucket> buckets = (Iterator<Histogram.Bucket>)dates.getBuckets().iterator();
        while (buckets.hasNext()) {
            Histogram.Bucket dateBucket = buckets.next();
            System.out.println("\n\n月份: " + dateBucket.getKeyAsString() + "\n计数： " + dateBucket.getDocCount());
            Aggregation groupByTag = dateBucket.getAggregations().asMap().get("by_tag");
            StringTerms terms = (StringTerms) groupByTag;
            Iterator<StringTerms.Bucket> tagsBucket = terms.getBuckets().iterator();
            while (tagsBucket.hasNext()) {
                StringTerms.Bucket tagBucket = tagsBucket.next();
                System.out.println("\t标签名称: " + tagBucket.getKey() + "\n\t数量: " + tagBucket.getDocCount());
                Aggregation avgPrice = tagBucket.getAggregations().asMap().get("avg_price");
                Avg avg = (Avg)avgPrice;
                System.out.println("\t平均价格: " + avg.getValue() + "\n");
            }
        }
    }
}
