package renderer;

import geometries.Geometry;
import geometries.Plane;
import geometries.Sphere;
import geometries.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests camera and geometry intersection counts.
 */
public class CameraIntersIntegsTest {

    /**
     * EP01 to EP05: Equivalence Partitioning tests for camera-sphere intersections.
     */
    @Test
    void testCameraIntersectionsWithSphere() throws CloneNotSupportedException {
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
        assertEquals(2, assertPixelIntersections(camera1, sphere1), "Sphere in front of camera should have 2 intersection points");

        Camera camera_2_3_4_5 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();

        // TC02: Camera is just outside a large sphere intersecting the view plane
        Sphere sphere2 = new Sphere(new Point(0, 0, -2.5), 2.5);
        assertEquals(18, assertPixelIntersections(camera_2_3_4_5, sphere2), "Camera inside sphere should have 18 intersection points");

        // TC03: View plane intersects a large sphere
        Sphere sphere3 = new Sphere(new Point(0, 0, -2), 2);
        assertEquals(10, assertPixelIntersections(camera_2_3_4_5, sphere3), "View plane inside sphere should have 10 intersection points");

        // TC04: Camera is inside a large sphere
        Sphere sphere4 = new Sphere(new Point(0, 0, -2), 4);
        assertEquals(9, assertPixelIntersections(camera_2_3_4_5, sphere4), "Camera inside sphere should have 9 intersection points");

        // TC05: Sphere is behind the camera
        Sphere sphere5 = new Sphere(new Point(0, 0, 1), 0.5);
        assertEquals(0, assertPixelIntersections(camera1, sphere5), "Sphere behind camera should have 0 intersection points");
    }

    /**
     * TC06 to TC07: Equivalence Partitioning tests for camera-triangle intersections.
     */
    @Test
    void testCameraIntersectionsWithTriangle() {
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();

        // TC06: Small triangle in front of the camera
        Triangle triangle1 = new Triangle(new Point(-1, -1, -2), new Point(1, -1, -2), new Point(0, 1, -2));
        assertEquals(1, assertPixelIntersections(camera1, triangle1), "Triangle in front of camera should have 1 intersection point");

        // TC07: Large triangle in front of the camera
        Triangle triangle2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertEquals(2, assertPixelIntersections(camera1, triangle2), "Triangle in front of camera should have 2 intersection points");
    }

    /**
     * TC01 to TC03: Boundary Value Analysis tests for camera-plane intersections.
     */
    @Test
    void testCameraIntersectionsWithPlane() throws CloneNotSupportedException {
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
        assertEquals(9, assertPixelIntersections(camera, plane2), "Plane in front of camera with different angle should have 9 intersection points");

        // TC03: Plane with a steeper tilt angle
        Plane plane3 = new Plane(new Point(0, 0, -2), new Point(0, 10, -1.5), new Point(1, 5, -4));
        assertEquals(6, assertPixelIntersections(camera, plane3), "Plane in front of camera with different angle should have 6 intersection points");
    }

    /**
     * Counts intersection points for all camera rays and a geometry.
     *
     * @param camera   the camera to use for ray construction
     * @param geometry the geometry to check for intersections
     * @return the total number of intersection points found
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
