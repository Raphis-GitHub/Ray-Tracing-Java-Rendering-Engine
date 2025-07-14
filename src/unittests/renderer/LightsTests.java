package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.BLUE;

/**
 * Test rendering a basic image
 *
 * @author Dan Zilberstein
 */
class LightsTests {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    LightsTests() { /* to satisfy JavaDoc generator */ }

    /**
     * First scene for some of tests
     */
    private final Scene scene1 = new Scene("Test scene");
    /**
     * Second scene for some of tests
     */
    private final Scene scene2 = new Scene("Test scene")
            .setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

    /**
     * First camera builder for some of tests
     */
    private final Camera.Builder camera1 = Camera.getBuilder()                                          //
            .setRayTracer(scene1, RayTracerType.SIMPLE)                                                                      //
            .setLocation(new Point(0, 0, 1000))                                                                              //
            .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
            .setVpSize(150, 150).setVpDistance(1000);

    /**
     * Second camera builder for some of tests
     */
    private final Camera.Builder camera2 = Camera.getBuilder()                                          //
            .setRayTracer(scene2, RayTracerType.SIMPLE)                                                                      //
            .setLocation(new Point(0, 0, 1000))                                                                              //
            .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
            .setVpSize(200, 200).setVpDistance(1000);

    /**
     * Shininess value for most of the geometries in the tests
     */
    private static final int SHININESS = 301;
    /**
     * Diffusion attenuation factor for some of the geometries in the tests
     */
    private static final double KD = 0.5;
    /**
     * Diffusion attenuation factor for some of the geometries in the tests
     */
    private static final Double3 KD3 = new Double3(0.2, 0.6, 0.4);

    /**
     * Specular attenuation factor for some of the geometries in the tests
     */
    private static final double KS = 0.5;
    /**
     * Specular attenuation factor for some of the geometries in the tests
     */
    private static final Double3 KS3 = new Double3(0.2, 0.4, 0.3);

    /**
     * Material for some of the geometries in the tests
     */
    private final Material material = new Material().setKd(KD3).setKs(KS3).setShininess(SHININESS);
    /**
     * Light color for tests with triangles
     */
    private final Color trianglesLightColor = new Color(800, 500, 250);
    /**
     * Light color for tests with sphere
     */
    private final Color sphereLightColor = new Color(800, 500, 0);
    /**
     * Color of the sphere
     */
    private final Color sphereColor = new Color(BLUE).reduce(2);

    /**
     * Center of the sphere
     */
    private final Point sphereCenter = new Point(0, 0, -50);
    /**
     * Radius of the sphere
     */
    private static final double SPHERE_RADIUS = 50d;

    /**
     * The triangles' vertices for the tests with triangles
     */
    private final Point[] vertices =
            {
                    // the shared left-bottom:
                    new Point(-110, -110, -150),
                    // the shared right-top:
                    new Point(95, 100, -150),
                    // the right-bottom
                    new Point(110, -110, -150),
                    // the left-top
                    new Point(-75, 78, 100)
            };
    /**
     * Position of the light in tests with sphere
     */
    private final Point sphereLightPosition = new Point(-50, -50, 25);
    /**
     * Light direction (directional and spot) in tests with sphere
     */
    private final Vector sphereLightDirection = new Vector(1, 1, -0.5);
    /**
     * Position of the light in tests with triangles
     */
    private final Point trianglesLightPosition = new Point(30, 10, -100);
    /**
     * Light direction (directional and spot) in tests with triangles
     */
    private final Vector trianglesLightDirection = new Vector(-2, -2, -2);

    /**
     * The sphere in appropriate tests
     */
    private final Geometry sphere = new Sphere(sphereCenter, SPHERE_RADIUS)
            .setEmission(sphereColor).setMaterial(new Material().setKd(KD).setKs(KS).setShininess(SHININESS));
    /**
     * The first triangle in appropriate tests
     */
    private final Geometry triangle1 = new Triangle(vertices[0], vertices[1], vertices[2])
            .setMaterial(material);
    /**
     * The first triangle in appropriate tests
     */
    private final Geometry triangle2 = new Triangle(vertices[0], vertices[1], vertices[3])
            .setMaterial(material);

