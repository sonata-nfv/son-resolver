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

import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.controller.ImageController;
import eu.sonata.nfv.nec.resolver.controller.ImageControllerFactory;
import eu.sonata.nfv.nec.resolver.controller.PackageController;
import eu.sonata.nfv.nec.resolver.controller.PackageControllerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * The main application that starts the Spark services,
 * listens to routes, and handles incoming HTTP requests.
 *
 * @author Michael Bredel
 */
@Singleton
public class Application {

    private PackageController packageController;
    private ImageController imageController;
    private ConfigurationService configurationService;

    @Inject
    public Application(
            ImageControllerFactory imageControllerFactory,
            PackageControllerFactory packageControllerFactory,
            ConfigurationService configurationService
    ) {
        this.packageController = packageControllerFactory.create();
        this.imageController = imageControllerFactory.create();
        this.configurationService = configurationService;
    }

    /**
     * Start listening to routes.
     */
    public void start() {

        // Post a package descriptor.
        post(Routes.PACKAGE_DESCRIPTORS, packageController::postPackageDescriptor);

        // Get a list of available images.
        get(Routes.IMAGES, imageController::getImages);
    }
}
