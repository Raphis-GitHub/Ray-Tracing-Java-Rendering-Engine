package renderer;

import geometries.Plane;
import geometries.Sphere;
import lighting.AmbientLight;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Debug test for Soft Shadows - step by step verification
 */
public class SoftShadowsDebugTest {

    /**
     * First ensure hard shadows work with simple scene
     */
    @Test
    void testHardShadowsFirst() {
        Scene scene = new Scene("Hard Shadow Debug");
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20))); // Very low ambient

        // Simple sphere and plane
        scene.geometries.add(
                new Sphere(new Point(0, 0, -20), 10)
                        .setEmission(new Color(100, 100, 100)) // Medium grey sphere
                        .setMaterial(new Material().setKd(0.8).setKs(0.2).setShininess(50)),

                new Plane(new Point(0, -15, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(200, 200, 200)) // Light grey plane
                        .setMaterial(new Material().setKd(0.9))
        );

        // Single strong light to cast clear shadow
        scene.lights.add(
                new PointLight(new Color(255, 255, 255), new Point(-30, 30, 0))
                        .setKl(0.0001).setKq(0.0001)
                        .setRadius(0) // NO radius = hard shadows
        );

        Camera camera = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(40, 20, 50))
                .setDirection(new Point(0, -5, -20), new Vector(0, 1, 0))
                .setVpSize(60, 60)
                .setVpDistance(50)
                .setResolution(300, 300)
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(false) // Hard shadows only
                        .build())
                .build();

        camera.renderImage()
                .writeToImage("Debug-HardShadows");
    }

    /**
     * Test with light radius but soft shadows OFF (should still be hard)
     */
    @Test
    void testLightRadiusButSoftShadowsOff() {
        Scene scene = new Scene("Light Radius But Soft Off");
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        scene.geometries.add(
                new Sphere(new Point(0, 0, -20), 10)
                        .setEmission(new Color(100, 100, 100))
                        .setMaterial(new Material().setKd(0.8).setKs(0.2).setShininess(50)),

                new Plane(new Point(0, -15, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(200, 200, 200))
                        .setMaterial(new Material().setKd(0.9))
        );

        scene.lights.add(
                new PointLight(new Color(255, 255, 255), new Point(-30, 30, 0))
                        .setKl(0.0001).setKq(0.0001)
                        .setRadius(15) // HAS radius but soft shadows OFF
        );

        Camera camera = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(40, 20, 50))
                .setDirection(new Point(0, -5, -20), new Vector(0, 1, 0))
                .setVpSize(60, 60)
                .setVpDistance(50)
                .setResolution(300, 300)
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(false) // Soft shadows OFF despite radius
                        .build())
                .build();

        camera.renderImage()
                .writeToImage("Debug-RadiusButOff");
    }

    /**
     * Test with both radius and soft shadows ON
     */
    @Test
    void testSoftShadowsOn() {
        Scene scene = new Scene("Soft Shadows On");
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        scene.geometries.add(
                new Sphere(new Point(0, 0, -20), 10)
                        .setEmission(new Color(100, 100, 100))
                        .setMaterial(new Material().setKd(0.8).setKs(0.2).setShininess(50)),

                new Plane(new Point(0, -15, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(200, 200, 200))
                        .setMaterial(new Material().setKd(0.9))
        );

        scene.lights.add(
                new PointLight(new Color(255, 255, 255), new Point(-30, 30, 0))
                        .setKl(0.0001).setKq(0.0001)
                        .setRadius(15) // HAS radius
        );

        Camera camera = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(40, 20, 50))
                .setDirection(new Point(0, -5, -20), new Vector(0, 1, 0))
                .setVpSize(60, 60)
                .setVpDistance(50)
                .setResolution(300, 300)
                .setBlackboard(Blackboard.getBuilder()
                        .setSoftShadows(true) // Soft shadows ON
                        .setSoftShadowSamples(300) // 9x9 samples
                        .build())
                .build();

        camera.renderImage()
                .writeToImage("Debug-SoftShadowsOn");
    }
}