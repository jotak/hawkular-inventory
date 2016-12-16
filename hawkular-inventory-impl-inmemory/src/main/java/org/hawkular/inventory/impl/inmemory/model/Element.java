/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.inventory.impl.inmemory.model;

import java.util.Map;

/**
 * @author Joel Takvorian
 */
public abstract class Element {
    private final String canonicalPath;
    private final String name;
    private final Map<String, String> properties;

    Element(String canonicalPath, String name, Map<String, String> properties) {
        this.canonicalPath = canonicalPath;
        this.name = name;
        this.properties = properties;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (canonicalPath != null ? !canonicalPath.equals(element.canonicalPath) : element.canonicalPath != null)
            return false;
        if (name != null ? !name.equals(element.name) : element.name != null) return false;
        return properties != null ? properties.equals(element.properties) : element.properties == null;
    }

    @Override public int hashCode() {
        int result = canonicalPath != null ? canonicalPath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Element{" +
                "canonicalPath='" + canonicalPath + '\'' +
                ", name=" + name +
                ", properties=" + properties +
                '}';
    }
}
