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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tugmodel.client.model.Model;

/**
 * Clauses that can be run o model records.
 * TODO: Needs enhancement or better a free Java sql parser.
 */
public class Clauses {

    public static interface Clause {
        public boolean validates(Model record);
    }

    private static Map<String, OP> OPERATIONS = new HashMap<String, OP>();
    public static enum OP {
        ADD("+"), MINUS("-"), EQ("="), LT("<"), GT(">"), LE("<="), GE(">="), LIKE("like");
        private final String op;

        private OP(String op) {
            this.op = op;
            OPERATIONS.put(op, this);
        }

        public String getOperation() {
            return op;
        }
    }

    public static class AritmeticCondition implements Clause {
        public Object left;
        public Object right;
        public OP op;

        public boolean validates(Model record) {
            switch (op) {
                case EQ: {
                return right.equals(record.get((String) left));
            }
            case LT: {
                return record.asInt((String) left) < (Integer) right;
            }
            }
            return true;
        }
    }

    // TODO: Needs enhancement or better a free Java sql parser.
    public static Clause getClause(String where, Object... params) {
        Object ENUM = OP.ADD;
        List<String> names = new ArrayList<String>();
        String[] splits = where.split("\\?");
        for (String s : splits) {
            // Always start from end.
            s = s.trim();
            boolean isCandidate = false;
            StringBuilder candidate = new StringBuilder();
            StringBuilder operation = new StringBuilder();
            for (int i = s.length() - 1; i >= 0; i--) {
                char c = s.charAt(i);
                if (Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '$' || c == '.') {
                    candidate.insert(0, c);
                    isCandidate = true;
                } else {
                    if (isCandidate) {
                        // A candidate was found let's check is not "in", "like", "between", "and".
                        // TODO: Needs fixing in case it is a valid name(backward/forward analysis).
                        if (candidate.toString().trim().equals("") || candidate.toString().equalsIgnoreCase("in")
                                || candidate.toString().equalsIgnoreCase("like")
                                || candidate.toString().equalsIgnoreCase("between")
                                || candidate.toString().equalsIgnoreCase("and")
                                || candidate.toString().equalsIgnoreCase("not")) {
                            isCandidate = false;
                            candidate.setLength(0);
                        } else {
                            names.add(candidate.toString());
                            isCandidate = false;
                            break;
                        }
                    } else {
                        operation.insert(0, c);
                    }
                }
            }
            if (isCandidate) {
                names.add(candidate.toString());
                isCandidate = false;
            }
            AritmeticCondition cond = new AritmeticCondition();
            cond.left = names.get(0);
            cond.right = params[0];
            cond.op = OPERATIONS.get(operation.toString().trim());
            return cond;
        }
        return null;
    }
}
