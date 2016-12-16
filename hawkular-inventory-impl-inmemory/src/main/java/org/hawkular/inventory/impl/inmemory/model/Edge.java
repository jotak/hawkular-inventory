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
public class Edge extends Element {

    private final String sourceCp;
    private final String targetCp;

    public Edge(String canonicalPath, String name, Map<String, String> properties, String sourceCp, String targetCp) {
        super(canonicalPath, name, properties);
        this.sourceCp = sourceCp;
        this.targetCp = targetCp;
    }

    public String getSourceCp() {
        return sourceCp;
    }

    public String getTargetCp() {
        return targetCp;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Edge edge = (Edge) o;

        if (sourceCp != null ? !sourceCp.equals(edge.sourceCp) : edge.sourceCp != null) return false;
        return targetCp != null ? targetCp.equals(edge.targetCp) : edge.targetCp == null;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sourceCp != null ? sourceCp.hashCode() : 0);
        result = 31 * result + (targetCp != null ? targetCp.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Edge{" +
                "sourceCp='" + sourceCp + '\'' +
                ", targetCp='" + targetCp + '\'' +
                "} " + super.toString();
    }
}
