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
package eu.sonata.nfv.nec.resolver.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The base class for models. Contains some
 * helper methods. Each model class should
 * extend this base model.
 *
 * @author Michael Bredel
 */
public abstract class BaseModel<T> {

    /**
     * Create a JSON node object.
     *
     * @return A JSON node object representation of this object.
     */
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, JsonNode.class);
    }

    /**
     * Create a JSON string.
     *
     * @return A string representation of the JSON object of this object.
     */
    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // This should never happen.
            return "ERROR: Could not convert object to a JSON string.";
        }
    }

    /**
     * Helper method that creates model objects from
     * JSON objects.
     *
     * @param jsonNode The JSON object to parse.
     * @param clazz The object to parse to.
     * @return An model data object.
     */
    public T fromJSON(JsonNode jsonNode, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.treeToValue(jsonNode, clazz);
    }
}
