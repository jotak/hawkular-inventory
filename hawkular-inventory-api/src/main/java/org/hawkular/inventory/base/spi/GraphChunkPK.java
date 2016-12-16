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
package org.hawkular.inventory.base.spi;

/**
 * Primary key to use with {@link GraphChunkBackend#loadChunk(GraphChunkPK)}
 * @author Joel Takvorian
 */
public class GraphChunkPK {
    private final String tenantId;
    private final String feedId;

    public GraphChunkPK(String tenantId, String feedId) {
        this.tenantId = tenantId;
        this.feedId = feedId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getFeedId() {
        return feedId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphChunkPK that = (GraphChunkPK) o;

        if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null) return false;
        return feedId != null ? feedId.equals(that.feedId) : that.feedId == null;
    }

    @Override public int hashCode() {
        int result = tenantId != null ? tenantId.hashCode() : 0;
        result = 31 * result + (feedId != null ? feedId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "GraphChunkPK{" +
                "tenantId='" + tenantId + '\'' +
                ", feedId='" + feedId + '\'' +
                '}';
    }
}
