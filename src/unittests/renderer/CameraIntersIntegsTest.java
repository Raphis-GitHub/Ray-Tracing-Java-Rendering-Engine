package renderer;

import geometries.*;
import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests camera and geometry intersection counts.
 */
public class CameraIntersIntegsTest {

    /**
     * Tests camera-sphere intersections.
     */
    @Test
    void testCameraIntersectionsWithSphere() {
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();
        // ============ Equivalence Partitions Tests ==============
        // TC01: Sphere is in front of the camera
        Sphere sphere1 = new Sphere(new Point(0, 0, -3), 1);
        assertPixelIntersections(2, camera1, sphere1);

        Camera camera_2_3_4_5 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();

        // TC02: Camera is just outside a large sphere intersecting the view plane
        Sphere sphere2 = new Sphere(new Point(0, 0, -2.5), 2.5);
        assertPixelIntersections(18, camera_2_3_4_5, sphere2);

        // TC03: View plane intersects a large sphere
        Sphere sphere3 = new Sphere(new Point(0, 0, -2), 2);
        assertPixelIntersections(10, camera_2_3_4_5, sphere3);

        // TC04: Camera is inside a large sphere
        Sphere sphere4 = new Sphere(new Point(0, 0, -2), 4);
        assertPixelIntersections(9, camera_2_3_4_5, sphere4);

        // TC05: Sphere is behind the camera
        Sphere sphere5 = new Sphere(new Point(0, 0, 1), 0.5);
        assertPixelIntersections(0, camera1, sphere5);
    }

    /**
     * Tests camera-triangle intersections.
     */
    @Test
    void testCameraIntersectionsWithTriangle() {
        //========================Equivalence Partitions Tests========================
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();

        // TC01: Small triangle in front of the camera
        Triangle triangle1 = new Triangle(new Point(-1, -1, -2), new Point(1, -1, -2), new Point(0, 1, -2));
        assertPixelIntersections(1, camera1, triangle1);
        //TC 02: Triangle is in front of the camera and is the size of the view plane
        Triangle triangle2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertPixelIntersections(2, camera1, triangle2);

    }

    /**
     * Tests camera-plane intersections.
     */
    @Test
    void testCameraIntersectionsWithPlane() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();

        // =============== Boundary Values Tests ==================
        // TC01: Plane orthogonal to the view plane
        assertPixelIntersections(9, camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, -1)));

        // TC02: Plane with a moderate tilt angle
        Plane plane2 = new Plane(new Point(0, 0, -2), new Point(0, 5, -1.5), new Point(1, -20, -4));
        assertPixelIntersections(9, camera, plane2);

        // TC03: Plane with a steeper tilt angle
        Plane plane3 = new Plane(new Point(0, 0, -2), new Point(0, 10, -1.5), new Point(1, 5, -4));
        assertPixelIntersections(6, camera, plane3);

    }

    /**
     * Counts intersection points for all camera rays and a geometry.
     *
     * @param camera   the camera to use for ray construction
     * @param geometry the geometry to check for intersections
     */
    private void assertPixelIntersections(int expected, Camera camera, Geometry geometry) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Ray ray = camera.constructRay(3, 3, i, j);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        assertEquals(expected, count, "wrong amount of intersections found");
    }
}
