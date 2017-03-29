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
package com.tugmodel.tug.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tugmodel.client.model.Model;
import com.tugmodel.tug.util.Clauses.Clause;

/**
 * Tug utilities.
 */
public class TestClauses {

    @Test
    public void testClauses() {
        // assertTrue(getSQLWhereParams("like = ?").get(0).equals("like"));
        // assertTrue(getSQLWhereParams("y = 1 and ax != ?").get(0).equals("ax"));
        // assertTrue(getSQLWhereParams("y = 1 and (ay != ?)").get(0).equals("ay"));

        // Current limitation. Needs fix.
        // assertTrue(getSQLWhereParams("y = 1 and like = ?)").get(0).equals("like"));
        // assertTrue(getSQLWhereParams("y = 1 and not = ?)").get(0).equals("like"));

        // assertTrue(getSQLWhereParams("y = 1 and x like ?)").get(0).equals("x"));
        // assertTrue(getSQLWhereParams("y = 1 and x like ?)").size() == 1);
        // assertTrue(getSQLWhereParams("y = 1 and x not like ?)").get(0).equals("x"));
        //
        // assertTrue(getSQLWhereParams("y = ? and x not like ?)").get(0).equals("y"));
        // assertTrue(getSQLWhereParams("y = ? and x not like ?)").get(1).equals("x"));
        //
        // assertTrue(getSQLWhereParams("y = 1 and x beetween ? and ?)").get(0).equals("x"));

        Clause clause = Clauses.getClause("x = ?", 2);
        assertTrue(clause.validates(new Model().set("x", 2)));
        assertFalse(clause.validates(new Model().set("x", 3)));

    }

}
