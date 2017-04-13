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
package eu.sonata.nfv.nec.resolver.store.fileSystemStore;

import eu.sonata.nfv.nec.resolver.configuration.ConfigurationService;
import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;
import eu.sonata.nfv.nec.resolver.store.StoreService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The file system store stores images
 * on the file system.
 *
 * @author Michael Bredel
 */
@Singleton
public class FileSystemStore implements StoreService {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStore.class);
    /** The default directory to upload the files. */
    private static final String DEFAULT_UPLOAD_DIRECTORY = "/tmp/son-resolver/images";

    /** The configuration service. */
    private ConfigurationService configurationService;

    /**
     * The default constructor that also injects
     * the Guice services.
     *
     * @param configurationService The configuration service.
     */
    @Inject
    public FileSystemStore(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Nonnull
    @Override
    public String create(String fileName, InputStream inputStream) throws PersistenceException {
        if (fileName == null || inputStream == null) {
            throw new IllegalArgumentException();
        }

        String pathName = this.configurationService.getProperty("storage.uri", DEFAULT_UPLOAD_DIRECTORY, String.class);

        // Create the directory if needed.
        File upload = new File(pathName);
        if (!upload.exists() && !upload.mkdirs()) {
            LOGGER.error("Failed to create directory " + upload.getAbsolutePath());
            throw new PersistenceException("Failed to create directory " + upload.getAbsolutePath());
        }

        // Upload and store the file using Apache commons.
        File file = new File(pathName + File.separator + fileName);
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            LOGGER.error("Something bad happened: " + e.getMessage());
            throw new PersistenceException(e);
        }

        // Return the file name.
        return fileName;
    }

    @Nullable
    @Override
    public File read(String fileName) {
        if (fileName == null || fileName.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        String pathName = this.configurationService.getProperty("storage.uri", DEFAULT_UPLOAD_DIRECTORY, String.class);

        File result = new File(pathName + File.separator + fileName);
        if (result.exists() && result.isFile() && result.canRead()) {
            return result;
        } else {
            LOGGER.warn("Could not read the file: " + result.getName());
            return null;
        }
    }

    @Nonnull
    @Override
    public List<String> readAll() {
        String pathName = this.configurationService.getProperty("storage.uri", DEFAULT_UPLOAD_DIRECTORY, String.class);
        List<String> result = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(Paths.get(pathName))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    System.out.println(filePath);
                    result.add(filePath.getFileName().toString());
                }
            });
        } catch (IOException e) {
            // Just log, but nothing more, as we can return an empty list.
            LOGGER.error("Could not read files: " + e.getMessage());
        }
        return result;
    }

    @Nonnull
    @Override
    public String delete(String fileName) throws PersistenceException, IllegalArgumentException {
        if (fileName == null || fileName.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        String pathName = this.configurationService.getProperty("storage.uri", DEFAULT_UPLOAD_DIRECTORY, String.class);
        String file = pathName + File.separator + fileName;

        if (new File(file).delete()) {
            return fileName;
        } else {
            LOGGER.warn("Could not delete file: " + file);
            throw new PersistenceException("Could not delete file: " + file);
        }
    }
}
