package renderer;// Add this test to your test package - shows DoF, AA, reflections, transparency

import geometries.Sphere;
import geometries.Triangle;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

public class testFastDemoScene {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    testFastDemoScene() { /* to satisfy JavaDoc generator */ }

    @Test
    void fastdemoscene() {
        Scene scene = new Scene("Fast Demo Scene")
                .setBackground(new Color(20, 30, 50))  // Dark blue background
                .setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

        // Materials for different effects
        Material mirror = new Material()
                .setKd(0.1).setKs(0.9).setShininess(100)
                .setKr(0.8);  // Highly reflective

        Material glass = new Material()
                .setKd(0.1).setKs(0.2).setShininess(30)
                .setKt(0.7);  // Transparent

        Material matte = new Material()
                .setKd(0.7).setKs(0.3).setShininess(40);  // Regular surface

        // DEPTH TEST: Objects at different distances (for DoF)

        // Near sphere (distance ~400) - will be blurry
        scene.geometries.add(
                new Sphere(new Point(-50, 20, -400), 20)
                        .setEmission(new Color(100, 50, 50))  // Red
                        .setMaterial(matte)
        );

        // Middle sphere (distance ~700) - will be IN FOCUS
        scene.geometries.add(
                new Sphere(new Point(0, 0, -700), 25)
                        .setEmission(new Color(30, 30, 80))   // Blue
                        .setMaterial(mirror)  // Reflective!
        );

        // Far sphere (distance ~1000) - will be blurry
        scene.geometries.add(
                new Sphere(new Point(40, -20, -1000), 30)
                        .setEmission(new Color(50, 100, 50))  // Green
                        .setMaterial(glass)   // Transparent!
        );

        // ALIASING TEST: Sharp triangle edges (for anti-aliasing test)
        scene.geometries.add(
                new Triangle(new Point(-80, -40, -600), new Point(-40, 40, -600), new Point(-120, 40, -600))
                        .setEmission(new Color(80, 80, 20))   // Yellow
                        .setMaterial(matte)
        );

        // REFLECTION TARGET: Something for the mirror sphere to reflect
        scene.geometries.add(
                new Sphere(new Point(80, 60, -500), 15)
                        .setEmission(new Color(120, 60, 120)) // Purple
                        .setMaterial(matte)
        );

        // Simple floor plane (optional - shows reflections)
        scene.geometries.add(
                new Triangle(new Point(-200, -60, -1200), new Point(200, -60, -1200), new Point(200, -60, -200))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKd(0.5).setKs(0.2).setShininess(20).setKr(0.3)),
                new Triangle(new Point(-200, -60, -1200), new Point(200, -60, -200), new Point(-200, -60, -200))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKd(0.5).setKs(0.2).setShininess(20).setKr(0.3))
        );

        // LIGHTING: Simple but effective
        scene.lights.add(
                new PointLight(new Color(400, 400, 400), new Point(-60, 80, -300))
                        .setKl(0.001).setKq(0.0001)
        );

        scene.lights.add(
                new DirectionalLight(new Vector(1, -1, -1), new Color(200, 200, 200))
        );

        // FAST SETTINGS: Good quality but renders quickly
        Blackboard settings = Blackboard.getBuilder()
                .setAntiAliasing(true)
                .setAntiAliasingSamples(9)          // 3x3 - good quality, fast
                .setUseJitteredSampling(true)       // Smoother edges
                .setDepthOfField(true)
                .setDepthOfFieldSamples(16)         // Good blur quality, not too slow
                .build();

        // CAMERA: Positioned to see all objects clearly
        Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))                    // Camera position
                .setDirection(new Point(0, 0, -600), Vector.AXIS_Y)    // Look toward scene center
                .setVpSize(200, 200)                                   // View plane size
                .setVpDistance(1000)                                   // Distance to view plane
                .setResolution(400, 400)                               // Image resolution (fast)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(settings)
                .setFocusPointDistance(700)                            // Focus on middle sphere (blue mirror)
                .setAperture(8.0)                                      // Medium blur strength
                .build()
                .renderImage()
                .writeToImage("fastDemoScene");
    }

