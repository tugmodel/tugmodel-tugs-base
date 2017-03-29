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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.tugmodel.client.model.list.ModelList;
import com.tugmodel.client.model.sample.Employee;
import com.tugmodel.client.tug.Tug;

public class TestFolderBasedTug {

    @Test
    public void testNoStackOverflowOnPrettyPrint() throws URISyntaxException {
        ModelList<Employee> emps = Employee.s.fetchAll();
        Employee.s.getConfig().mapper().deserialize("{}");
        String s = emps.tug().getConfig().toString();
    }

    @Test
    public void testFolderWithinJar() throws URISyntaxException {

        Tug t = Employee.s;

        ModelList<Employee> emps = Employee.s.fetchAll();
        // System.out.println(emps.get(0));

        assertTrue(emps.get(0).getName() != null);
    }

    @Test
    public void testFolderOutside() throws URISyntaxException {
        File project = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                .getParentFile().getParentFile();
        
        File folder = new File(project, "/src/test/resources/employees2");

        Employee.s.getConfig().set("path", folder.getAbsolutePath());

        ModelList<Employee> emps = Employee.s.fetchAll();

        assertTrue(emps.size() == 1);
    }
}
