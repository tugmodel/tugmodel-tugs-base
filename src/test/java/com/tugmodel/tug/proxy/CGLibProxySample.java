package com.tugmodel.tug.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 */
public class CGLibProxySample {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		List<String> ary = new ArrayList<String>();
		ary.add("Hello");
		ary.add("Proxy");
		ary.add("World!!");
		
		log("create a interface proxy");
		List<String> proxyAry = (List<String>)Enhancer.create(List.class, new MyInvocationHandler(ary));
		for (int i = 0; i < 4; i++) { log(proxyAry.get(i)); }
		
		log("create a class proxy");
		proxyAry = (List<String>)Enhancer.create(ArrayList.class, new MyInvocationHandler(ary));
		for (int i = 0; i < 4; i++) { log(proxyAry.get(i)); }
	}
	
	static class MyInvocationHandler implements MethodInterceptor {
		
		private List<String> ary;

		public MyInvocationHandler(List<String> ary) {
			this.ary = ary;
		}

		/**
		 * @param obj "this", the enhanced object
		 * @param method intercepted Method
		 * @param args argument array; primitive types are wrapped
		 * @param proxy used to invoke super (non-intercepted method); may be called 
		 * as many times as needed
		 */
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			if (isFourthGet(method, args)) {
				return "Bow!!";
			}
			return proxy.invoke(ary, args);
		}

		private boolean isFourthGet(Method method, Object[] args) {
			return "get".equals(method.getName()) && ((Integer)args[0]) == 3;
		}
	}
	
	private static void log(Object msg) {
		System.out.println(msg);
	}
}