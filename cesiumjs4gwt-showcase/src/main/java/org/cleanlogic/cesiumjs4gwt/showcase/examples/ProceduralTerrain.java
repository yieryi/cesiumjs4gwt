/*
 * Copyright 2021 iserge.
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

package org.cleanlogic.cesiumjs4gwt.showcase.examples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import org.cesiumjs.cs.core.Cartesian3;
import org.cesiumjs.cs.core.providers.CustomHeightmapTerrainProvider;
import org.cesiumjs.cs.scene.Scene;
import org.cesiumjs.cs.scene.options.ViewOptions;
import org.cesiumjs.cs.widgets.Viewer;
import org.cesiumjs.cs.widgets.ViewerPanel;
import org.cesiumjs.cs.widgets.options.ViewerOptions;
import org.cleanlogic.cesiumjs4gwt.showcase.basic.AbstractExample;
import org.cleanlogic.cesiumjs4gwt.showcase.components.store.ShowcaseExampleStore;

import javax.inject.Inject;

public class ProceduralTerrain  extends AbstractExample {
    private Viewer viewer;
    private Scene scene;

    @Inject
    public ProceduralTerrain(ShowcaseExampleStore store) {
        super("Procedural Terrain", "Generate procedural heightmap terrain.",
                new String[]{"Showcase", "3D", "Tiles", "Height"}, store);
    }

    @Override
    public void buildPanel() {
        int width = 32;
        int height = 32;
        CustomHeightmapTerrainProvider noiseTerrainProvider = CustomHeightmapTerrainProvider.create((x, y, level) -> {
            float[] buffer = new float[width * height];

            for (int yy = 0; yy < height; yy++) {
                for (int xx = 0; xx < width; xx++) {
                    double u = (x + xx / (width - 1.0)) / Math.pow(2, level);
                    double v = (y + yy / (height - 1.0)) / Math.pow(2, level);

                    float heightValue = (float) (9000.0 * terrainNoise(u * 1750.0 - 10.0, v * 1500.0));

                    int index = yy * width + xx;
                    buffer[index] = heightValue;
                }
            }

            Float32Array result = TypedArrays.createFloat32Array(buffer.length);
            result.set(buffer);
            return result;
            }, width, height);

        CustomHeightmapTerrainProvider sineTerrainProvider = CustomHeightmapTerrainProvider.create((x, y, level) -> {
            float[] buffer = new float[width * height];

            for (int yy = 0; yy < height; yy++) {
                for (int xx = 0; xx < width; xx++) {
                    double v = (y + yy / (height - 1.0)) / Math.pow(2, level);

                    float heightValue = (float) (4000.0 * (Math.sin(8000.0 * v) * 0.5 + 0.5));

                    int index = yy * width + xx;
                    buffer[index] = heightValue;
                }
            }

            Float32Array result = TypedArrays.createFloat32Array(buffer.length);
            result.set(buffer);
            return result;
            }, width, height);

        ViewerOptions viewerOptions = new ViewerOptions();
        viewerOptions.terrainProvider = noiseTerrainProvider;
        ViewerPanel csVPanel = new ViewerPanel(viewerOptions);

        final ListBox modelsLBox = new ListBox();
        modelsLBox.addItem("Noise", "0");
        modelsLBox.addItem("Sine", "1");
        modelsLBox.addChangeHandler(event -> {
            String value = ((ListBox) event.getSource()).getSelectedValue();
            switch (value) {
                case "0": csVPanel.getViewer().terrainProvider = noiseTerrainProvider; break;
                case "1": csVPanel.getViewer().terrainProvider = sineTerrainProvider; break;
                default: break;
            }
        });

        AbsolutePanel aPanel = new AbsolutePanel();
        aPanel.add(csVPanel);
        aPanel.add(modelsLBox, 20, 20);

        contentPanel.add(new HTML(
                "<p>Generate procedural heightmap terrain</p>"));
        contentPanel.add(aPanel);

        ViewOptions viewOptions = new ViewOptions();
        viewOptions.destinationPos = new Cartesian3(339907.1874329616, 5654554.279066735, 2936259.008266917);
        viewOptions.orientation = new org.cesiumjs.cs.core.HeadingPitchRoll(5.473742192009368, -0.2225518333236931, 6.28274245960864);

        csVPanel.getViewer().scene().camera().setView(viewOptions);

        initWidget(contentPanel);
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "ProceduralTerrain.txt";
        return sourceCodeURLs;
    }

    private double fract(double x) {
        return x - Math.floor(x);
    }

    private double smoothstep(double x) {
        return x * x * (3.0 - 2.0 * x);
    }

    private double hash(double x, double y) {
        double a = 50.0 * fract(x * 0.3183099 + 0.71);
        double b = 50.0 * fract(y * 0.3183099 + 0.113);
        double v = fract(a * b * (a + b));
        return -1.0 + 2.0 * v; // -1 to +1
    }

    public double lerp(double x, double y, double t) {
        return x * (1.0 - t) + y * t;
    }

    public double noise(double x, double y) {
        // Value noise: lerp between random values
        double ix = Math.floor(x);
        double iy = Math.floor(y);
        double fx = fract(x);
        double fy = fract(y);
        double tx = smoothstep(fx);
        double ty = smoothstep(fy);
        double v00 = hash(ix, iy);
        double v10 = hash(ix + 1, iy);
        double v01 = hash(ix, iy + 1);
        double v11 = hash(ix + 1, iy + 1);
        double v = lerp(lerp(v00, v10, tx), lerp(v01, v11, tx), ty);
        return v; // -1 to +1
    }

    private double fbm(double x, double y) {
        // Fractal brownian motion: accumulate octaves of self-similar noise
        double v = 0.5 * noise(x * 1.0, y * 1.0);
        v += 0.25 * noise(x * 0.4, y * 2.8);
        v += 0.125 * noise(x * -2.72, y * 4.96);
        v += 0.0625 * noise(x * -10.3, y * 4.67);
        v += 0.03125 * noise(x * -22.09, y * -4.89);
        v += 0.015625 * noise(x * -29.48, y * -34.33);
        // v += 0.0078125 * noise(x * -5.97, y * -90.31);
        // v += 0.00390625 * noise(x * 98.83, y * -151.66);
        return v; // -1 to +1
    }

    private double terrainNoise(double x, double y) {
        double v = fbm(x, y);
        // Move to 0 to 1 range, then make it pointier
        v = Math.pow(v * 0.5 + 0.5, 2.0);
        return v;
    }
}
