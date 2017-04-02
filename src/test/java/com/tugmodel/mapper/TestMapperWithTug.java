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
package com.tugmodel.mapper;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.Config;
import com.tugmodel.client.model.config.tugs.TowConfig;
import com.tugmodel.client.model.sample.Employee;
import com.tugmodel.mapper.jackson.JacksonMappers;
/**
 * 
 *
 */
@SuppressWarnings("all")
public class TestMapperWithTug {

    @Test
    public void testConfigReaderMapper() {
        Mapper<Model> mapper = JacksonMappers.getConfigReaderMapper();
        Map map = new HashMap();
        map.put("id", "1");
        map.put("@c", "com.tugmodel.client.model.config.Config");

        Config config = mapper.convert(map, Config.class);
        assertTrue(config.getClass() == Config.class);
    }

    @Test
    public void testPrettyPrintForChildren() {
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        Model child = new Employee().set("c", 3);

        // This will work because it has dedicated getters and setters for it.
        Config config = new Config();
        TowConfig tow = new TowConfig();
        ArrayList list = new ArrayList();
        list.add(tow);
        config.setTows(list);
        assertTrue(config.toString().contains(TowConfig.class.getCanonicalName()));

        /**
         * Mixin on Object adds "@c" also on arrays and maps which is ugly. On the other hand it creates the @c for the
         * generic childs contained in model. The alternative solution would be adding @JsonSubTypes in at the Model
         * level mixin or better add a mixin for each Model subclass. For the moment let's leave it as it is.
         */
        // Uncomment next when the above is fixed.
        // m.set("child", child); // m.get("child")
        // assertTrue(m.toString().contains(Employee.class.getCanonicalName()));

    }
}
