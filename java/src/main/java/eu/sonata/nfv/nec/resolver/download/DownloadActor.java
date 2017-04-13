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
package eu.sonata.nfv.nec.resolver.download;

import akka.actor.UntypedActor;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.eventBus.Context;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusService;
import eu.sonata.nfv.nec.resolver.store.StoreService;
import jdk.internal.util.xml.impl.Input;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;

/**
 * The download actor starts downloading an
 * artifact when it receives a download event.
 * It stores the artifact in the artifact store.
 * Once finished, it emits an download finished
 * event.
 *
 * @author Michael Bredel
 */
public class DownloadActor extends UntypedActor {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadActor.class);
    /** The configuration service. */
    @Inject
    private ConfigurationService configurationService;
    /** The store service. */
    @Inject
    private StoreService storeService;
    /** The event bus service. */
    @Inject
    private EventBusService eventBusService;

    @Override
    public void preStart() {
        // Register to the event bus.
        this.eventBusService.subscribe(this.getSelf(), DownloadEvents.StartDownload.class);
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof DownloadEvents.StartDownload) {
            DownloadEvents.StartDownload event = (DownloadEvents.StartDownload) o;
            LOGGER.trace("Received an event of type DownloadEvents.StartDownload: " + event);

            String url = (String) event.getContext().get(DownloadEvents.StartDownload.URI);

            // Initialize some objects we need.
            String fileName;
            Context context;

            // Check if we have the artifact already on stock.

            // Store persist the download information such that we can resume in case of an App failure.

            // Actually start the download.
            HttpResponse<InputStream> response = Unirest
                    .get(url)
                    .asBinary();
            if (response != null && response.getStatus() == HttpStatus.SC_OK) {
                fileName = storeService.create("test", response.getBody());
            } else {
                LOGGER.error("Could not download the artifact: " + response.getStatusText());
                context = new Context()
                        .add("Response", response);
                this.eventBusService.publish(new DownloadEvents.DownloadError(context));
                return;
            }

            // Update (delete) the persisted information on the download.

            // Finally, tell everyone that the download is finished.
            context = new Context()
                    .add("fileName", fileName);
            this.eventBusService.publish(new DownloadEvents.DownloadFinished(context));
        } else {
            unhandled(o);
        }
    }
}
