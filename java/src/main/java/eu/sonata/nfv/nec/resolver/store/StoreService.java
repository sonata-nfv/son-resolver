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
package eu.sonata.nfv.nec.resolver.store;

import eu.sonata.nfv.nec.resolver.core.exceptions.persistence.PersistenceException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Interface that must be implemented by the
 * store module that actually stores the image
 * to a persistent storage, such as disk.
 *
 * @author  Michael Bredel
 */
public interface StoreService {

    /**
     * Extracts the uploaded file form the HTTP
     * request and stores it using the filename.
     *
     * @param filename The name of the file, usually a UUID.
     * @param inputStream The input stream that should be written to a file.
     * @return The name of the file that has been written successfully.
     * @throws PersistenceException When the file cannot be stored.
     * @throws IllegalArgumentException When at least one of the arguments is null.
     */
    @Nonnull
    String create(String filename, InputStream inputStream) throws PersistenceException, IllegalArgumentException;

    /**
     * Read and return a file by its file name.
     *
     * @param fileName The file name, usually a UUID.
     * @return The file.
     * @throws IllegalArgumentException When the arguments is null.
     */
    @Nullable
    File read(String fileName) throws IllegalArgumentException;

    /**
     * Read and return all file names stored in
     * the image store.
     *
     * @return A list of all file names in the store. The list might be empty.
     */
    @Nonnull
    List<String> readAll();

    /**
     * Delete the file by its file name.
     *
     * @param fileName The file name, usually a UUID.
     * @return The name of the file that has been deleted successfully.
     * @throws PersistenceException When the file cannot be deleted.
     * @throws IllegalArgumentException When the arguments is null.
     */
    @Nonnull
    String delete(String fileName) throws PersistenceException, IllegalArgumentException;
}
