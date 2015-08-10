package com.own.cache2k.direct;

import org.cache2k.Cache;
import org.cache2k.CacheBuilder;
import org.junit.Assert;


public class CacheManipulation {
//	http://www.slideshare.net/cruftex/cache2k-java-caching-turbo-charged-fosdem-2015
public static void main(String[] args) {
	Cache<String, String> c=CacheBuilder.newCache(String.class, String.class).build();
	String val=c.peek("something");
	System.out.println("没有向缓存放入数据时获取："+val);
	c.put("somthing", "hello");
	val=c.get("somthing");
	System.out.println("已向缓存放数据："+val);
	c.close();
}
}
