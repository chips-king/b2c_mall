package com.b2cmall.order.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 商品查询快照来源，店铺和商品ID必须与本次请求身份及目标一致。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductVO(Long id, Long shopId, String cateName, String skuName, String sellPoint,
                        Integer stock, Integer price, Integer status, Integer type) { }
