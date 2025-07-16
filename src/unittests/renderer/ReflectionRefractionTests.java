package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 *
 * @author Dan Zilberstein
 */
class ReflectionRefractionTests {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    ReflectionRefractionTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Scene for the tests
     */
    private final Scene scene = new Scene("Test scene");
    /**
     * Camera builder for the tests with triangles
     */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /**
     * Produce a picture of a sphere lighted by a spot light
     */
    @Test
    void twoSpheres() {
        scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50d).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKd(0.4).setKs(0.3).setShininess(100).setKt(0.3)), //
                new Sphere(new Point(0, 0, -50), 25d).setEmission(new Color(RED)) //
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100))); //
        scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004).setKq(0.0000006));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(150, 150) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("refractionTwoSpheres");
    }

    /**
     * Produce a picture of a sphere lighted by a spot light
     */
    @Test
    void twoSpheresOnMirrors() {
        scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400d).setEmission(new Color(0, 50, 100)) //
                        .setMaterial(new Material().setKd(0.25).setKs(0.25).setShininess(20) //
                                .setKt(new Double3(0.5, 0, 0))), //
                new Sphere(new Point(-950, -900, -1000), 200d).setEmission(new Color(100, 50, 20)) //
                        .setMaterial(new Material().setKd(0.25).setKs(0.25).setShininess(20)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKr(1)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKr(new Double3(0.5, 0, 0.4))));
        scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
        scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001).setKq(0.000005));

        cameraBuilder
                .setLocation(new Point(0, 0, 10000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(10000).setVpSize(2500, 2500) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("reflectionTwoSpheresMirrored");
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    void trianglesTransparentSphere() {
        scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),
                new Sphere(new Point(60, 50, -50), 30d).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKd(0.2).setKs(0.2).setShininess(30).setKt(0.6)));
        scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(200, 200) //
                .setResolution(600, 600) //
                .build() //
                .renderImage() //
                .writeToImage("refractionShadow");
    }

    /**
     * Test demonstrating reflectiveness, shadows, and transparency with multiple objects
     * on a colored floor, viewed from an angled camera position above
     */
    @Test
    void combinedEffectsDemo() {
        Scene scene = new Scene("Combined Effects Demo")
                .setBackground(new Color(10, 15, 25))
                .setAmbientLight(new AmbientLight(new Color(15, 15, 20)));

        // Camera positioned at an angle from above
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(-100, 80, 150))
                .setDirection(new Point(0, -20, -50), new Vector(0, 1, 0))
                .setVpSize(200, 200)
                .setVpDistance(200)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // Materials for different effects
        Material floorMaterial = new Material()
                .setKd(0.6).setKs(0.3).setShininess(50)
                .setKr(0.3); // Partially reflective floor

        Material reflectiveMaterial = new Material()
                .setKd(0.3).setKs(0.8).setShininess(100)
                .setKr(0.6); // Highly reflective sphere

        Material transparentMaterial = new Material()
                .setKd(0.2).setKs(0.2).setShininess(30)
                .setKt(0.7); // Transparent cylinder (not closed, so safe)

        Material opaqueMaterial = new Material()
                .setKd(0.8).setKs(0.3).setShininess(40); // Opaque triangle for shadows

        // COLORED FLOOR - Two triangles forming a reflective surface
        scene.geometries.add(
                new Triangle(new Point(-150, -50, -150), new Point(150, -50, -150), new Point(150, -50, 100))
                        .setEmission(new Color(20, 30, 20)) // Green-tinted floor
                        .setMaterial(floorMaterial),
                new Triangle(new Point(-150, -50, -150), new Point(150, -50, 100), new Point(-150, -50, 100))
                        .setEmission(new Color(20, 30, 20))
                        .setMaterial(floorMaterial)
        );

        // REFLECTIVE SPHERE - Shows reflections of other objects and environment
        scene.geometries.add(
                new Sphere(new Point(-40, -10, -30), 25)
                        .setEmission(new Color(10, 10, 30)) // Blue-tinted sphere
                        .setMaterial(reflectiveMaterial)
        );

        // OPAQUE TRIANGLE - Casts clear shadows on floor and other objects
        scene.geometries.add(
                new Triangle(new Point(30, 20, -20), new Point(70, 20, -40), new Point(50, 60, -30))
                        .setEmission(new Color(40, 20, 10)) // Orange triangle
                        .setMaterial(opaqueMaterial)
        );

        // TRANSPARENT CYLINDER - Shows refraction and allows light through
        // Note: Cylinder is open-ended, so it's safe to make transparent
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(20, -20, 20)), 15, 60)
                        .setEmission(new Color(10, 30, 10)) // Green-tinted cylinder
                        .setMaterial(transparentMaterial)
        );

        // LIGHTING SETUP
        // Main light source creating dramatic shadows
        scene.lights.add(
                new SpotLight(new Color(400, 350, 300),
                        new Point(-80, 100, 50),
                        new Vector(1, -1.2, -0.8))
                        .setKl(0.0001).setKq(0.000008)
        );

        // Fill light to soften shadows and add ambient illumination
        scene.lights.add(
                new PointLight(new Color(150, 180, 200),
                        new Point(60, 60, 80))
                        .setKl(0.0005).setKq(0.00003)
        );

        // Subtle rim light to enhance the transparent cylinder
        scene.lights.add(
                new DirectionalLight(new Vector(0.3, -0.2, -1),
                        new Color(80, 100, 120))
        );

        camera.renderImage().writeToImage("combinedEffectsDemo");
    }
}
