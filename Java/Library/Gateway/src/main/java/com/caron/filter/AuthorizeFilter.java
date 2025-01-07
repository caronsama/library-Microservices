package com.caron.filter;

import com.caron.commom.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.caron.util.TokenUtils.MyVerifyToken;

// 过滤器注解， 优先级约小越早
// @Order(-1)
// 过滤器优先级最高
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private ObjectMapper objectMapper;

    /*
    * ServerWebExchange exchange： 请求上下文，里面可以获取Request、Response等信息
    * GatewayFilterChain chain：过滤器链，用来把请求委托给下一个过滤器
    * 这里是检验请求头的第一个参数 "authorization" 是否等于 admin
    * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 0.排除登录注册模块的验证
        String path = exchange.getRequest().getURI().getPath();
        // 0.1.如果请求路径是 /user/login 或 /user/register，跳过校验
        if ("/user/login".equals(path) || "/user/register".equals(path)) {
            return chain.filter(exchange); // 跳过校验，继续执行后续过滤器
        }

        // 1.获取cookie
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        // 1.1.查找名为 "token" 的 Cookie
        if (cookies.containsKey("token")) {
            // 获取 token 的值
            String verifyToken = cookies.get("token").get(0).getValue();
            // 2.核实token
            try {
                if (MyVerifyToken(verifyToken)){
                    // 2.1.是，放行
                    return chain.filter(exchange);
                }else {
                    // 2.2.否，拦截
                    // 获取响应对象
                    ServerHttpResponse response = exchange.getResponse();
                    // 设置响应头为 JSON 类型
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    // 创建自定义的错误结果
                    Result errorResult = Result.error("-2", "验证错误，请重新登录");
                    // 将错误结果转成 JSON
                    String errorMessage = "";
                    try {
                        errorMessage = objectMapper.writeValueAsString(errorResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 设置响应状态码
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    // 写入响应体
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes())));
                }
            }catch (Exception e){
                // 获取响应对象
                ServerHttpResponse response = exchange.getResponse();
                // 设置响应头为 JSON 类型
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                // 创建自定义的错误结果
                Result errorResult = Result.error("-2", "验证错误，请重新登录");
                // 将错误结果转成 JSON
                String errorMessage = "";
                try {
                    errorMessage = objectMapper.writeValueAsString(errorResult);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                // 设置响应状态码
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                // 写入响应体
                return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes())));
            }
        } else {
            // 1.2.不存在，拦截
            // 获取响应对象
            ServerHttpResponse response = exchange.getResponse();
            // 设置响应头为 JSON 类型
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            // 创建自定义的错误结果
            Result errorResult = Result.error("-2", "token过期或不存在，请重新登录");
            // 将错误结果转成 JSON
            String errorMessage = "";
            try {
                errorMessage = objectMapper.writeValueAsString(errorResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 设置响应状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 写入响应体
            return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes())));
        }
    }


    /*
    * 这个方法和 @Order(-1) 效果一样
    * */
    @Override
    public int getOrder() {
        return -1;
    }
}
