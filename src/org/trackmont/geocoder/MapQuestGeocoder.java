/*
 * Copyright 2014 - 2015 Stefaan Van Dooren (stefaan.vandooren@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trackmont.geocoder;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class MapQuestGeocoder extends JsonGeocoder {

    public MapQuestGeocoder(String url, String key, int cacheSize) {
        super(url + "?key=" + key + "&location=%f,%f", cacheSize);
    }

    @Override
    public Address parseAddress(JsonObject json) {
        JsonArray result = json.getJsonArray("results");
        if (result != null) {
            JsonArray locations = result.getJsonObject(0).getJsonArray("locations");
            if (locations != null) {
                JsonObject location = locations.getJsonObject(0);

                Address address = new Address();

                if (location.containsKey("street")) {
                    address.setStreet(location.getString("street"));
                }
                if (location.containsKey("adminArea5")) {
                    address.setSettlement(location.getString("adminArea5"));
                }
                if (location.containsKey("adminArea4")) {
                    address.setDistrict(location.getString("adminArea4"));
                }
                if (location.containsKey("adminArea3")) {
                    address.setState(location.getString("adminArea3"));
                }
                if (location.containsKey("adminArea1")) {
                    address.setCountry(location.getString("adminArea1").toUpperCase());
                }
                if (location.containsKey("postalCode")) {
                    address.setPostcode(location.getString("postalCode"));
                }

                return address;
            }
        }
        return null;
    }

}
