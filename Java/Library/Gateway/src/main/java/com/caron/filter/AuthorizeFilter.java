package com.caron.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 过滤器注解， 优先级约小越早
// @Order(-1)
// 过滤器优先级最高
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    /*
    * ServerWebExchange exchange： 请求上下文，里面可以获取Request、Response等信息
    * GatewayFilterChain chain：过滤器链，用来把请求委托给下一个过滤器
    * 这里是检验请求头的第一个参数 "authorization" 是否等于 admin
    * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        // 2.获取参数中的 authorization 参数
        String token = params.getFirst("token");
        // 3.判断参数值是否等于 admin
        if ("admin".equals(auth)) {
            // 4.是，放行
            return chain.filter(exchange);
        }
        // 5.否，拦截
        // 5.1.设置状态码 401 未登录
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 5.2.拦截请求
        return exchange.getResponse().setComplete();
    }


    /*
    * 这个方法和 @Order(-1) 效果一样
    * */
    @Override
    public int getOrder() {
        return -1;
    }
}
