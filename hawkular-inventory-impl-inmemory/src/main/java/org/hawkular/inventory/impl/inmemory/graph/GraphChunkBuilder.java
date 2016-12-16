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
import java.util.Map;
import java.util.stream.Collectors;

import org.hawkular.inventory.base.spi.GraphChunkPK;
import org.hawkular.inventory.impl.inmemory.model.Edge;
import org.hawkular.inventory.impl.inmemory.model.Vertex;
import org.jboss.logging.Logger;

/**
 * Builds a graph from raw lists of vertices and edges
 * @author Joel Takvorian
 */
public class GraphChunkBuilder {
    private static final Logger DBG = Logger.getLogger(GraphChunkBuilder.class);

    private final GraphChunkPK primaryKey;
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public GraphChunkBuilder(GraphChunkPK primaryKey) {
        this.primaryKey = primaryKey;
    }

    public GraphChunkBuilder addVertices(List<Vertex> vertices) {
        this.vertices.addAll(vertices);
        return this;
    }

    public GraphChunkBuilder addEdges(List<Edge> edges) {
        this.edges.addAll(edges);
        return this;
    }

    public GraphChunk build() {
        Map<String, Node> nodes = vertices.stream().map(Node::new).collect(Collectors.toMap(
                n -> n.getVertex().getCanonicalPath(),
                n -> n
        ));
        // Add edges
        edges.forEach(edge -> {
            Node source = nodes.get(edge.getSourceCp());
            if (source == null) {
                DBG.warnf("Invalid edge '{}': source not found ({})", edge.getCanonicalPath(), edge.getSourceCp());
                return;
            }
            Node target = nodes.get(edge.getTargetCp());
            if (target == null) {
                DBG.warnf("Invalid edge '{}': target not found ({})", edge.getCanonicalPath(), edge.getTargetCp());
                return;
            }
            NodeEdge nodeEdge = new NodeEdge(edge, source, target);
            source.addOut(nodeEdge);
            target.addIn(nodeEdge);
        });
        return new GraphChunk(primaryKey, nodes);
    }
}
