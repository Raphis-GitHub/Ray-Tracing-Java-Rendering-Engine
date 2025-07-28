package renderer;// Add this test to your test package - shows DoF, AA, reflections, transparency

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Tests for fast demo scene with various effects
 */
@Disabled
public class TestFastDemoScene {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    TestFastDemoScene() { /* to satisfy JavaDoc generator */ }

    /**
     * Test demonstrating a fast demo scene with various effects
     */
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

    /**
     * Test demonstrating the same scene without any effects
     */
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

    /**
     * Test demonstrating anti-aliasing only, no depth of field
     */
    @Test
    void fastDemoScene_OnlyAA() {
        // Same scene, only anti-aliasing
        Scene scene = createDemoScene();

        Blackboard onlyAA = Blackboard.getBuilder()
                .setAntiAliasing(true)
                .setAntiAliasingSamples(23 * 23)
                .build();

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Point(0, 0, -600), Vector.AXIS_Y)
                .setVpSize(200, 200)
                .setVpDistance(1000)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(onlyAA)
                .build()
                .renderImage()
                .writeToImage("fastDemoScene_OnlyAA");
    }

    /**
     * Test demonstrating depth of field only, no anti-aliasing
     */
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
                .setAperture(10)
                .build()
                .renderImage()
                .writeToImage("fastDemoScene_OnlyDoF");
    }

    // Helper method to create the same scene for comparison tests

    /**
     * Creates the demo scene with all objects and lights
     *
     * @return the created scene
     */
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

    /**
     * Test demonstrating depth of field with a tube stretching into the distance
     */
    @Test
    void tubeDepthOfFieldDemo() {
        Scene scene = new Scene("Tube DoF Demo")
                .setBackground(new Color(20, 30, 40))  // Dark blue-gray background
                .setAmbientLight(new AmbientLight(new Color(15, 20, 25)));

        // Materials
        Material reflectiveTubeMaterial = new Material()
                .setKd(0.6).setKs(0.4).setShininess(80)
                .setKr(0.3);  // Slightly reflective tube

        Material planeMaterial = new Material()
                .setKd(0.7).setKs(0.2).setShininess(20)
                .setKr(0.1);  // Slightly reflective plane

        // INFINITE TUBE stretching from left into the distance
        // The tube goes from near the camera far into the scene
        Ray tubeAxis = new Ray(new Vector(0, 0, -1), new Point(-60, 0, -100));
        scene.geometries.add(
                new Tube(tubeAxis, 15)  // Radius of 15, infinite length
                        .setEmission(new Color(60, 80, 100))  // Blue-gray tube
                        .setMaterial(reflectiveTubeMaterial)
        );

        // LIGHT BLUE FLOOR PLANE - infinite horizontal plane
        scene.geometries.add(
                new Plane(new Point(0, -40, 0), new Vector(0, 1, 0))  // Horizontal plane at y = -40
                        .setEmission(new Color(120, 160, 200))  // Light blue
                        .setMaterial(planeMaterial)
        );

        // LIGHT BLUE BACKGROUND WALL - vertical plane far in the distance
        scene.geometries.add(
                new Plane(new Point(0, 0, -800), new Vector(0, 0, 1))  // Vertical plane at z = -800
                        .setEmission(new Color(140, 180, 220))  // Lighter blue
                        .setMaterial(planeMaterial)
        );

        // Add some reference objects at different distances to show DoF effect

        // Near sphere (will be blurry) - close to camera
        scene.geometries.add(
                new Sphere(new Point(40, 20, -200), 12)
                        .setEmission(new Color(200, 100, 100))  // Red
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50))
        );

        // Focus sphere (will be sharp) - at focus distance
        scene.geometries.add(
                new Sphere(new Point(20, -10, -400), 15)
                        .setEmission(new Color(100, 200, 100))  // Green
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50))
        );

        // Far sphere (will be blurry) - far from camera
        scene.geometries.add(
                new Sphere(new Point(-20, 30, -600), 18)
                        .setEmission(new Color(100, 100, 200))  // Blue
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50))
        );

        // LIGHTING - Multiple lights to illuminate the scene nicely

        // Main key light from upper right
        scene.lights.add(
                new SpotLight(new Color(300, 280, 250),
                        new Point(100, 80, -200),
                        new Vector(-1, -0.8, -0.5))
                        .setKl(0.0001).setKq(0.000005)
                        .setNarrowBeam(3)
        );

        // Fill light from left to illuminate the tube
        scene.lights.add(
                new PointLight(new Color(200, 220, 250),
                        new Point(-100, 50, -100))
                        .setKl(0.0005).setKq(0.00002)
        );

        // Rim light from behind to separate tube from background
        scene.lights.add(
                new DirectionalLight(new Vector(0.2, -0.3, 1),
                        new Color(150, 180, 200))
        );

        // Subtle colored accent light
        scene.lights.add(
                new PointLight(new Color(180, 200, 250),
                        new Point(50, 100, -300))
                        .setKl(0.001).setKq(0.00005)
        );

        // DEPTH OF FIELD SETTINGS
        Blackboard dofSettings = Blackboard.getBuilder()
                .setAntiAliasing(true)
                .setAntiAliasingSamples(64)          // 3x3 AA for smooth edges
                .setDepthOfField(false)
                .setDepthOfFieldSamples(32)         // 25 aperture samples for smooth blur
                .setUseJitteredSampling(true)       // Smoother sampling
                .build();

        // CAMERA SETUP - positioned to see the tube stretching into distance
        Camera.getBuilder()
                .setLocation(new Point(0, 0, 200))                    // Camera position
                .setDirection(new Point(-30, -10, -400), Vector.AXIS_Y)  // Look toward focus point
                .setVpSize(300, 200)                                  // View plane size
                .setVpDistance(200)                                   // Distance to view plane
                .setResolution(800, 600)                              // High resolution for detail
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(dofSettings)
                .setFocusPointDistance(400)                           // Focus on the green sphere
                .setAperture(12.0)                                    // Strong blur effect
                .build()
                .renderImage()
                .writeToImage("tubeDepthOfFieldDemo--optimaized- only AA64");
    }

    // Comparison without DoF to see the difference

    /**
     * Test the same tube scene without depth of field
     */
    @Test
    void tubeDepthOfFieldDemo_NoDoF() {
        Scene scene = new Scene("Tube No DoF")
                .setBackground(new Color(20, 30, 40))
                .setAmbientLight(new AmbientLight(new Color(15, 20, 25)));

        // Same scene setup but no DoF
        Material reflectiveTubeMaterial = new Material()
                .setKd(0.6).setKs(0.4).setShininess(80)
                .setKr(0.3);

        Material planeMaterial = new Material()
                .setKd(0.7).setKs(0.2).setShininess(20)
                .setKr(0.1);

        // Tube
        Ray tubeAxis = new Ray(new Vector(0, 0, -1), new Point(-60, 0, -100));
        scene.geometries.add(
                new Tube(tubeAxis, 15)
                        .setEmission(new Color(60, 80, 100))
                        .setMaterial(reflectiveTubeMaterial)
        );

        // Floor plane
        scene.geometries.add(
                new Plane(new Point(0, -40, 0), new Vector(0, 1, 0))  // Horizontal plane at y = -40
                        .setEmission(new Color(120, 160, 200))
                        .setMaterial(planeMaterial)
        );

        // Background wall plane
        scene.geometries.add(
                new Plane(new Point(0, 0, -800), new Vector(0, 0, 1))  // Vertical plane at z = -800
                        .setEmission(new Color(140, 180, 220))
                        .setMaterial(planeMaterial)
        );

        // Reference spheres
        scene.geometries.add(
                new Sphere(new Point(40, 20, -200), 12)
                        .setEmission(new Color(200, 100, 100))
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50)),
                new Sphere(new Point(20, -10, -400), 15)
                        .setEmission(new Color(100, 200, 100))
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50)),
                new Sphere(new Point(-20, 30, -600), 18)
                        .setEmission(new Color(100, 100, 200))
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(50))
        );

        // Same lighting
        scene.lights.add(
                new SpotLight(new Color(300, 280, 250),
                        new Point(100, 80, -200),
                        new Vector(-1, -0.8, -0.5))
                        .setKl(0.0001).setKq(0.000005)
                        .setNarrowBeam(3)
        );
        scene.lights.add(
                new PointLight(new Color(200, 220, 250),
                        new Point(-100, 50, -100))
                        .setKl(0.0005).setKq(0.00002)
        );
        scene.lights.add(
                new DirectionalLight(new Vector(0.2, -0.3, 1),
                        new Color(150, 180, 200))
        );

        // NO DoF settings
        Blackboard noDoF = Blackboard.getBuilder()
                .setAntiAliasing(true)
                .setAntiAliasingSamples(9)
                .build();

        Camera.getBuilder()
                .setLocation(new Point(0, 0, 200))
                .setDirection(new Point(-30, -10, -400), Vector.AXIS_Y)
                .setVpSize(300, 200)
                .setVpDistance(200)
                .setResolution(800, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setBlackboard(noDoF)
                .build()
                .renderImage()
                .writeToImage("tubeDepthOfFieldDemo_NoDoF");
    }
}