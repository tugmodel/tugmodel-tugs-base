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

import com.tugmodel.client.mapper.Mapper;
import com.tugmodel.client.model.Model;
import com.tugmodel.mapper.jackson.JacksonMappers;

/**
 * DO NOT USE. It is used only internally when loading config.
 */
public class BootstrapConfigTug<M extends Model> {
    public static Model fetch(Model model) {
        Mapper mapper = JacksonMappers.getConfigReaderMapper();
        mapper.updateModel(readClasspathFile("/tugmodel/tugmodel-config-defaults.json"), model);

        String customConfigRes = "tugmodel-config.json";
        if (!resourceExists(customConfigRes)) {
            customConfigRes = "/tugmodel/tugmodel-config.json";
        }
        if (resourceExists(customConfigRes)) {
            mapper.updateModel(readClasspathFile(customConfigRes), model);
        }
        return model;
    }

    public static boolean resourceExists(String name) {
        return BootstrapConfigTug.class.getResource(name) != null;
    }

    /**
     * Reads a file from the classpath.
     */
    public static String readClasspathFile(String path) {
        // Take a look at
        // https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/io/support/PathMatchingResourcePatternResolver.java
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = BootstrapConfigTug.class.getResourceAsStream(path);
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

}
