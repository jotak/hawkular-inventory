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
public class Vertex extends Element {

    private final String id;
    private final int type;

    public Vertex(String canonicalPath, String name, Map<String, String> properties, String id, int type) {
        super(canonicalPath, name, properties);
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Vertex vertex = (Vertex) o;

        if (type != vertex.type) return false;
        return id != null ? id.equals(vertex.id) : vertex.id == null;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", type=" + type +
                "} " + super.toString();
    }
}
