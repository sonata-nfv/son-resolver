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
package eu.sonata.nfv.nec.resolver.eventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Bredel
 */
public abstract class BasicEvent {
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicEvent.class);

    /** A message string that describes the event. */
    protected String message;
    /** An arbitrary context object. */
    protected Context context;

    /**
     * Default constructor.
     */
    protected BasicEvent() {
        this.message = "";
    }

    /**
     * Constructor.
     *
     * @param message The message that should be transported by the event.
     */
    protected BasicEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the raw message.
     *
     * @return The message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the arbitrary context object.
     *
     * @return The arbitrary context object.
     */
    public Context getContext() {
        return this.context;
    }

    @Override
    public String toString() {
        String result = "event=[";
        if (message != null && !message.equals(""))
            result += "message=\"" + message + "\",";
        if (context != null)
            result += context;
        if (result.endsWith(","))
            result = result.substring(0, result.length()-1);
        result += "]";
        return result;
    }
}
