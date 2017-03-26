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
package com.tugmodel.tug.file;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.MapperConfig;
import com.tugmodel.client.model.config.TugConfig;
import com.tugmodel.client.model.list.ModelList;
import com.tugmodel.mapper.jackson.JacksonMappers;

public class TestJarTug {


    
    @Test
    public void testFolderWithinJar() throws URISyntaxException {

        // getResources(Pattern.compile("deleteme2\\.txt"));
        // new File(getClass().getClassLoader().getResource("tugmodel/meta").toURI()).list()

        FolderBasedTug<Model> tug = new FolderBasedTug<Model>();
        TugConfig config = new TugConfig();

        // File parent = new
        // File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile()
        // .getParentFile();
        config.set(config.KEY_MODELS_SERVED, Model.class.getCanonicalName());
        config.set(FolderBasedTug.KEY_PATH, "file_test");
        MapperConfig mc = new MapperConfig();

        config.setMapper(JacksonMappers.getTypedMapper());
        tug.setConfig(config);

        ModelList list = tug.fetchAll(); // list.toString()
        list.setCrudTug(tug);
        list.toString(); // Trigger fetching.
        System.out.println(list);
    }

    @Test
    public void testFileSystemFolder() throws URISyntaxException {

        new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile()
                .getParentFile();

    }

}
