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
package com.tugmodel.tug.base;

import java.util.List;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.config.tugs.TugConfig;
import com.tugmodel.client.model.list.ModelList;
import com.tugmodel.client.tug.CrudTug;

/**
 * You can use this as base class when implementing tugs.
 */
@SuppressWarnings("all")
public class BaseCrudTug<M extends Model> implements CrudTug<M> {
    protected TugConfig config = new TugConfig();

    public TugConfig getConfig() {
        return config;
    }

    public CrudTug<M> setConfig(TugConfig config) {
        this.config = config;
        return this;
    }

    private Object notImplementedException() {
        throw new RuntimeException("You need to implement the inherited Tug method.");
    }

    /**
     * Returns null if the model is not found. Can be used to test if the model EXISTS.
     */
    public M fetch(M model) {
        ModelList<M> list = new ModelList<M>().where("id=?", model.get("id")).modelId(modelId()).tug(this);
        if (list.size() >= 1) {
            getConfig().mapper().updateModel(list.get(0), model);
            return model;
        }
        return null;
    }

    public M create(M model) {
        return (M) notImplementedException();
    }

    public M update(M model) {
        return (M) notImplementedException();
    }

    public M delete(M model) {
        return (M) notImplementedException();
    }

    public <C extends Model> List<C> add(M model, List<C> childs) {
        return (List<C>) notImplementedException();
    }

    public Object run(String operation, List<Object> params) {
        return notImplementedException();
    }

    protected String modelId() {
        return getConfig().asString("modelId");
    }

    // Additional parameters provided for sending authorization token.
    public M fetchById(String id) {
        ModelList<M> list = new ModelList<M>().where("id=?", id).modelId(modelId()).tug(this);
        return list.size() == 0 ? null : list.get(0);
    }

    public M fetchFirst() {
        List<M> res = fetch(new ModelList<M>().limit(1).modelId(modelId()).tug(this));
        return res.size() == 0 ? null : res.get(0);
    }

    public ModelList<M> fetchAll() {
        ModelList<M> list = new ModelList<M>().where("").modelId(modelId()).tug(this);
        return list;
    }

    public List<M> fetch(ModelList<M> query) {
        return (List<M>) notImplementedException();
    }

    public ModelList<M> where(String query, Object... params) {
        ModelList<M> list = new ModelList<M>().where(query).params(params).modelId(modelId()).tug(this);
        return list;
    }

    public <C extends Model> ModelList<C> where(Class<C> child, String query, Object... params) {
        ModelList<C> list = new ModelList().child(child.getCanonicalName()).where(query).params(params)
                .modelId(modelId()).tug(this);
        return list;
    }

    public <C extends Model> List<C> fetchByRawQuery(Class<C> c, String query, Object... params) {
        return (List<C>) notImplementedException();
    }

    public void transactionStart() {
        notImplementedException();
    }

    public void trasactionCommit() {
        notImplementedException();
    }

    public void trasactionRollback() {
        notImplementedException();
    }

    // public String toString() {
    // return getConfig().toString();
    // }
}
