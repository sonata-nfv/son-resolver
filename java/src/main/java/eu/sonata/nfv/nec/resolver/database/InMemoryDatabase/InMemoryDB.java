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
package eu.sonata.nfv.nec.resolver.database.InMemoryDatabase;

import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;
import eu.sonata.nfv.nec.resolver.database.DatabaseService;
import eu.sonata.nfv.nec.resolver.model.Artifact;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A dummy in-memory database implementation that
 * stores the image meta-data in a simple map
 * structure.
 *
 * @author Michael Bredel
 */
@Singleton
public class InMemoryDB implements DatabaseService {

    /** The data structure that stores the image meta-data. */
    private Map<String, Artifact> imageDataMap;

    /**
     * The default constructor.
     */
    public InMemoryDB() {
        this.imageDataMap = new ConcurrentHashMap<>();
    }

    @Nonnull
    @Override
    public String create(Artifact data) throws PersistenceException {
        // Check if image is already stored.
        Collection<Artifact> imageDataList = this.imageDataMap.values();
        for (Artifact imageData : imageDataList) {
            if (imageData.equals(data))
                throw new PersistenceException();
        }

        // Store new image.
        if (data.uuid == null || data.uuid.equals("")) {
            data.uuid = getUUID();
        }
        this.imageDataMap.put(data.uuid, data);
        return data.uuid;
    }

    @Nullable
    @Override
    public Artifact read(String uuid) {
        return this.imageDataMap.getOrDefault(uuid, null);
    }

    @Nullable
    @Override
    public Artifact read(String vendor, String name, String version) {
        Artifact temp = new Artifact();
        temp.vendor = vendor;
        temp.name = name;
        temp.version = version;
        for (Artifact imageData : this.imageDataMap.values()) {
            if (imageData.equals(temp)) {
                return imageData;
            }
        }

        // No image data found.
        return null;
    }

    @Nonnull
    @Override
    public List<Artifact> readAll() {
        return new ArrayList<>(this.imageDataMap.values());
    }

    @Nonnull
    @Override
    public Artifact update(Artifact data) throws PersistenceException {
        if (data.uuid == null)
            throw new PersistenceException("The image data does not have a UUID.");
        this.imageDataMap.put(data.uuid, data);
        return data;
    }

    @Nonnull
    @Override
    public Artifact delete(String uuid) throws PersistenceException {
        return this.imageDataMap.remove(uuid);
    }

    /**
     * Create a new UUID.
     *
     * @return A UUID string.
     */
    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}
