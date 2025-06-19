package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Triangle.findIntersections
 */
public class TriangleTest {

    /**
     * Getnormal tests
     * //TODO Add test cases
     */
    @Test
    void testGetNormal() {
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
