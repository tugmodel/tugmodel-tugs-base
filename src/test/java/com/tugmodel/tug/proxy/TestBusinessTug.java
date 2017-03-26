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

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.TugConfig;
import com.tugmodel.client.tug.BusinessTug;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 
 */
public class TestBusinessTug {

    public static class XModel extends Model<XModel> {

    }

    public static class YModel extends Model<YModel> {

    }

    public static interface MyTug extends BusinessTug {
        public void enable(XModel x, YModel y);
    }

    public static class MyTugImpl implements MyTug {

        public void enable(XModel x, YModel y) {
            x.set("x1", 1);
            y.set("y1", 1);
        }

        public TugConfig getConfig() {
            // TODO Auto-generated method stub
            return null;
        }

        public BusinessTug setConfig(TugConfig config) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    static class MyInvocationHandler implements MethodInterceptor {

        private MyTug target;

        public MyInvocationHandler(MyTug ary) {
            this.target = ary;
        }

        /**
         * @param obj
         *            "this", the enhanced object
         * @param method
         *            intercepted Method
         * @param args
         *            argument array; primitive types are wrapped
         * @param proxy
         *            used to invoke super (non-intercepted method); may be called as many times as needed
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getName().equals("enable")) {
                XModel x = (XModel) args[0];
                x.set("setByProxy", true);
                System.out.println("inside proxy.");
            }
            return proxy.invoke(target, args);
        }
    }

    @Test
    public void testRemoteBussinessTugViaProxy() {

        MyTug proxy = (MyTug) Enhancer.create(MyTug.class, new MyInvocationHandler(new MyTugImpl()));
        XModel x = new XModel();
        YModel y = new YModel();
        proxy.enable(x, y);

        assertTrue(x.asBoolean("setByProxy") == true);
        assertTrue(x.asInteger("x1") == 1);

    }
}