    /**
     * Produce a picture of a sphere lighted by a directional light
     */
    @Test
    void sphereDirectional() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new DirectionalLight(sphereLightDirection, sphereLightColor));

        camera1 //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightSphereDirectional");
    }

    /**
     * Produce a picture of a sphere lighted by a point light
     */
    @Test
    void spherePoint() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new PointLight(sphereLightColor, sphereLightPosition) //
                .setKl(0.001).setKq(0.0002));

        camera1 //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightSpherePoint");
    }

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    void sphereSpot() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection) //
                .setKl(0.001).setKq(0.0001));

        camera1 //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightSphereSpot");
    }

    /**
     * Produce a picture of two triangles lighted by a directional light
     */
    @Test
    void trianglesDirectional() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new DirectionalLight(trianglesLightDirection, trianglesLightColor));

        camera2.setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightTrianglesDirectional");
    }

    /**
     * Produce a picture of two triangles lighted by a point light
     */
    @Test
    void trianglesPoint() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new PointLight(trianglesLightColor, trianglesLightPosition) //
                .setKl(0.001).setKq(0.0002));

        camera2.setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightTrianglesPoint");
    }

    /**
     * Produce a picture of two triangles lighted by a spotlight
     */
    @Test
    void trianglesSpot() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
                .setKl(0.001).setKq(0.0001));

        camera2.setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightTrianglesSpot");
    }

    /**
     * Produce a picture of a sphere lighted by a narrow spotlight
     */
    @Test
    void sphereSpotSharp() {
        scene1.geometries.add(sphere);
        scene1.lights
                .add(new SpotLight(sphereLightColor, sphereLightPosition, new Vector(1, 1, -0.5)) //
                        .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

        camera1.setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightSphereSpotSharp");
    }

    /**
     * Produce a picture of two triangles lighted by a narrow spotlight
     */
    @Test
    void trianglesSpotSharp() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
                .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

        camera2.setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightTrianglesSpotSharp");
    }

    /**
     * Epic Light Show Test - Creates a dramatic scene with multiple spheres,
     * triangular backdrop, and colorful lights from different directions
     */
    @Test
    @Disabled
    void epicLightShowTest() {
        Scene scene = new Scene("Epic Light Show")
                .setBackground(new Color(5, 5, 15)) // Dark blue background
                .setAmbientLight(new AmbientLight(new Color(20, 20, 30))); // Subtle ambient

        // Camera positioned for dramatic angle
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(50, 50, 100))
                .setDirection(new Point(0, 0, -50), new Vector(0, 1, 0))
                .setVpSize(200, 200)
                .setVpDistance(150)
                .setResolution(500, 500)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // Materials for different effects
        Material chrome = new Material()
                .setKd(0.1).setKs(0.9).setShininess(100); // Highly reflective

        Material goldMetal = new Material()
                .setKd(0.3).setKs(0.8).setShininess(80);

        Material plasticMatte = new Material()
                .setKd(0.8).setKs(0.2).setShininess(20);

        Material crystal = new Material()
                .setKd(0.1).setKs(0.95).setShininess(200); // Super shiny

        Material roughMetal = new Material()
                .setKd(0.6).setKs(0.4).setShininess(40);

        // Center stage - Large crystal sphere
        scene.geometries.add(
                new Sphere(new Point(0, 0, -50), 25)
                        .setEmission(new Color(10, 10, 20)) // Slight blue glow
                        .setMaterial(crystal)
        );

        // Orbiting spheres around the center
        double radius = 60;
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3; // 60 degrees apart
            double x = radius * Math.cos(angle);
            double z = -50 + radius * Math.sin(angle);

            Material mat;
            Color emission;
            double sphereRadius;

            switch (i % 5) {
                case 0:
                    mat = chrome;
                    emission = new Color(15, 5, 5); // Red tint
                    sphereRadius = 12;
                    break;
                case 1:
                    mat = goldMetal;
                    emission = new Color(20, 15, 5); // Gold tint
                    sphereRadius = 15;
                    break;
                case 2:
                    mat = plasticMatte;
                    emission = new Color(5, 15, 5); // Green tint
                    sphereRadius = 10;
                    break;
                case 3:
                    mat = crystal;
                    emission = new Color(5, 5, 15); // Blue tint
                    sphereRadius = 13;
                    break;
                default:
                    mat = roughMetal;
                    emission = new Color(15, 10, 15); // Purple tint
                    sphereRadius = 11;
            }

            scene.geometries.add(
                    new Sphere(new Point(x, 0, z), sphereRadius)
                            .setEmission(emission)
                            .setMaterial(mat),
                    new Tube(new Ray(new Vector(0, 0, 1), new Point(1, 0, 5)), sphereRadius / 2)
                            .setEmission(new Color(5, 5, 5))
                            .setMaterial(mat), // Decorative tube connecting to center)
                    new Cylinder(new Ray(new Vector(0, 1, 0), new Point(1, 0, 5)), sphereRadius / 3, 20)
                            .setEmission(new Color(5, 100, 5))
                            .setMaterial(crystal) // Decorative tube connecting to center)
            );
        }

        // Floating spheres at different heights
        scene.geometries.add(
                new Sphere(new Point(-40, 30, -80), 8)
                        .setEmission(new Color(25, 5, 25))
                        .setMaterial(chrome),

                new Sphere(new Point(40, -25, -30), 12)
                        .setEmission(new Color(5, 25, 5))
                        .setMaterial(goldMetal),

                new Sphere(new Point(0, 40, -100), 10)
                        .setEmission(new Color(5, 15, 25))
                        .setMaterial(crystal)
        );

        // Geometric backdrop - Triangular panels
        scene.geometries.add(
                // Back wall triangles
                new Triangle(new Point(-100, -60, -150), new Point(0, 60, -150), new Point(-100, 60, -150))
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(roughMetal),

                new Triangle(new Point(-100, -60, -150), new Point(0, 60, -150), new Point(0, -60, -150))
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(roughMetal),

                new Triangle(new Point(0, -60, -150), new Point(0, 60, -150), new Point(100, 60, -150))
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(roughMetal),

                new Triangle(new Point(0, -60, -150), new Point(100, 60, -150), new Point(100, -60, -150))
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(roughMetal),

                // Floor triangles
                new Triangle(new Point(-150, -60, -150), new Point(150, -60, -150), new Point(0, -60, 50))
                        .setEmission(new Color(3, 3, 8))
                        .setMaterial(plasticMatte)
        );

        // EPIC LIGHTING SETUP - Multiple colored lights from different angles

        // Primary key light - Warm white from upper left
        scene.lights.add(new SpotLight(new Color(400, 350, 300),
                new Point(-60, 80, 20),
                new Vector(1, -1, -1))
                .setKl(0.0001).setKq(0.000005));

        // Dramatic red light from the right
        scene.lights.add(new SpotLight(new Color(600, 100, 100),
                new Point(80, 20, 30),
                new Vector(-1, -0.2, -1))
                .setKl(0.0001).setKq(0.00001));

        // Cool blue light from below left
        scene.lights.add(new PointLight(new Color(100, 200, 500),
                new Point(-50, -40, 0))
                .setKl(0.0002).setKq(0.00001));

        // Green accent light from behind
        scene.lights.add(new DirectionalLight(new Vector(0.3, 0.5, -1),
                new Color(150, 400, 150)));

        // Purple rim light from upper right
        scene.lights.add(new SpotLight(new Color(300, 100, 400),
                new Point(70, 60, -20),
                new Vector(-1, -0.8, -0.5))
                .setKl(0.0001).setKq(0.000008));

        // Cyan fill light from front left
        scene.lights.add(new PointLight(new Color(100, 400, 400),
                new Point(-30, 20, 80))
                .setKl(0.0003).setKq(0.00002));

        // Render the epic scene
        camera.renderImage()
                .writeToImage("epicLightShow");
    }

    /**
     * Tests rendering a sphere with multiple light sources.
     */
    @Test
    void testMultipleLightsSphere() {
        scene1.geometries.add(new Sphere(sphereCenter, SPHERE_RADIUS).setEmission(sphereColor).setMaterial(material));

        scene1.lights.add(new PointLight(sphereLightColor, new Point(-50, -50, 25)).setKl(0.0005).setKq(0.0005));
        scene1.lights.add(new SpotLight(sphereLightColor, new Point(-50, -50, 25), sphereLightDirection).setKl(0.0001).setKq(0.0001));
        scene1.lights.add(new DirectionalLight(new Vector(1, 1, -0.5), sphereLightColor));

        camera1 //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightSphereMultipleLights");
    }

    /**
     * Tests rendering two triangles with multiple light sources.
     */
    @Test
    void testMultipleLightsTriangles() {
        scene2.geometries.add(
                new Triangle(vertices[0], vertices[1], vertices[2]).setEmission(new Color(0, 0, 100)).setMaterial(material),
                new Triangle(vertices[0], vertices[1], vertices[3]).setEmission(new Color(100, 0, 0)).setMaterial(material)
        );

        scene2.lights.add(new PointLight(trianglesLightColor, new Point(30, 10, -100)).setKl(0.0005).setKq(0.0005));
        scene2.lights.add(new SpotLight(trianglesLightColor, new Point(30, 10, -100), new Vector(-2, -2, -1)).setKl(0.0001).setKq(0.0001));
        scene2.lights.add(new DirectionalLight(new Vector(1, 1, -0.5), trianglesLightColor));

        camera2 //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("lightTrianglesMultipleLights");
    }

    /**
     * Bonus test: Produce a picture of a sphere lighted by a narrow beam spotlight
     */
    @Test
    void sphereSpotNarrowBeam() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection)
                .setKl(0.001).setKq(0.0001)
                .setNarrowBeam(10)); // Narrow beam factor

        camera1
                .setResolution(500, 500)
                .build()
                .renderImage()
                .writeToImage("lightSphereSpotNarrow");
    }

    /**
     * Bonus test: Produce a picture of two triangles lighted by a narrow beam spotlight
     */
    @Test
    void trianglesSpotNarrowBeam() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection)
                .setKl(0.001).setKq(0.0001)
                .setNarrowBeam(10)); // Narrow beam factor

        camera2.setResolution(500, 500)
                .build()
                .renderImage()
                .writeToImage("lightTrianglesSpotNarrow");
    }

    /**
     * Comparison test showing different narrow beam intensities
     */
    @Test
    void spotLightNarrowBeamComparison() {
        Scene scene = new Scene("Narrow Beam Comparison")
                .setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

        // Create multiple spheres to show different beam effects
        scene.geometries.add(
                // Left sphere - no narrow beam (factor = 1)
                new Sphere(new Point(-60, 0, -100), 25)
                        .setEmission(new Color(50, 50, 100))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100)),

                // Center sphere - moderate narrow beam
                new Sphere(new Point(0, 0, -100), 25)
                        .setEmission(new Color(50, 100, 50))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100)),

                // Right sphere - very narrow beam
                new Sphere(new Point(60, 0, -100), 25)
                        .setEmission(new Color(100, 50, 50))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100))
        );

        // Three spotlights with different narrow beam factors
        scene.lights.add(
                // Regular spotlight (no narrow beam)
                new SpotLight(new Color(400, 400, 400), new Point(-60, 50, 0), new Vector(0, -1, -2))
                        .setKl(0.001).setKq(0.0001));

        // Moderately focused beam
        scene.lights.add(new SpotLight(new Color(400, 400, 400), new Point(0, 50, 0), new Vector(0, -1, -2))
                .setKl(0.001).setKq(0.0001).setNarrowBeam(5));

        // Very focused beam
        scene.lights.add(new SpotLight(new Color(400, 400, 400), new Point(60, 50, 0), new Vector(0, -1, -2))
                .setKl(0.001).setKq(0.0001).setNarrowBeam(20));
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 30)));
        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(300, 200).setVpDistance(1000)
                .setResolution(800, 600)
                .build()
                .renderImage()
                .writeToImage("spotLightNarrowBeamComparison");
    }
}
