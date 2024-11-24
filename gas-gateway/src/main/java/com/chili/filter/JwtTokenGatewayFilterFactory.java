package com.chili.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.chili.utils.JwtUtil;

@Component
@Slf4j
public class JwtTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtTokenGatewayFilterFactory.Config> {

    public JwtTokenGatewayFilterFactory() {
        // 传递 Config.class 以确保正确的类型传递
        super(Config.class);
        log.info("JwtTokenGatewayFilterFactory 已加载");
    }

    @Override
    public GatewayFilter apply(Config config) {
        //log.info("进行jwt令牌校验");

        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("token");
            String url = exchange.getRequest().getURI().getPath();

            // 登录和注册接口不需要校验 JWT
            if (url.contains("/login") || url.contains("/enroll")) {
                log.info("登录或注册请求，不需要校验");
                return chain.filter(exchange);
            }

            if (token == null || token.isEmpty()) {
                log.info("令牌为空");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                // 校验 JWT 令牌
                JwtUtil.parseJWT("02chili", token);
            } catch (Exception e) {
                log.error("令牌校验失败", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            log.info("令牌校验成功，放行");
            return chain.filter(exchange);
        };
    }

    // 配置类，用于未来扩展配置
    public static class Config {
        // 可以在此处添加更多配置参数
    }
}
