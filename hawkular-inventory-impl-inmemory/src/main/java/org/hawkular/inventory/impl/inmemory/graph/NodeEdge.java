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

import org.hawkular.inventory.impl.inmemory.model.Edge;

/**
 * Graph's edge, that contains a raw {@link Edge} and source and target {@link Node}s
 * @author Joel Takvorian
 */
public class NodeEdge implements GraphElement {
    private final Edge edge;
    private final Node source;
    private final Node target;

    NodeEdge(Edge edge, Node source, Node target) {
        this.edge = edge;
        this.source = source;
        this.target = target;
    }

    public Edge getEdge() {
        return edge;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    @Override public GraphChunk getOwner() {
        return source.getOwner();
    }
}
