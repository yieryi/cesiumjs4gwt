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
import com.google.gwt.user.client.ui.HTML;
import org.cesiumjs.cs.Cesium;
import org.cesiumjs.cs.collections.CloudCollection;
import org.cesiumjs.cs.collections.options.CumulusCloudAddOptions;
import org.cesiumjs.cs.core.HeadingPitchRoll;
import org.cesiumjs.cs.core.Math;
import org.cesiumjs.cs.core.*;
import org.cesiumjs.cs.core.enums.ClockRange;
import org.cesiumjs.cs.core.interpolation.HermitePolynomialApproximation;
import org.cesiumjs.cs.datasources.Entity;
import org.cesiumjs.cs.datasources.graphics.ModelGraphics;
import org.cesiumjs.cs.datasources.graphics.options.ModelGraphicsOptions;
import org.cesiumjs.cs.datasources.options.EntityOptions;
import org.cesiumjs.cs.datasources.properties.ConstantProperty;
import org.cesiumjs.cs.datasources.properties.SampledPositionProperty;
import org.cesiumjs.cs.datasources.properties.options.SampledPropertyInterpolationOptions;
import org.cesiumjs.cs.js.JsDate;
import org.cesiumjs.cs.js.JsObject;
import org.cesiumjs.cs.scene.options.CameraFlyToOptions;
import org.cesiumjs.cs.widgets.ViewerPanel;
import org.cesiumjs.cs.widgets.options.ViewerOptions;
import org.cleanlogic.cesiumjs4gwt.showcase.basic.AbstractExample;
import org.cleanlogic.cesiumjs4gwt.showcase.components.store.ShowcaseExampleStore;

import javax.inject.Inject;

public class Clouds extends AbstractExample {
    private CloudCollection clouds;
    private JulianDate start;
    private JulianDate stop;

    @Inject
    public Clouds(ShowcaseExampleStore store) {
        super("Clouds",
                "Add clouds to the scene",
                new String[]{"Showcase", "Cesium", "3d", "Clouds", "CloudCollection"}, store);
    }

    @Override
    public void buildPanel() {
        ViewerOptions viewerOptions = new ViewerOptions();
        viewerOptions.terrainProvider = Cesium.createWorldTerrain();
        viewerOptions.infoBox = false;
        viewerOptions.shouldAnimate = true;
        ViewerPanel csVPanel = new ViewerPanel(viewerOptions);

        csVPanel.getViewer().scene().primitives().add(Cesium.createOsmBuildings());

        ///////////////////////////
        // Create clouds
        ///////////////////////////
        org.cesiumjs.cs.core.Math.setRandomNumberSeed(2.5);
        clouds = new CloudCollection();
        // Fix texture size
        // TODO: remove this fix after Cesium fixed them in core
        ((JsObject) (Object) clouds).setProperty("_noiseTextureLength", 64.0);
        Cesium.log(((JsObject) (Object) clouds).getNumber("_noiseTextureLength"));
        csVPanel.getViewer().scene().primitives().add(clouds);

        createBackLayerClouds();
        createRandomClouds(8, -122.685, -122.67, 45.51, 45.525, 50, 250);
        createFrontLayerClouds();

        ///////////////////////////
        // Create hot air balloons
        ///////////////////////////
        start = JulianDate.fromDate(new JsDate(2021, 7, 21, 12));
        stop = JulianDate.addSeconds(start, 90, new JulianDate());

        ModelGraphicsOptions modelGraphicsOptions = new ModelGraphicsOptions();
        modelGraphicsOptions.uri = new ConstantProperty<>(
                GWT.getModuleBaseURL() + "SampleData/models/CesiumBalloon/CesiumBalloon.glb");
        modelGraphicsOptions.minimumPixelSize = new ConstantProperty<>(128);
        modelGraphicsOptions.maximumScale = new ConstantProperty<>(20000);
        EntityOptions options = new EntityOptions();
        options.position = computeBalloonFlight(-122.661, 45.524, 400, 500);
        options.model = new ModelGraphics(modelGraphicsOptions);
        Entity ballon0 = csVPanel.getViewer().entities().add(options);
        SampledPropertyInterpolationOptions interpolationOptions = new SampledPropertyInterpolationOptions();
        interpolationOptions.interpolationDegree = 2;
        interpolationOptions.interpolationAlgorithm = HermitePolynomialApproximation.instance();
        ((SampledPositionProperty) ballon0.position).setInterpolationOptions(interpolationOptions);

        modelGraphicsOptions = new ModelGraphicsOptions();
        modelGraphicsOptions.uri = new ConstantProperty<>(
                GWT.getModuleBaseURL() + "SampleData/models/CesiumBalloon/CesiumBalloon.glb");
        modelGraphicsOptions.minimumPixelSize = new ConstantProperty<>(128);
        modelGraphicsOptions.maximumScale = new ConstantProperty<>(20000);
        options = new EntityOptions();
        options.position = computeBalloonFlight(-122.662, 45.517, 400, 300);
        options.model = new ModelGraphics(modelGraphicsOptions);
        Entity ballon1 = csVPanel.getViewer().entities().add(options);
        interpolationOptions = new SampledPropertyInterpolationOptions();
        interpolationOptions.interpolationDegree = 2;
        interpolationOptions.interpolationAlgorithm = HermitePolynomialApproximation.instance();
        ((SampledPositionProperty) ballon1.position).setInterpolationOptions(interpolationOptions);

        csVPanel.getViewer().clock().startTime = start.clone();
        csVPanel.getViewer().clock().stopTime = stop.clone();
        csVPanel.getViewer().clock().currentTime = start.clone();
        csVPanel.getViewer().clock().clockRange = ClockRange.LOOP_STOP();
        csVPanel.getViewer().clock().multiplier = 1.0;

        // Fly to Portland
        CameraFlyToOptions flyToOptions = new CameraFlyToOptions();
        flyToOptions.destinationPos = Cartesian3.fromDegrees(-122.6515, 45.5252, 525);
        flyToOptions.orientation = new HeadingPitchRoll(Math.toRadians(-115), Math.toRadians(-12), 0.0);
        csVPanel.getViewer().camera.flyTo(flyToOptions);

        csVPanel.getViewer().scene().fog.density = 1.15e-4;

        contentPanel.add(new HTML(
                "<p>Add clouds to the scene.</p>"));
        contentPanel.add(csVPanel);

        initWidget(contentPanel);
    }

