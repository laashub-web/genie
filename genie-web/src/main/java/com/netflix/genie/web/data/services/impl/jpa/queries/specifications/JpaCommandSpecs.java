/*
 * Copyright 2015 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.netflix.genie.web.data.services.impl.jpa.queries.specifications;

import com.google.common.collect.Lists;
import com.netflix.genie.common.external.dtos.v4.Criterion;
import com.netflix.genie.web.data.services.impl.jpa.entities.CommandEntity;
import com.netflix.genie.web.data.services.impl.jpa.entities.CommandEntity_;
import com.netflix.genie.web.data.services.impl.jpa.entities.TagEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

/**
 * Specifications for JPA queries.
 *
 * @author tgianos
 * @see <a href="http://tinyurl.com/n6nubvm">Docs</a>
 */
public final class JpaCommandSpecs {

    /**
     * Private constructor for utility class.
     */
    private JpaCommandSpecs() {
    }

    /**
     * Get a specification using the specified parameters.
     *
     * @param name     The name of the command
     * @param user     The name of the user who created the command
     * @param statuses The status of the command
     * @param tags     The set of tags to search the command for
     * @return A specification object used for querying
     */
    public static Specification<CommandEntity> find(
        @Nullable final String name,
        @Nullable final String user,
        @Nullable final Set<String> statuses,
        @Nullable final Set<TagEntity> tags
    ) {
        return (final Root<CommandEntity> root, final CriteriaQuery<?> cq, final CriteriaBuilder cb) -> {
            final List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotBlank(name)) {
                predicates.add(
                    JpaSpecificationUtils.getStringLikeOrEqualPredicate(cb, root.get(CommandEntity_.name), name)
                );
            }
            if (StringUtils.isNotBlank(user)) {
                predicates.add(
                    JpaSpecificationUtils.getStringLikeOrEqualPredicate(cb, root.get(CommandEntity_.user), user)
                );
            }
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(
                    cb.or(
                        statuses
                            .stream()
                            .map(status -> cb.equal(root.get(CommandEntity_.status), status))
                            .toArray(Predicate[]::new)
                    )
                );
            }
            if (tags != null && !tags.isEmpty()) {
                final Join<CommandEntity, TagEntity> tagEntityJoin = root.join(CommandEntity_.tags);
                predicates.add(tagEntityJoin.in(tags));
                cq.groupBy(root.get(CommandEntity_.id));
                cq.having(cb.equal(cb.count(root.get(CommandEntity_.id)), tags.size()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Get the specification for the query which will find the commands which match the given criterion.
     *
     * @param criterion The {@link Criterion} to match commands against
     * @return A {@link Specification} for this query
     */
    public static Specification<CommandEntity> findCommandsMatchingCriterion(final Criterion criterion) {
        return (final Root<CommandEntity> root, final CriteriaQuery<?> cq, final CriteriaBuilder cb) -> {
            final Predicate predicate = JpaSpecificationUtils.createCriterionPredicate(
                root,
                cq,
                cb,
                CommandEntity_.uniqueId,
                CommandEntity_.name,
                CommandEntity_.version,
                CommandEntity_.status,
                () -> root.join(CommandEntity_.tags, JoinType.INNER),
                CommandEntity_.id,
                criterion
            );

            return cb.and(predicate, cb.isNotEmpty(root.get(CommandEntity_.clusterCriteria)));
        };
    }
}
