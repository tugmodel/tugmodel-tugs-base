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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.tugmodel.client.model.Model;
import com.tugmodel.client.model.list.ModelList;
import com.tugmodel.tug.base.BaseCrudTug;
import com.tugmodel.tug.util.Clauses;
import com.tugmodel.tug.util.Clauses.Clause;

/**
 * A handy tug that loads stuff from both classpath and folders on the disc.
 * A "path" parameter denoting the folder is expected in the configuration of the tug :
 *  - If the path is relative then a JAR (classpath) is tried.
 *  - Else a file system folder is used. 
 */
public class FolderBasedTug<M extends Model> extends BaseCrudTug<M> {
    public static final String KEY_PATH = "path";

    // getResources(Pattern.compile("deleteme2\\.txt"));
    // This has the benefits that iterates in all jars.
    // public static Collection<String> getResources(final Pattern pattern) {
    // final ArrayList<String> retval = new ArrayList<String>();
    // final String classPath = System.getProperty("java.class.path", ".");
    // final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
    // for (final String element : classPathElements) {
    // retval.addAll(getResources(element, pattern));
    // }
    // return retval;
    // }
    //
    // private static Collection<String> getResources(final String element, final Pattern pattern) {
    // final ArrayList<String> retval = new ArrayList<String>();
    // final File file = new File(element);
    // if (file.isDirectory()) {
    // retval.addAll(getResourcesFromDirectory(file, pattern));
    // } else {
    // retval.addAll(getResourcesFromJarFile(file, pattern));
    // }
    // return retval;
    // }
    //
    // private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
    // final ArrayList<String> retval = new ArrayList<String>();
    // ZipFile zf;
    // try {
    // zf = new ZipFile(file);
    // } catch (final ZipException e) {
    // throw new Error(e);
    // } catch (final IOException e) {
    // throw new Error(e);
    // }
    // final Enumeration e = zf.entries();
    // while (e.hasMoreElements()) {
    // final ZipEntry ze = (ZipEntry) e.nextElement();
    // final String fileName = ze.getName();
    // final boolean accept = pattern.matcher(fileName).matches();
    // if (accept) {
    // retval.add(fileName);
    // }
    // }
    // try {
    // zf.close();
    // } catch (final IOException e1) {
    // throw new Error(e1);
    // }
    // return retval;
    // }
    //
    // private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
    // final ArrayList<String> retval = new ArrayList<String>();
    // final File[] fileList = directory.listFiles();
    // for (final File file : fileList) {
    // if (file.isDirectory()) {
    // retval.addAll(getResourcesFromDirectory(file, pattern));
    // } else {
    // try {
    // final String fileName = file.getCanonicalPath();
    // final boolean accept = pattern.matcher(fileName).matches();
    // if (accept) {
    // retval.add(fileName);
    // }
    // } catch (final IOException e) {
    // throw new Error(e);
    // }
    // }
    // }
    // return retval;
    // }

    // Take a look also at
    // https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/io/support/PathMatchingResourcePatternResolver.java
    protected List<InputStream> getResourceListing(String path) {
        List<InputStream> res = new ArrayList<InputStream>();
        try {
            Class clazz = this.getClass();
            File folder = new File(path);
            if (folder.isDirectory()) {
                for (File file : folder.listFiles()) {
                    res.add(new FileInputStream(file));
                }
            } else {
                path = path.trim();
                if (!(path.charAt(0) == '.')) {
                    path = "./" + path;
                }
                URL dirURL = clazz.getClassLoader().getResource(path);
                if (dirURL != null && dirURL.getProtocol().equals("file")) {
                    // A file path. Like for example in development mode when
                    // files are in target folder.
                    File parent = new File(dirURL.toURI());
                    String[] files = parent.list();
                    for (String file : files) {
                        res.add(new FileInputStream(new File(parent, file)));
                    }
                }

                if (dirURL == null) {
                    // Assume same jar as class.
                    String me = clazz.getName().replace(".", "/") + ".class";
                    dirURL = clazz.getClassLoader().getResource(me);
                }

                if (dirURL.getProtocol().equals("jar")) {
                    // JAR path.
                    String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out JAR
                                                                                                   // file.
                    JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                    Enumeration<JarEntry> entries = jar.entries();
                    Set<String> result = new HashSet<String>();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(path)) {
                            String entry = name.substring(path.length());
                            int checkSubdir = entry.indexOf("/");
                            if (checkSubdir >= 0) {
                                // If subdirectory, return directory name.
                                entry = entry.substring(0, checkSubdir);
                            }
                            // result.add(entry);
                            res.add(this.getClass().getResourceAsStream(name));
                        }
                    }
                }
            }
        } catch (Exception e) {
            for (InputStream is : res) {
                try {
                    is.close();
                } catch (Exception ex) {}
            }
            throw new RuntimeException(e);
        }
        return res;
    }

    public List<M> getModels(String like) {
        List<M> list = new ArrayList<M>();
        // Take a look at Spring PathMatchingResourcePatternResolver that has is more flexible.   
        
        List<InputStream> streams = getResourceListing(this.getConfig().asString(KEY_PATH));
        for (InputStream is : streams) {
            try {
                StringBuilder sb = new StringBuilder();
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
                M m = (M) getConfig().mapper().deserialize(sb.toString());
                list.add(m);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if ("*".equals(like)) {
            for (M m : list) {
                if (m.getId().equals(like)) {
                    List<M> single = new ArrayList<M>();
                    single.add(m);
                    return single;
                }
            }
        }

        return list;
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

