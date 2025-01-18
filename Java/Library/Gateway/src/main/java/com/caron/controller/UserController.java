package com.caron.controller;


import com.caron.commom.Result;
import com.caron.entity.User;
import com.caron.service.Impl.UserServiceImpl;
import com.caron.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;


import java.util.List;


@RestController
@RequestMapping("/local/user")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    /*
     * 用户注册
     * */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user){
        return userServiceImpl.register(user);
    }

    /*
     * 用户登录
     * */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user, ServerWebExchange exchange){
        //         ? Result.success(userServiceImpl.login(user)) : Result.error("-1","用户名或密码错误");
        // 1. 用户登录验证
        Result<?> login = userServiceImpl.login(user);
        // 2. 创建token
        // 2.1. 判断登录是否成功
        if(login.getCode().equals("0")){
            // 2.2. 创建token
            String token = TokenUtils.genToken((User) login.getData());
            // 2.3. 将token放入cookie
            // Cookie cookie = new Cookie("token", token);
            // 2.4. 限制 Cookie 的作用域，使其只对指定域名下的页面生效
            // cookie.setDomain("");
            // 2.5. setPath("/") 是为了确保在整个应用程序的根路径下都可以访问这个 Cookie
            // cookie.setPath("/");
            // 2.6. 将cookie放入respond
            // response.addCookie(cookie);
            // 2.2. 使用 ResponseCookie 创建 Cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .domain("")  // 设置 Cookie 的域名
                    .path("/")  // 设置 Cookie 的作用路径
//                    .httpOnly(true)  // 设置为 HttpOnly，防止 JavaScript 访问 Cookie
//                    .secure(true)  // 在 HTTPS 上才会发送
//                    .maxAge(3600)  // 设置 Cookie 过期时间（单位秒）
                    .build();

            // 2.3. 将 Cookie 添加到响应头
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Set-Cookie", cookie.toString());
            return login;
        }
        return login;
    }

    /*
     * 修改密码
     * */
    @PutMapping("/password")
    public  Result<?> update( @RequestBody User user,
                              ServerWebExchange exchange){
        // 1. 修改密码
        Result<?> result = userServiceImpl.modifyPassword(Math.toIntExact(user.getId()), user.getPassword());
        // 2. 撤销token
        // 2.1. 创建一个同名的 Cookie，并将其 Max-Age 设置为 0，使其过期
        // Cookie cookie = new Cookie("token", null);
        // cookie.setMaxAge(0);
        // 2.2. 限制 Cookie 的作用域，使其只对指定域名下的页面生效
        // cookie.setDomain("");
        // 2.3. setPath("/") 是为了确保在整个应用程序的根路径下都可以访问这个 Cookie
        // cookie.setPath("/");
        // 2.4. 将cookie放入respond
        // response.addCookie(cookie);
        ResponseCookie cookie = ResponseCookie.from("token", null)
                .domain("")  // 设置 Cookie 的域名
                .path("/")  // 设置 Cookie 的作用路径
//                    .httpOnly(true)  // 设置为 HttpOnly，防止 JavaScript 访问 Cookie
//                    .secure(true)  // 在 HTTPS 上才会发送
//                    .maxAge(3600)  // 设置 Cookie 过期时间（单位秒）
                .build();

        // 2.3. 将 Cookie 添加到响应头
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Set-Cookie", cookie.toString());

        return result;
    }

    /*
    * 修改用户信息
    * */
    @PutMapping("/modifyUser")
    public Result<?> modifyUser(@RequestBody User user){
        return userServiceImpl.modifyUser(user);
    }

    /*
    * 批量删除用户
    * */
    @PostMapping("/deleteBatchUser")
    public  Result<?> deleteBatchUser(@RequestBody List<Integer> ids){
        return userServiceImpl.deleteBatchUser(ids);
    }

    /*
    * 删除用户
    * */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id){
        return userServiceImpl.deleteUser(id);
    }

    /*
     * 分页查询用户
     * */
    @GetMapping("/findPageUser")
    public Result<?> findPageUser(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search1,
                               @RequestParam(defaultValue = "") String search2,
                               @RequestParam(defaultValue = "") String search3,
                               @RequestParam(defaultValue = "") String search4
                                ){
        return userServiceImpl.findPageUser(pageNum, pageSize, search1, search2, search3, search4);
    }

    /*
    * 授权借书
    * */
    @PutMapping("/accreditBorrow/{id}")
    public Result<?> accreditBorrow(@PathVariable Long id){
        return userServiceImpl.accreditBorrow(id);
    }

    /*
     * 取消授权借书
     * */
    @PutMapping("/NoBorrow/{id}")
    public Result<?> NoBorrow(@PathVariable Long id){
        return userServiceImpl.NoBorrow(id);
    }

}
