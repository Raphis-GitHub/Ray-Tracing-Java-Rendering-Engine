package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Tests for Stage 2-B: Manual Boundary Volume Hierarchy
 * Demonstrates performance improvements through strategic object grouping
 */
public class BVHStage2Test {

    /**
     * Stage 1: Flat CBR - All objects directly in scene.geometries
     * This creates the baseline for comparison
     */
    @Test
    void stage1_FlatCBR() {
        Scene scene = createBaseScene();

        // ADD ALL OBJECTS DIRECTLY TO SCENE (FLAT STRUCTURE)
        Geometries flatGeometries = new Geometries();
        addAllObjects(flatGeometries);
        scene.setGeometries(flatGeometries);

        // Timing measurement
        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), Vector.AXIS_Y)
                .setVpSize(200, 150)
                .setVpDistance(100)
                .setResolution(800, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(true)  // CBR enabled
                .setMultithreading(0)  // Single thread for accurate timing
                .build()
                .renderImage()
                .writeToImage("BVH_Stage1_FlatCBR");

        long endTime = System.currentTimeMillis();
        System.out.printf("Stage 1 (Flat CBR): %d ms\n", endTime - startTime);
    }

    /**
     * Stage 2-B: Manual Hierarchy - Same objects organized strategically
     * Expected 3+ times performance improvement over Stage 1
     */
    @Test
    void stage2B_ManualHierarchy() {
        Scene scene = createBaseScene();

        // CREATE MANUAL HIERARCHY - Strategic grouping by spatial proximity
        Geometries manualHierarchy = createManualHierarchy();
        scene.setGeometries(manualHierarchy);

        // Timing measurement
        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), Vector.AXIS_Y)
                .setVpSize(200, 150)
                .setVpDistance(100)
                .setResolution(800, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(true)  // CBR enabled
                .setMultithreading(0)  // Single thread for accurate timing
                .build()
                .renderImage()
                .writeToImage("BVH_Stage2B_ManualHierarchy");

        long endTime = System.currentTimeMillis();
        System.out.printf("Stage 2-B (Manual Hierarchy): %d ms\n", endTime - startTime);
    }

    /**
     * Performance comparison test - runs both stages and compares timing
     */
    @Test
    void performanceComparison() {
        System.out.println("=== BVH Performance Comparison ===");

        // Test Stage 1 (Flat)
        Scene scene1 = createBaseScene();
        Geometries flatGeometries = new Geometries();
        addAllObjects(flatGeometries);
        scene1.setGeometries(flatGeometries);

        long startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), Vector.AXIS_Y)
                .setVpSize(200, 150)
                .setVpDistance(100)
                .setResolution(600, 450)  // Smaller for faster testing
                .setRayTracer(scene1, RayTracerType.SIMPLE)
                .setCBR(true)
                .setMultithreading(0)
                .build()
                .renderImage();
        long flatTime = System.currentTimeMillis() - startTime;

        // Test Stage 2-B (Hierarchical)
        Scene scene2 = createBaseScene();
        scene2.setGeometries(createManualHierarchy());

        startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), Vector.AXIS_Y)
                .setVpSize(200, 150)
                .setVpDistance(100)
                .setResolution(600, 450)
                .setRayTracer(scene2, RayTracerType.SIMPLE)
                .setCBR(true)
                .setMultithreading(0)
                .build()
                .renderImage();
        long hierarchyTime = System.currentTimeMillis() - startTime;

        // Results
        double improvement = (double) flatTime / hierarchyTime;
        System.out.printf("Flat CBR time: %d ms\n", flatTime);
        System.out.printf("Manual Hierarchy time: %d ms\n", hierarchyTime);
        System.out.printf("Performance improvement: %.2fx\n", improvement);

        if (improvement >= 3.0) {
            System.out.println("✅ SUCCESS: Achieved 3+ times improvement!");
        } else {
            System.out.println("⚠️  Improvement below expected 3x threshold");
        }
    }

    /**
     * Creates the base scene with lighting and materials
     * This scene will be used for both flat and hierarchical tests
     */
    private Scene createBaseScene() {
        Scene scene = new Scene("BVH Test Scene")
                .setBackground(new Color(10, 15, 35))
                .setAmbientLight(new AmbientLight(new Color(15, 18, 30)));

        // Enhanced lighting for better visibility
        scene.lights.add(new DirectionalLight(
                new Vector(0.3, -0.8, -0.2),
                new Color(100, 110, 130)));

        scene.lights.add(new PointLight(
                new Color(150, 160, 170),
                new Point(-80, 80, -180))
                .setKl(0.0001).setKq(0.000001));

        scene.lights.add(new SpotLight(
                new Color(80, 90, 120),
                new Point(-30, 60, 30),
                new Vector(0.3, -1, -0.2))
                .setKl(0.0005).setKq(0.00005)
                .setNarrowBeam(6));

        return scene;
    }

    /**
     * Creates the manual hierarchy by strategically grouping objects
     * Strategy: Group by spatial proximity and ray intersection likelihood
     */
    private Geometries createManualHierarchy() {
        // STRATEGY: Create spatial clusters that rays are likely to hit together

        // 1. LEFT FOREST CLUSTER (x < -30)
        Geometries leftForest = new Geometries();
        leftForest.add(
                // Large tree
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-70, -25, -40)), 12, 45)
                        .setEmission(new Color(40, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-70, 30, -40), 28)
                        .setEmission(new Color(45, 60, 25)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(-55, 25, -25), 18)
                        .setEmission(new Color(60, 45, 20)).setMaterial(createLeavesMaterial()),

                // Background pine
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-90, -25, -80)), 7, 50)
                        .setEmission(new Color(30, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-90, 35, -80), 18)
                        .setEmission(new Color(15, 45, 30)).setMaterial(createLeavesMaterial()),

                // Left area rocks and fallen logs
                new Sphere(new Point(-60, -15, 0), 12)
                        .setEmission(new Color(35, 35, 45)).setMaterial(createRockMaterial()),
                new Cylinder(new Ray(new Vector(1, 0, 0.3), new Point(-80, -20, 30)), 5, 25)
                        .setEmission(new Color(30, 22, 12)).setMaterial(createDeadWoodMaterial()),
                new Sphere(new Point(-75, -19, 70), 9)
                        .setEmission(new Color(38, 38, 50)).setMaterial(createRockMaterial())
        );

        // 2. CENTER FOREST CLUSTER (-30 <= x <= 30)
        Geometries centerForest = new Geometries();
        centerForest.add(
                // Central mystical birch
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-20, -25, 20)), 6, 35)
                        .setEmission(new Color(80, 80, 90)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-20, 20, 20), 22)
                        .setEmission(new Color(35, 65, 45)).setMaterial(createLeavesMaterial()),

                // Center objects and small trees
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-15, -25, -5)), 3, 20)
                        .setEmission(new Color(35, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-15, -5, -5), 12)
                        .setEmission(new Color(30, 55, 35)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(15, -25, -25)), 2, 15)
                        .setEmission(new Color(30, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(15, -10, -25), 10)
                        .setEmission(new Color(45, 35, 25)).setMaterial(createLeavesMaterial()),

                // Center rocks
                new Sphere(new Point(-25, -20, -30), 8)
                        .setEmission(new Color(34, 34, 45)).setMaterial(createRockMaterial()),
                new Sphere(new Point(25, -18, 25), 10)
                        .setEmission(new Color(32, 32, 42)).setMaterial(createRockMaterial()),

                // Distant giant (background)
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(0, -25, -120)), 15, 60)
                        .setEmission(new Color(28, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(0, 45, -120), 35)
                        .setEmission(new Color(18, 45, 30)).setMaterial(createLeavesMaterial())
        );

        // 3. RIGHT FOREST CLUSTER (x > 30)
        Geometries rightForest = new Geometries();
        rightForest.add(
                // Pine tree
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(60, -25, -60)), 8, 55)
                        .setEmission(new Color(35, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(60, 40, -60), 20)
                        .setEmission(new Color(20, 50, 35)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(60, 25, -60), 25)
                        .setEmission(new Color(25, 55, 40)).setMaterial(createLeavesMaterial()),

                // Gnarled oak
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(30, -25, 40)), 10, 40)
                        .setEmission(new Color(38, 28, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(30, 25, 40), 24)
                        .setEmission(new Color(40, 70, 35)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(45, 20, 35), 16)
                        .setEmission(new Color(65, 40, 25)).setMaterial(createLeavesMaterial()),

                // Young oak
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(80, -25, 10)), 5, 30)
                        .setEmission(new Color(40, 30, 18)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(80, 15, 10), 20)
                        .setEmission(new Color(35, 65, 40)).setMaterial(createLeavesMaterial()),

                // Right area rocks
                new Sphere(new Point(65, -16, -20), 14)
                        .setEmission(new Color(30, 30, 40)).setMaterial(createRockMaterial()),
                new Sphere(new Point(35, -17, 60), 11)
                        .setEmission(new Color(28, 28, 38)).setMaterial(createRockMaterial())
        );

        // 4. GROUND ELEMENTS CLUSTER (low y values)
        Geometries groundElements = new Geometries();
        groundElements.add(
                // Ground plane
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, -200), new Point(150, -25, 150))
                        .setEmission(new Color(25, 35, 20)).setMaterial(createMossMaterial()),
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, 150), new Point(-150, -25, 150))
                        .setEmission(new Color(25, 35, 20)).setMaterial(createMossMaterial()),

                // Small rocks scattered on ground
                new Sphere(new Point(-10, -23, 15), 3).setEmission(new Color(28, 28, 35)).setMaterial(createRockMaterial()),
                new Sphere(new Point(5, -24, -8), 2).setEmission(new Color(30, 30, 38)).setMaterial(createRockMaterial()),
                new Sphere(new Point(-35, -23, 25), 4).setEmission(new Color(26, 26, 32)).setMaterial(createRockMaterial()),
                new Sphere(new Point(55, -24, 35), 3).setEmission(new Color(32, 32, 40)).setMaterial(createRockMaterial()),
                new Sphere(new Point(45, -24, 75), 2).setEmission(new Color(36, 36, 45)).setMaterial(createRockMaterial()),

                // Fallen logs on ground
                new Cylinder(new Ray(new Vector(0.8, 0, -0.6), new Point(40, -22, 70)), 4, 20)
                        .setEmission(new Color(32, 25, 15)).setMaterial(createDeadWoodMaterial()),
                new Cylinder(new Ray(new Vector(-0.7, 0, 0.7), new Point(20, -23, -50)), 6, 18)
                        .setEmission(new Color(28, 18, 10)).setMaterial(createDeadWoodMaterial())
        );

        // 5. SKY ELEMENTS CLUSTER (high y values and far z)
        Geometries skyElements = new Geometries();
        skyElements.add(
                // Moon
                new Sphere(new Point(-80, 80, -180), 25)
                        .setEmission(new Color(220, 220, 190))
                        .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
        );

        // Add some stars to sky cluster
        addStarsToGeometry(skyElements);

        // 6. SPECIAL EFFECTS CLUSTER (magical elements)
        Geometries magicalElements = new Geometries();
        magicalElements.add(
                // Crystal formations
                new Cylinder(new Ray(new Vector(0, 1, 0.2), new Point(-25, -25, 5)), 3, 15)
                        .setEmission(new Color(40, 60, 80)).setMaterial(createCrystalMaterial()),
                new Cylinder(new Ray(new Vector(0.1, 1, 0), new Point(35, -25, 15)), 2, 12)
                        .setEmission(new Color(60, 40, 80)).setMaterial(createCrystalMaterial()),
                new Sphere(new Point(-10, -18, 25), 4)
                        .setEmission(new Color(50, 80, 60)).setMaterial(createCrystalMaterial()),

                // Mushrooms
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-12, -25, 8)), 0.5, 2)
                        .setEmission(new Color(35, 28, 20)).setMaterial(createMossMaterial()),
                new Sphere(new Point(-12, -23, 8), 1.5)
                        .setEmission(new Color(50, 25, 25)).setMaterial(createMossMaterial())
        );

        // CREATE FINAL HIERARCHY - Top level groups the spatial clusters
        Geometries topLevel = new Geometries();
        topLevel.add(
                leftForest,      // Objects on the left
                centerForest,    // Objects in center
                rightForest,     // Objects on the right
                groundElements,  // Ground-level elements
                skyElements,     // Sky elements
                magicalElements  // Special effects
        );

        return topLevel;
    }

    /**
     * Adds all objects to a geometry collection in flat structure (for Stage 1)
     */
    private void addAllObjects(Geometries geometries) {
        // Add the same objects as in manual hierarchy, but all flat

        // Trees
        geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-70, -25, -40)), 12, 45)
                        .setEmission(new Color(40, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-70, 30, -40), 28)
                        .setEmission(new Color(45, 60, 25)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(-55, 25, -25), 18)
                        .setEmission(new Color(60, 45, 20)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(60, -25, -60)), 8, 55)
                        .setEmission(new Color(35, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(60, 40, -60), 20)
                        .setEmission(new Color(20, 50, 35)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(60, 25, -60), 25)
                        .setEmission(new Color(25, 55, 40)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-20, -25, 20)), 6, 35)
                        .setEmission(new Color(80, 80, 90)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-20, 20, 20), 22)
                        .setEmission(new Color(35, 65, 45)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(30, -25, 40)), 10, 40)
                        .setEmission(new Color(38, 28, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(30, 25, 40), 24)
                        .setEmission(new Color(40, 70, 35)).setMaterial(createLeavesMaterial()),
                new Sphere(new Point(45, 20, 35), 16)
                        .setEmission(new Color(65, 40, 25)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-90, -25, -80)), 7, 50)
                        .setEmission(new Color(30, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-90, 35, -80), 18)
                        .setEmission(new Color(15, 45, 30)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(80, -25, 10)), 5, 30)
                        .setEmission(new Color(40, 30, 18)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(80, 15, 10), 20)
                        .setEmission(new Color(35, 65, 40)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(0, -25, -120)), 15, 60)
                        .setEmission(new Color(28, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(0, 45, -120), 35)
                        .setEmission(new Color(18, 45, 30)).setMaterial(createLeavesMaterial())
        );

        // Small trees
        geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-15, -25, -5)), 3, 20)
                        .setEmission(new Color(35, 25, 15)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(-15, -5, -5), 12)
                        .setEmission(new Color(30, 55, 35)).setMaterial(createLeavesMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(15, -25, -25)), 2, 15)
                        .setEmission(new Color(30, 20, 12)).setMaterial(createBarkMaterial()),
                new Sphere(new Point(15, -10, -25), 10)
                        .setEmission(new Color(45, 35, 25)).setMaterial(createLeavesMaterial())
        );

        // Ground
        geometries.add(
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, -200), new Point(150, -25, 150))
                        .setEmission(new Color(25, 35, 20)).setMaterial(createMossMaterial()),
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, 150), new Point(-150, -25, 150))
                        .setEmission(new Color(25, 35, 20)).setMaterial(createMossMaterial())
        );

        // Rocks
        geometries.add(
                new Sphere(new Point(-60, -15, 0), 12)
                        .setEmission(new Color(35, 35, 45)).setMaterial(createRockMaterial()),
                new Sphere(new Point(25, -18, 25), 10)
                        .setEmission(new Color(32, 32, 42)).setMaterial(createRockMaterial()),
                new Sphere(new Point(-25, -20, -30), 8)
                        .setEmission(new Color(34, 34, 45)).setMaterial(createRockMaterial()),
                new Sphere(new Point(65, -16, -20), 14)
                        .setEmission(new Color(30, 30, 40)).setMaterial(createRockMaterial()),
                new Sphere(new Point(-75, -19, 70), 9)
                        .setEmission(new Color(38, 38, 50)).setMaterial(createRockMaterial()),
                new Sphere(new Point(35, -17, 60), 11)
                        .setEmission(new Color(28, 28, 38)).setMaterial(createRockMaterial())
        );

        // Small rocks
        geometries.add(
                new Sphere(new Point(-10, -23, 15), 3).setEmission(new Color(28, 28, 35)).setMaterial(createRockMaterial()),
                new Sphere(new Point(5, -24, -8), 2).setEmission(new Color(30, 30, 38)).setMaterial(createRockMaterial()),
                new Sphere(new Point(-35, -23, 25), 4).setEmission(new Color(26, 26, 32)).setMaterial(createRockMaterial()),
                new Sphere(new Point(55, -24, 35), 3).setEmission(new Color(32, 32, 40)).setMaterial(createRockMaterial()),
                new Sphere(new Point(45, -24, 75), 2).setEmission(new Color(36, 36, 45)).setMaterial(createRockMaterial())
        );

        // Fallen logs
        geometries.add(
                new Cylinder(new Ray(new Vector(1, 0, 0.3), new Point(-80, -20, 30)), 5, 25)
                        .setEmission(new Color(30, 22, 12)).setMaterial(createDeadWoodMaterial()),
                new Cylinder(new Ray(new Vector(0.8, 0, -0.6), new Point(40, -22, 70)), 4, 20)
                        .setEmission(new Color(32, 25, 15)).setMaterial(createDeadWoodMaterial()),
                new Cylinder(new Ray(new Vector(-0.7, 0, 0.7), new Point(20, -23, -50)), 6, 18)
                        .setEmission(new Color(28, 18, 10)).setMaterial(createDeadWoodMaterial())
        );

        // Moon
        geometries.add(
                new Sphere(new Point(-80, 80, -180), 25)
                        .setEmission(new Color(220, 220, 190))
                        .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
        );

        // Stars
        addStarsToGeometry(geometries);

        // Magical elements
        geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0.2), new Point(-25, -25, 5)), 3, 15)
                        .setEmission(new Color(40, 60, 80)).setMaterial(createCrystalMaterial()),
                new Cylinder(new Ray(new Vector(0.1, 1, 0), new Point(35, -25, 15)), 2, 12)
                        .setEmission(new Color(60, 40, 80)).setMaterial(createCrystalMaterial()),
                new Sphere(new Point(-10, -18, 25), 4)
                        .setEmission(new Color(50, 80, 60)).setMaterial(createCrystalMaterial()),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-12, -25, 8)), 0.5, 2)
                        .setEmission(new Color(35, 28, 20)).setMaterial(createMossMaterial()),
                new Sphere(new Point(-12, -23, 8), 1.5)
                        .setEmission(new Color(50, 25, 25)).setMaterial(createMossMaterial())
        );
    }

    /**
     * Adds stars to the given geometry collection
     */
    private void addStarsToGeometry(Geometries geometries) {
        // Add a subset of stars (reduced for performance)
        double[] starDistances = {-300, -250, -200};

        for (int layer = 0; layer < starDistances.length; layer++) {
            double distance = starDistances[layer];
            int numStars = 8 + layer * 2; // Fewer stars for testing

            for (int i = 0; i < numStars; i++) {
                double x = (Math.random() - 0.5) * 300;
                double y = Math.random() * 80 + 20;
                double z = distance + (Math.random() - 0.5) * 20;

                double starSize = 0.8 + Math.random() * 1.2;
                int brightness = 150 + (int) (Math.random() * 80);

                geometries.add(
                        new Sphere(new Point(x, y, z), starSize)
                                .setEmission(new Color(brightness, brightness, brightness))
                                .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
                );
            }
        }
    }

    // Material creation helper methods
    private Material createBarkMaterial() {
        return new Material().setKd(0.6).setKs(0.1).setShininess(8);
    }

    private Material createLeavesMaterial() {
        return new Material().setKd(0.7).setKs(0.2).setShininess(15);
    }

    private Material createRockMaterial() {
        return new Material().setKd(0.5).setKs(0.4).setShininess(30).setKr(0.3);
    }

    private Material createMossMaterial() {
        return new Material().setKd(0.8).setKs(0.05).setShininess(5);
    }

    private Material createDeadWoodMaterial() {
        return new Material().setKd(0.6).setKs(0.1).setShininess(3);
    }

    private Material createCrystalMaterial() {
        return new Material().setKd(0.1).setKs(0.9).setShininess(100).setKt(0.7).setKr(0.3);
    }
}