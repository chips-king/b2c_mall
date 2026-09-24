package com.b2cmall.shop.web;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.shop.service.ShopService;
import com.b2cmall.shop.service.ShopRegisterAdapter;
import com.b2cmall.shop.web.request.ShopRegisterV2RequestVO;
import com.b2cmall.shop.service.ShopInitializationService;
import com.b2cmall.shop.web.request.ShopRegisterRequestVO;
import com.b2cmall.shop.web.request.RetryInitializationRequestVO;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 网关剥离 /api/shop 前缀；注册仅受理异步任务，查询接口返回持久化状态。 */
@RestController
public class ShopController {
    private final ShopService shopService;
    private final ShopInitializationService initializationService;

    public ShopController(ShopService shopService, ShopInitializationService initializationService) {
        this.shopService = shopService;
        this.initializationService = initializationService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponseVO<Map<String, Object>>> register(@RequestBody @Valid ShopRegisterRequestVO request) {
        Long shopId = shopService.register(ShopRegisterAdapter.adapt(request));
        return response(initializationService.initialize(shopId));
    }

    @PostMapping("/v2/register")
    public ResponseEntity<BaseResponseVO<Map<String, Object>>> registerV2(@RequestBody @Valid ShopRegisterV2RequestVO request) {
        return response(initializationService.initialize(shopService.register(ShopRegisterAdapter.adapt(request))));
    }

    @GetMapping("/registration/status")
    public BaseResponseVO<Map<String, Object>> status(@RequestParam("shopId") Long shopId) {
        return BaseResponseVO.success(initializationService.status(shopId));
    }

    @PostMapping("/registration/retry")
    public ResponseEntity<BaseResponseVO<Map<String, Object>>> retry(@RequestParam("shopId") Long shopId,
            @RequestBody @Valid RetryInitializationRequestVO request) {
        return response(initializationService.retry(shopId, request));
    }

    private ResponseEntity<BaseResponseVO<Map<String, Object>>> response(ShopInitializationService.Submission result) {
        return ResponseEntity.status(result.status())
                .body(new BaseResponseVO<>(result.status(), result.message(), result.data()));
    }
}
