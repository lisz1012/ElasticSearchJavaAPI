package com.lisz.mybatisplus;

import com.lisz.es.entity.Product;
import com.lisz.es.mapper.ProductMapper;
import com.lisz.es.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Timestamp;

/*
在MySQL中插入数据，为ES操作提供准备
 */
@RunWith(SpringRunner.class)
@SpringBootTest //不要指定classes
public class AddRecords {
	@Autowired
	private ProductService service;

	@Test
	public void add() {
		Product product1 = Product.builder()
			     .name("xiaomi phone")
				 .desc("shouji zhong de zhandouji")
				 .price(13999)
				 .tags("xingjiabi,fashao,buka")
				 .createTime(Timestamp.valueOf("2020-01-01 00:00:00"))
				 .build();
		service.save(product1);

		Product product2 = Product.builder()
				.name("xiaomi nfc phone")
				.desc("zhichi quangongneng nfc,shouji zhong de jianjiji")
				.price(4999)
				.tags("xingjiabi,fashao,gongjiaoka")
				.createTime(Timestamp.valueOf("2020-01-01 00:00:00"))
				.build();
		service.save(product2);

		Product product3 = Product.builder()
				.name("nfc phone")
				.desc("shouji zhong de hongzhaji")
				.price(2999)
				.tags("xingjiabi,fashao,menjinka")
				.createTime(Timestamp.valueOf("2020-06-22 00:00:00"))
				.build();
		service.save(product3);

		Product product4 = Product.builder()
				.name("xiaomi erji")
				.desc("erji zhong de huangmenji")
				.price(999)
				.tags("low,bufangshui,yinzhicha")
				.createTime(Timestamp.valueOf("2020-06-22 00:00:00"))
				.build();
		service.save(product4);

		Product product5 = Product.builder()
				.name("hongmi erji")
				.desc("erji zhong de kendeji")
				.price(399)
				.tags("lowbaole,xuhangduan,zhiliangx")
				.createTime(Timestamp.valueOf("2020-06-22 00:00:00"))
				.build();
		service.save(product5);
	}
}
