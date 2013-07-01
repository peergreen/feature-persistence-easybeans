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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import javax.persistence.EntityManager;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ow2.easybeans.persistence.EZBPersistenceUnitManager;
import org.ow2.util.ee.metadata.common.api.struct.IJavaxPersistenceContext;
import org.ow2.util.ee.metadata.common.api.struct.IJavaxPersistenceContextType;
import org.ow2.util.ee.metadata.common.api.view.IJavaxPersistenceView;
import org.ow2.util.scan.api.ScanException;
import org.ow2.util.scan.api.metadata.IClassMetadata;
import org.ow2.util.scan.api.metadata.IFieldMetadata;
import org.ow2.util.scan.api.metadata.IMethodMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.metadata.adapter.Binding;
import com.peergreen.metadata.adapter.InjectionContext;

/**
 * Test injection of persistence context
 * @author Florent Benoit
 */
public class TestPersistenceContextInjectionProcessor {


    @Mock
    private IClassMetadata classMetadata;

    @Mock
    private IFieldMetadata fieldMetadata;

    @Mock
    private IMethodMetadata methodMetadata;

    @Mock
    private Artifact artifact;

    @Mock
    private InjectionContext injectionContextOnClass;

    @Mock
    private InjectionContext injectionContextOnMethod;

    @Mock
    private InjectionContext injectionContextOnField;


    @Mock
    private EZBPersistenceUnitManager persistenceUnitManager;

    @Mock
    private IJavaxPersistenceView javaxPersistenceView;

    @Mock
    private IJavaxPersistenceContext javaxPersistenceContextTransaction;

    @Mock
    private IJavaxPersistenceContext javaxPersistenceContextExtended;

    private PersistenceContextInjectionProcessor persistenceContextInjectionProcessor;

    @Mock
    private Binding<EntityManager> binding;


    private static final String UNIT_NAME = "unit-name-00";

     @BeforeMethod
     public void init() throws ScanException {
         MockitoAnnotations.initMocks(this);
         this.persistenceContextInjectionProcessor = new PersistenceContextInjectionProcessor();

         doReturn(artifact).when(injectionContextOnClass).getArtifact();
         doReturn(artifact).when(injectionContextOnMethod).getArtifact();
         doReturn(artifact).when(injectionContextOnField).getArtifact();

         doReturn(persistenceUnitManager).when(artifact).as(EZBPersistenceUnitManager.class);


         doReturn(IJavaxPersistenceContextType.TRANSACTION).when(javaxPersistenceContextTransaction).getType();
         doReturn(UNIT_NAME).when(javaxPersistenceContextTransaction).getUnitName();

         doReturn(IJavaxPersistenceContextType.EXTENDED).when(javaxPersistenceContextExtended).getType();
         doReturn(UNIT_NAME).when(javaxPersistenceContextExtended).getUnitName();

         doReturn(binding).when(injectionContextOnClass).createBinding(anyString(), anyObject());
         doReturn(binding).when(injectionContextOnMethod).createBinding(anyString(), anyObject());
         doReturn(binding).when(injectionContextOnField).createBinding(anyString(), anyObject());

         doReturn(classMetadata).when(injectionContextOnClass).getMetadata();
         doReturn(methodMetadata).when(injectionContextOnMethod).getMetadata();
         doReturn(fieldMetadata).when(injectionContextOnField).getMetadata();

         doReturn(javaxPersistenceView).when(classMetadata).as(IJavaxPersistenceView.class);
         doReturn(javaxPersistenceView).when(methodMetadata).as(IJavaxPersistenceView.class);
         doReturn(javaxPersistenceView).when(fieldMetadata).as(IJavaxPersistenceView.class);



     }

     @Test
     public void testNoPersistenceUnitManager() {
         doReturn(null).when(artifact).as(EZBPersistenceUnitManager.class);
         Binding<?> binding = persistenceContextInjectionProcessor.handle(injectionContextOnClass);
         assertNull(binding);
         binding = persistenceContextInjectionProcessor.handle(injectionContextOnMethod);
         assertNull(binding);
         binding = persistenceContextInjectionProcessor.handle(injectionContextOnField);
         assertNull(binding);


     }

     @Test
     public void testClassTransaction() {
         doReturn(javaxPersistenceContextTransaction).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnClass);
         assertNotNull(binding);
         verify(persistenceUnitManager).getTransactionEntityManager(UNIT_NAME);
     }

     @Test
     public void testClassExtended() {
         doReturn(javaxPersistenceContextExtended).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnClass);
         assertNotNull(binding);
         verify(persistenceUnitManager).getExtendedEntityManager(UNIT_NAME);
     }

     @Test
     public void testMethodTransaction() {
         doReturn(javaxPersistenceContextTransaction).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnMethod);
         assertNotNull(binding);
         verify(persistenceUnitManager).getTransactionEntityManager(UNIT_NAME);
     }

     @Test
     public void testMethodExtended() {
         doReturn(javaxPersistenceContextExtended).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnMethod);
         assertNotNull(binding);
         verify(persistenceUnitManager).getExtendedEntityManager(UNIT_NAME);
     }


     @Test
     public void testFieldTransaction() {
         doReturn(javaxPersistenceContextTransaction).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnField);
         assertNotNull(binding);
         verify(persistenceUnitManager).getTransactionEntityManager(UNIT_NAME);
     }

     @Test
     public void testFieldExtended() {
         doReturn(javaxPersistenceContextExtended).when(javaxPersistenceView).getJavaxPersistenceContext();
         Binding<EntityManager> binding = persistenceContextInjectionProcessor.handle(injectionContextOnField);
         assertNotNull(binding);
         verify(persistenceUnitManager).getExtendedEntityManager(UNIT_NAME);
     }

}
