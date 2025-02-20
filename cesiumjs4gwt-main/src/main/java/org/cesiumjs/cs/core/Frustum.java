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

package org.cesiumjs.cs.core;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * @author Serge Silaev aka iSergio
 */
@JsType(isNative = true, namespace = "Cesium", name = "Frustum")
public class Frustum {
    /**
     * The distance of the far plane. Default: 500000000.0;
     */
    @JsProperty
    public double far;
    /**
     * The distance of the near plane. Default: 1.0
     */
    @JsProperty
    public double near;

    @JsConstructor
    public Frustum() {
    }

    /**
     * Gets the perspective projection matrix computed from the view frustum.
     */
    @JsProperty(name = "projectionMatrix")
    public native Matrix4 projectionMatrix();
}
