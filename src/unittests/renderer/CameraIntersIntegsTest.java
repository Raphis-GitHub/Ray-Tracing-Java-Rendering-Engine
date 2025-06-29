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
     *
     * @throws CloneNotSupportedException if cloning fails
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

        //TC 01: Sphere is in front of the camera
        Sphere sphere1 = new Sphere(new Point(0, 0, -3), 1);
        assertEquals(2, assertPixelIntersections(camera1, sphere1), "Sphere in front of camera should have 2 intersection points");

        //TC 02: view plane is inside the sphere
        Camera camera_2_3_4_5 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();
        Sphere sphere2 = new Sphere(new Point(0, 0, -2.5), 2.5);
        assertEquals(18, assertPixelIntersections(camera_2_3_4_5, sphere2), "Camera inside sphere should have 18 intersection points");

        //TC 03: View plane is inside the sphere
        Sphere sphere3 = new Sphere(new Point(0, 0, -2), 2);
        assertEquals(10, assertPixelIntersections(camera_2_3_4_5, sphere3), "View plane inside sphere should have 10 intersection points");

        //TC 04: Camera is inside the sphere
        Sphere sphere4 = new Sphere(new Point(0, 0, -2), 4);
        assertEquals(9, assertPixelIntersections(camera_2_3_4_5, sphere4), "Camera inside sphere should have 9 intersection points");

        //TC 05: Sphere is behind the camera
        Sphere sphere5 = new Sphere(new Point(0, 0, 1), 0.5);
        assertEquals(0, assertPixelIntersections(camera1, sphere5), "Sphere behind camera should have 0 intersection points");
    }

    /**
     * Tests camera-triangle intersections.
     *
     * @throws CloneNotSupportedException if cloning fails
     */
    @Test
    void testCameraIntersectionsWithTriangle() {
        //TC 01: Triangle is in front of the camera and is the size of a pixel
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .setVpDistance(1)
                .build();
        Triangle triangle1 = new Triangle(new Point(-1, -1, -2), new Point(1, -1, -2), new Point(0, 1, -2));
        assertEquals(1, assertPixelIntersections(camera1, triangle1), "Triangle in front of camera should have 1 intersection point");
        //TC 02: Triangle is in front of the camera and is the size of the view plane
        Triangle triangle2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertEquals(2, assertPixelIntersections(camera1, triangle2), "Triangle in front of camera should have 3 intersection points");

    }

    /**
     * Tests camera-plane intersections.
     *
     * @throws CloneNotSupportedException if cloning fails
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

        //TC 01: Plane is in front of the camera and orthogonal to the view plane
        assertPixelIntersections(9, camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, -1)));

        //TC 02: Plane is in front of the camera and has a different angle
        Plane plane2 = new Plane(new Point(0, 0, -2), new Point(0, 5, -1.5), new Point(1, -20, -4));
        assertEquals(9, assertPixelIntersections(camera, plane2), "Plane in front of camera with different angle should have 9 intersection points");

        //TC 03: Plane is in front of the camera and has a different angle
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
