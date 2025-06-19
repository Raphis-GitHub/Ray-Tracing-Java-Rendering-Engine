package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Planes
 * these authors are Raphael and Eitan
 */
class PlaneTest {
    /**
     * accuracy check
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructorThreePoints() {
        Point P0 = new Point(0, 0, 0);
        Point P1 = new Point(1, 1, 1);

        // Equivalence Partitions Test
        assertDoesNotThrow(() -> {
            Plane plane = new Plane(
                    P0,
                    new Point(1, 0, 0),
                    new Point(0, 1, 0)
            );

            Vector normal = plane.getNormal();

            // Ensure normal is perpendicular to at least two vectors on the plane
            Vector v1 = new Vector(1, 0, 0);
            Vector v2 = new Vector(0, 1, 0);

            assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not perpendicular to first vector");
            assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not perpendicular to second vector");

            // Ensure normal is normalized (length = 1)
            assertEquals(1, normal.length(), DELTA, "Normal vector is not normalized");
        }, "Plane constructor with three points failed");

        // Boundary Values Tests - Collinear or coinciding points

        // Case 1: First and second points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P0, P1),
                "Plane constructor allowed first and second points to coincide");

        // Case 2: First and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P1, P0),
                "Plane constructor allowed first and third points to coincide");

        // Case 3: Second and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P1, P0, P0),
                "Plane constructor allowed second and third points to coincide");

        // Case 4: All points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P1, P1, P1),
                "Plane constructor allowed all points to coincide");

        // Case 5: Points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P1, new Point(2, 2, 2)),
                "Plane constructor allowed collinear points");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     */
    @Test
    void testConstructorPointVector() {
        Point P0 = new Point(0, 0, 0);
        Vector normal = new Vector(0, 0, 1);

        assertDoesNotThrow(() -> {
            Plane plane = new Plane(P0, normal);
            assertEquals(1, plane.getNormal().length(), DELTA, "Normal vector is not normalized");
            assertEquals(normal.normalize(), plane.getNormal(), "Plane constructor with point and vector failed to set normal correctly");
        }, "Plane constructor with point and vector threw an unexpected exception");
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Point P0 = new Point(0, 0, 1);
        Point P1 = new Point(1, 0, 1);
        Point P2 = new Point(0, 1, 1);

        Plane plane = new Plane(P0, P1, P2);
        Vector normal = plane.getNormal(new Point(0, 0, 1));

        // Check length is 1
        assertEquals(1, normal.length(), DELTA, "Normal is not a unit vector");

        // Check orthogonality to two edges
        Vector v1 = P1.subtract(P0);
        Vector v2 = P2.subtract(P0);

        assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not perpendicular to first edge");
        assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not perpendicular to second edge");
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(
                new Point(0, 0, 1),
                new Vector(0, 0, 1)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the plane (1 point)
        Ray ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        List<Point> result = plane.findIntersections(ray);
        assertNotNull(result, "TC01: Expected 1 intersection point");
        assertEquals(1, result.size(), "TC01: Wrong number of points");
        assertEquals(new Point(0, 0, 1), result.getFirst(), "TC01: Incorrect intersection point");

        // TC02: Ray does not intersect the plane
        ray = new Ray(new Vector(0, 1, 0), new Point(0, 0, 0));
        assertNull(plane.findIntersections(ray), "TC02: Ray parallel and outside plane");

        // =============== Boundary Values Tests ==================

        // TC11: Ray is orthogonal to plane and starts before (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, -1));
        result = plane.findIntersections(ray);
        assertNotNull(result, "TC11: Expected 1 point");
        assertEquals(new Point(0, 0, 1), result.getFirst(), "TC11: Incorrect intersection");

        // TC12: Ray is orthogonal to plane and starts on plane (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 2));
        assertNull(plane.findIntersections(ray), "TC12: Ray starts on plane");

        // TC13: Ray is orthogonal and starts after plane (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 2));
        assertNull(plane.findIntersections(ray), "TC13: Ray after plane");

        // TC14: Ray is in the plane (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 1));
        assertNull(plane.findIntersections(ray), "TC14: Ray in the plane");
        //TODO
    }

}
