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

import java.util.Map;
import java.util.Optional;

import org.hawkular.inventory.base.spi.GraphChunkPK;

/**
 * Holds data of a graph chunk, that is a subpart of the whole graph for a given primary key (which is at the moment tenantid + feedid)
 * @author Joel Takvorian
 */
public class GraphChunk {
    private final GraphChunkPK primaryKey; // might be useless here...
    private final Map<String, Node> nodesByCp;

    GraphChunk(GraphChunkPK primaryKey, Map<String, Node> nodesByCp) {
        this.primaryKey = primaryKey;
        this.nodesByCp = nodesByCp;
        // Set this as node's owner
        nodesByCp.values().forEach(node -> node.setOwner(this));
    }

    public Optional<Node> findNode(String canonicalPath) {
        return Optional.ofNullable(nodesByCp.get(canonicalPath));
    }
}
