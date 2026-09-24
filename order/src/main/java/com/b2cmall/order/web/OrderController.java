package com.b2cmall.order.web;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.order.service.OrderService;
import com.b2cmall.order.web.request.CreateOrderRequestVO;
import com.b2cmall.order.web.response.OrderResponseVO;
import org.springframework.web.bind.annotation.*;

/** 网关剥离/api，保留课堂/order/create路径；原始token继续传给Product验证。 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orders;
    public OrderController(OrderService orders) { this.orders = orders; }
    @PostMapping("/create")
    public BaseResponseVO<OrderResponseVO> create(@RequestBody CreateOrderRequestVO request,
            @RequestHeader("Authorization") String token) {
        return BaseResponseVO.success(orders.create(request, token));
    }
    @GetMapping("/{id}")
    public BaseResponseVO<OrderResponseVO> get(@PathVariable("id") long id) {
        return BaseResponseVO.success(orders.get(id));
    }
    @PostMapping("/sent")
    public BaseResponseVO<OrderResponseVO> sent(@RequestBody @javax.validation.Valid com.b2cmall.order.web.request.OrderActionRequestVO request) {
        return BaseResponseVO.success(orders.advance(request.orderId(), com.b2cmall.order.enums.OrderEvent.SENT));
    }
    @PostMapping("/complete")
    public BaseResponseVO<OrderResponseVO> complete(@RequestBody @javax.validation.Valid com.b2cmall.order.web.request.OrderActionRequestVO request) {
        return BaseResponseVO.success(orders.advance(request.orderId(), com.b2cmall.order.enums.OrderEvent.COMPLETED));
    }
}