// COMPARISON TESTS: See the difference with/without effects

    @Test
    void fastDemoScene_NoEffects() {
        // Same scene, no AA or DoF for comparison
        Scene scene = createDemoScene();  // Use same scene creation

        Blackboard noEffects = Blackboard.getBuilder().build();  // Default = no effects

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Point(0, 0, -600), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(1000)
                .setResolution(400, 400)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(noEffects)
                .build()
                .renderImage()
                .writeToImage("fastDemoScene_NoEffects");
    }

    @Test
    void fastDemoScene_OnlyAA() {
        // Same scene, only anti-aliasing
        Scene scene = createDemoScene();

        Blackboard onlyAA = Blackboard.getBuilder()
                .setAntiAliasing(true)
                .setAntiAliasingSamples(9)
                .build();

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Point(0, 0, -600), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(1000)
                .setResolution(400, 400)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(onlyAA)
                .build()
                .renderImage()
                .writeToImage("fastDemoScene_OnlyAA");
    }

    @Test
    void fastDemoScene_OnlyDoF() {
        // Same scene, only depth of field
        Scene scene = createDemoScene();

        Blackboard onlyDoF = Blackboard.getBuilder()
                .setDepthOfField(true)
                .setDepthOfFieldSamples(16)
                .build();

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Point(0, 0, -600), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(1000)
                .setResolution(400, 400)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(onlyDoF)
                .setFocusPointDistance(700)
                .setAperture(30)
                .build()
                .renderImage()
                .writeToImage("fastDemoScene_OnlyDoF");
    }

    // Helper method to create the same scene for comparison tests
    private Scene createDemoScene() {
        Scene scene = new Scene("Demo Scene")
                .setBackground(new Color(20, 30, 50))
                .setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

        Material mirror = new Material().setKd(0.1).setKs(0.9).setShininess(100).setKr(0.8);
        Material glass = new Material().setKd(0.1).setKs(0.2).setShininess(30).setKt(0.7);
        Material matte = new Material().setKd(0.7).setKs(0.3).setShininess(40);

        scene.geometries.add(
                // Near sphere - red, blurry
                new Sphere(new Point(-50, 20, -400), 20)
                        .setEmission(new Color(100, 50, 50)).setMaterial(matte),

                // Middle sphere - blue mirror, in focus
                new Sphere(new Point(0, 0, -700), 25)
                        .setEmission(new Color(30, 30, 80)).setMaterial(mirror),

                // Far sphere - green glass, blurry
                new Sphere(new Point(40, -20, -1000), 30)
                        .setEmission(new Color(50, 100, 50)).setMaterial(glass),

                // Triangle for aliasing test
                new Triangle(new Point(-80, -40, -600), new Point(-40, 40, -600), new Point(-120, 40, -600))
                        .setEmission(new Color(80, 80, 20)).setMaterial(matte),

                // Reflection target
                new Sphere(new Point(80, 60, -500), 15)
                        .setEmission(new Color(120, 60, 120)).setMaterial(matte),

                // Floor
                new Triangle(new Point(-200, -60, -1200), new Point(200, -60, -1200), new Point(200, -60, -200))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKd(0.5).setKs(0.2).setShininess(20).setKr(0.3)),
                new Triangle(new Point(-200, -60, -1200), new Point(200, -60, -200), new Point(-200, -60, -200))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKd(0.5).setKs(0.2).setShininess(20).setKr(0.3))
        );

        scene.lights.add(
                new PointLight(new Color(400, 400, 400), new Point(-60, 80, -300))
                        .setKl(0.001).setKq(0.0001)
        );
        scene.lights.add(
                new DirectionalLight(new Vector(1, -1, -1), new Color(200, 200, 200))
        );

        return scene;
    }
}