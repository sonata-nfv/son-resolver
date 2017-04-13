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
package eu.sonata.nfv.nec.resolver.controller;

import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.download.DownloadEvents;
import eu.sonata.nfv.nec.resolver.eventBus.Context;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

/**
 * @author Michael Bredel
 */
public class PackageController {

    /** The configuration service. */
    @SuppressWarnings("unused")
    private ConfigurationService configurationService;
    /** The event bus. */
    private EventBusService eventBusService;


    /**
     * The default constructor used by Guice. Since
     * the constructor is package-private PackageController
     * objects can (and should) only be instantiated
     * using the PackageController factory.
     *
     * @param configurationService The configuration service injected by Guice.
     * @param eventBusService The event bus injected by Guice.
     */
    @Inject
    PackageController(
            ConfigurationService configurationService,
            EventBusService eventBusService
    ) {
        this.configurationService = configurationService;
        this.eventBusService = eventBusService;
    }

    /**
     * Post a package descriptor via HTTP. The
     * package descriptor will be verified and
     * then handled by actors that downloads
     * the related artifacts.
     *
     * @param req The HTTP request information.
     * @param res The HTTP response information.
     * @return The HTTP status and, in case of success, the package descriptor.
     */
    public String postPackageDescriptor(Request req, Response res) {

        // Get the package descriptor.

        // validate the package descriptor.

        // Extract the resolvers from the package descriptor.

        // Extract the artifact from the package descriptor.

        // Emit an event to download the artifact.
        Context context = new Context()
                .add(DownloadEvents.StartDownload.USERNAME, "username")
                .add(DownloadEvents.StartDownload.PASSWORD, "password")
                .add(DownloadEvents.StartDownload.URI, "http://files.sonata-nfv.eu/test");
        eventBusService.publish(new DownloadEvents.StartDownload(context));

        return null;
    }

    /**
     *
     * @param req
     * @param res
     * @return
     */
    public String testPackageController(Request req, Response res) {
        Context context = new Context()
                .add(DownloadEvents.StartDownload.USERNAME, "username")
                .add(DownloadEvents.StartDownload.PASSWORD, "password")
                .add(DownloadEvents.StartDownload.URI, "http://files.sonata-nfv.eu/test");
        eventBusService.publish(new DownloadEvents.StartDownload(context));
        return "TEST Package Controller";
    }
}
