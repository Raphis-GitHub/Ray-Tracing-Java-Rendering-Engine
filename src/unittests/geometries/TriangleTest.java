package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Triangle.findIntersections
 */
public class TriangleTest {

    private static final double DELTA = 1e-10;

    /**
     * Getnormal tests
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 1, 0);
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(-1, 0, 0);
        Triangle triangle = new Triangle(p0, p1, p2);
        // ============ Equivalence Partitions Tests ==============
        // Get the normal at a point
        Vector normal = triangle.getNormal(new Point(0, 0, 0));
        // The expected normal (could be (0,0,1) or (0,0,-1))
        Vector expectedNormal = new Vector(0, 0, -1);
        // Allow for opposite direction
        boolean sameDirection = normal.equals(expectedNormal);
        boolean oppositeDirection = normal.equals(expectedNormal.scale(-1));
        assertTrue(sameDirection || oppositeDirection, "Normal vector is not as expected (or its opposite)");
        // Check normal is perpendicular to two edges
        Vector v1 = p1.subtract(p0);
        Vector v2 = p2.subtract(p0);
        assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not perpendicular to first edge");
        assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not perpendicular to second edge");
        // Check normal is normalized
        assertEquals(1, normal.length(), DELTA, "Normal is not a unit vector");
    }

    /**
     * Test method for {@link Triangle#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(
                new Point(0, 1, 0),
                new Point(1, 0, 0),
                new Point(-1, 0, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the triangle
        Ray ray = new Ray(new Vector(0, 0, 1), new Point(0, 0.5, -1));

        List<Point> result = triangle.findIntersections(ray);

        assertNotNull(result, "TC01: Expected 1 intersection");
        assertEquals(1, result.size(), "TC01: Wrong number of points");
        assertEquals(new Point(0, 0.5, 0), result.getFirst(), "TC01: Incorrect point");

        // TC02: Ray intersects outside the triangle against edge
        ray = new Ray(new Vector(0, -1, 1), new Point(2, 1, -1));
        assertNull(triangle.findIntersections(ray), "TC02: Ray outside against edge");

        // TC03: Ray intersects outside the triangle against vertex
        ray = new Ray(new Vector(0, -1, 1), new Point(1, 1, -1));
        assertNull(triangle.findIntersections(ray), "TC03: Ray outside against vertex");

        // =============== Boundary Values Tests ==================

        // TC11: Ray intersects on edge of triangle
        ray = new Ray(new Vector(0, -1, 1), new Point(0.5, 0.5, -1));
        assertNull(triangle.findIntersections(ray), "TC11: Ray on edge");

        // TC12: Ray intersects on vertex of triangle
        ray = new Ray(new Vector(0, -1, 1), new Point(0, 1, -1));
        assertNull(triangle.findIntersections(ray), "TC12: Ray on vertex");

        // TC13: Ray intersects on edge extension
        ray = new Ray(new Vector(0, -1, 1), new Point(-2, 1, -1));
        assertNull(triangle.findIntersections(ray), "TC13: Ray on edge extension");
    }

}
