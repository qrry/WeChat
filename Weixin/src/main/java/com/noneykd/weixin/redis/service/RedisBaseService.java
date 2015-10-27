package com.noneykd.weixin.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.redis.base.ShardedJedisSentinelPool;

import redis.clients.jedis.ShardedJedis;

@Component
public class RedisBaseService {

	@Autowired
    protected ShardedJedisSentinelPool sentinelPool;

    protected int MINUTES = 60;
    protected int HOURS = 60 * 60;
    protected int ONEDAY = 60 * 60 * 24;

    /**
     * 使用完后释放jedis连接
     * 
     * @param jedis
     */
    protected void colseJedis(ShardedJedis jedis) {

        if (jedis != null) {
            jedis.close();
        }
    }

    public void setSentinelPool(ShardedJedisSentinelPool sentinelPool) {
        this.sentinelPool = sentinelPool;
    }

}
