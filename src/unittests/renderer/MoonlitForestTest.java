package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Creates a dark, atmospheric moonlit forest scene with 40+ objects
 */
public class MoonlitForestTest {

    /**
     * Renders a comprehensive moonlit forest scene with many objects
     */
    @Test
    void renderMoonlitForest() {
        Scene scene = new Scene("Moonlit Forest")
                .setBackground(new Color(15, 20, 35)) // Dark night sky but visible
                .setAmbientLight(new AmbientLight(new Color(12, 15, 25))); // More ambient light

        // Camera positioned to capture the dense forest
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), new Vector(0, 1, 0))
                .setVpSize(300, 200)
                .setVpDistance(100)
                .setResolution(1600, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // ===== DARK MATERIALS =====

        Material darkBark = new Material()
                .setKd(0.6).setKs(0.1).setShininess(8);

        Material darkLeaves = new Material()
                .setKd(0.7).setKs(0.1).setShininess(10);

        Material wetRock = new Material()
                .setKd(0.5).setKs(0.4).setShininess(30);

        Material moss = new Material()
                .setKd(0.8).setKs(0.05).setShininess(5);

        Material deadWood = new Material()
                .setKd(0.6).setKs(0.1).setShininess(3);

        // ===== GROUND =====
        scene.geometries.add(
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, -200), new Point(150, -25, 150))
                        .setEmission(new Color(15, 25, 15)) // Brighter forest floor
                        .setMaterial(moss),

                new Triangle(new Point(-150, -25, -200), new Point(150, -25, 150), new Point(-150, -25, 150))
                        .setEmission(new Color(15, 25, 15))
                        .setMaterial(moss)
        );

        // ===== THE MOON! =====
        scene.geometries.add(
                new Sphere(new Point(-80, 80, -180), 25)
                        .setEmission(new Color(200, 200, 180)) // Bright moon
                        .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
        );

        // ===== LARGE TREES (8 major trees) =====

        // Tree 1 - Ancient Oak
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-70, -25, -40)), 12, 45)
                        .setEmission(new Color(30, 20, 10))
                        .setMaterial(darkBark),
                new Sphere(new Point(-70, 30, -40), 28)
                        .setEmission(new Color(15, 35, 20))
                        .setMaterial(darkLeaves),
                new Sphere(new Point(-55, 25, -25), 18)
                        .setEmission(new Color(18, 40, 25))
                        .setMaterial(darkLeaves)
        );

        // Tree 2 - Tall Pine
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(60, -25, -60)), 8, 55)
                        .setEmission(new Color(25, 18, 10))
                        .setMaterial(darkBark),
                new Sphere(new Point(60, 40, -60), 20)
                        .setEmission(new Color(10, 30, 18))
                        .setMaterial(darkLeaves),
                new Sphere(new Point(60, 25, -60), 25)
                        .setEmission(new Color(12, 35, 20))
                        .setMaterial(darkLeaves)
        );

        // Tree 3 - Birch
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-20, -25, 20)), 6, 35)
                        .setEmission(new Color(60, 60, 65)) // Brighter birch bark
                        .setMaterial(darkBark),
                new Sphere(new Point(-20, 20, 20), 22)
                        .setEmission(new Color(18, 40, 25))
                        .setMaterial(darkLeaves)
        );

        // Tree 4 - Gnarled Oak
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(30, -25, 40)), 10, 40)
                        .setEmission(new Color(28, 20, 10))
                        .setMaterial(darkBark),
                new Sphere(new Point(30, 25, 40), 24)
                        .setEmission(new Color(20, 42, 28))
                        .setMaterial(darkLeaves),
                new Sphere(new Point(45, 20, 35), 16)
                        .setEmission(new Color(15, 35, 22))
                        .setMaterial(darkLeaves)
        );

        // Tree 5 - Background Pine
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-90, -25, -80)), 7, 50)
                        .setEmission(new Color(22, 15, 8))
                        .setMaterial(darkBark),
                new Sphere(new Point(-90, 35, -80), 18)
                        .setEmission(new Color(8, 25, 15))
                        .setMaterial(darkLeaves)
        );

        // Tree 6 - Young Oak
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(80, -25, 10)), 5, 30)
                        .setEmission(new Color(30, 22, 12))
                        .setMaterial(darkBark),
                new Sphere(new Point(80, 15, 10), 20)
                        .setEmission(new Color(18, 40, 25))
                        .setMaterial(darkLeaves)
        );

        // Tree 7 - Crooked Pine
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0.2, 1, 0), new Point(-40, -25, -15)), 6, 42)
                        .setEmission(new Color(24, 18, 8))
                        .setMaterial(darkBark),
                new Sphere(new Point(-35, 25, -15), 19)
                        .setEmission(new Color(12, 32, 20))
                        .setMaterial(darkLeaves)
        );

        // Tree 8 - Distant Giant
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(0, -25, -120)), 15, 60)
                        .setEmission(new Color(20, 15, 8))
                        .setMaterial(darkBark),
                new Sphere(new Point(0, 45, -120), 35)
                        .setEmission(new Color(10, 28, 18))
                        .setMaterial(darkLeaves)
        );

        // ===== SMALL TREES AND SAPLINGS (8 smaller trees) =====

        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-15, -25, -5)), 3, 20)
                        .setEmission(new Color(25, 18, 10)).setMaterial(darkBark),
                new Sphere(new Point(-15, -5, -5), 12)
                        .setEmission(new Color(15, 35, 20)).setMaterial(darkLeaves),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(15, -25, -25)), 2, 15)
                        .setEmission(new Color(22, 15, 8)).setMaterial(darkBark),
                new Sphere(new Point(15, -10, -25), 10)
                        .setEmission(new Color(12, 30, 18)).setMaterial(darkLeaves),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-50, -25, 60)), 2, 18)
                        .setEmission(new Color(28, 20, 10)).setMaterial(darkBark),
                new Sphere(new Point(-50, -7, 60), 11)
                        .setEmission(new Color(18, 38, 22)).setMaterial(darkLeaves),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(50, -25, -10)), 3, 22)
                        .setEmission(new Color(20, 14, 8)).setMaterial(darkBark),
                new Sphere(new Point(50, -3, -10), 13)
                        .setEmission(new Color(15, 35, 20)).setMaterial(darkLeaves)
        );

        // ===== FALLEN LOGS (6 logs) =====

        scene.geometries.add(
                new Cylinder(new Ray(new Vector(1, 0, 0.3), new Point(-80, -20, 30)), 5, 25)
                        .setEmission(new Color(20, 15, 8)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(0.8, 0, -0.6), new Point(40, -22, 70)), 4, 20)
                        .setEmission(new Color(22, 18, 10)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(-0.7, 0, 0.7), new Point(20, -23, -50)), 6, 18)
                        .setEmission(new Color(18, 12, 6)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(1, 0.2, 0), new Point(-30, -21, 80)), 3, 15)
                        .setEmission(new Color(25, 20, 10)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(0, 0, 1), new Point(70, -24, 50)), 4, 22)
                        .setEmission(new Color(20, 15, 8)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(-1, 0, 0.5), new Point(-10, -20, -70)), 5, 16)
                        .setEmission(new Color(22, 18, 10)).setMaterial(deadWood)
        );

        // ===== LARGE BOULDERS (8 boulders) =====

        scene.geometries.add(
                new Sphere(new Point(-60, -15, 0), 12)
                        .setEmission(new Color(25, 25, 30)).setMaterial(wetRock),

                new Sphere(new Point(25, -18, 25), 10)
                        .setEmission(new Color(22, 22, 28)).setMaterial(wetRock),

                new Sphere(new Point(-25, -20, -30), 8)
                        .setEmission(new Color(24, 24, 30)).setMaterial(wetRock),

                new Sphere(new Point(65, -16, -20), 14)
                        .setEmission(new Color(20, 20, 25)).setMaterial(wetRock),

                new Sphere(new Point(-75, -19, 70), 9)
                        .setEmission(new Color(28, 28, 35)).setMaterial(wetRock),

                new Sphere(new Point(35, -17, 60), 11)
                        .setEmission(new Color(18, 18, 24)).setMaterial(wetRock),

                new Sphere(new Point(-45, -21, -60), 7)
                        .setEmission(new Color(30, 30, 38)).setMaterial(wetRock),

                new Sphere(new Point(10, -19, 90), 13)
                        .setEmission(new Color(22, 22, 28)).setMaterial(wetRock)
        );

        // ===== SMALL ROCKS AND STONES (20+ rocks) =====

        // Scattered around the forest floor
        scene.geometries.add(
                new Sphere(new Point(-10, -23, 15), 3).setEmission(new Color(18, 18, 22)).setMaterial(wetRock),
                new Sphere(new Point(5, -24, -8), 2).setEmission(new Color(20, 20, 24)).setMaterial(wetRock),
                new Sphere(new Point(-35, -23, 25), 4).setEmission(new Color(16, 16, 20)).setMaterial(wetRock),
                new Sphere(new Point(55, -24, 35), 3).setEmission(new Color(22, 22, 26)).setMaterial(wetRock),
                new Sphere(new Point(-65, -23, -15), 2).setEmission(new Color(24, 24, 28)).setMaterial(wetRock),
                new Sphere(new Point(75, -24, -35), 4).setEmission(new Color(18, 18, 22)).setMaterial(wetRock),
                new Sphere(new Point(-80, -23, 45), 3).setEmission(new Color(20, 20, 24)).setMaterial(wetRock),
                new Sphere(new Point(45, -24, 75), 2).setEmission(new Color(26, 26, 30)).setMaterial(wetRock),
                new Sphere(new Point(-20, -23, 65), 3).setEmission(new Color(16, 16, 20)).setMaterial(wetRock),
                new Sphere(new Point(20, -24, -35), 4).setEmission(new Color(22, 22, 26)).setMaterial(wetRock),
                new Sphere(new Point(-55, -23, -45), 2).setEmission(new Color(18, 18, 22)).setMaterial(wetRock),
                new Sphere(new Point(85, -24, 5), 3).setEmission(new Color(20, 20, 24)).setMaterial(wetRock),
                new Sphere(new Point(-85, -23, 15), 2).setEmission(new Color(24, 24, 28)).setMaterial(wetRock),
                new Sphere(new Point(30, -24, -55), 4).setEmission(new Color(16, 16, 20)).setMaterial(wetRock),
                new Sphere(new Point(-30, -23, 45), 3).setEmission(new Color(22, 22, 26)).setMaterial(wetRock),
                new Sphere(new Point(60, -24, 85), 2).setEmission(new Color(18, 18, 22)).setMaterial(wetRock),
                new Sphere(new Point(-70, -23, 85), 3).setEmission(new Color(20, 20, 24)).setMaterial(wetRock),
                new Sphere(new Point(40, -24, -75), 4).setEmission(new Color(26, 26, 30)).setMaterial(wetRock),
                new Sphere(new Point(-40, -23, -75), 2).setEmission(new Color(16, 16, 20)).setMaterial(wetRock),
                new Sphere(new Point(0, -24, 45), 3).setEmission(new Color(22, 22, 26)).setMaterial(wetRock),
                new Sphere(new Point(-5, -23, -45), 2).setEmission(new Color(18, 18, 22)).setMaterial(wetRock)
        );

        // ===== MUSHROOMS (Growing on logs and ground - 8 mushrooms) =====

        scene.geometries.add(
                // On fallen log
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-78, -18, 32)), 1, 3)
                        .setEmission(new Color(30, 25, 18)).setMaterial(moss),
                new Sphere(new Point(-78, -15, 32), 2)
                        .setEmission(new Color(35, 22, 18)).setMaterial(moss),

                // Ground mushrooms
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-12, -25, 8)), 0.5, 2)
                        .setEmission(new Color(25, 20, 15)).setMaterial(moss),
                new Sphere(new Point(-12, -23, 8), 1.5)
                        .setEmission(new Color(30, 18, 15)).setMaterial(moss),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(8, -25, 12)), 0.8, 2.5)
                        .setEmission(new Color(28, 23, 18)).setMaterial(moss),
                new Sphere(new Point(8, -22.5, 12), 1.8)
                        .setEmission(new Color(32, 20, 18)).setMaterial(moss),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-38, -25, 55)), 0.6, 2)
                        .setEmission(new Color(24, 18, 13)).setMaterial(moss),
                new Sphere(new Point(-38, -23, 55), 1.3)
                        .setEmission(new Color(28, 16, 13)).setMaterial(moss)
        );

        // ===== LIGHTING (Atmospheric moonlight) =====

        // Strong moonlight from the moon's position
        scene.lights.add(new DirectionalLight(
                new Vector(0.3, -0.8, -0.2),
                new Color(80, 90, 110) // Bright cool moonlight
        ));

        // Direct light from the moon itself
        scene.lights.add(new PointLight(
                new Color(120, 130, 140),
                new Point(-80, 80, -180)) // Moon's position
                .setKl(0.0001).setKq(0.000001)
        );

        // Moonbeam filtering through trees
        scene.lights.add(new SpotLight(
                new Color(60, 70, 90),
                new Point(-30, 60, 30),
                new Vector(0.3, -1, -0.2))
                .setKl(0.0005).setKq(0.00005)
                .setNarrowBeam(6)
        );

        // Another moonbeam
        scene.lights.add(new SpotLight(
                new Color(50, 60, 85),
                new Point(50, 70, -40),
                new Vector(-0.4, -1, 0.3))
                .setKl(0.0005).setKq(0.00005)
                .setNarrowBeam(8)
        );

        // Atmospheric fill light
        scene.lights.add(new PointLight(
                new Color(25, 30, 45),
                new Point(0, 50, 0))
                .setKl(0.001).setKq(0.00008)
        );

        // Render the atmospheric forest scene
        camera.renderImage()
                .writeToImage("moonlitForest");
    }
}