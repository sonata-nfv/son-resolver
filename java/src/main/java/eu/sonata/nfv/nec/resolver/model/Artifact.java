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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The artifact dependency model reflects the information
 * a package descriptor may contain on an artifact dependency.
 * It might be used to download the actual artifact.
 *
 * @author Michael Bredel
 */
public class Artifact extends BaseModel {

    public String uuid;
    public boolean persistent;
    @JsonProperty(required = true)
    public String name;
    @JsonProperty(required = true)
    public String vendor;
    @JsonProperty(required = true)
    public String version;
    @JsonProperty(required = true)
    public String url;
    public String md5;
    public Credentials credentials;


    /**
     * Default constructor.
     */
    public Artifact() {
        this.uuid = null;
        this.persistent = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Artifact other = (Artifact) o;
        if (!other.vendor.equalsIgnoreCase(vendor))
            return false;
        if (!other.name.equalsIgnoreCase(name))
            return false;
        if (!other.version.equalsIgnoreCase(version))
            return false;

        return true;

    }
}
