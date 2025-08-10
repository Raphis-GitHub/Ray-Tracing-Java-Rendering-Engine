package renderer;

import geometries.Plane;
import geometries.Sphere;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Simple test for Soft Shadows effect - basic demonstration
 */
public class SoftShadowsSimpleTest {

    /**
     * Very basic soft shadows test with grey cylinder and off-white ground
     */
    @Test
    void testBasicSoftShadows() {
        Scene scene = new Scene("Basic Soft Shadows");
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 10))); // Very low ambient for clear shadows

        // Simple scene: dark sphere above light plane for maximum shadow contrast
        scene.geometries.add(
                // Dark grey sphere casting shadow
                new Sphere(new Point(0, -5, -20), 15)
                        .setEmission(new Color(60, 60, 60)) // Darker grey
                        .setMaterial(new Material().setKd(0.8).setKs(0.2).setShininess(30)),

                // Light grey ground plane for clear shadow visibility
                new Plane(new Point(0, -20, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(180, 180, 180)) // Light grey (not white)
                        .setMaterial(new Material().setKd(0.9).setKs(0.1).setShininess(10))
        );

        // Strong directional light to cast clear shadows  
        scene.lights.add(
                new PointLight(new Color(255, 255, 255), new Point(-50, 50, 20))
                        .setKl(0.0001).setKq(0.0001)
                        .setRadius(20) // Large area light source for soft shadows
        );

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(50, 20, 80))
                .setDirection(new Point(0, -10, -20), new Vector(0, 1, 0))
                .setVpSize(100, 100)
                .setVpDistance(80)
                .setResolution(400, 400);

        // Hard shadows (OFF)
        Camera cameraOff = cameraBuilder
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(false)
                        .build())
                .build();

        cameraOff.renderImage()
                .writeToImage("BasicSoftShadows-OFF");

        // Soft shadows (ON)
        Camera cameraOn = cameraBuilder
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(true)
                        .setSoftShadowSamples(50) // 9x9 grid
                        .build())
                .build();

        cameraOn.renderImage()
                .writeToImage("BasicSoftShadows-ON");

        Camera cameraOn300 = cameraBuilder
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(true)
                        .setSoftShadowSamples(300) // 9x9 grid
                        .build())
                .build();

        cameraOn300.renderImage()
                .writeToImage("BasicSoftShadows-ON300");
    }

    /**
     * Test with SpotLight to verify it works with different light types
     */
    @Test
    void testSpotLightSoftShadows() {
        Scene scene = new Scene("SpotLight Soft Shadows");
        scene.setAmbientLight(new AmbientLight(new Color(WHITE)));

        // Two objects to show shadow interaction
        scene.geometries.add(
                new Sphere(new Point(-15, 0, 0), 12)
                        .setEmission(new Color(RED))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),

                new Sphere(new Point(15, 0, 0), 12)
                        .setEmission(new Color(GREEN))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),

                new Plane(new Point(0, -20, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(30, 30, 30))
                        .setMaterial(new Material().setKd(0.7))
        );

        // SpotLight with area for soft shadows
        scene.lights.add(
                new SpotLight(new Color(255, 255, 255), new Point(0, 40, 30), new Vector(0, -1, -0.5))
                        .setKl(0.0001).setKq(0.0001)
                        .setRadius(12) // Area light source
                        .setNarrowBeam(2)
        );

        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 20, 80))
                .setDirection(new Point(0, -4, -20), new Vector(0, 1, 0))
                .setVpSize(100, 100)
                .setVpDistance(80)
                .setResolution(400, 400);

        // Compare hard vs soft shadows
        Camera cameraHard = cameraBuilder
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(false)
                        .build())
                .build();

        cameraHard.renderImage()
                .writeToImage("SpotLight-HardShadows");

        Camera cameraSoft = cameraBuilder
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(true)
                        .setSoftShadowSamples(100) // ~10x10 grid
                        .build())
                .build();

        cameraSoft.renderImage()
                .writeToImage("SpotLight-SoftShadows");
    }
}