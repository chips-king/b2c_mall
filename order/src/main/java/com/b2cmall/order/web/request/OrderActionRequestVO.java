package com.b2cmall.order.web.request;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
public record OrderActionRequestVO(@NotNull @Positive Long orderId) { }
