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
package eu.sonata.nfv.nec.resolver.database;

import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;
import eu.sonata.nfv.nec.resolver.model.Artifact;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Michael Bredel
 */
public interface DatabaseService {
    /**
     * Store the image meta-data.
     *
     * @param data The meta-data to the image.
     * @return The UUID of the stored data.
     */
    @Nonnull
    String create(Artifact data) throws PersistenceException;

    /**
     * Read the image meta-data.
     *
     * @param uuid The UUID of the meta-data to read.
     * @return The stored meta-data.
     */
    @Nullable
    Artifact read(String uuid);

    /**
     * Read the image meta-data by the vendor-name-version identifier.
     *
     * @param vendor The vendor of the image.
     * @param name The name of the image.
     * @param version The version of the image.
     * @return The stored meta-data.
     */
    @Nullable
    Artifact read(String vendor, String name, String version);

    /**
     * Get all image meta-data as stored in the database.
     *
     * @return A list of all image meta-data.
     */
    @Nonnull
    List<Artifact> readAll();

    /**
     * Update the image meta-data.
     *
     * @param data The meta-data to update.
     * @return The data of the updated meta-data.
     */
    @Nonnull
    Artifact update(Artifact data) throws PersistenceException;

    /**
     * Delete the image meta-data.
     *
     * @param uuid The UUID of the meta-data to delete.
     * @return The data of the meta-data object that has been delete successfully.
     */
    @Nonnull
    Artifact delete(String uuid) throws PersistenceException;
}
