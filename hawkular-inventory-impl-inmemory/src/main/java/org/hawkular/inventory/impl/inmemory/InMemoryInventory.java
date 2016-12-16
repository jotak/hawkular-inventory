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

import org.hawkular.inventory.api.Configuration;
import org.hawkular.inventory.base.BaseInventory;
import org.hawkular.inventory.base.TransactionConstructor;
import org.hawkular.inventory.base.spi.GraphChunkBackend;
import org.hawkular.inventory.base.spi.InventoryBackend;
import org.hawkular.inventory.impl.inmemory.graph.GraphElement;

/**
 * Inventory implementation for in-memory operations on a graph.
 * This implementation requires a {@link GraphChunkBackend} (basically, a blob store).
 * @author Joel Takvorian
 */
public final class InMemoryInventory extends BaseInventory<GraphElement> {

    private final GraphChunkBackend graphChunkBackend;

    public InMemoryInventory(GraphChunkBackend graphChunkBackend) {
        this.graphChunkBackend = graphChunkBackend;
    }

    private InMemoryInventory(InMemoryInventory orig, TransactionConstructor<GraphElement> txCtor) {
        super(orig, null, txCtor);
        this.graphChunkBackend = orig.graphChunkBackend;
    }

    @Override protected BaseInventory<GraphElement> cloneWith(TransactionConstructor<GraphElement> transactionCtor) {
        return new InMemoryInventory(this, transactionCtor);
    }

    @Override protected InventoryBackend<GraphElement> doInitialize(Configuration configuration) {
        return InMemoryBackend.create(graphChunkBackend);
    }
}
