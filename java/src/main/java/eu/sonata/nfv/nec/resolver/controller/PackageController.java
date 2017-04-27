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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.download.DownloadEvents;
import eu.sonata.nfv.nec.resolver.eventBus.Context;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusService;
import eu.sonata.nfv.nec.resolver.model.Artifact;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Michael Bredel
 */
public class PackageController {

    /** The field name of the artifact dependencies in the package descriptor. */
    private final String ARTIFACT_DEPENDENCIES = "artifact_dependencies";

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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode packageDescriptorNode;
        try {
            packageDescriptorNode = mapper.readTree(req.body());
        } catch (IOException e) {
            res.status(HttpStatus.SC_BAD_REQUEST);
            return "ERROR";
        }

        // Validate the package descriptor (optionally).

        // Extract the resolvers from the package descriptor (optionally).

        // Extract the artifact dependencies from the package descriptor.
        ArrayList<Artifact> artifactDependencies = new ArrayList<>();
        try {
            ArrayNode artifacts = (ArrayNode) packageDescriptorNode.findValue(ARTIFACT_DEPENDENCIES);
            for (JsonNode artifact : artifacts) {
                artifactDependencies.add(mapper.treeToValue(artifact, Artifact.class));
            }
        } catch (NullPointerException | ClassCastException | JsonProcessingException e) {
            e.printStackTrace();
            res.status(HttpStatus.SC_BAD_REQUEST);
            return "ERROR";
        }

        // Emit an event to download the artifact.
        for (Artifact artifact : artifactDependencies) {
            Context context = new Context()
                    .add(DownloadEvents.StartDownload.ARTIFACT, artifact)
                    .add(DownloadEvents.StartDownload.USERNAME, artifact.credentials.username)
                    .add(DownloadEvents.StartDownload.PASSWORD, artifact.credentials.password)
                    .add(DownloadEvents.StartDownload.URI, artifact.url);
            eventBusService.publish(new DownloadEvents.StartDownload(context));
        }

        // Just return.
        return "SUCCESS";
    }
}