    private void createBackLayerClouds() {
        CumulusCloudAddOptions options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.6908, 45.496, 300);
        options.scale = new Cartesian2(1500, 250);
        options.maximumSize = new Cartesian3(50, 15, 13);
        options.slice = 0.3f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.72, 45.5, 335);
        options.scale = new Cartesian2(1500, 300);
        options.maximumSize = new Cartesian3(50, 12, 15);
        options.slice = 0.36f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.72, 45.51, 260);
        options.scale = new Cartesian2(2000, 300);
        options.maximumSize = new Cartesian3(50, 12, 15);
        options.slice = 0.49f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.705, 45.52, 250);
        options.scale = new Cartesian2(230, 110);
        options.maximumSize = new Cartesian3(13, 13, 13);
        options.slice = 0.2f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.71, 45.522, 270);
        options.scale = new Cartesian2(1700, 300);
        options.maximumSize = new Cartesian3(50, 12, 15);
        options.slice = 0.6f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.705, 45.525, 250);
        options.scale = new Cartesian2(230, 110);
        options.maximumSize = new Cartesian3(15, 13, 15);
        options.slice = 0.35f;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.721, 45.53, 220);
        options.scale = new Cartesian2(1500, 500);
        options.maximumSize = new Cartesian3(30, 20, 17);
        options.slice = 0.45f;
        clouds.add(options);
    }

    private double getRandomNumberInRange(double minValue, double maxValue) {
        return minValue + org.cesiumjs.cs.core.Math.nextRandomNumber() * (maxValue - minValue);
    }

    private void createRandomClouds(int numClouds,
                                    double startLong, double stopLong,
                                    double startLat, double stopLat,
                                    double minHeight, double maxHeight) {
        double rangeLong = stopLong - startLong;
        double rangeLat = stopLat - startLat;
        for (int i = 0; i < numClouds; i++) {
            double lon = startLong + getRandomNumberInRange(0, rangeLong);
            double lat = startLat + getRandomNumberInRange(0, rangeLat);
            double height = getRandomNumberInRange(minHeight, maxHeight);
            double scaleX = getRandomNumberInRange(150, 350);
            double scaleY = scaleX / 2.0 - getRandomNumberInRange(0, scaleX / 4.0);
            double slice = getRandomNumberInRange(0.3, 0.7);
            double depth = getRandomNumberInRange(5, 20);
            double aspectRatio = getRandomNumberInRange(1.5, 2.1);
            double cloudHeight = getRandomNumberInRange(5, 20);

            CumulusCloudAddOptions options = new CumulusCloudAddOptions();
            options.position = Cartesian3.fromDegrees(lon, lat, height);
            options.scale = new Cartesian2(scaleX, scaleY);
            options.maximumSize = new Cartesian3(aspectRatio * cloudHeight, cloudHeight, depth);
            options.slice = slice;
            clouds.add(options);
        }
    }

    private void createFrontLayerClouds() {
        CumulusCloudAddOptions options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.666, 45.5126, 97);
        options.scale = new Cartesian2(400, 150);
        options.maximumSize = new Cartesian3(25, 12, 15);
        options.slice = 0.36;
        clouds.add(options);

        options = new CumulusCloudAddOptions();
        options.position = Cartesian3.fromDegrees(-122.6665, 45.5262, 76);
        options.scale = new Cartesian2(450, 200);
        options.maximumSize = new Cartesian3(25, 14, 12);
        options.slice = 0.3;
        clouds.add(options);
    }

    private SampledPositionProperty computeBalloonFlight(double lon, double lat, double height0, double height1) {
        SampledPositionProperty property = new SampledPositionProperty();
        JulianDate time0 = start.clone();
        JulianDate time1 = JulianDate.addSeconds(time0, 30, new JulianDate());
        JulianDate time2 = JulianDate.addSeconds(time1, 15, new JulianDate());
        JulianDate time3 = JulianDate.addSeconds(time2, 30, new JulianDate());
        JulianDate time4 = JulianDate.addSeconds(time3, 15, new JulianDate());

        Cartesian3 position0 = Cartesian3.fromDegrees(lon, lat, height0);
        Cartesian3 position1 = Cartesian3.fromDegrees(lon, lat, height1);

        property.addSample(time0, position0);
        property.addSample(time1, position1);
        property.addSample(time2, position1);
        property.addSample(time3, position0);
        property.addSample(time4, position0);

        return property;
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "Clouds.txt";
        return sourceCodeURLs;
    }
}
