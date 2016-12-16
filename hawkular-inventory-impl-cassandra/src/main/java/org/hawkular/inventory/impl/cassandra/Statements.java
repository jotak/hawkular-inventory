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
package org.hawkular.inventory.impl.cassandra;

import java.util.Collection;

import org.hawkular.rx.cassandra.driver.RxSession;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;

import rx.Observable;

/**
 * @author Lukas Krejci
 * @since 2.0.0
 */
final class Statements {
    static final String RELATIONSHIP = "relationship";
    static final String RELATIONSHIP_OUT = "relationship_out";
    static final String RELATIONSHIP_IN = "relationship_in";
    static final String ENTITY = "entity";
    static final String ENTITY_ID_IDX = "entity_id_idx";
    static final String ENTITY_TYPE_IDX = "entity_type_idx";
    static final String ENTITY_NAME_IDX = "entity_name_idx";
    static final String CP = "cp";
    static final String ID = "id";
    static final String TYPE = "type";
    static final String SOURCE_CP = "source_cp";
    static final String TARGET_CP = "target_cp";
    static final String SOURCE_ID = "source_id";
    static final String TARGET_ID = "target_id";
    static final String SOURCE_TYPE = "source_type";
    static final String TARGET_TYPE = "target_type";
    static final String NAME = "name";
    static final String PROPERTIES = "properties";

    private final PreparedStatement findEntityByCanonicalPath;
    private final PreparedStatement findRelationshipByCanonicalPath;
    private final PreparedStatement findEntityByCanonicalPaths;
    private final PreparedStatement findRelationshipByCanonicalPaths;
    private final PreparedStatement findEntityCpsByIds;
    private final PreparedStatement findEntityCpsById;
    private final PreparedStatement findEntityCpsByTypes;
    private final PreparedStatement findEntityCpsByType;
    private final PreparedStatement countOutRelationshipBySourceAndName;
    private final PreparedStatement countInRelationshipByTargetAndName;
    private final PreparedStatement findOutRelationshipBySourceAndName;
    private final PreparedStatement findInRelationshipByTargetAndName;
    private final RxSession session;

    Statements(RxSession session) {
        this.session = session;
        findEntityByCanonicalPath = prepare(session, "SELECT * FROM " + ENTITY + " WHERE " + CP + " = ?;");
        findEntityByCanonicalPaths = prepare(session, "SELECT * FROM " + ENTITY + " WHERE " + CP + " IN ?;");
        findRelationshipByCanonicalPath =
                prepare(session, "SELECT * FROM " + RELATIONSHIP + " WHERE " + CP + " = ?;");
        findRelationshipByCanonicalPaths =
                prepare(session, "SELECT * FROM " + RELATIONSHIP + " WHERE " + CP + " IN ?;");
        findEntityCpsByIds = prepare(session, "SELECT " + CP + " FROM " + ENTITY_ID_IDX + " WHERE " + ID + " IN ?;");
        findEntityCpsById = prepare(session, "SELECT " + CP + " FROM " + ENTITY_ID_IDX + " WHERE " + ID + " = ?;");
        findEntityCpsByTypes = prepare(session, "SELECT " + CP + " FROM " + ENTITY_TYPE_IDX + " WHERE " + TYPE
                + " IN ?;");
        findEntityCpsByType = prepare(session, "SELECT " + CP + " FROM " + ENTITY_TYPE_IDX + " WHERE " + TYPE
                + " = ?;");
        countOutRelationshipBySourceAndName = prepare(
                session, "SELECT COUNT(*) FROM " + RELATIONSHIP_OUT + " WHERE " + SOURCE_CP + " = ? AND " + NAME + " = ?;");
        countInRelationshipByTargetAndName = prepare(
                session, "SELECT COUNT(*) FROM " + RELATIONSHIP_IN + " WHERE " + TARGET_CP + " = ? AND " + NAME + " = ?;");
        findOutRelationshipBySourceAndName = prepare(
                session, "SELECT * FROM " + RELATIONSHIP_OUT + " WHERE " + SOURCE_CP + " = ? AND " + NAME + " = ?;");
        findInRelationshipByTargetAndName = prepare(
                session, "SELECT * FROM " + RELATIONSHIP_IN + " WHERE " + TARGET_CP + " = ? AND " + NAME + " = ?;");
    }

    PreparedStatement findEntityByCanonicalPath() {
        return findEntityByCanonicalPath;
    }

    PreparedStatement findRelationshipByCanonicalPath() {
        return findRelationshipByCanonicalPath;
    }

    Observable<Row> findEntityByCanonicalPath(String cp) {
        return session.executeAndFetch(findEntityByCanonicalPath.bind(cp));
    }

    Observable<Row> findRelationshipByCanonicalPath(String cp) {
        return session.executeAndFetch(findRelationshipByCanonicalPath.bind(cp));
    }

    PreparedStatement findEntityByCanonicalPaths() {
        return findEntityByCanonicalPaths;
    }

    PreparedStatement findRelationshipByCanonicalPaths() {
        return findRelationshipByCanonicalPaths;
    }

    PreparedStatement findEntityCpsByIds() {
        return findEntityCpsByIds;
    }

    PreparedStatement findEntityCpsById() {
        return findEntityCpsById;
    }

    PreparedStatement findEntityCpsByTypes() {
        return findEntityCpsByTypes;
    }

    PreparedStatement findEntityCpsByType() {
        return findEntityCpsByType;
    }

    Observable<Long> countOutRelationshipBySourceAndName(String source, String name) {
        return session.executeAndFetch(countOutRelationshipBySourceAndName.bind(source, name))
                .first()
                .map(row -> row.getLong(0));
    }

    Observable<Long> countInRelationshipByTargetAndName(String target, String name) {
        return session.executeAndFetch(countInRelationshipByTargetAndName.bind(target, name))
                .first()
                .map(row -> row.getLong(0));
    }

    Observable<Row> findOutRelationshipBySourceAndName(String source, String name) {
        return session.executeAndFetch(findOutRelationshipBySourceAndName.bind(source, name));
    }

    Observable<Row> findInRelationshipByTargetAndName(String source, String name) {
        return session.executeAndFetch(findInRelationshipByTargetAndName.bind(source, name));
    }

    Observable<Row> findOutRelationshipBySourceAndNames(String source, Collection<String> names) {
        return Observable.from(names)
                .flatMap(name -> findOutRelationshipBySourceAndName(source, name));
    }

    Observable<Row> findInRelationshipByTargetAndNames(String target, Collection<String> names) {
        return Observable.from(names)
                .flatMap(name -> findInRelationshipByTargetAndName(target, name));
    }

    private PreparedStatement prepare(RxSession session, String statement) {
        return session.prepare(statement).toBlocking().first();
    }
}
