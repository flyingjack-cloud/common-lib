package top.flyingjack.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis实现的缓存操作
 *
 * @author Zumin Li
 * @date 2025/4/12 15:28
 */
public class RedisCacheServiceImpl implements CacheService {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    private RedisTemplate<String, Object> redisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setVerified(String key, Object value, long expireTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Cache set failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (redisTemplate.opsForValue().get(key) == null) {
            log.error("Cache set verification failed - key={} not found after write", key);
            throw new RuntimeException("Cache write verification failed: " + key);
        }
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setVerified(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Cache set failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (redisTemplate.opsForValue().get(key) == null) {
            log.error("Cache set verification failed - key={} not found after write", key);
            throw new RuntimeException("Cache write verification failed: " + key);
        }
    }

    @Override
    public void set(String key, Object value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delSafe(String key) {
        Boolean result;
        try {
            result = redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Cache del failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache delete failed: " + key, e);
        }
        if (result == null || !result) {
            log.error("Cache del failed - key={} delete returned false/null", key);
            throw new RuntimeException("Cache delete failed: " + key);
        }
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    @Override
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }

    @Override
    public void hSetSafe(String key, String hashKey, Object value, long time) {
        Boolean result;
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            result = expire(key, time);
        } catch (Exception e) {
            log.error("Cache hSet failed - key={}, hashKey={}, error={}", key, hashKey, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (result == null || !result) {
            log.error("Cache hSet expire failed - key={}, hashKey={}", key, hashKey);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public void hSetVerified(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Cache hSet failed - key={}, hashKey={}, error={}", key, hashKey, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (redisTemplate.opsForHash().get(key, hashKey) == null) {
            log.error("Cache hSet verification failed - key={}, hashKey={} not found after write", key, hashKey);
            throw new RuntimeException("Cache write verification failed: " + key);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public void sAddSafe(String key, Object... values) {
        Long count;
        try {
            count = redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Cache sAdd failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache sAdd failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Long sAdd(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public void sAddSafe(String key, long time, Object... values) {
        Long count;
        try {
            count = redisTemplate.opsForSet().add(key, values);
            expire(key, time);
        } catch (Exception e) {
            log.error("Cache sAdd failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache sAdd failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public void lPushSafe(String key, Object value) {
        Long count;
        try {
            count = redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Cache lPush failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache lPush failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Long lPush(String key, Object value, long time) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return count;
    }

    @Override
    public void lPushSafe(String key, Object value, long time) {
        Long count;
        try {
            count = redisTemplate.opsForList().rightPush(key, value);
            expire(key, time);
        } catch (Exception e) {
            log.error("Cache lPush failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache lPush failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public void lPushAllSafe(String key, Object... values) {
        Long count;
        try {
            count = redisTemplate.opsForList().rightPushAll(key, values);
        } catch (Exception e) {
            log.error("Cache lPushAll failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache lPushAll failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Long lPushAll(String key, long time, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public void lPushAllSafe(String key, long time, Object... values) {
        Long count;
        try {
            count = redisTemplate.opsForList().rightPushAll(key, values);
            expire(key, time);
        } catch (Exception e) {
            log.error("Cache lPushAll failed - key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Cache write failed: " + key, e);
        }
        if (count == null) {
            log.error("Cache lPushAll failed - key={} returned null", key);
            throw new RuntimeException("Cache write failed: " + key);
        }
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public Boolean tryLock(String key, String value, long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Boolean releaseLock(String key, String value) {
        String currentValue = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(currentValue) && currentValue.equals(value)) {
            return redisTemplate.delete(key);
        }
        return false;
    }
}
