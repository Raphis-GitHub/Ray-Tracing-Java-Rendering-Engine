package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Comprehensive performance test for BVH vs no BVH with multithreading permutations.
 * Tests all 4 combinations: no BVH/no MT, BVH/no MT, no BVH/MT, BVH/MT
 * Uses 200+ geometries with multiple light sources and 81-ray anti-aliasing for realistic performance comparison.
 */
public class BVHPerformanceTest {

    /**
     * Test 1: No BVH, No Multithreading (Baseline)
     */
    @Test
    void test1_NoBVH_NoMultithreading() {
        System.out.println("=== TEST 1: No BVH, No Multithreading ===");

        // Configure BVH off
        GeometryHierarchyBuilderMedian.setBVHEnabled(false);

        Scene scene = createMassiveTestScene();
        Geometries flatGeometries = new Geometries();
        addMassiveTestObjects(flatGeometries);

        // Use flat geometry (no hierarchy building)
        scene.setGeometries(flatGeometries);

        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 30, 200))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpSize(300, 200)
                .setVpDistance(150)
                .setResolution(900, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(false)
                .setMultithreading(0)  // No multithreading
                .setBlackboard(Blackboard.getBuilder()
                        .setAntiAliasing(true)
                        .setAntiAliasingSamples(81)  // 9x9 grid for high quality
                        .setUseJitteredSampling(true)  // Jittered sampling for smoother anti-aliasing
                        .build())
                .build()
                .renderImage()
                .writeToImage("BVH_Test1_NoBVH_NoMT");

        long endTime = System.currentTimeMillis();
        System.out.printf("Test 1 (No BVH, No MT): %d ms\n\n", endTime - startTime);
    }

    /**
     * Test 2: BVH Enabled, No Multithreading
     */
    @Test
    void test2_BVH_NoMultithreading() {
        System.out.println("=== TEST 2: BVH Enabled, No Multithreading ===");

        // Configure BVH on
        GeometryHierarchyBuilderMedian.setBVHEnabled(true);

        Scene scene = createMassiveTestScene();
        Geometries flatGeometries = new Geometries();
        addMassiveTestObjects(flatGeometries);

        // Build BVH hierarchy
        Geometries hierarchyGeometries = GeometryHierarchyBuilderMedian.buildHierarchy(flatGeometries);
        scene.setGeometries(hierarchyGeometries);

        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 30, 200))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpSize(300, 200)
                .setVpDistance(150)
                .setResolution(900, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(true)
                .setMultithreading(0)  // No multithreading
                .setBlackboard(Blackboard.getBuilder()
                        .setAntiAliasing(true)
                        .setAntiAliasingSamples(81)  // 9x9 grid for high quality
                        .setUseJitteredSampling(true)  // Jittered sampling for smoother anti-aliasing
                        .build())
                .build()
                .renderImage()
                .writeToImage("BVH_Test2_BVH_NoMT");

        long endTime = System.currentTimeMillis();
        System.out.printf("Test 2 (BVH, No MT): %d ms\n\n", endTime - startTime);
    }

    /**
     * Test 3: No BVH, Multithreading Enabled
     */
    @Test
    void test3_NoBVH_Multithreading() {
        System.out.println("=== TEST 3: No BVH, Multithreading Enabled ===");

        // Configure BVH off
        GeometryHierarchyBuilderMedian.setBVHEnabled(false);

        Scene scene = createMassiveTestScene();
        Geometries flatGeometries = new Geometries();
        addMassiveTestObjects(flatGeometries);

        // Use flat geometry (no hierarchy building)
        scene.setGeometries(flatGeometries);

        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 30, 200))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpSize(300, 200)
                .setVpDistance(150)
                .setResolution(900, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(false)
                .setMultithreading(-1)  // Auto multithreading
                .setBlackboard(Blackboard.getBuilder()
                        .setAntiAliasing(true)
                        .setAntiAliasingSamples(81)  // 9x9 grid for high quality
                        .setUseJitteredSampling(true)  // Jittered sampling for smoother anti-aliasing
                        .build())
                .build()
                .renderImage()
                .writeToImage("BVH_Test3_NoBVH_MT");

        long endTime = System.currentTimeMillis();
        System.out.printf("Test 3 (No BVH, MT): %d ms\n\n", endTime - startTime);
    }

    /**
     * Test 4: BVH Enabled, Multithreading Enabled (Optimal Performance)
     */
    @Test
    void test4_BVH_Multithreading() {
        System.out.println("=== TEST 4: BVH Enabled, Multithreading Enabled ===");

        // Configure BVH on
        GeometryHierarchyBuilderMedian.setBVHEnabled(true);

        Scene scene = createMassiveTestScene();
        Geometries flatGeometries = new Geometries();
        addMassiveTestObjects(flatGeometries);

        // Build BVH hierarchy
        Geometries hierarchyGeometries = GeometryHierarchyBuilderMedian.buildHierarchy(flatGeometries);
        scene.setGeometries(hierarchyGeometries);

        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setLocation(new Point(0, 30, 200))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpSize(300, 200)
                .setVpDistance(150)
                .setResolution(900, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setCBR(true)
                .setMultithreading(-1)  // Auto multithreading
                .setBlackboard(Blackboard.getBuilder()
                        .setAntiAliasing(true)
                        .setAntiAliasingSamples(81)  // 9x9 grid for high quality
                        .setUseJitteredSampling(true)  // Jittered sampling for smoother anti-aliasing
                        .build())
                .build()
                .renderImage()
                .writeToImage("BVH_Test4_BVH_MT");

        long endTime = System.currentTimeMillis();
        System.out.printf("Test 4 (BVH, MT): %d ms\n\n", endTime - startTime);
    }

    // Helper methods

    /**
     * Creates a complex scene with multiple light sources and geometries
     *
     * @return the constructed Scene object
     */
    private Scene createMassiveTestScene() {
        Scene scene = new Scene("BVH Performance Test Scene")
                .setBackground(new Color(10, 15, 25))
                .setAmbientLight(new AmbientLight(new Color(15, 20, 30)));

        // Multiple light sources for realistic complexity
        scene.lights.add(new DirectionalLight(
                new Vector(0.3, -0.7, -0.4),
                new Color(100, 110, 120)));

        scene.lights.add(new PointLight(
                new Color(150, 160, 170),
                new Point(-80, 80, -50))
                .setKl(0.0001).setKq(0.000001));

        scene.lights.add(new PointLight(
                new Color(120, 140, 160),
                new Point(80, 60, -30))
                .setKl(0.0002).setKq(0.000002));

        scene.lights.add(new SpotLight(
                new Color(80, 90, 110),
                new Point(-30, 50, 100),
                new Vector(0.3, -0.8, -0.5))
                .setKl(0.0003).setKq(0.00003)
                .setNarrowBeam(10));

        scene.lights.add(new SpotLight(
                new Color(90, 100, 120),
                new Point(40, 70, 80),
                new Vector(-0.2, -0.9, -0.4))
                .setKl(0.0004).setKq(0.00004)
                .setNarrowBeam(12));

        return scene;
    }

    /**
     * Adds a variety of geometries to the scene for performance testing.
     *
     * @param geometries the collection of geometries to which the test objects will be added
     */
    private void addMassiveTestObjects(Geometries geometries) {
        // Add 200+ objects in various spatial distributions
        addSphereField(geometries, 50);      // 50 spheres in grid
        addRandomSpheres(geometries, 30);    // 30 random spheres
        addRandomSpheres(geometries, 50);    // 30 random spheres
        addSphereCluster(geometries, -60, -60, -20, 25);  // 25 clustered spheres
        addSphereCluster(geometries, 60, -60, -20, 25);   // 25 clustered spheres
        addSphereCluster(geometries, 0, 60, -30, 25);     // 25 clustered spheres
        addTriangleStructures(geometries, 20); // 20 triangle structures
        addMixedGeometries(geometries, 30);   // 30 finite geometries (15 polygons + 15 cylinders)
        // Total: ~225 finite objects (no infinite objects)
    }

    /**
     * Adds a grid of spheres to the scene.
     *
     * @param geometries the collection of geometries to which the spheres will be added
     * @param count      number of spheres to add
     */
    private void addSphereField(Geometries geometries, int count) {
        int gridSize = (int) Math.ceil(Math.sqrt(count));
        double spacing = 15;
        double startX = -(gridSize - 1) * spacing / 2;
        double startZ = -(gridSize - 1) * spacing / 2;

        for (int i = 0; i < count; i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            double x = startX + col * spacing;
            double z = startZ + row * spacing;
            double y = -10 + Math.random() * 5;
            double radius = 2 + Math.random() * 2;

            geometries.add(new Sphere(new Point(x, y, z), radius)
                    .setEmission(new Color(
                            100 + (int) (Math.random() * 100),
                            50 + (int) (Math.random() * 100),
                            80 + (int) (Math.random() * 100)))
                    .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(30)));
        }
    }

    /**
     * Adds a specified number of random spheres to the scene.
     *
     * @param geometries the collection of geometries to which the spheres will be added
     * @param count      number of random spheres to add
     */
    private void addRandomSpheres(Geometries geometries, int count) {
        for (int i = 0; i < count; i++) {
            double x = (Math.random() - 0.5) * 150;
            double y = Math.random() * 40 - 5;
            double z = (Math.random() - 0.5) * 100;
            double radius = 1 + Math.random() * 4;

            geometries.add(new Sphere(new Point(x, y, z), radius)
                    .setEmission(new Color(
                            (int) (Math.random() * 200),
                            (int) (Math.random() * 200),
                            (int) (Math.random() * 200)))
                    .setMaterial(new Material().setKd(0.6).setKs(0.4).setShininess(25)));
        }
    }

    /**
     * Adds a cluster of spheres around a center point.
     *
     * @param geometries the collection of geometries to which the spheres will be added
     * @param centerX    the X coordinate of the center of the cluster
     * @param centerY    the y coordinate of the center of the cluster
     * @param centerZ    the Z coordinate of the center of the cluster
     * @param count      number of spheres in the cluster
     */
    private void addSphereCluster(Geometries geometries, double centerX, double centerY, double centerZ, int count) {
        for (int i = 0; i < count; i++) {
            double x = centerX + (Math.random() - 0.5) * 25;
            double y = centerY + (Math.random() - 0.5) * 15;
            double z = centerZ + (Math.random() - 0.5) * 25;
            double radius = 1 + Math.random() * 3;

            geometries.add(new Sphere(new Point(x, y, z), radius)
                    .setEmission(new Color(
                            150 + (int) (Math.random() * 50),
                            100 + (int) (Math.random() * 100),
                            50 + (int) (Math.random() * 150)))
                    .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(40)));
        }
    }

    /**
     * Adds small pyramid-like triangle structures to the scene.
     *
     * @param geometries the collection of geometries to which the structures will be added
     * @param count      the number of triangle structures to add
     */
    private void addTriangleStructures(Geometries geometries, int count) {
        for (int i = 0; i < count; i++) {
            double baseX = (Math.random() - 0.5) * 120;
            double baseY = -15 + Math.random() * 10;
            double baseZ = (Math.random() - 0.5) * 80;
            double size = 3 + Math.random() * 5;

            // Create a small pyramid structure
            Point base1 = new Point(baseX, baseY, baseZ);
            Point base2 = new Point(baseX + size, baseY, baseZ);
            Point base3 = new Point(baseX + size / 2, baseY, baseZ + size);
            Point apex = new Point(baseX + size / 2, baseY + size, baseZ + size / 2);

            Material material = new Material().setKd(0.8).setKs(0.2).setShininess(15);
            Color emission = new Color(
                    80 + (int) (Math.random() * 120),
                    60 + (int) (Math.random() * 140),
                    100 + (int) (Math.random() * 100));

            geometries.add(new Triangle(base1, base2, apex).setEmission(emission).setMaterial(material));
            geometries.add(new Triangle(base2, base3, apex).setEmission(emission).setMaterial(material));
            geometries.add(new Triangle(base3, base1, apex).setEmission(emission).setMaterial(material));
            geometries.add(new Triangle(base1, base3, base2).setEmission(emission).setMaterial(material));
        }
    }

    /**
     * Adds a mix of polygons and cylinders to the scene.
     *
     * @param geometries the collection of geometries to which the mixed geometries will be added
     * @param count      number of mixed geometries to add
     */
    private void addMixedGeometries(Geometries geometries, int count) {
        for (int i = 0; i < count; i++) {
            double x = (Math.random() - 0.5) * 100;
            double y = Math.random() * 30 - 10;
            double z = (Math.random() - 0.5) * 60;

            Color emission = new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255));
            Material material = new Material().setKd(0.6).setKs(0.4).setShininess(20);

            if (i % 2 == 0) {
                // Add polygon (finite geometry only)
                double size = 2 + Math.random() * 3;
                Point p1 = new Point(x, y, z);
                Point p2 = new Point(x + size, y, z);
                Point p3 = new Point(x + size, y + size, z);
                Point p4 = new Point(x, y + size, z);
                geometries.add(new Polygon(p1, p2, p3, p4)
                        .setEmission(emission).setMaterial(material));
            } else {
                // Add cylinder (finite geometry only)
                double radius = 1 + Math.random() * 2;
                double height = 3 + Math.random() * 4;
                geometries.add(new Cylinder(new Ray(Vector.AXIS_Y, new Point(x, y, z)), radius, height)
                        .setEmission(emission).setMaterial(material));
            }
        }
    }
}