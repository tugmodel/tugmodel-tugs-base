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
package com.tugmodel.mapper.jackson;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.sample.Employee;

/**
 * NOTE: This suite of tests should become a test suite for any new Mapper implementation.
 */
public class TestJacksonTypedMapper extends TestJacksonMapper {
    @Before    
    public void init() {
        mapper = JacksonMappers.getTypedMapper();
    }
    
    @Test
    public void prettyPrintTest() {
        Model m = new Model().set("x", 1).set("y", "1").set("z", null);

        String s = (String) mapper.serialize(m);
        String indent = " ", lf = System.getProperty("line.separator");

        assertTrue(s.contains(Model.class.getCanonicalName()));
    }

    @Test
    public void test2wayUsingPrettyMapper() {        
        super.test2wayUsingPrettyMapper();
    }
    
    @Test
    public void twoWayWithChild() {
        super.twoWayWithChild();
    }
   
    // This does not work due to problem
    // @Test
    // public void testGettersWithConfigMapper() {
    // super.testGettersWithConfigMapper();
    // }
    
    @Test
    public void testUnderlyingDataMap2Way() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = ((JacksonMapper) JacksonMappers.getTypedMapper()).getMapper();

        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        Model child = new Model().set("c", 3);

        m.set("child", child);

        ArrayList l = new ArrayList();
        l.add("aaaa");
        l.add(child);

        Model m3 = new Model().set("name", "m3");
        Model m2 = new Model().set("name", "m2").set("m3", m3);
        l.add(m2);

        m.set("list", l);

        String s = objectMapper.writeValueAsString(m.data());
        Map map = objectMapper.readValue(s, java.util.Map.class);

        assertTrue(map.get("child") instanceof Model);
        assertTrue(((List) map.get("list")).get(1) instanceof Model);

        Model m2d = (Model) ((List) map.get("list")).get(2);
        assertTrue(m2d.get("m3") instanceof Model); // Pica fiindca e un model inside alt model sub any getter deci pica
                                                    // sub aceeasi incidenta.

        //////////////////////////////////////////////////
        // FAI SI UN TEST CU O CLASA DERIVATA DIN MODEL SI VEZI CA ISI PASTREAZA TIPUL.
        //////////////////////////////////////////////////
    }

    public static class AnEmployee extends Model<AnEmployee> {

        public String getName() {
            return asString("name");
        }

        public AnEmployee setName(String name) {
            return set("name", name);
        }

    }

    @Test
    @SuppressWarnings("all")
    public void testHashMap2Way() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = ((JacksonMapper) JacksonMappers.getTypedMapper()).getMapper();

        Model m = new Model().set("x", 1).set("y", "1").set("z", null);
        Model child = new Employee().set("c", 3);

        m.set("child", child);

        HashMap map = new HashMap();
        map.put("m", m);

        String s = objectMapper.writeValueAsString(map);
        Map res = objectMapper.readValue(s, Map.class);
        Model deserM = (Model) res.get("m");
        assertTrue(deserM.get("child") instanceof Employee); // Tests that derived classes keep the type.

    }
}















