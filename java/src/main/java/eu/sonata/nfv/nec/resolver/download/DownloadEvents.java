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

import eu.sonata.nfv.nec.resolver.eventBus.BasicEvent;
import eu.sonata.nfv.nec.resolver.eventBus.Context;

import javax.annotation.concurrent.Immutable;

/**
 * Container class that contains all the events that
 * are used and emitted by the download actor.
 *
 * @author Michael Bredel
 */
public final class DownloadEvents {

    /**
     * Event that is used to start a download. The download
     * actor listens to these kind of events in the akka
     * event bus to start the download. The context object
     * contains information about the resource location
     * and credentials.
     */
    @Immutable
    public static final class StartDownload extends BasicEvent {
        /** The URI string used for the context information,. */
        public static final String URI = "uri";
        /** The username string used for the context information,. */
        public static final String USERNAME = "username";
        /** The password string used for the context information,. */
        public static final String PASSWORD = "password";
        /** The artifact related to the start download event. */
        public static final String ARTIFACT = "artifact";


        /**
         * Constructor that allows to transport context information. The
         * context contains further information on where to download
         * the artifact and, optionally, the credentials to access the
         * resource location.
         *
         * @param context The context information transported with this event.
         */
        public StartDownload(Context context) {
            super("Start the download of a new artifact.");
            this.context = context;
        }
    }

    /**
     * Event that is emitted by the download actor after a download
     * has started successfully. The context contains information
     * on the download process, e.g. needed to cancel the download.
     */
    @Immutable
    public static final class DownloadStarted extends BasicEvent {
        /**
         * Constructor that allows to transport context information. The
         * context contains further information on the download process,
         * e.g. needed to cancel the download
         *
         * @param context The context information transported with this event.
         */
        public DownloadStarted(Context context) {
            super("The download has started.");
            this.context = context;
        }
    }

    /**
     * Event that is emitted by the download actor once a download
     * has been finished successfully. The context contains the
     * information where the artifact has been stored and how
     * it can be accessed.
     */
    @Immutable
    public static final class DownloadFinished extends BasicEvent {
        /**
         * Constructor that allows to transport context information. The
         * context contains further information on where the artifact
         * has been stored.
         *
         * @param context The context information transported with this event.
         */
        public DownloadFinished(Context context) {
            super("The download has finished");
            this.context = context;
        }

        /**
         * Convenient constructor that creates a context object and
         * store the given key-value parameter.
         *
         * @param key The key for the context object.
         * @param o The value for the context object.
         */
        public DownloadFinished(String key, Object o) {
            super("The download did not complete due to an error.");
            this.context = new Context()
                    .add(key, o);
        }
    }

    /**
     * Even that is emitted whenever the download experienced and error.
     * The context contains further information on the error as such.
     */
    @Immutable
    public static final class DownloadError extends BasicEvent {
        /**
         * Constructor that allows to transport context information. The
         * context contains further information on the error as such.
         *
         * @param context The context information transported with this event.
         */
        public DownloadError(Context context) {
            super("The download did not complete due to an error.");
            this.context = context;
        }

        /**
         * Convenient constructor that creates a context object and
         * store the given key-value parameter.
         *
         * @param key The key for the context object.
         * @param o The value for the context object.
         */
        public DownloadError(String key, Object o) {
            super("The download did not complete due to an error.");
            this.context = new Context()
                    .add(key, o);
        }
    }
}
