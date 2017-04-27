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
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;
import eu.sonata.nfv.nec.resolver.database.DatabaseService;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusService;
import eu.sonata.nfv.nec.resolver.model.Artifact;
import eu.sonata.nfv.nec.resolver.store.StoreService;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;

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

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadActor.class);

    /** The database service. */
    @Inject
    private DatabaseService databaseService;
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
    public void onReceive(Object o) {
        if (o instanceof DownloadEvents.StartDownload) {
            DownloadEvents.StartDownload event = (DownloadEvents.StartDownload) o;
            LOGGER.trace("Received an event of type DownloadEvents.StartDownload: " + event);
            Artifact artifact = (Artifact) event.getContext().get(DownloadEvents.StartDownload.ARTIFACT);
            this.downloadArtifact(artifact);
        } else {
            unhandled(o);
        }
    }

    /**
     * Start the download of an artifact.
     *
     * @param artifact The artifact (and its information) to download.
     */
    private void downloadArtifact(Artifact artifact) {
        // The file name, i.e. the uuid of the file stored.
        String fileName;

        // Check if we have the artifact already on stock.
        if (this.isArtifactInStore(artifact)) {
            fileName = artifact.uuid;
            this.eventBusService.publish(new DownloadEvents.DownloadFinished("fileName", fileName));
            return;
        }

        // Store the download information such that we can resume in case of an app failure.
        try {
            this.saveDownloadInformation(artifact);
        } catch (PersistenceException e) {
            LOGGER.error("Could not persist the artifact meta-data: " + e, e);
            this.eventBusService.publish(new DownloadEvents.DownloadError("exception", e));
            return;
        }

        // Actually start the download.
        GetRequest getRequest = Unirest.get(artifact.url);
        if (artifact.credentials.username != null && artifact.credentials.password != null) {
            getRequest.basicAuth(artifact.credentials.username, artifact.credentials.password);
        }

        try {
            HttpResponse<InputStream> response = getRequest.asBinary();
            if (response != null && response.getStatus() == HttpStatus.SC_OK) {
                fileName = storeService.create(artifact.uuid, response.getBody());
            } else {
                assert response != null;
                LOGGER.error("Could not download the artifact: " + response.getStatusText());
                this.eventBusService.publish(new DownloadEvents.DownloadError("response", response));
                return;
            }
        } catch (UnirestException | NullPointerException e) {
            LOGGER.error("Could not download the artifact: " + e);
            this.eventBusService.publish(new DownloadEvents.DownloadError("exception", e));
            return;
        }

        // Update the persisted information on the download.
        try {
            artifact.persistent = true;
            this.updateDownloadInformation(artifact);
        } catch (PersistenceException e) {
            LOGGER.error("Could not persist the artifact meta-data.", e);
            this.eventBusService.publish(new DownloadEvents.DownloadError("exception", e));
            return;
        }

        // Finally, tell everyone that the download is finished.
        this.eventBusService.publish(new DownloadEvents.DownloadFinished("fileName", fileName));
    }

    /**
     * Stores the artifact information in the persistence store. Checks
     * if the artifact information is stored in the persistence store
     * already. If yes, it just updates the information. If not, it
     * creates a new entry in the database.
     *
     * @param artifact The artifact to store.
     */
    private void saveDownloadInformation(Artifact artifact) throws PersistenceException {
        Artifact storedArtifact = this.databaseService.read(artifact.vendor, artifact.name, artifact.version);
        if (storedArtifact != null) {
            artifact.uuid = storedArtifact.uuid;
        } else {
            artifact.uuid = this.databaseService.create(artifact);
        }
    }

    /**
     * Update the artifact information in the persistence store.
     *
     * @param artifact The artifact to update.
     */
    private void updateDownloadInformation(Artifact artifact) throws PersistenceException {
        this.databaseService.update(artifact);
    }

    /**
     * Checks if the given artifact is in store. If
     * true, we don't need to download it again.
     *
     * @param artifact The artifact to check.
     * @return True if the artifact is in store already.
     */
    private boolean isArtifactInStore(Artifact artifact) {
        artifact = this.databaseService.read(artifact.vendor, artifact.name, artifact.version);
        return artifact != null && artifact.persistent;
    }
}
