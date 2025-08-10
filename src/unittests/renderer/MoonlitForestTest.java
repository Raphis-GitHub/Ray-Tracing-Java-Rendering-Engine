package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Creates a magical moonlit forest scene with stars, enhanced colors, and reflection/transparency effects
 */
public class MoonlitForestTest {

    /**
     * Renders an enhanced moonlit forest scene with stars and new visual effects
     */
    @Test
    @Disabled
    void renderEnhancedMoonlitForest() {
        Scene scene = new Scene("Enhanced Moonlit Forest")
                .setBackground(new Color(10, 15, 35)) // Darker sky for better star visibility
                .setAmbientLight(new AmbientLight(new Color(15, 18, 30))); // Slightly more ambient light
        Blackboard settings = Blackboard.getBuilder()
                .setAntiAliasing(false)
                .setAntiAliasingSamples(9)// 3x3 for speed
                .setDepthOfField(false)
                .setDepthOfFieldSamples(49)
                .setUseJitteredSampling(true)// 16 aperture samples
                .setSoftShadows(true)
                .setSoftShadowSamples(50)
                .build();

        // Camera positioned to capture the dense forest
        Camera camera = Camera.getBuilder()
                .setBlackboard(settings).setCBR(true)
                .setFocusPointDistance(100)         // Focus on objects 100 units away
                .setAperture(4)
                .setLocation(new Point(0, 15, 120))
                .setDirection(new Point(0, -5, -80), new Vector(0, 1, 0))
                .setVpSize(300, 200)
                .setVpDistance(100)
                .setResolution(800, 500)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // ===== ENHANCED MATERIALS WITH NEW EFFECTS =====

        Material darkBark = new Material()
                .setKd(0.6).setKs(0.1).setShininess(8);

        Material colorfulLeaves = new Material()  // More vibrant leaves
                .setKd(0.7).setKs(0.2).setShininess(15);

        Material wetRock = new Material()
                .setKd(0.5).setKs(0.4).setShininess(30)
                .setKr(0.3); // Add reflection to wet rocks

        Material moss = new Material()
                .setKd(0.8).setKs(0.05).setShininess(5);

        Material deadWood = new Material()
                .setKd(0.6).setKs(0.1).setShininess(3);

        Material crystalMaterial = new Material()  // For magical elements
                .setKd(0.1).setKs(0.9).setShininess(100)
                .setKt(0.7).setKr(0.3); // Transparent and reflective

        Material waterMaterial = new Material()   // For water puddles
                .setKd(0.2).setKs(0.8).setShininess(80)
                .setKt(0.6).setKr(0.8); // Highly reflective water

        // ===== STARRY SKY =====
        // Create a constellation of stars at various distances and sizes
        double[] starDistances = {-300, -250, -200, -180, -150};

        for (int layer = 0; layer < starDistances.length; layer++) {
            double distance = starDistances[layer];
            int numStars = 15 + layer * 3; // More stars in closer layers

            for (int i = 0; i < numStars; i++) {
                // Random positions across the sky
                double x = (Math.random() - 0.5) * 400;
                double y = Math.random() * 120 + 20; // Above horizon
                double z = distance + (Math.random() - 0.5) * 30;

                // Varying star sizes and brightness
                double starSize = 0.8 + Math.random() * 1.5;
                int brightness = 150 + (int) (Math.random() * 105);

                // Star colors - mix of white, blue-white, and warm white
                Color starColor;
                double colorRand = Math.random();
                if (colorRand < 0.6) {
                    starColor = new Color(brightness, brightness, brightness); // White
                } else if (colorRand < 0.8) {
                    starColor = new Color(brightness - 20, brightness - 10, brightness); // Blue-white
                } else {
                    starColor = new Color(brightness, brightness - 15, brightness - 30); // Warm white
                }

                scene.geometries.add(
                        new Sphere(new Point(x, y, z), starSize)
                                .setEmission(starColor)
                                .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
                );
            }
        }

        // ===== ENHANCED GROUND WITH WATER PUDDLES =====
        scene.geometries.add(
                new Triangle(new Point(-150, -25, -200), new Point(150, -25, -200), new Point(150, -25, 150))
                        .setEmission(new Color(25, 35, 20)) // More vibrant forest floor
                        .setMaterial(moss),

                new Triangle(new Point(-150, -25, -200), new Point(150, -25, 150), new Point(-150, -25, 150))
                        .setEmission(new Color(25, 35, 20))
                        .setMaterial(moss)
        );

        // Add magical water puddles with reflection
        scene.geometries.add(
                new Triangle(new Point(-30, -24.5, 10), new Point(-10, -24.5, 10), new Point(-10, -24.5, 30))
                        .setEmission(new Color(5, 15, 25))
                        .setMaterial(waterMaterial),
                new Triangle(new Point(-30, -24.5, 10), new Point(-10, -24.5, 30), new Point(-30, -24.5, 30))
                        .setEmission(new Color(5, 15, 25))
                        .setMaterial(waterMaterial),

                new Triangle(new Point(40, -24.5, -20), new Point(60, -24.5, -20), new Point(60, -24.5, 0))
                        .setEmission(new Color(8, 18, 30))
                        .setMaterial(waterMaterial),
                new Triangle(new Point(40, -24.5, -20), new Point(60, -24.5, 0), new Point(40, -24.5, 0))
                        .setEmission(new Color(8, 18, 30))
                        .setMaterial(waterMaterial)
        );

        // ===== THE ENHANCED MOON =====
        scene.geometries.add(
                new Sphere(new Point(-80, 80, -180), 25)
                        .setEmission(new Color(220, 220, 190)) // Warmer moon
                        .setMaterial(new Material().setKd(0.1).setKs(0.9).setShininess(100))
        );

        // ===== LARGE TREES WITH MORE COLORFUL FOLIAGE =====

        // Tree 1 - Ancient Oak with autumn colors
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-70, -25, -40)), 12, 45)
                        .setEmission(new Color(40, 25, 15))
                        .setMaterial(darkBark),
                new Sphere(new Point(-70, 30, -40), 28)
                        .setEmission(new Color(45, 60, 25)) // More vibrant green
                        .setMaterial(colorfulLeaves),
                new Sphere(new Point(-55, 25, -25), 18)
                        .setEmission(new Color(60, 45, 20)) // Autumn orange
                        .setMaterial(colorfulLeaves)
        );

        // Tree 2 - Magical Pine with ethereal glow
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(60, -25, -60)), 8, 55)
                        .setEmission(new Color(35, 25, 15))
                        .setMaterial(darkBark),
                new Sphere(new Point(60, 40, -60), 20)
                        .setEmission(new Color(20, 50, 35)) // Magical green
                        .setMaterial(colorfulLeaves),
                new Sphere(new Point(60, 25, -60), 25)
                        .setEmission(new Color(25, 55, 40))
                        .setMaterial(colorfulLeaves)
        );

        // Tree 3 - Mystical Birch
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-20, -25, 20)), 6, 35)
                        .setEmission(new Color(80, 80, 90)) // Brighter birch bark
                        .setMaterial(darkBark),
                new Sphere(new Point(-20, 20, 20), 22)
                        .setEmission(new Color(35, 65, 45)) // Vibrant foliage
                        .setMaterial(colorfulLeaves)
        );

        // Tree 4 - Gnarled Oak with colorful leaves
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(30, -25, 40)), 10, 40)
                        .setEmission(new Color(38, 28, 15))
                        .setMaterial(darkBark),
                new Sphere(new Point(30, 25, 40), 24)
                        .setEmission(new Color(40, 70, 35)) // Rich green
                        .setMaterial(colorfulLeaves),
                new Sphere(new Point(45, 20, 35), 16)
                        .setEmission(new Color(65, 40, 25)) // Reddish-brown
                        .setMaterial(colorfulLeaves)
        );

        // Tree 5 - Background Pine
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-90, -25, -80)), 7, 50)
                        .setEmission(new Color(30, 20, 12))
                        .setMaterial(darkBark),
                new Sphere(new Point(-90, 35, -80), 18)
                        .setEmission(new Color(15, 45, 30))
                        .setMaterial(colorfulLeaves)
        );

        // Tree 6 - Young Oak
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(80, -25, 10)), 5, 30)
                        .setEmission(new Color(40, 30, 18))
                        .setMaterial(darkBark),
                new Sphere(new Point(80, 15, 10), 20)
                        .setEmission(new Color(35, 65, 40))
                        .setMaterial(colorfulLeaves)
        );

        // Tree 7 - Crooked Pine
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0.2, 1, 0), new Point(-40, -25, -15)), 6, 42)
                        .setEmission(new Color(32, 24, 12))
                        .setMaterial(darkBark),
                new Sphere(new Point(-35, 25, -15), 19)
                        .setEmission(new Color(20, 50, 35))
                        .setMaterial(colorfulLeaves)
        );

        // Tree 8 - Distant Giant
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(0, -25, -120)), 15, 60)
                        .setEmission(new Color(28, 20, 12))
                        .setMaterial(darkBark),
                new Sphere(new Point(0, 45, -120), 35)
                        .setEmission(new Color(18, 45, 30))
                        .setMaterial(colorfulLeaves)
        );

        // ===== SMALL TREES AND SAPLINGS WITH COLORFUL FOLIAGE =====
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-15, -25, -5)), 3, 20)
                        .setEmission(new Color(35, 25, 15)).setMaterial(darkBark),
                new Sphere(new Point(-15, -5, -5), 12)
                        .setEmission(new Color(30, 55, 35)).setMaterial(colorfulLeaves),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(15, -25, -25)), 2, 15)
                        .setEmission(new Color(30, 20, 12)).setMaterial(darkBark),
                new Sphere(new Point(15, -10, -25), 10)
                        .setEmission(new Color(45, 35, 25)).setMaterial(colorfulLeaves), // Autumn colors

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-50, -25, 60)), 2, 18)
                        .setEmission(new Color(38, 28, 15)).setMaterial(darkBark),
                new Sphere(new Point(-50, -7, 60), 11)
                        .setEmission(new Color(35, 60, 40)).setMaterial(colorfulLeaves),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(50, -25, -10)), 3, 22)
                        .setEmission(new Color(28, 18, 12)).setMaterial(darkBark),
                new Sphere(new Point(50, -3, -10), 13)
                        .setEmission(new Color(55, 40, 30)).setMaterial(colorfulLeaves) // Reddish autumn
        );

        // ===== MAGICAL CRYSTAL FORMATIONS =====
        // Add some transparent/reflective crystal formations
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(0, 1, 0.2), new Point(-25, -25, 5)), 3, 15)
                        .setEmission(new Color(40, 60, 80))
                        .setMaterial(crystalMaterial),

                new Cylinder(new Ray(new Vector(0.1, 1, 0), new Point(35, -25, 15)), 2, 12)
                        .setEmission(new Color(60, 40, 80))
                        .setMaterial(crystalMaterial),

                new Sphere(new Point(-10, -18, 25), 4)
                        .setEmission(new Color(50, 80, 60))
                        .setMaterial(crystalMaterial)
        );

        // ===== FALLEN LOGS =====
        scene.geometries.add(
                new Cylinder(new Ray(new Vector(1, 0, 0.3), new Point(-80, -20, 30)), 5, 25)
                        .setEmission(new Color(30, 22, 12)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(0.8, 0, -0.6), new Point(40, -22, 70)), 4, 20)
                        .setEmission(new Color(32, 25, 15)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(-0.7, 0, 0.7), new Point(20, -23, -50)), 6, 18)
                        .setEmission(new Color(28, 18, 10)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(1, 0.2, 0), new Point(-30, -21, 80)), 3, 15)
                        .setEmission(new Color(35, 28, 15)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(0, 0, 1), new Point(70, -24, 50)), 4, 22)
                        .setEmission(new Color(30, 22, 12)).setMaterial(deadWood),

                new Cylinder(new Ray(new Vector(-1, 0, 0.5), new Point(-10, -20, -70)), 5, 16)
                        .setEmission(new Color(32, 25, 15)).setMaterial(deadWood)
        );

        // ===== ENHANCED BOULDERS WITH REFLECTION =====
        scene.geometries.add(
                new Sphere(new Point(-60, -15, 0), 12)
                        .setEmission(new Color(35, 35, 45)).setMaterial(wetRock),

                new Sphere(new Point(25, -18, 25), 10)
                        .setEmission(new Color(32, 32, 42)).setMaterial(wetRock),

                new Sphere(new Point(-25, -20, -30), 8)
                        .setEmission(new Color(34, 34, 45)).setMaterial(wetRock),

                new Sphere(new Point(65, -16, -20), 14)
                        .setEmission(new Color(30, 30, 40)).setMaterial(wetRock),

                new Sphere(new Point(-75, -19, 70), 9)
                        .setEmission(new Color(38, 38, 50)).setMaterial(wetRock),

                new Sphere(new Point(35, -17, 60), 11)
                        .setEmission(new Color(28, 28, 38)).setMaterial(wetRock),

                new Sphere(new Point(-45, -21, -60), 7)
                        .setEmission(new Color(40, 40, 55)).setMaterial(wetRock),

                new Sphere(new Point(10, -19, 90), 13)
                        .setEmission(new Color(32, 32, 42)).setMaterial(wetRock)
        );

        // ===== SMALL ROCKS AND STONES =====
        scene.geometries.add(
                new Sphere(new Point(-10, -23, 15), 3).setEmission(new Color(28, 28, 35)).setMaterial(wetRock),
                new Sphere(new Point(5, -24, -8), 2).setEmission(new Color(30, 30, 38)).setMaterial(wetRock),
                new Sphere(new Point(-35, -23, 25), 4).setEmission(new Color(26, 26, 32)).setMaterial(wetRock),
                new Sphere(new Point(55, -24, 35), 3).setEmission(new Color(32, 32, 40)).setMaterial(wetRock),
                new Sphere(new Point(-65, -23, -15), 2).setEmission(new Color(34, 34, 42)).setMaterial(wetRock),
                new Sphere(new Point(75, -24, -35), 4).setEmission(new Color(28, 28, 35)).setMaterial(wetRock),
                new Sphere(new Point(-80, -23, 45), 3).setEmission(new Color(30, 30, 38)).setMaterial(wetRock),
                new Sphere(new Point(45, -24, 75), 2).setEmission(new Color(36, 36, 45)).setMaterial(wetRock),
                new Sphere(new Point(-20, -23, 65), 3).setEmission(new Color(26, 26, 32)).setMaterial(wetRock),
                new Sphere(new Point(20, -24, -35), 4).setEmission(new Color(32, 32, 40)).setMaterial(wetRock),
                new Sphere(new Point(-55, -23, -45), 2).setEmission(new Color(28, 28, 35)).setMaterial(wetRock),
                new Sphere(new Point(85, -24, 5), 3).setEmission(new Color(30, 30, 38)).setMaterial(wetRock),
                new Sphere(new Point(-85, -23, 15), 2).setEmission(new Color(34, 34, 42)).setMaterial(wetRock),
                new Sphere(new Point(30, -24, -55), 4).setEmission(new Color(26, 26, 32)).setMaterial(wetRock),
                new Sphere(new Point(-30, -23, 45), 3).setEmission(new Color(32, 32, 40)).setMaterial(wetRock),
                new Sphere(new Point(60, -24, 85), 2).setEmission(new Color(28, 28, 35)).setMaterial(wetRock),
                new Sphere(new Point(-70, -23, 85), 3).setEmission(new Color(30, 30, 38)).setMaterial(wetRock),
                new Sphere(new Point(40, -24, -75), 4).setEmission(new Color(36, 36, 45)).setMaterial(wetRock),
                new Sphere(new Point(-40, -23, -75), 2).setEmission(new Color(26, 26, 32)).setMaterial(wetRock),
                new Sphere(new Point(0, -24, 45), 3).setEmission(new Color(32, 32, 40)).setMaterial(wetRock),
                new Sphere(new Point(-5, -23, -45), 2).setEmission(new Color(28, 28, 35)).setMaterial(wetRock)
        );

        // ===== COLORFUL MUSHROOMS =====
        scene.geometries.add(
                // Magical glowing mushrooms
                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-78, -18, 32)), 1, 3)
                        .setEmission(new Color(40, 35, 25)).setMaterial(moss),
                new Sphere(new Point(-78, -15, 32), 2)
                        .setEmission(new Color(60, 30, 40)) // Purple mushroom
                        .setMaterial(moss),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-12, -25, 8)), 0.5, 2)
                        .setEmission(new Color(35, 28, 20)).setMaterial(moss),
                new Sphere(new Point(-12, -23, 8), 1.5)
                        .setEmission(new Color(50, 25, 25)) // Red mushroom
                        .setMaterial(moss),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(8, -25, 12)), 0.8, 2.5)
                        .setEmission(new Color(38, 32, 25)).setMaterial(moss),
                new Sphere(new Point(8, -22.5, 12), 1.8)
                        .setEmission(new Color(25, 45, 35)) // Green mushroom
                        .setMaterial(moss),

                new Cylinder(new Ray(new Vector(0, 1, 0), new Point(-38, -25, 55)), 0.6, 2)
                        .setEmission(new Color(32, 25, 18)).setMaterial(moss),
                new Sphere(new Point(-38, -23, 55), 1.3)
                        .setEmission(new Color(45, 35, 55)) // Blue-purple mushroom
                        .setMaterial(moss)
        );

        // ===== ENHANCED LIGHTING WITH COLORED EFFECTS =====

        // Strong moonlight from the moon's position
        scene.lights.add(new DirectionalLight(
                new Vector(0.3, -0.8, -0.2),
                new Color(100, 110, 130) // Brighter cool moonlight
        ));

        // Direct light from the moon itself
        scene.lights.add(new PointLight(
                new Color(150, 160, 170),
                new Point(-80, 80, -180)) // Moon's position
                .setKl(0.0001).setKq(0.000001)
        );

        // Colorful moonbeam filtering through trees
        scene.lights.add(new SpotLight(
                new Color(80, 90, 120), // Cool blue moonbeam
                new Point(-30, 60, 30),
                new Vector(0.3, -1, -0.2))
                .setKl(0.0005).setKq(0.00005)
                .setNarrowBeam(6)
        );

        // Magical purple light
        scene.lights.add(new SpotLight(
                new Color(90, 60, 120), // Purple magical light
                new Point(50, 70, -40),
                new Vector(-0.4, -1, 0.3))
                .setKl(0.0005).setKq(0.00005)
                .setNarrowBeam(8)
        );

        // Warm amber glow from magical crystals
        scene.lights.add(new PointLight(
                new Color(80, 90, 60), // Warm amber
                new Point(-25, -10, 5))
                .setKl(0.002).setKq(0.0002)
        );

        // Atmospheric fill light with slight color variation
        scene.lights.add(new PointLight(
                new Color(35, 40, 60), // Slightly purple fill
                new Point(0, 50, 0))
                .setKl(0.001).setKq(0.00008)
        );

        // Additional colored accent lights
        scene.lights.add(new PointLight(
                new Color(60, 80, 50), // Green forest glow
                new Point(-60, 20, 40))
                .setKl(0.003).setKq(0.0003)
        );

        scene.lights.add(new PointLight(
                new Color(70, 50, 90), // Purple mystical glow
                new Point(40, 25, -30))
                .setKl(0.003).setKq(0.0003)
        );

        // Render the enhanced magical forest scene
        camera.renderImage()
                .writeToImage("MoonlitForest-AA(300)");
    }
}