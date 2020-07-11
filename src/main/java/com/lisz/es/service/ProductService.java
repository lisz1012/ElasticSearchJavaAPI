package com.lisz.es.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisz.es.entity.Product;
import com.lisz.es.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {
}
