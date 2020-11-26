/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tmaskibail.appengine.scheduler.util;

import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CustomJsonHttpContent extends JsonHttpContent {

    private final String stringObject;

    /**
     * @param jsonFactory JSON factory to use
     * @param data        JSON key name/value data
     * @since 1.5
     */
    public CustomJsonHttpContent(JsonFactory jsonFactory, Object data) {
        super(jsonFactory, data);
        this.stringObject = (String) data;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(stringObject.getBytes(StandardCharsets.UTF_8));
    }
}
