/*
 * Copyright 2018 iserge.
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

package org.cesiumjs.cs.datasources.options;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.cesiumjs.cs.js.JsObject;

/**
 * @author Serge Silaev aka iSergio
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CzmlDataSourceOptions {
    /**
     * Overrides the url to use for resolving relative links
     */
    @JsProperty
    public String sourceUri;
    /**
     * Key-value pairs which are appended to all URIs in the CZML.
     */
    @JsProperty
    public JsObject query;

    @JsConstructor
    private CzmlDataSourceOptions() {
    }
}
