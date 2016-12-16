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
package org.hawkular.inventory.impl.inmemory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hawkular.inventory.api.Query;
import org.hawkular.inventory.api.Relationships;
import org.hawkular.inventory.api.model.AbstractElement;
import org.hawkular.inventory.api.model.Blueprint;
import org.hawkular.inventory.api.model.Entity;
import org.hawkular.inventory.api.model.Hashes;
import org.hawkular.inventory.api.model.StructuredData;
import org.hawkular.inventory.api.paging.Page;
import org.hawkular.inventory.api.paging.Pager;
import org.hawkular.inventory.base.spi.CommitFailureException;
import org.hawkular.inventory.base.spi.ElementNotFoundException;
import org.hawkular.inventory.base.spi.GraphChunkBackend;
import org.hawkular.inventory.base.spi.GraphChunkPK;
import org.hawkular.inventory.base.spi.InventoryBackend;
import org.hawkular.inventory.impl.inmemory.graph.GraphChunk;
import org.hawkular.inventory.impl.inmemory.graph.GraphChunkBuilder;
import org.hawkular.inventory.impl.inmemory.graph.GraphElement;
import org.hawkular.inventory.impl.inmemory.graph.Node;
import org.hawkular.inventory.impl.inmemory.graph.NodeEdge;
import org.hawkular.inventory.impl.inmemory.model.Edge;
import org.hawkular.inventory.impl.inmemory.model.Vertex;
import org.hawkular.inventory.paths.CanonicalPath;
import org.hawkular.inventory.paths.RelativePath;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Inventory backend implementation for in-memory operations on a graph.
 * This implementation requires a {@link GraphChunkBackend} (basically, a blob store).
 * @author Joel Takvorian
 */
public final class InMemoryBackend implements InventoryBackend<GraphElement> {

    private static final Logger DBG = Logger.getLogger(InMemoryBackend.class);
    private final GraphChunkBackend graphChunkBackend;
    private final LoadingCache<GraphChunkPK, GraphChunk> chunksCache;

    // Transaction-specific data
    private final Map<GraphChunkPK, GraphChunk> txContent;

    private InMemoryBackend(GraphChunkBackend graphChunkBackend,
                            LoadingCache<GraphChunkPK, GraphChunk> chunksCache,
                            Map<GraphChunkPK, GraphChunk> txContent) {
        this.graphChunkBackend = graphChunkBackend;
        this.chunksCache = chunksCache;
        this.txContent = txContent;
    }

    static InMemoryBackend create(GraphChunkBackend graphChunkBackend) {
        return new InMemoryBackend(
                graphChunkBackend,
                CacheBuilder.<GraphChunkPK, GraphChunk>newBuilder()
                    .expireAfterAccess(15, TimeUnit.MINUTES)
                    .build(new CacheLoader<GraphChunkPK, GraphChunk>() {
                        @Override public GraphChunk load(GraphChunkPK key) throws Exception {
                            return fromBlob(key, graphChunkBackend.loadChunk(key));
                        }
                    }),
                null);
    }

    private static GraphChunk fromBlob(GraphChunkPK pk, String blob) {
        // TODO: use an efficient format
        JSONObject json = new JSONObject(blob);
        JSONArray verticesJson = json.getJSONArray("vertices");
        JSONArray edgesJson = json.getJSONArray("edges");
        List<Vertex> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (Object o : verticesJson) {
            JSONObject jsonObject = (JSONObject)o;
            vertices.add(new Vertex(
                    jsonObject.getString("cp"),
                    jsonObject.getString("name"),
                    (Map<String, String>) (Object) jsonObject.getJSONObject("props").toMap(),
                    jsonObject.getString("id"),
                    jsonObject.getInt("type")));
        }
        for (Object o : edgesJson) {
            JSONObject jsonObject = (JSONObject)o;
            edges.add(new Edge(
                    jsonObject.getString("cp"),
                    jsonObject.getString("name"),
                    (Map<String, String>) (Object) jsonObject.getJSONObject("props").toMap(),
                    jsonObject.getString("source"),
                    jsonObject.getString("target")));
        }
        return new GraphChunkBuilder(pk)
                .addVertices(vertices)
                .addEdges(edges)
                .build();
    }

    private boolean isInTransaction() {
        return txContent != null;
    }

    @Override public boolean isPreferringBigTransactions() {
        // TODO
        return false;
    }

    @Override public boolean isUniqueIndexSupported() {
        // TODO
        return false;
    }

    @Override public InventoryBackend<GraphElement> startTransaction() {
        return new InMemoryBackend(graphChunkBackend, chunksCache, new HashMap<>());
    }

    @Override public GraphElement find(CanonicalPath element) throws ElementNotFoundException {
        // It doesn't seem to be really called
        // TODO: Remove from interface?
        throw new NotImplementedException();
    }

    @Override public Page<GraphElement> query(Query query, Pager pager) {
        String tenantId = "";
        String feedId = "";
        // TODO: extract tenant and feed from query. If absent, keep empty
        GraphChunk graph = chunksCache.getUnchecked(new GraphChunkPK(tenantId, feedId));
        // TODO: perform query on graph
        return null;
    }

    @Override public GraphElement querySingle(Query query) {
        // TODO
        // Same as above
        return null;
    }

    @Override public Page<GraphElement> traverse(GraphElement startingPoint, Query query, Pager pager) {
        // TODO
        GraphChunk graph = startingPoint.getOwner();
        return null;
    }

