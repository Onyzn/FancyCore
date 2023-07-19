package tk.fancystore.noisier.database.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisBlob {

  public static JedisPool pool;

  //jedis.hset("key", "field", "value");
  //      jedis.hset("key", "field2", "value");
  //      jedis.hdel("players", "field2");
  //      jedis.hexists("key", "field");
  //      jedis.hgetAll("key");

  public static void initJedisPool() {
    pool = new JedisPool("some-fancy-ip", 12345);

    try (Jedis jedis = pool.getResource()) {
      jedis.auth("some-secure-password");

    }
  }
}
