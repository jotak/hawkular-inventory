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
package org.hawkular.inventory.base;

import static org.hawkular.inventory.api.Action.created;
import static org.hawkular.inventory.api.Relationships.WellKnown.defines;
import static org.hawkular.inventory.api.Relationships.WellKnown.incorporates;
import static org.hawkular.inventory.api.filters.With.id;

import java.util.ArrayList;
import java.util.List;

import org.hawkular.inventory.api.EntityAlreadyExistsException;
import org.hawkular.inventory.api.EntityNotFoundException;
import org.hawkular.inventory.api.Metrics;
import org.hawkular.inventory.api.filters.Filter;
import org.hawkular.inventory.api.model.Metric;
import org.hawkular.inventory.api.model.MetricType;
import org.hawkular.inventory.api.model.Relationship;
import org.hawkular.inventory.api.model.Resource;
import org.hawkular.inventory.base.spi.ElementNotFoundException;
import org.hawkular.inventory.base.spi.InventoryBackend;
import org.hawkular.inventory.paths.CanonicalPath;
import org.hawkular.inventory.paths.Path;

/**
 * @author Lukas Krejci
 * @since 0.1.0
 */
public final class BaseMetrics {

    private BaseMetrics() {

    }

    public static class ReadWrite<BE> extends Mutator<BE, Metric, Metric.Blueprint,
            Metric.Update, String>
            implements Metrics.ReadWrite {

        public ReadWrite(TraversalContext<BE, Metric> context) {
            super(context);
        }

        @Override
        protected String getProposedId(Metric.Blueprint blueprint) {
            return blueprint.getId();
        }

        @Override
        protected EntityAndPendingNotifications<Metric> wireUpNewEntity(BE entity, Metric.Blueprint blueprint,
                                                                        CanonicalPath parentPath, BE parent,
                                                                        InventoryBackend.Transaction transaction) {

            BE metricTypeObject;

            try {
                CanonicalPath tenant = CanonicalPath.of().tenant(parentPath.ids().getTenantId()).get();
                CanonicalPath metricTypePath = Util.canonicalize(blueprint.getMetricTypePath(), tenant, parentPath,
                        MetricType.class);
                metricTypeObject = context.backend.find(metricTypePath);

            } catch (ElementNotFoundException e) {
                throw new IllegalArgumentException("A metric type with path '" + blueprint.getMetricTypePath() +
                        "' not found relative to '" + parentPath + "'.");
            }

            //specifically do NOT check relationship rules, here because defines cannot be created "manually".
            //here we "know what we are doing" and need to create the defines relationship to capture the
            //contract of the metric.
            BE r = context.backend.relate(metricTypeObject, entity, defines.name(), null);

            CanonicalPath entityPath = context.backend.extractCanonicalPath(entity);

            MetricType metricType = context.backend.convert(metricTypeObject, MetricType.class);

            Metric ret = new Metric(blueprint.getName(), parentPath.extend(Metric.class,
                    context.backend.extractId(entity)).get(), metricType, blueprint.getCollectionInterval(),
                    blueprint.getProperties());

            Relationship rel = new Relationship(context.backend.extractId(r), defines.name(), parentPath, entityPath);

            List<Notification<?, ?>> notifs = new ArrayList<>();
            notifs.add(new Notification<>(rel, rel, created()));

            if (Resource.class.equals(context.backend.extractType(parent))) {
                r = context.backend.relate(parent, entity, incorporates.name(), null);
                rel = new Relationship(context.backend.extractId(r), incorporates.name(), parentPath, entityPath);
                notifs.add(new Notification<>(rel, rel, created()));
            }

            return new EntityAndPendingNotifications<>(ret, notifs);
        }

        @Override
        public Metrics.Multiple getAll(Filter[][] filters) {
            return new Multiple<>(context.proceed().whereAll(filters).get());
        }

        @Override
        public Metrics.Single get(String id) throws EntityNotFoundException {
            return new Single<>(context.proceed().where(id(id)).get());
        }

        @Override
        public Metrics.Single create(Metric.Blueprint blueprint) throws EntityAlreadyExistsException {
            return new Single<>(context.toCreatedEntity(doCreate(blueprint)));
        }
    }

    public static class ReadContained<BE> extends Traversal<BE, Metric> implements Metrics.ReadContained {

        public ReadContained(TraversalContext<BE, Metric> context) {
            super(context);
        }

        @Override
        public Metrics.Multiple getAll(Filter[][] filters) {
            return new Multiple<>(context.proceed().whereAll(filters).get());
        }

        @Override
        public Metrics.Single get(String id) throws EntityNotFoundException {
            return new Single<>(context.proceed().where(id(id)).get());
        }
    }

    public static class Read<BE> extends Traversal<BE, Metric> implements Metrics.Read {

        public Read(TraversalContext<BE, Metric> context) {
            super(context);
        }

        @Override
        public Metrics.Multiple getAll(Filter[][] filters) {
            return new Multiple<>(context.proceed().whereAll(filters).get());
        }

        @Override
        public Metrics.Single get(Path id) throws EntityNotFoundException {
            return new Single<>(context.proceedTo(id));
        }
    }

    public static class ReadAssociate<BE> extends Associator<BE, Metric> implements Metrics.ReadAssociate {

        public ReadAssociate(TraversalContext<BE, Metric> context) {
            super(context, incorporates, Resource.class);
        }

        @Override
        public Metrics.Multiple getAll(Filter[][] filters) {
            return new Multiple<>(context.proceed().whereAll(filters).get());
        }

        @Override
        public Metrics.Single get(Path id) throws EntityNotFoundException {
            return new Single<>(context.proceedTo(id));
        }
    }

    public static class Single<BE> extends SingleEntityFetcher<BE, Metric, Metric.Update> implements Metrics.Single {

        public Single(TraversalContext<BE, Metric> context) {
            super(context);
        }
    }

    public static class Multiple<BE> extends MultipleEntityFetcher<BE, Metric, Metric.Update>
            implements Metrics.Multiple {

        public Multiple(TraversalContext<BE, Metric> context) {
            super(context);
        }
    }
}
