package com.own.cache2k;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.CacheBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.CacheSource;
import org.cache2k.EntryExpiryCalculator;
import org.cache2k.impl.Entry;
import org.junit.Test;

public class ExpireTime {

//	@Test
	public void expireDurationTest() {
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.expiryDuration(1000, TimeUnit.MILLISECONDS)//过期的时间指定
				.build();
        try {
        	cache.put("key", "value");
        	System.out.println("缓存中的值:"+cache.peek("key"));
			TimeUnit.SECONDS.sleep(2);//大概有2毫秒的偏差
			System.out.println("peek:"+cache.peek("key"));
			System.out.println("get:"+cache.get("key"));
			cache.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
//	@Test
	public void keepDataAfterExpiredTest() {
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.expiryDuration(1000, TimeUnit.MILLISECONDS)//过期的时间指定
				.keepDataAfterExpired(true)//get设置这个之后必须要有数据源,peek可以正常使用
				.build();
		try {
			System.out.println("peek:"+cache.peek("a"));
			System.out.println("not exist:"+cache.get("a"));
			TimeUnit.SECONDS.sleep(2);//大概有2毫秒的偏差
			cache.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 *calculateExpiryTimez中每个参数的含义
	 */
//	@Test
	public void entryExpiryCalculatorTest1(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
			.entryExpiryCalculator(
				new EntryExpiryCalculator<String,String>() {
					/**
					 * put数据进缓存时使用
					 * _key - the cache key
					   _value - the value to be cached, may be null
					   _fetchTime - this is the current time in millis. If a cache source was used to fetch the value, this is the time before the fetch was started.
					   _oldEntry - entry representing the current mapping, if there is a value present. If the current entry holds an exception, this is null.
					         如果存在同一个key，那么原来的那个就变成了_oldEntry，对于缓存中不存在的key,执行put的时候它的值也会是null
					   */
					public long calculateExpiryTime(String _key, String _value,
							long _fetchTime, CacheEntry<String, String> _oldEntry) {
						// TODO Auto-generated method stub
						System.out.println("新的数据是 key:"+_key+",value:"+_value+",fetchTime:"+_fetchTime);
						if (_oldEntry!=null) {
							System.out.println("旧的数据是 key:"+_oldEntry.getKey()+",value:"+_oldEntry.getValue()+",fetchTime:"+_oldEntry.getLastModification());
						}
						return 0;
					}
				})
			.build();
    	cache.put("key", "value");
		cache.put("key", "new Value");
		cache.put("key1", "value");
		cache.put("key2", "value");
		System.out.println("缓存中存在的实体数:"+cache.getTotalEntryCount());
		cache.close();
		System.out.println("---------new test end--------");
	}
	
	/**
	 * calculateExpiryTime 返回值 
	 */
//	@Test
	public void entryExpiryCalculatorTest2(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
			.source(new CacheSource<String, String>() {
				
				public String get(String arg0) throws Throwable {
					// TODO Auto-generated method stub
					System.out.println("cache source使用中.....");
					return "value1";
				}
			})
			.entryExpiryCalculator(
				  new EntryExpiryCalculator<String,String>() {
	                  /* 
					   If 0 is returned, this means entry expires immediately  or is always fetched from the source.
					 */
					public long calculateExpiryTime(String _key, String _value,
							long _fetchTime, CacheEntry<String, String> _oldEntry) {
						// TODO Auto-generated method stub
						System.out.println("calculateExpiryTime调用中。。。");
						return 0;
					}
				})
			.build();
		cache.put("a", "value");
		System.out.println(cache.peek("a"));//peek拿到空值
		System.out.println(cache.get("a"));//数据已经过期 ，从新出发了新的从数据源中拿数据
		System.out.println("---------new test end--------");
	}
//	@Test
	public void entryExpiryCalculatorTest3(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.source(new CacheSource<String, String>() {
					
					public String get(String arg0) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("cache source使用中.....");
						return "value1";
					}
				})
				.entryExpiryCalculator(
						new EntryExpiryCalculator<String,String>() {
							/* 返回从纪元到现在的毫秒值作为过期时间
							 * 返回的值只要在创建时间之前，都会过时，但是默认设置的最大值之能是10分钟
					   Returns the time of expiry in milliseconds since epoch.
							 */
							public long calculateExpiryTime(String _key, String _value,
									long _fetchTime, CacheEntry<String, String> _oldEntry) {
								// TODO Auto-generated method stub
								System.out.println("calculateExpi=ryTime调用中。。。");
								//这里返回的是过期时间
								return System.currentTimeMillis()-1;
							}
						})
				.build();
		cache.put("a", "value");
		try {
			TimeUnit.MILLISECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println(cache.peek("a"));//peek拿到null
		System.out.println(cache.get("a"));//从新出发了新的从数据源中拿数据
		System.out.println("---------new test end--------");

	}
	
//	@Test
	public void entryExpiryCalculatorTest4(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.source(new CacheSource<String, String>() {
					
					public String get(String arg0) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("cache source使用中.....");
						return "value1";
					}
				})
				.entryExpiryCalculator(
						new EntryExpiryCalculator<String,String>() {
							/* 
							 */
							public long calculateExpiryTime(String _key, String _value,
									long _fetchTime, CacheEntry<String, String> _oldEntry) {
								// TODO Auto-generated method stub
								System.out.println("calculateExpi=ryTime调用中。。。");
								//这里返回的是过期时间
//								   If Long.MAX_VALUE is returned it means there is no specific expiry time known or needed.
//							       In any case the effective expiry duration will never be longer than the configured expiry
								return System.currentTimeMillis()+3000;
							}
						})
				.build();
		System.out.println("缓存中放入  a--->value");
		cache.put("a", "value");
		System.out.println("休眠2秒");
				try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("get:"+cache.get("a"));//从缓存拿到数据  
		System.out.println("peek："+cache.peek("a"));//从缓存拿到数据 
		System.out.println("休眠2秒");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("peek:"+cache.peek("a"));//没有拿到数据  
		System.out.println("get:"+cache.get("a"));//从新出发了新的从数据源中拿数据  
		System.out.println("---------new test end--------");
	}
	
//    If a negated value of the expiry time is returned, this means that sharp expiry is requested explicitly.
				/*
				 * When sharp expiry is enabled, the expiry timer goes
				 * before the actual expiry to switch back to a time checking
				 * scheme when the get method is invoked. This prevents
				 * that an expired value gets served by the cache if the time
				 * is too late. A recent GC should not produce more then 200
				 * milliseconds stall. If longer GC stalls are expected, this
				 * value needs to be changed. A value of LONG.MaxValue
				 * suppresses the timer usage completely.
				 * 
				 * 为true正好在是这个时间点的时候 到达2000毫秒的时候是不会反回旧值，而是FALSE是有可能的
				 */
//	@Test
	public void entryExpiryCalculatorTest(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.source(new CacheSource<String, String>() {
					
					public String get(String arg0) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("cache source使用中.....");
						return "value1";
					}
				})
				.entryExpiryCalculator(
						new EntryExpiryCalculator<String,String>() {
							public long calculateExpiryTime(String _key, String _value,
									long _fetchTime, CacheEntry<String, String> _oldEntry) {
								// TODO Auto-generated method stub
//								If sharp expiry is requested, the value will not be returned any more by the cache when the point in time is reached.
								System.out.println("calculateExpiryTime调用中。。。"+_fetchTime);
								return _fetchTime+2000;
							}
						})
						.sharpExpiry(false)//默认值为false
						.backgroundRefresh(true)
						.build();
		System.out.println("缓存中放入  a--->value");
		cache.put("a", "value");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("get:"+cache.get("a"));//从数据源拿到值  value也可以
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("get:"+cache.get("a"));//从缓存中拿数据  
		System.out.println("---------new test end--------");
	} 
//	@Test
	public void entryExpiryCalculatorTest5(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
//				.source(new CacheSource<String, String>() {
//					
//					public String get(String arg0) throws Throwable {
//						// TODO Auto-generated method stub
//						System.out.println("cache source使用中.....");
//						return "value1";
//					}
//				})
				.entryExpiryCalculator(
						new EntryExpiryCalculator<String,String>() {
							/* 
							 */
							public long calculateExpiryTime(String _key, String _value,
									long _fetchTime, CacheEntry<String, String> _oldEntry) {
								// TODO Auto-generated method stub
								System.out.println("calculateExpi=ryTime调用中。。。");
								return System.currentTimeMillis()+1000;
							}
						})
				.keepDataAfterExpired(false)//这样get只会从数据源那数据 
				.build();
		cache.put("a", "value");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("peek:"+cache.peek("a"));
		System.out.println("get:"+cache.get("a"));//从新出发了新的从数据源中拿数据  
		System.out.println("---------new test end--------");
		
	}
//	@Test
	public void entryExpiryCalculatorTest6(){
		System.out.println("---------new test start--------");
		Cache<String, String> cache=CacheBuilder.newCache(String.class, String.class)
				.source(new CacheSource<String, String>() {
					
					public String get(String arg0) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("cache source使用中.....");
						return "value1";
					}
				})
				.entryExpiryCalculator(
						new EntryExpiryCalculator<String,String>() {
							/* 
							 */
							public long calculateExpiryTime(String _key, String _value,
									long _fetchTime, CacheEntry<String, String> _oldEntry) {
								// TODO Auto-generated method stub
								System.out.println("calculateExpiryTime调用中。。。");
								return System.currentTimeMillis()+3000;
							}
						})
				.expiryDuration(1, TimeUnit.SECONDS)
				.build();
		cache.put("a", "value");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println(cache.get("a"));//从新出发了新的从数据源中拿数据  
		System.out.println("---------new test end--------");
	}
}
