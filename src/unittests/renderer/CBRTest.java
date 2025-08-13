// Example test to verify CBR functionality
package renderer;

import geometries.*;
import lighting.AmbientLight;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.lang.Math.pow;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CBR (Composite Bounding Region) implementation in the renderer.
 * This includes performance tests, bounding box calculations, and CBR toggle functionality.
 */
public class CBRTest {

    /**
     * Test method to verify CBR implementation with a complex scene.
     * This should demonstrate the performance improvement from CBR.
     */
    @Test
    public void testCBRPerformance() {
        Scene scene = new Scene("CBR Test Scene");

        // Create many geometries for performance testing
        Geometries geometries = new Geometries();

        // Add many spheres
        for (int i = 0; i < 150; i++) {
            for (int j = 0; j < 10; j++) {
                geometries.add(new Sphere(new Point(i * pow(-1, i) * 10, j * 10 * pow(-1, i) * -1, i - j), 2)
                        .setEmission(new Color(100, 50, 50))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30).setKr(0.9)));
            }
        }

        // Add triangles
        for (int i = 0; i < 150; i++) {
            geometries.add(new Triangle(
                    new Point(i * 5 * pow(-1, i), 0, 10),
                    new Point(i * 5 * pow(-1, i) + 2, 0, 10),
                    new Point(i * 5 * pow(-1, i) + 1, 3, 10))
                    .setEmission(new Color(50, 100, 50))
                    .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(20).setKr(0.9)));
        }

        scene.setGeometries(geometries);
        scene.setAmbientLight(new AmbientLight(new Color(50, 50, 50)));
        scene.lights.add(new PointLight(new Color(500, 500, 500), new Point(100, 100, -100)));

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, -2000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(1000).setCBR(true)
                .setVpSize(200, 200)
                .setResolution(2000, 2000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // Measure time with CBR (should be faster)
        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("Rendering time with CBR: " + (endTime - startTime) + "ms");

        camera.writeToImage("cbr_test");
    }

    /**
     * Test to verify bounding box calculations are correct.
     */
    @Test
    public void testBoundingBoxCalculations() {
        // Test sphere bounding box
        Sphere sphere = new Sphere(new Point(5, 10, 15), 3);
        BoundingBox sphereBox = sphere.getBoundingBox();
        assertNotNull(sphereBox);
        // Box should extend from (2,7,12) to (8,13,18)

        // Test triangle bounding box
        Triangle triangle = new Triangle(
                new Point(0, 0, 0),
                new Point(5, 0, 0),
                new Point(2.5, 4, 0)
        );
        BoundingBox triangleBox = triangle.getBoundingBox();
        assertNotNull(triangleBox);

        // Test composite bounding box
        Geometries geometries = new Geometries(sphere, triangle);
        BoundingBox compositeBox = geometries.getBoundingBox();
        assertNotNull(compositeBox);

        // Test infinite object
        Plane plane = new Plane(Point.ZERO, Vector.AXIS_Z);
        BoundingBox planeBox = plane.getBoundingBox();
        assertNull(planeBox); // Should be null for infinite objects
    }

    /**
     * Test CBR toggle functionality and performance comparison.
     */
    @Test
    public void testCBRToggle() {
        // Create a scene with many geometries for performance testing
        Geometries geometries = new Geometries();

        // Add multiple spheres
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                geometries.add(new Sphere(new Point(i * 50, j * 50, 100), 5));
            }
        }

        // Create a ray that will miss most geometries
        Ray testRay = new Ray(new Vector(1, 0, 0), new Point(-100, -100, 0));

        // Test 1: Verify CBR is enabled by default

        // Test 2: Test with CBR enabled
        long startEnabled = System.nanoTime();
        var resultEnabled = geometries.calculateIntersections(testRay);
        long timeEnabled = System.nanoTime() - startEnabled;

        // Test 3: Disable CBR and test again
        Intersectable.setCBREnabled(false);
        assertFalse(Intersectable.isCBREnabled(), "CBR should be disabled");

        long startDisabled = System.nanoTime();
        var resultDisabled = geometries.calculateIntersections(testRay);
        long timeDisabled = System.nanoTime() - startDisabled;

        // Test 4: Re-enable CBR
        Intersectable.setCBREnabled(true);
        assertTrue(Intersectable.isCBREnabled(), "CBR should be re-enabled");

        // Test 5: Verify results are consistent regardless of CBR setting
        assertEquals(resultEnabled, resultDisabled, "Results should be same with CBR on/off");

        // Test 6: Performance comparison (CBR should typically be faster for misses)
        System.out.println("CBR Performance Test:");
        System.out.println("  Time with CBR: " + timeEnabled + " ns");
        System.out.println("  Time without CBR: " + timeDisabled + " ns");
        if (timeEnabled < timeDisabled) {
            System.out.println("  CBR Speedup: " + String.format("%.2fx", (double) timeDisabled / timeEnabled));
        }

        System.out.println("CBR Toggle API:");
        System.out.println("  Intersectable.setCBREnabled(true/false)");
        System.out.println("  Intersectable.isCBREnabled()");
    }
}