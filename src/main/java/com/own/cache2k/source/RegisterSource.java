package com.own.cache2k.source;

import java.net.SocketTimeoutException;

import org.cache2k.Cache;
import org.cache2k.CacheBuilder;
import org.cache2k.CacheSource;
import org.cache2k.PropagatedCacheException;

public class RegisterSource {
	// cache2k. get() will always do its best to get the date, peek() will only return data if the cache contains it. 
public static void main(String[] args) {
   officeTest();
}
public static void officeTest(){
	CacheSource<String, Integer> _lengthCacheSource=
			new CacheSource<String, Integer>() {

				public Integer get(String arg0) throws Throwable {
					// TODO Auto-generated method stub
					System.out.println("从数据源获取");
					return arg0.length();
				}
			};
	Cache<String, Integer> cache=CacheBuilder.newCache(String.class, Integer.class)
			.source(_lengthCacheSource)
			.build();
	int v=cache.get("hello");
	System.out.println(v==5);
	v=cache.get("long string");
	System.out.println(v==11);
	cache.close();
}
public static void mySuppressExceptionTest(){
	CacheSource<String, Integer> _lengthCacheSource=
			new CacheSource<String, Integer>() {

				public Integer get(String arg0) throws Throwable {
					// TODO Auto-generated method stub
					//int a=1/0;// org.cache2k.PropagatedCacheException: (expiry=2015-08-06 16:56:27.988)
//					throw new FileNotFoundException();  org.cache2k.PropagatedCacheException: (expiry=2015-08-06 16:59:32.085) java.io.FileNotFoundException
					throw new SocketTimeoutException();
					//return arg0.length();
				}
			};
			//包装异常成
			try {
				Cache<String, Integer> exceptionCache=CacheBuilder.newCache(String.class, Integer.class)
						.source(_lengthCacheSource)
						.build();
				int j=0;
				
				while(j++<2)
				{
					int v=exceptionCache.get("1");
				System.out.println(v);
				exceptionCache.close();
				}
			} catch (PropagatedCacheException e) {
				// TODO: handle exception
				System.out.println(e.getCause()+":message:"+e.getMessage());
			}
}
}
