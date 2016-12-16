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
package org.hawkular.inventory.impl.inmemory.graph;

import java.util.ArrayList;
import java.util.List;

import org.hawkular.inventory.impl.inmemory.model.Vertex;

/**
 * Graph's node, that contains a raw {@link Vertex} and lists of in/out {@link NodeEdge}s
 * @author Joel Takvorian
 */
public class Node implements GraphElement {
    private GraphChunk owner;
    private final Vertex vertex;
    private final List<NodeEdge> in;
    private final List<NodeEdge> out;

    Node(Vertex vertex) {
        this.vertex = vertex;
        this.in = new ArrayList<>();
        this.out = new ArrayList<>();
    }

    public Vertex getVertex() {
        return vertex;
    }

    public List<NodeEdge> getIn() {
        return in;
    }

    public List<NodeEdge> getOut() {
        return out;
    }

    public void addIn(NodeEdge edge) {
        in.add(edge);
    }

    public void addOut(NodeEdge edge) {
        out.add(edge);
    }

    @Override public GraphChunk getOwner() {
        return owner;
    }

    public void setOwner(GraphChunk owner) {
        this.owner = owner;
    }
}
