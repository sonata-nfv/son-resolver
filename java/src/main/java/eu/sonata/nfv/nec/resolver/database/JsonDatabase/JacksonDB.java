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
package eu.sonata.nfv.nec.resolver.database.JsonDatabase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;
import eu.sonata.nfv.nec.resolver.database.InMemoryDatabase.InMemoryDB;
import eu.sonata.nfv.nec.resolver.model.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A very basic persistent database that stores
 * all image meta-data in an in-memory database
 * as well as a JSON file on disk.
 *
 * @author Michael Bredel
 */
public class JacksonDB extends InMemoryDB {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonDB.class);
    /** The default file location. */
    private static final String DB_FILE = "/tmp/son-resolver/database/images.json";

    /** The location of the database JSON file. */
    private String dbFilesLocation;

    /**
     * Default constructor.
     *
     * @param configurationService The configuration service as injected by Guice.
     */
    @Inject
    public JacksonDB(ConfigurationService configurationService) {
        this.dbFilesLocation = configurationService.getProperty("database.uri", DB_FILE, String.class);
    }

    @Nonnull
    @Override
    public String create(Artifact data) throws PersistenceException {
        String uuid = super.create(data);

        // Persist the data.
        try {
            this.writeDataToFile(super.readAll());
        } catch (IOException e) {
            super.delete(uuid);
            throw new PersistenceException(e);
        }

        return uuid;
    }

    @Nullable
    @Override
    public Artifact read(String uuid) {
        // If the in-memory database is empty, try to fill it from the file.
        if (super.readAll().isEmpty()) {
            try {
                List<Artifact> imageDataList = this.readDataFromFile();
                for (Artifact imageData : imageDataList) {
                    super.create(imageData);
                }
            } catch (IOException e) {
                LOGGER.warn("Could not read the data file: " + e);
                return null;
            }
        }

        // Reading from the in-memory database.
        return super.read(uuid);
    }

    @Nullable
    @Override
    public Artifact read(String vendor, String name, String version) {
        // If the in-memory database is empty, try to fill it from the file.
        if (super.readAll().isEmpty()) {
            try {
                List<Artifact> imageDataList = this.readDataFromFile();
                for (Artifact imageData : imageDataList) {
                    super.create(imageData);
                }
            } catch (IOException e) {
                LOGGER.warn("Could not read the data file: " + e);
                return null;
            }
        }

        // Reading from the in-memory database only.
        return super.read(vendor, name, version);
    }

    @Nonnull
    @Override
    public List<Artifact> readAll() {
        // If the in-memory database is empty, try to fill it from the file.
        if (super.readAll().isEmpty()) {
            try {
                List<Artifact> imageDataList = this.readDataFromFile();
                for (Artifact imageData : imageDataList) {
                    super.create(imageData);
                }
            } catch (IOException e) {
                LOGGER.warn("Could not read the data file: " + e);
                return new ArrayList<>();
            }
        }

        // Return all data from the in-memory database.
        return super.readAll();
    }

    @Nonnull
    @Override
    public Artifact update(Artifact data) throws PersistenceException {
        // Store the old data temporarily in case we need to roll back.
        Artifact tempData = super.read(data.uuid);

        // Update the data in the in-memory database.
        Artifact result = super.update(data);

        // Persist the data.
        try {
            this.writeDataToFile(super.readAll());
        } catch (IOException e) {
            // Roll back and re-install the temp-data.
            super.update(tempData);
            throw new PersistenceException(e);
        }

        return result;
    }

    @Nonnull
    @Override
    public Artifact delete(String uuid) throws PersistenceException {
        // Store the old data temporarily in case we need to roll back.
        Artifact tempData = super.read(uuid);

        // Delete the data in in-memory database.
        Artifact result = super.delete(uuid);

        // Persist the data.
        try {
            this.writeDataToFile(super.readAll());
        } catch (IOException e) {
            // Roll back and re-install the temp-data.
            super.create(tempData);
            throw new PersistenceException(e);
        }

        return result;
    }

    /**
     * Checks if we have write access ot the database file
     * and directory.
     *
     * @return True if the database file readable and writable.
     */
    public boolean isHealthy() {
        File file = new File(dbFilesLocation);
        if (file.exists()) {
            if (!file.isFile())
                return false;
            if (!file.canWrite())
                return false;
            if (!file.canRead())
                return false;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Can not access the database file: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Writes the in-memory data to a JSON file.
     *
     * @param data The list of image data that should be persisted.
     * @throws IOException When the JSON file can not be written to disk.
     */
    private synchronized void writeDataToFile(List<Artifact> data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(dbFilesLocation);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        mapper.writeValue(file, data);
    }

    /**
     * Reads the JSON data from a file and converts it into
     * the in-memory database structure.
     *
     * @return A list of image data objects.
     * @throws IOException When the the JSON file can not be read.
     */
    private synchronized List<Artifact> readDataFromFile() throws IOException {
        List<Artifact> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(dbFilesLocation);
        if (!file.exists()) {
            return result;
        }
        JsonNode jsonNode = mapper.readTree(file);
        if (jsonNode.isArray()) {
            for (JsonNode objNode : jsonNode) {
                Artifact data = mapper.treeToValue(objNode, Artifact.class);
                result.add(data);
            }
        }

        return result;
    }

}
