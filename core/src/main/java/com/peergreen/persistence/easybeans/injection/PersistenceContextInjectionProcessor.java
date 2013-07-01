/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.persistence.easybeans.injection;

import javax.persistence.EntityManager;

import org.ow2.easybeans.persistence.EZBPersistenceUnitManager;
import org.ow2.util.ee.metadata.common.api.struct.IJavaxPersistenceContext;
import org.ow2.util.ee.metadata.common.api.struct.IJavaxPersistenceContextType;
import org.ow2.util.ee.metadata.common.api.view.IJavaxPersistenceView;
import org.ow2.util.scan.api.metadata.IMetadata;

import com.peergreen.deployment.Artifact;
import com.peergreen.metadata.adapter.Binding;
import com.peergreen.metadata.adapter.InjectionContext;
import com.peergreen.metadata.adapter.InjectionProcessor;

@InjectionProcessor("javax.persistence.PersistenceContext")
public class PersistenceContextInjectionProcessor {


    public Binding<EntityManager> handle(InjectionContext injectionContext) {

        // Get artifact
        Artifact artifact = injectionContext.getArtifact();

        // Get persistence unit manager
        EZBPersistenceUnitManager persistenceUnitManager = artifact.as(EZBPersistenceUnitManager.class);

        // Do not continue if there is no persistence unit manager
        if (persistenceUnitManager == null) {
            return null;
        }

        // Get metadata
        IMetadata metadata = injectionContext.getMetadata();
        // get Persistence Context data
        IJavaxPersistenceView persistenceView = metadata.as(IJavaxPersistenceView.class);

        IJavaxPersistenceContext persistenceContext = persistenceView.getJavaxPersistenceContext();
        EntityManager entityManager;
        if (persistenceContext.getType() == IJavaxPersistenceContextType.TRANSACTION) {
            entityManager = persistenceUnitManager.getTransactionEntityManager(persistenceContext.getUnitName());
        } else {
            entityManager = persistenceUnitManager.getExtendedEntityManager(persistenceContext.getUnitName());
        }
        Binding<EntityManager> binding = injectionContext.createBinding(persistenceContext.getName(), entityManager);

        return binding;

    }

}
