package com.caron.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.caron.dao.UserMapper;
import com.caron.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


@Component
public class TokenUtils {

    @Autowired
    private UserMapper userMapper;

    private static UserMapper staticUserMapper;

    public static String signature = "2140129049";

    @PostConstruct
    public void init() {
        staticUserMapper = userMapper;
    }

    /**
     * 生成token
     * @param user
     * @return
     * 这段代码是用于生成用户令牌（Token）的静态方法。让我逐步解释这个方法的功能和代码结构：
     *
     * 1.public static String genToken(User user) {：这是方法的签名，它是一个公有的静态方法，接受一个 User 类型的参数 user，并返回一个字符串（Token）。
     * 2.JWT.create()：这是一个用于创建JWT（JSON Web Token）的方法。JWT是一种用于在网络上安全传输信息的开放标准。通过 create() 方法创建了一个JWT对象。
     * 3..withExpiresAt(DateUtil.offsetDay(new Date(), 1))：这是在JWT中设置过期时间的部分。DateUtil.offsetDay(new Date(), 1) 表示获取当前日期，然后向后偏移1天，即在当前时间的基础上增加1天，这将成为令牌的过期时间。过期时间是一个标识，指定了令牌的有效期，确保令牌在一段时间后自动失效。
     * 4..withAudience(user.getId().toString())：这是在JWT中设置受众（Audience）的部分。user.getId().toString() 会将用户的ID转换为字符串，并将其设置为JWT的受众。这表示这个令牌是为特定的用户生成的。
     * 5..sign(Algorithm.HMAC256(user.getPassword()))：这一部分是用于对JWT进行签名的。签名是为了确保令牌的完整性和安全性。Algorithm.HMAC256(user.getPassword()) 使用了HMAC（Hash-based Message Authentication Code）算法，使用用户的密码作为密钥对JWT进行签名。这意味着只有知道用户密码的人才能验证和解析令牌。签名后的JWT最终以字符串形式返回。
     * 6..Algorithm 类用于指定用于签署和验证 JWT 的算法。
     *
     * 综合起来，这个方法的作用是生成一个JWT令牌，其中包含了用户的ID信息、过期时间以及使用用户密码进行签名以确保令牌的安全性。这个令牌可以用于身份验证和授权，通常在客户端与服务器之间进行安全通信时使用。
     */
    public static String genToken(User user) {
        String passwd = user.getPassword();
        try {
            // 创建MessageDigest对象，并指定使用SHA-256算法
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将输入字符串转换为字节数组
            byte[] inputBytes = passwd.getBytes();

            // 使用SHA-256算法对字节数组进行哈希
            byte[] hashBytes = digest.digest(inputBytes);

            // 将哈希结果转换为十六进制字符串
            String passwdString = bytesToHex(hashBytes);

            // 这段token的过期时间是1个小时
            return JWT.create().withExpiresAt(DateUtil.offsetHour(new Date(), 12)).withAudience(user.getId().toString())
                    .withClaim("caron", passwdString)
                    .sign(Algorithm.HMAC256(user.getId() + passwdString + signature));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取token中的用户信息
     * @return
     *
     * 这段代码是一个用于从HTTP请求中解析用户信息的方法。它执行以下操作：
     *
     * 1.HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();：这一行代码用于获取当前HTTP请求对象。RequestContextHolder.getRequestAttributes() 方法用于获取请求的上下文，然后将其转换为 ServletRequestAttributes 类型，最后通过 .getRequest() 方法获取到 HttpServletRequest 对象。这是为了从HTTP请求中获取用户令牌（Token）以后进行解析。
     * 2.String token = request.getHeader("token");：这一行代码从HTTP请求的头部（Header）中获取名为 "token" 的值，该值应该是用户的令牌（Token）。通常，客户端会在HTTP请求的Header中包含令牌以进行身份验证。
     * 3.String aud = JWT.decode(token).getAudience().get(0);：这一行代码用于解码JWT令牌，并获取JWT中的受众（Audience）信息。JWT通常包含了受众信息，表示这个令牌是为哪个用户生成的。在这里，JWT.decode(token) 解码了令牌，然后通过 .getAudience().get(0) 获取了第一个受众信息（通常JWT令牌只有一个受众）。
     * 4.Integer userId = Integer.valueOf(aud);：这一行代码将受众信息（通常是用户的ID）转换为整数，以便后续使用。
     * 5.return staticUserMapper.selectById(userId);：在这里，根据解析出来的用户ID，代码尝试从 staticUserMapper 中选择用户信息。这可能是一个数据库查询操作，用于获取用户的详细信息。
     * 6.catch (Exception e) { log.error("解析token失败", e); return null; }：如果在解析令牌或查找用户信息时发生异常，代码会捕获异常，并记录错误信息到日志中。然后返回 null，表示无法获取用户信息。
     *
     * 总的来说，这段代码用于从HTTP请求中提取令牌，解析令牌以获取用户的ID，然后使用该ID从数据库或其他数据源中获取用户信息。如果出现任何异常，它将返回 null 并记录错误信息。这是一种常见的身份验证和授权机制的一部分，用于验证用户的身份并获取其详细信息。
     *
     */
/*    public static User getUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            String aud = JWT.decode(token).getAudience().get(0);
            Integer userId = Integer.valueOf(aud);
            return staticUserMapper.selectById(userId);
        } catch (Exception e) {
            log.error("解析token失败", e);
            return null;
        }
    }*/


    // 解密JWT Token
    public static boolean MyVerifyToken(String token) {
        try {
            // 解码
            DecodedJWT decodedJWT = JWT.decode(token);
            // 获取受众
            String aud = decodedJWT.getAudience().get(0);
            Integer userId = Integer.valueOf(aud);
            // 获取password
            String passwdString = decodedJWT.getClaim("caron").asString();
            // 获取过期时间
            Long expirationTime = decodedJWT.getExpiresAt().getTime();
            // 获取当前时间
            Long currentTime = System.currentTimeMillis();
            // 检查是否过期
            if (currentTime > expirationTime) {
                System.out.println("Token 已过期");
                return false;
            }
            // 再次制造签名部分
            Algorithm algorithm = Algorithm.HMAC256(userId + passwdString + signature);
            // 验证签名
            JWT.require(algorithm).withAudience(aud).build().verify(token);
            return true;
        } catch (JWTDecodeException exception){
            return false;
        }
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


}
