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
package com.peergreen.persistence.easybeans.extension.webapplication;

import java.net.URI;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.easybeans.persistence.EZBPersistenceUnitManager;
import org.ow2.easybeans.persistence.EZBPersistenceXmlAnalyzer;
import org.ow2.easybeans.persistence.PersistenceXmlAnalyzerException;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.handler.Processor;
import com.peergreen.webcontainer.WebApplication;

/**
 * Allows to analyze the persistence metadata
 * @author Florent Benoit
 */
@Component
@Instantiate
@Processor
@Phase("PRE_METADATA")
public class WebApplicationPersistenceProcessor {

    private static final String ORM_IN_WAR_PATH = "WEB-INF/classes/META-INF/orm.xml" ;
    private static final String PERSISTENCE_IN_WAR_PATH = "WEB-INF/classes/META-INF/persistence.xml" ;


    private static final String ORM_IN_LIBRARY_PATH = "META-INF/orm.xml" ;
    private static final String PERSISTENCE_IN_LIBRARY_PATH = "META-INF/persistence.xml" ;


   private final EZBPersistenceXmlAnalyzer persistenceXmlAnalyzer;


   public WebApplicationPersistenceProcessor(@Requires EZBPersistenceXmlAnalyzer persistenceXmlAnalyzer) {
       this.persistenceXmlAnalyzer = persistenceXmlAnalyzer;
   }


   /**
    * Adds the EZBPersistenceUnitManager on the artifact by analyzing the persistence.xml file
    * @param webApplication the given web app
    * @param processorContext the context used to add facets
    * @throws ProcessorException if unable to scan persistence data
    */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        Artifact artifact = processorContext.getArtifact();

        Archive archive = artifact.as(Archive.class);
        EZBPersistenceUnitManager existingPersistenceUnitManager = artifact.as(EZBPersistenceUnitManager.class);

        URI rootURI;
        try {
            rootURI = archive.getURI();
        } catch (ArchiveException e) {
            throw new ProcessorException("Unable to get the root URI", e);
        }

        ClassLoader classLoader = webApplication.getClassLoader();


        URI persistenceXMLPath = null;
        try {
            persistenceXMLPath = archive.getResource(PERSISTENCE_IN_WAR_PATH);
        } catch (ArchiveException e) {
            throw new ProcessorException(String.format("Unable to check if entry %s is available",  PERSISTENCE_IN_WAR_PATH), e);
        }


        URI ormXMLPath = null;
        try {
            ormXMLPath = archive.getResource(ORM_IN_WAR_PATH);
        } catch (ArchiveException e) {
            throw new ProcessorException(String.format("Unable to check if entry %s is available",  ORM_IN_WAR_PATH), e);
        }

        EZBPersistenceUnitManager newPersistenceUnitManager = addPersistenceManager(existingPersistenceUnitManager, rootURI, persistenceXMLPath, ormXMLPath, classLoader);

        // check for persistence in the libraries of the web application
        if (webApplication.getLibraries() != null) {
            for (Archive library : webApplication.getLibraries()) {
                URI uriPersistenceInLibrary;
                try {
                    uriPersistenceInLibrary = library.getResource(PERSISTENCE_IN_LIBRARY_PATH);
                } catch (ArchiveException e) {
                    throw new ProcessorException(String.format("Unable to check if entry %s is available",  PERSISTENCE_IN_LIBRARY_PATH), e);
                }

                URI uriORMInLibrary;
                try {
                    uriORMInLibrary = library.getResource(ORM_IN_LIBRARY_PATH);
                } catch (ArchiveException e) {
                    throw new ProcessorException(String.format("Unable to check if entry %s is available",  ORM_IN_LIBRARY_PATH), e);
                }

                // merge
                newPersistenceUnitManager = addPersistenceManager(existingPersistenceUnitManager, rootURI, uriPersistenceInLibrary, uriORMInLibrary, classLoader);
            }
        }

        if (newPersistenceUnitManager != null) {
            processorContext.addFacet(EZBPersistenceUnitManager.class, newPersistenceUnitManager);
        }


    }


    /**
     * Gets the PersistenceUnitManager by merging with existing unit manager if any.
     * @param existingPersistenceUnitManager the previous entity manager
     * @param rootURI URI of the archive
     * @param persistenceXMLPath the path of the persistence.xml file
     * @param ormXMLPath the path of the orm.xml file
     * @param classLoader the classloader used to load Entities
     * @return the built persistence unit manager
     * @throws ProcessorException
     */
    protected EZBPersistenceUnitManager addPersistenceManager(EZBPersistenceUnitManager existingPersistenceUnitManager, URI rootURI, URI persistenceXMLPath, URI ormXMLPath, ClassLoader classLoader) throws ProcessorException {

        EZBPersistenceUnitManager newPersistenceUnitManager = null;

        if (persistenceXMLPath != null) {
            try {
                newPersistenceUnitManager = persistenceXmlAnalyzer.analyzePersistenceXmlFile(rootURI, persistenceXMLPath, ormXMLPath, classLoader);
            } catch (PersistenceXmlAnalyzerException e) {
                throw new ProcessorException("Unable to build persistence unit manager", e);
            }
        }

        // merge before replacing
        if (existingPersistenceUnitManager != null) {
            if (newPersistenceUnitManager != null) {
                existingPersistenceUnitManager.merge(newPersistenceUnitManager);
            }
            return existingPersistenceUnitManager;
        }
        return newPersistenceUnitManager;
    }


}
