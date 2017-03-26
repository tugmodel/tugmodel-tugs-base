/*
 * Copyright (c) 2017- Cristian Donoiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tugmodel.tug.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Proxy interceptor.
 * You have 3 alternatives: java.lang.Proxy, CGLib or javaassist. Will try cglib.
 * http://stackoverflow.com/questions/10664182/what-is-the-difference-between-jdk-dynamic-proxy-and-cglib
 * https://gist.github.com/premraj10/3a3eac42a72c32de3a41ec13ef3d56ad
 * https://github.com/edc4it/jpa-case/blob/master/src/main/java/util/JPAUtil.java
 */
public class ProxyUtil {
    // Should be populated with information from Config model.
    private static final Map<String, Object> PROXY_REGISTRY = new HashMap();

    /**
     * Returns the implementation of a tug id/ interface id.
     */
    public static final Object getImpl(Class tugInterface) {
        try {
            String tugClass = tugInterface.getCanonicalName();
            Object impl = PROXY_REGISTRY.get(tugClass);
            if (impl == null) {
                impl = Class.forName(tugClass + "Impl").newInstance();
            }
            return impl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This can call a local or a remote implementation.
     */
    public interface RpcProxy {
        public Object execute(MethodProxy methodProxy, Object[] args) throws Throwable;
    }

    /**
     * This should be a local(same process) proxy.
     */
    public static class LocalRpcProxy implements RpcProxy {
        protected Object target;

        public LocalRpcProxy(Object target) {
            this.target = target;
        }

        public Object execute(MethodProxy methodProxy, Object[] args) throws Throwable {
            return methodProxy.invoke(target, args);
        }
    }
    // /**
    // * This should be a Java RMI proxy.
    // */
    // public static class RmiProxy implements RpcProxy{
    // public Object execute(MethodProxy methodProxy, Object target, Object[]
    // args) throws Throwable {
    // return methodProxy.invoke(target, args);
    // }
    // }

    /**
     * Provide an interface. The default proxy is
     */

    public static <T> T getProxy(Class<T> tugInterface) {
        TugInvocationHandler handler = new TugInvocationHandler(new LocalRpcProxy(getImpl(tugInterface)));
        return (T) Enhancer.create(tugInterface, handler);
    }

    public static class TugInvocationHandler implements MethodInterceptor {

        private RpcProxy rpcProxy; // Tug target.

        public TugInvocationHandler(RpcProxy target) {
            this.rpcProxy = target;
        }

        /**
         * @param obj
         *            "this", the enhanced object
         * @param method
         *            intercepted Method
         * @param args
         *            argument array; primitive types are wrapped
         * @param proxy
         *            used to invoke super (non-intercepted method); may be
         *            called as many times as needed
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("inside proxy.");
            // return proxy.invoke(target, args);
            return rpcProxy.execute(proxy, args);
        }
    }
}
