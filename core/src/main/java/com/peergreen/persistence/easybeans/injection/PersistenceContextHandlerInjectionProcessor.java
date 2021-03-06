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

import javax.persistence.PersistenceContext;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import com.peergreen.metadata.adapter.Binding;
import com.peergreen.metadata.adapter.HandlerInjectionProcessor;
import com.peergreen.metadata.adapter.InjectionContext;

@Component
@Instantiate
@Provides
public class PersistenceContextHandlerInjectionProcessor implements HandlerInjectionProcessor {

    private final PersistenceContextInjectionProcessor wrapped;

    public PersistenceContextHandlerInjectionProcessor() {
        this.wrapped = new PersistenceContextInjectionProcessor();
    }

    @Override
    public String getAnnotation() {
        return PersistenceContext.class.getName();
    }

    @Override
    public Binding<?> handle(InjectionContext injectionContext) {
        return wrapped.handle(injectionContext);
    }

}
