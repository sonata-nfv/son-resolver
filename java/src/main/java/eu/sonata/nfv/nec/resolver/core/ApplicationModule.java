/*
 Copyright (c) 2015 SONATA-NFV, NEC
 ALL RIGHTS RESERVED.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Neither the name of the SONATA-NFV, NEC
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written
 permission.

 This work has been performed in the framework of the SONATA project,
 funded by the European Commission under Grant number 671517 through
 the Horizon 2020 and 5G-PPP programmes. The authors would like to
 acknowledge the contributions of their colleagues of the SONATA
 partner consortium (www.sonata-nfv.eu).
 */
package eu.sonata.nfv.nec.resolver.core;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import eu.sonata.nfv.nec.resolver.controller.ImageControllerFactory;
import eu.sonata.nfv.nec.resolver.controller.PackageControllerFactory;

/**
 * The basic module that binds the application-related
 * factories and interfaces to their implementations.
 *
 * @author Michael Bredel
 */
public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {

        // Create a factory to inject ImageController objects whenever needed.
        install(new FactoryModuleBuilder().build(ImageControllerFactory.class));

        // Create a factory to inject PackageController objects whenever needed.
        install(new FactoryModuleBuilder().build(PackageControllerFactory.class));
    }
}
