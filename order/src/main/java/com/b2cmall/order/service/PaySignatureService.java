package com.b2cmall.order.service;

import com.b2cmall.order.web.request.PayCallbackRequestVO;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 模拟渠道与回调共享HMAC密钥；密钥仅从进程配置读取，每次计算使用独立Mac实例。 */
@Service
public class PaySignatureService {
    private final byte[] key;
    public PaySignatureService(@Value("${mall.payment.callback-secret-base64}") String encoded) {
        try { key = Base64.getDecoder().decode(encoded == null ? "" : encoded); }
        catch (IllegalArgumentException error) { throw new IllegalArgumentException("支付回调密钥必须为Base64编码"); }
        if (key.length < 32) { throw new IllegalArgumentException("支付回调密钥至少需要32字节"); }
    }
    public String sign(PayCallbackRequestVO callback) {
        // 固定字段顺序及版本前缀；渠道、结果和流水只接受无换行的已校验值。
        String text = String.join("\n", "b2c-mall-mock-pay-v1", callback.getPaymentId().toString(),
                callback.getOrderId().toString(), callback.getPayType(), callback.getAmount().toString(),
                callback.getChannelTradeNo(), callback.getResult());
        try {
            Mac mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException error) { throw new IllegalStateException("支付签名组件不可用", error); }
    }
    public boolean verify(PayCallbackRequestVO callback) {
        String supplied = callback.getSignature();
        return supplied != null && supplied.matches("[0-9a-f]{64}")
                && MessageDigest.isEqual(sign(callback).getBytes(StandardCharsets.US_ASCII),
                        supplied.getBytes(StandardCharsets.US_ASCII));
    }
}
