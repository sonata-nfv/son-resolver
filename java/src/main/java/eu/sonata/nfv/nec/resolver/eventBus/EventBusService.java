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

import akka.actor.ActorRef;

/**
 * @author Michael Bredel
 */
public interface EventBusService {
    /**
     * Returns  a size hint for the number of Classifiers you
     * expect to have (use powers of 2).
     *
     * @return A size hint for the number of Classifiers you expect to have.
     */
    int mapSize();

    /**
     * Provides a total ordering of Subscribers.
     *
     * @param subscriberA The first subscriber.
     * @param subscriberB The second Subscriber.
     * @return
     */
    int compareSubscribers(ActorRef subscriberA, ActorRef subscriberB);

    /**
     * Returns the classifier associated with the given event. The
     * classifier is used to identify the topic of an event.
     *
     * @param event The event to classify.
     * @return The classifier associated with the given event.
     */
    String classify(BasicEvent event);

    /**
     * Publishes the given Event to the message bus.
     *
     * @param event The event to publish.
     */
    void publish(BasicEvent event);

    /**
     * Convenient method that subscribes an actor to the channel/topic
     * identified by the event class simple name, inferred by
     * event.getClass().getSimpleName();
     *
     * @param subscriber The actor reference that is subscribed to the event bus.
     * @param eventClass The event class that identifies the channel/topic.
     */
    void subscribe(ActorRef subscriber, Class<? extends BasicEvent> eventClass);
}
