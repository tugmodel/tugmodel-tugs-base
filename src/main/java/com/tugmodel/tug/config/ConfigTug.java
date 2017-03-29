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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.Config;
import com.tugmodel.client.model.config.tugs.TugConfig;
import com.tugmodel.client.model.list.ModelList;
import com.tugmodel.client.tug.config.IConfigTug;
import com.tugmodel.mapper.jackson.JacksonMappers;
import com.tugmodel.tug.base.BaseCrudTug;
import com.tugmodel.tug.util.Clauses;
import com.tugmodel.tug.util.Clauses.Clause;

/**
 * Merges on the loaded file config the defaults config.
 * If you need to use a model that gets stuff from db. Then you need to provide a tm-config.json and  
 * 
 * 
 */

public class ConfigTug<M extends Model> extends BaseCrudTug<M> implements IConfigTug<M> {
    public ConfigTug() {
        // Direct setting of mapper. Not recommended.
        getConfig().mapper(JacksonMappers.getConfigReaderMapper());
    }

    private Config cache;

    public ConfigTug<M> workWith(Config config) {
        cache = config;
        return this;
    }
    public List<M> getModels(String like) {
        List<Model> list = new ArrayList();
        TugConfig tc = getConfig();
        String type = tc.asString("type");

        boolean mustCache = getConfig().get("cache", Boolean.class, false);

        if (type.equals("all")) {
            if (!mustCache || cache == null) {
                // May need to use convert.
                // Can not deserialize directly in Config because @c is not within string.
                Model mm = tc.mapper().deserialize(readClasspathFile("/tugmodel/tugmodel-config-defaults.json")); // getConfig().mapper().toPrettyString(mm)
                Config config = tc.mapper().convert(mm, Config.class);

                String customConfigRes = "tugmodel-config.json";
                if (!resourceExists(customConfigRes)) {
                    customConfigRes = "/tugmodel/tugmodel-config.json";
                }
                if (resourceExists(customConfigRes)) {
                    Model customConfig = tc.mapper().deserialize(readClasspathFile(customConfigRes)); // Model.class
                    tc.mapper().updateModel(customConfig, config);
                }
                if (mustCache) {
                    cache = config;
                }
                list.add(config);
            } else {
                list.add(cache);
            }
        } else {
            // Will need some caching setting for speed.
            if (cache == null)
                cache = new Config().setId("defaults").fetch();
            if (type.equals("models")) {
                list.addAll(cache.getModels());
            } else if (type.equals("dataTypes")) {
                list.addAll(cache.getDataTypes());
            } else if (type.equals("tugs")) {
                list.addAll(cache.getTugs());
            } else if (type.equals("mappers")) {
                list.addAll(cache.getMappers());
            } else if (type.equals("tows")) {
                list.addAll(cache.getTows());
            }
        }
        List<M> r = (List) list;
        if (!"*".equals(like)) {
            r = new ArrayList();
            for (Model m : list) {
                if (m.getId().equals(like)) {
                    // r.add((M) m.clone());
                    r.add((M) m);
                }
            }
        }
        return r;
    }

    public boolean resourceExists(String name) {
        return this.getClass().getResource(name) != null;
    }

    /**
     * Reads a file from the classpath.
     */
    public String readClasspathFile(String path) {
        // Take a look at
        // https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/io/support/PathMatchingResourcePatternResolver.java
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = this.getClass().getResourceAsStream(path);
            if (is != null) {
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                while (line != null) {
                    sb.append(line + "\n");
                    line = buf.readLine();
                }
                buf.close();
            } else {
                sb.append("{}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    @Override
    public List<M> fetch(ModelList<M> ml) {
        if (ml.isFetchAll()) {
            return new ModelList<M>(getModels("*"));
        } else if (ml.isFetchById()) {
            return getModels((String) ml.getParams()[0]);
        } else if (ml.isWhere()) {
            List<M> models = getModels("*");
            List<M> valid = new ArrayList<M>();
            for (M model : models) {
                // Java 8 filter ;).
                Clause clause = Clauses.getClause(ml.getWhere(), ml.getParams());
                if (clause.validates(model))
                    valid.add(model);
            }
            return valid;
        }
        return null;
    }
}
