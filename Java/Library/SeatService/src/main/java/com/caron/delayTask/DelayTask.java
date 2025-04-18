package com.caron.delayTask;

import com.caron.service.SeatService;
import com.caron.service.impl.SeatServiceImpl;
import com.caron.util.SpringBeanUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.springframework.beans.factory.annotation.Autowired;

public class DelayTask {

    private static final String REDIS_URL = "redis://192.168.241.97:6379";
    private static final String EXPIRED_CHANNEL = "__keyevent@0__:expired";

    /**
     * 订阅 Redis 过期事件
     */
    public static void subscribeToExpireEvent(RedisClient redisClient) {
        StatefulRedisPubSubConnection<String, String> pubSubConnection = redisClient.connectPubSub();

        pubSubConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.err.println("1订单超时自动释放：" + message);
                // 这里可以添加额外的业务逻辑，如通知用户、回滚库存等
            }

            @Override
            public void message(String pattern, String channel, String message) {
                // 回滚订单
                SpringBeanUtil.getBean(SeatServiceImpl.class).cancelSeat(Integer.parseInt(message));
                System.err.println("2订单超时自动释放：" + message);
            }

            @Override
            public void subscribed(String channel, long count) {}
            @Override
            public void psubscribed(String pattern, long count) {}
            @Override
            public void unsubscribed(String channel, long count) {}
            @Override
            public void punsubscribed(String pattern, long count) {}
        });

        // 订阅 Redis 过期事件
        pubSubConnection.sync().psubscribe(EXPIRED_CHANNEL);
        System.out.println("已订阅 Redis 过期事件...");
    }

    /**
     * 添加订单并设置自动过期时间
     */
    public static void addOrder(RedisClient redisClient, String orderKey, String orderData, int expireSeconds) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisCommands<String, String> syncCommands = connection.sync();
            syncCommands.setex(orderKey, expireSeconds, orderData);
            System.err.println("订单 " + orderKey + " 已创建，" + expireSeconds + " 秒后自动过期...");
        }
    }

    public static void deleteOrder(RedisClient redisClient, String orderKey) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisCommands<String, String> commands = connection.sync();
            commands.del(orderKey); // 删除订单消息
        }catch (Exception e){
            System.err.println("网络连接超时");
        }
    }


    /**
     * 用于设置 notify-keyspace-events Ex
     * */
    public static void checkNotifyConfig(){
        RedisClient redisClient = RedisClient.create(REDIS_URL);
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisCommands<String, String> commands = connection.sync();

            // **检查是否已启用 notify-keyspace-events**
            String notifyConfig = commands.configGet("notify-keyspace-events").get("notify-keyspace-events");
            if (notifyConfig == null || notifyConfig.isEmpty()) {
                System.out.println("未检测到 notify-keyspace-events 配置，正在启用...");
                commands.configSet("notify-keyspace-events", "Ex");
                System.out.println("已成功启用 notify-keyspace-events Ex");
            } else {
                System.out.println("notify-keyspace-events 已启用：" + notifyConfig);
            }
        }
    }

}
