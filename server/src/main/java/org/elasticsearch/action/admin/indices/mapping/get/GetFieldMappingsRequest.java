/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.indices.mapping.get;

import org.elasticsearch.Version;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.IndicesRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;
import java.util.Arrays;

/**
 * Request the mappings of specific fields
 *
 * Note: there is a new class with the same name for the Java HLRC that uses a typeless format.
 * Any changes done to this class should go to that client class as well.
 */
public class GetFieldMappingsRequest extends ActionRequest implements IndicesRequest.Replaceable {

    private String[] fields = Strings.EMPTY_ARRAY;

    private boolean includeDefaults = false;

    private String[] indices = Strings.EMPTY_ARRAY;

    private IndicesOptions indicesOptions = IndicesOptions.strictExpandOpen();

    public GetFieldMappingsRequest() {}

    public GetFieldMappingsRequest(StreamInput in) throws IOException {
        super(in);
        indices = in.readStringArray();
        if (in.getVersion().before(Version.V_8_0_0)) {
            String[] types = in.readStringArray();
            if (types != Strings.EMPTY_ARRAY) {
                throw new IllegalArgumentException("Expected empty type array but received [" + Arrays.toString(types) + "]");
            }

        }
        indicesOptions = IndicesOptions.readIndicesOptions(in);
        // Consume the deprecated local parameter
        if (in.getVersion().before(Version.V_8_0_0)) {
            in.readBoolean();
        }
        fields = in.readStringArray();
        includeDefaults = in.readBoolean();
    }

    @Override
    public GetFieldMappingsRequest indices(String... indices) {
        this.indices = indices;
        return this;
    }

    public GetFieldMappingsRequest indicesOptions(IndicesOptions indicesOptions) {
        this.indicesOptions = indicesOptions;
        return this;
    }

    @Override
    public String[] indices() {
        return indices;
    }

    @Override
    public IndicesOptions indicesOptions() {
        return indicesOptions;
    }

    /** @param fields a list of fields to retrieve the mapping for */
    public GetFieldMappingsRequest fields(String... fields) {
        this.fields = fields;
        return this;
    }

    public String[] fields() {
        return fields;
    }

    public boolean includeDefaults() {
        return includeDefaults;
    }

    /** Indicates whether default mapping settings should be returned */
    public GetFieldMappingsRequest includeDefaults(boolean includeDefaults) {
        this.includeDefaults = includeDefaults;
        return this;
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeStringArray(indices);
        if (out.getVersion().before(Version.V_8_0_0)) {
            out.writeStringArray(Strings.EMPTY_ARRAY);
        }
        indicesOptions.writeIndicesOptions(out);
        if (out.getVersion().before(Version.V_8_0_0)) {
            out.writeBoolean(true);
        }
        out.writeStringArray(fields);
        out.writeBoolean(includeDefaults);
    }
}
