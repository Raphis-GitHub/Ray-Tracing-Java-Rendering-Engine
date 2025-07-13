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

    @Test
    void testStage7Effects() {
        Scene scene = new Scene("Stage 7 Effects Demo")
                .setBackground(new Color(20, 30, 40))
                .setAmbientLight(new AmbientLight(new Color(25, 25, 25)));
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(1000)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

// Materials for different effects
        Material reflective = new Material()
                .setKd(0.2).setKs(0.8).setShininess(100)
                .setKr(0.8); // Reflective only

        Material transparent = new Material()
                .setKd(0.1).setKs(0.3).setShininess(50)
                .setKt(0.9); // Transparent only

        Material semiTransparent = new Material()
                .setKd(0.3).setKs(0.2).setShininess(30)
                .setKt(0.5); // For partial shadows

        Material opaque = new Material()
                .setKd(0.7).setKs(0.3).setShininess(20);

// 4 OBJECTS DEMONSTRATING ALL EFFECTS:

// 1. REFLECTIVE SPHERE (shows reflection)
        scene.geometries.add(
                new Sphere(new Point(-50, 0, -150), 30)
                        .setEmission(new Color(30, 30, 60))
                        .setMaterial(reflective)
        );

// 2. TRANSPARENT CYLINDER (shows refraction/transparency)
        scene.geometries.add(
                new Cylinder(new Ray(Vector.AXIS_Y, new Point(50, -40, -120)), 20, 80)
                        .setEmission(new Color(10, 40, 10))
                        .setMaterial(transparent)
        );

// 3. SEMI-TRANSPARENT TRIANGLE (shows partial shadows)
        scene.geometries.add(
                new Triangle(new Point(-20, 60, -100), new Point(20, 60, -100), new Point(0, 100, -120))
                        .setEmission(new Color(60, 20, 20))
                        .setMaterial(semiTransparent)
        );

// 4. OPAQUE PLANE (receives shadows and reflections)
        scene.geometries.add(
                new Triangle(new Point(-150, -50, -200), new Point(150, -50, -200), new Point(150, -50, 50))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(opaque),
                new Triangle(new Point(-150, -50, -200), new Point(150, -50, 50), new Point(-150, -50, 50))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(opaque)
        );

// LIGHTING
        scene.lights.add(new SpotLight(new Color(800, 600, 400),
                new Point(-80, 80, 100),
                new Vector(1, -1, -2))
                .setKl(0.0001).setKq(0.000005));

        scene.lights.add(new PointLight(new Color(300, 400, 500),
                new Point(80, 50, 0))
                .setKl(0.0002).setKq(0.00001));

        camera.renderImage().writeToImage("stage7EffectsDemo");
    }
}
