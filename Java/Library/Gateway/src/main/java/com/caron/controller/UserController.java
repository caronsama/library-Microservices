package com.caron.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/user")
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
    public Result<?> login(@RequestBody User user,HttpServletResponse response){
        //         ? Result.success(userServiceImpl.login(user)) : Result.error("-1","用户名或密码错误");
        // 1. 用户登录验证
        Result<?> login = userServiceImpl.login(user);
        // 2. 创建token
        // 2.1. 判断登录是否成功
        if(login.getCode().equals("0")){
            // 2.2. 创建token
            String token = TokenUtils.genToken((User) login.getData());
            // 2.3. 将token放入cookie
            Cookie cookie = new Cookie("token", token);
            // 2.4. 限制 Cookie 的作用域，使其只对指定域名下的页面生效
            cookie.setDomain("");
            // 2.5. setPath("/") 是为了确保在整个应用程序的根路径下都可以访问这个 Cookie
            cookie.setPath("/");
            // 2.6. 将cookie放入respond
            response.addCookie(cookie);
            return login;
        }
        return login;
    }

    /*
     * 修改密码
     * */
    @Token
    @PutMapping("/password")
    public  Result<?> update( @RequestBody User user,
                              @CookieValue(name = "token")String token,
                              HttpServletResponse response){
        // 1. 修改密码
        Result<?> result = userServiceImpl.modifyPassword(Math.toIntExact(user.getId()), user.getPassword());
        // 2. 撤销token
        // 2.1. 创建一个同名的 Cookie，并将其 Max-Age 设置为 0，使其过期
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        // 2.2. 限制 Cookie 的作用域，使其只对指定域名下的页面生效
        cookie.setDomain("");
        // 2.3. setPath("/") 是为了确保在整个应用程序的根路径下都可以访问这个 Cookie
        cookie.setPath("/");
        // 2.4. 将cookie放入respond
        response.addCookie(cookie);
        return result;
    }

    /*
    * 修改用户信息
    * */
    @Token
    @PutMapping("/modifyUser")
    public Result<?> modifyUser(@RequestBody User user,@CookieValue(name = "token")String token){
        return userServiceImpl.modifyUser(user);
    }

    /*
    * 批量删除用户
    * */
    @Token
    @PostMapping("/deleteBatchUser")
    public  Result<?> deleteBatchUser(@RequestBody List<Integer> ids,@CookieValue(name = "token")String token){
        return userServiceImpl.deleteBatchUser(ids);
    }

    /*
    * 删除用户
    * */
    @Token
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id,@CookieValue(name = "token")String token){
        return userServiceImpl.deleteUser(id);
    }

    /*
     * 分页查询用户
     * */
    @Token
    @GetMapping("/findPageUser")
    public Result<?> findPageUser(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search1,
                               @RequestParam(defaultValue = "") String search2,
                               @RequestParam(defaultValue = "") String search3,
                               @RequestParam(defaultValue = "") String search4
                                ,@CookieValue(name = "token")String token){
        return userServiceImpl.findPageUser(pageNum, pageSize, search1, search2, search3, search4);
    }

    /*
    * 授权借书
    * */
    @Token
    @PutMapping("/accreditBorrow/{id}")
    public Result<?> accreditBorrow(@PathVariable Long id,@CookieValue(name = "token")String token){
        return userServiceImpl.accreditBorrow(id);
    }

    /*
     * 取消授权借书
     * */
    @Token
    @PutMapping("/NoBorrow/{id}")
    public Result<?> NoBorrow(@PathVariable Long id,@CookieValue(name = "token")String token){
        return userServiceImpl.NoBorrow(id);
    }

}
