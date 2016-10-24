/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.closure.domain;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface GLClosureRepository extends JpaRepository<GLClosure, Long>, JpaSpecificationExecutor<GLClosure> {

    @Query("from GLClosure closure where closure.closingDate = (select max(closure1.closingDate) from GLClosure closure1 where closure1.office.id=:officeId)  and closure.office.id= :officeId")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    GLClosure getLatestGLClosureByBranch(@Param("officeId") Long officeId);
   
    @Modifying
    @Query("UPDATE GLClosure closure SET closure.deleted =1 WHERE  closure.id = :closureId")
    void deleteGLClosure(@Param("closureId") Long  closureId);
}
