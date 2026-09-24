package com.b2cmall.product.web;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.product.service.ProductService;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import com.b2cmall.product.web.response.ProductResponseVO;
import org.springframework.web.bind.annotation.*;

/** 网关只剥离/api，保留课堂/product/create路径。 */
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService products;
    public ProductController(ProductService products) { this.products = products; }
    @PostMapping("/create")
    public BaseResponseVO<ProductResponseVO> create(@RequestBody CreateProductRequestVO request) {
        return BaseResponseVO.success(ProductResponseVO.from(products.create(request)));
    }
    @GetMapping("/{id}")
    public BaseResponseVO<ProductResponseVO> get(@PathVariable("id") long id) {
        return BaseResponseVO.success(ProductResponseVO.from(products.get(id)));
    }
}
