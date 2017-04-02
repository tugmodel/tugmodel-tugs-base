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
package com.tugmodel.tug.config;

import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.Config;
import com.tugmodel.client.model.config.tugs.TugConfig;
import com.tugmodel.client.model.meta.Meta;
import com.tugmodel.client.model.meta.datatype.DataType;
import com.tugmodel.client.tug.Tug;
import com.tugmodel.client.tug.TugFactory;

/**
 * 
 *
 */
public class TestConfigTug {

    @Test
    public void testDefaultConfig() {
        Config config = new Config().setId("defaults");
        config.fetch();
        assertTrue(config.getTows().size() > 0);
        assertTrue(config.getDataTypes().get(0).getClass().equals(DataType.class));
        assertTrue(config.getTugs().get(0).getClass().equals(TugConfig.class));
        assertTrue(config.getModels().get(0).getClass().equals(Meta.class));

    }

    @Test
    public void testGetModels() {
        List<Meta> metas = Meta.s.fetchAll();
        assertTrue(metas.size() > 0);

        Meta meta = Meta.s.fetchById("tm.Model");
        assertTrue(meta.getId().equals("tm.Model"));
        assertTrue(meta.getAttributes().size() > 0); // At least "id" atribute is present.

    }

    @Test
    public void testGetTugByModelId() {
        Tug tug = TugFactory.getByTug(ConfigTug.class);
        assertTrue(tug != null);

        Tug tug2 = TugFactory.getByModel("tm.Config");
        assertTrue(tug2 != null);

        Tug tug3 = TugFactory.getByModel(Config.class);
        assertTrue(tug3 != null);

        assertTrue(tug.getClass() == tug2.getClass());
        assertTrue(tug2.getClass() == tug3.getClass());
    }

    @Test
    public void testPrettyPrint() {
        Model model = new Model();
        String s = model.toString();
        assertTrue(s.contains(" "));

        Model model2 = (Model) model.tug().getConfig().mapper().deserialize(s);
        assertTrue(Model.class.getCanonicalName().equals(model2.get("@c")));
    }

    @Test
    public void testWorkOn() {
        Config config = new Config().setId("defaults").fetch();
        ConfigTug<Meta> tug = new ConfigTug<Meta>().workWith(config);
        tug.getConfig().set("type", "models");
        Meta m = tug.where("id = ?", "tm.Model").first();
        assertTrue(m.getId().equals("tm.Model"));
    }
}

