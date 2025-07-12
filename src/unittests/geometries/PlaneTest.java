package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

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

        Point p0 = new Point(0, 0, 0);
        Point p1 = new Point(1, 1, 1);

        //============================Equivalence Partitions Test==============================
        //TC01: Correct plane with three non-collinear points
        assertDoesNotThrow(() -> {
            Plane plane = new Plane(
                    p0,
                    new Point(1, 0, 0),
                    new Point(0, 1, 0)
            );

            Vector normal = plane.getNormal();

            Vector v1 = new Vector(1, 0, 0);
            Vector v2 = new Vector(0, 1, 0);

            //TC02: Ensure normal is perpendicular to at least two vectors on the plane
            assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not perpendicular to first vector");
            assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not perpendicular to second vector");

            //TC03: Ensure normal is normalized (length = 1)
            assertEquals(1, normal.length(), DELTA, "Normal vector is not normalized");
        }, "Plane constructor with three points failed");

        //============================Boundary Values Tests==============================

        //TC11: First and second points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        p0, p0, p1),
                "Plane constructor allowed first and second points to coincide");

        //TC12: First and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        p0, p1, p0),
                "Plane constructor allowed first and third points to coincide");

        //TC13: Second and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        p1, p0, p0),
                "Plane constructor allowed second and third points to coincide");

        //TC14: All points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        p1, p1, p1),
                "Plane constructor allowed all points to coincide");

        //TC15: Points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        p0, p1, new Point(2, 2, 2)),
                "Plane constructor allowed collinear points");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     */
    @Test
    void testConstructorPointVector() {
        Point p0 = new Point(0, 0, 0);
        Vector normal = new Vector(0, 0, 1);
        //============================Equivalence Partitions Test==============================
        assertDoesNotThrow(() -> {
            Plane plane = new Plane(p0, normal);
            //TC01: Ensure normal is set correctly
            assertEquals(1, plane.getNormal().length(), DELTA, "Normal vector is not normalized");
            //TC02: Ensure normal is perpendicular to the plane
            assertEquals(normal.normalize(), plane.getNormal(), "Plane constructor with point and vector failed to set normal correctly");
        }, "Plane constructor with point and vector threw an unexpected exception");
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Point p1 = new Point(1, 0, 1);
        Point p2 = new Point(0, 1, 1);

        Plane plane = new Plane(p0, p1, p2);
        //============================Equivalence Partitions Test==============================
        //TC01: Check normal vector
        assertDoesNotThrow(() -> plane.getNormal(new Point(0, 0, 1)), "Plane.getNormal() threw an unexpected exception");
        Vector normal = plane.getNormal(new Point(0, 0, 1));
        assertNotNull(normal, "Normal vector should not be null");
        // check normal length == 1
        assertEquals(1, normal.length(), DELTA, "Normal is not a unit vector");
        // Check orthogonality to two edges
        Vector v1 = p1.subtract(p0);
        Vector v2 = p2.subtract(p0);
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
        assertEquals(List.of(new Point(0, 0, 1)), result, "TC01: Incorrect intersection point");

        // TC02: Ray does not intersect the plane
        ray = new Ray(new Vector(0, 1, 0), new Point(0, 0, 0));
        assertNull(plane.findIntersections(ray), "TC02: Ray parallel and outside plane");

        // =============== Boundary Values Tests ==================

        // TC11: Ray is orthogonal to plane and starts before (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, -1));
        result = plane.findIntersections(ray);
        assertEquals(List.of(new Point(0, 0, 1)), result, "TC11: Incorrect intersection");

        // TC12: Ray is orthogonal to plane and starts on plane (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 2));
        assertNull(plane.findIntersections(ray), "TC12: Ray starts on plane");

        // TC13: Ray is orthogonal and starts after plane (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 2));
        assertNull(plane.findIntersections(ray), "TC13: Ray after plane");

        // TC14: Ray is in the plane (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 1));
        assertNull(plane.findIntersections(ray), "TC14: Ray in the plane");
    }

    /**
     * Test method for {@link Plane#calculateIntersectionsHelper(Ray, double)}.
     */
    @Test
    void testIntersectionWithDistance() {
        final Point p1 = new Point(0, 0, 2);
        final Point p2 = new Point(0, 0, 1);
        final Vector v = new Vector(0, 0, 1);
        final Plane plane = new Plane(p2, v);
        // ============ Equivalence Partitions Tests ==============
        // TC01: The distance between the ray intersection point and the ray's start point is more than the distance(0 points)
        assertNull(plane.calculateIntersectionsHelper(new Ray(new Vector(0, 1, -1), p1), 1),
                "Ray's intersection point is out of the distance");

        // TC02: The distance between the ray intersection point and the ray's start point is less than the distance(1 point)
        assertEquals(1, plane.calculateIntersectionsHelper(new Ray(new Vector(0, 1, -1), p1), 10).size(),
                "Ray's intersection points is in the distance");
    }
}
