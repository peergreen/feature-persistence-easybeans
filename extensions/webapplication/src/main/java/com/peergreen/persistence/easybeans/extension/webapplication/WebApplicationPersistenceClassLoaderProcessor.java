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

import java.net.URL;

import org.ow2.easybeans.loader.EasyBeansClassLoader;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.webcontainer.WebApplication;

/**
 * Adds an enhancement classloader once the classloader for webapplication has been defined
 * @author Florent Benoit
 */
@Processor
@Phase("POST_CLASSLOADER")
public class WebApplicationPersistenceClassLoaderProcessor {

    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        // Gets the classloader
        ClassLoader classLoader = webApplication.getClassLoader();

        // Adds the enhanced classloader
        ClassLoader newClassLoader = new EasyBeansClassLoader(new URL[0], classLoader);
        webApplication.setClassLoader(newClassLoader);
    }

}
