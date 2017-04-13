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
package eu.sonata.nfv.nec.resolver.configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;

/**
 * The configuration service allows to access
 * configuration parameters in a key-value
 * manner.
 *
 * @author Michael Bredel
 */
public interface ConfigurationService {
    /**
     * Get a configuration property of a given basic {@code type}.
     *
     * @param key The configuration key.
     * @param <T> The property type.
     * @throws NoSuchElementException When the provided {@code key} doesn't have a corresponding config value
     * @throws IllegalArgumentException When property can't be converted to {@code type}
     * @throws IllegalStateException When provider is unable to fetch configuration value for the given {@code key}
     */
    @Nullable
    <T> T getProperty(String key, Class<T> type);

    /**
     * Get a configuration property of a given basic {@code type}.
     *
     * @param key The configuration key.
     * @param defaultValue The default value. Has to be the same type as property type.
     * @param <T> The property type.
     * @throws IllegalArgumentException When property can't be converted to {@code type}
     */
    @Nonnull
    <T> T getProperty(String key, Object defaultValue, Class<T> type);
}