    @Override public GraphElement traverseToSingle(GraphElement startingPoint, Query query) {
        // TODO
        GraphChunk graph = startingPoint.getOwner();
        return null;
    }

    @Override
    public <T> Page<T> query(Query query, Pager pager, Function<GraphElement, T> conversion, Function<T, Boolean> filter) {
        // TODO
        return null;
    }

    @Override
    public Iterator<GraphElement> getTransitiveClosureOver(GraphElement startingPoint, Relationships.Direction direction,
                                                      String... relationshipNames) {
        // TODO
        GraphChunk graph = startingPoint.getOwner();
        return null;
    }

    @Override
    public boolean hasRelationship(GraphElement entity, Relationships.Direction direction, String relationshipName) {
        return streamEdges((Node) entity, direction)
                .anyMatch(nodeEdge -> nodeEdge.getEdge().getName().equals(relationshipName));
    }

    private static Stream<NodeEdge> streamEdges(Node node, Relationships.Direction direction) {
        switch (direction) {
            case outgoing:
                return node.getOut().stream();
            case incoming:
                return node.getIn().stream();
            case both:
                return Stream.concat(node.getOut().stream(), node.getIn().stream());
        }
        throw new IllegalArgumentException("Unknown direction " + direction);

    }

    @Override public boolean hasRelationship(GraphElement source, GraphElement target, String relationshipName) {
        Node node = (Node) source;
        String targetCp = ((Node)target).getVertex().getCanonicalPath();
        return node.getOut().stream().anyMatch(nodeEdge -> nodeEdge.getEdge().getName().equals(relationshipName)
                && nodeEdge.getTarget().getVertex().getCanonicalPath().equals(targetCp));
    }

    @Override public Set<GraphElement> getRelationships(GraphElement entity, Relationships.Direction direction, String... names) {
        Set<String> namesSet = new HashSet<>(Arrays.asList(names));
        return streamEdges((Node) entity, direction)
                .filter(edge -> namesSet.contains(edge.getEdge().getName()))
                .collect(Collectors.toSet());
    }

    @Override public GraphElement getRelationship(GraphElement source, GraphElement target, String relationshipName)
            throws ElementNotFoundException {
        Node node = (Node) source;
        String targetCp = ((Node)target).getVertex().getCanonicalPath();
        return node.getOut().stream().filter(nodeEdge -> nodeEdge.getEdge().getName().equals(relationshipName)
                && nodeEdge.getTarget().getVertex().getCanonicalPath().equals(targetCp))
                .findFirst()
                .orElseThrow(ElementNotFoundException::new);
    }

    @Override public GraphElement getRelationshipSource(GraphElement relationship) {
        return ((NodeEdge)relationship).getSource();
    }

    @Override public GraphElement getRelationshipTarget(GraphElement relationship) {
        return ((NodeEdge)relationship).getTarget();
    }

    @Override public String extractRelationshipName(GraphElement relationship) {
        return ((NodeEdge)relationship).getEdge().getName();
    }

    @Override public String extractId(GraphElement entityRepresentation) {
        return ((Node)entityRepresentation).getVertex().getId();
    }

    @Override public Class<?> extractType(GraphElement entityRepresentation) {
        int type = ((Node)entityRepresentation).getVertex().getType();
        // TODO
        return null;
    }

    @Override public CanonicalPath extractCanonicalPath(GraphElement entityRepresentation) {
        String cp = ((Node)entityRepresentation).getVertex().getCanonicalPath();
        // TODO
        return null;
    }

    @Override public String extractIdentityHash(GraphElement entityRepresentation) {
        // TODO
        return null;
    }

    @Override public String extractContentHash(GraphElement entityRepresentation) {
        // TODO
        return null;
    }

    @Override public String extractSyncHash(GraphElement entityRepresentation) {
        // TODO
        return null;
    }

    @Override public <T> T convert(GraphElement entityRepresentation, Class<T> entityType) {
        // TODO
        return null;
    }

    @Override public GraphElement descendToData(GraphElement dataEntityRepresentation, RelativePath dataPath) {
        // TODO
        return null;
    }

    @Override
    public GraphElement relate(GraphElement sourceEntity, GraphElement targetEntity, String name, Map<String, Object> properties) {
        // TODO
        return null;
    }

    @Override public GraphElement persist(CanonicalPath path, Blueprint blueprint) {
        // TODO
        return null;
    }

    @Override public GraphElement persist(StructuredData structuredData) {
        // TODO
        return null;
    }

    @Override public void update(GraphElement entity, AbstractElement.Update update) {
        // TODO

    }

    @Override public void updateHashes(GraphElement entity, Hashes hashes) {
        // TODO

    }

    @Override public void delete(GraphElement entity) {
        // TODO

    }

    @Override public void deleteStructuredData(GraphElement dataRepresentation) {
        // TODO

    }

    @Override public void commit() throws CommitFailureException {
        // TODO

    }

    @Override public void rollback() {
        // TODO

    }

    @Override public boolean isBackendInternal(GraphElement element) {
        // TODO
        return false;
    }

    @Override public InputStream getGraphSON(String tenantId) {
        // TODO
        return null;
    }

    @Override public <T extends Entity<?, ?>> Iterator<T> getTransitiveClosureOver(CanonicalPath startingPoint,
                                                                                   Relationships.Direction direction,
                                                                                   Class<T> clazz,
                                                                                   String... relationshipNames) {
        // TODO
        return null;
    }

    @Override public void close() throws Exception {
        // TODO

    }
}
