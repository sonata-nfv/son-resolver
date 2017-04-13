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
package eu.sonata.nfv.nec.resolver.eventBus.akka;

import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;
import eu.sonata.nfv.nec.resolver.eventBus.BasicEvent;
import eu.sonata.nfv.nec.resolver.eventBus.EventBusService;

/**
 * @author Michael Bredel
 */
public class AkkaEventBus extends LookupEventBus<BasicEvent, ActorRef, String> implements EventBusService {
    /** Initial size of the index data structure, i.e. the expected number of different classifiers. Use powers of 2. */
    private static final int MAP_SIZE = 4;

    @Override
    public int mapSize() {
        return MAP_SIZE;
    }

    @Override
    public void subscribe(ActorRef subscriber, Class<? extends BasicEvent> eventClass) {
        super.subscribe(subscriber, eventClass.getSimpleName());
    }

    @Override
    public int compareSubscribers(ActorRef subscriberA, ActorRef subscriberB) {
        return subscriberA.compareTo(subscriberB);
    }

    @Override
    public String classify(BasicEvent event) {
        return event.getClass().getSimpleName();
    }

    @Override
    public void publish(BasicEvent event, ActorRef subscriber) {
        subscriber.tell(event, ActorRef.noSender());
    }
}
