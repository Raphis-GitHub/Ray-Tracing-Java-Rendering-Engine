package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Polygons
 *
 * @author Raphael
 */
class PolygonTests {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link Polygon#Polygon(Point...)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct concave quadrangular with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(-1, 1, 1)),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0), new Point(-1, 1, 1)), //
                "Constructed a polygon with wrong order of vertices");

        // TC03: Not in the same plane
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 2, 2)), //
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0.5, 0.25, 0.5)), //
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC10: Vertex on a side of a quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0, 0.5, 0.5)),
                "Constructed a polygon with vertix on a side");

        // TC11: Last point = first point
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                "Constructed a polygon with vertice on a side");

        // TC12: Co-located points
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
                "Constructed a polygon with vertice on a side");

    }

    /**
     * Test method for {@link Polygon#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here - using a quad
        Point[] pts =
                {new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1)};
        Polygon pol = new Polygon(pts);
        // ensure there are no exceptions
        assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
        // generate the test result
        Vector result = pol.getNormal(new Point(0, 0, 1));
        // ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
        // ensure the result is orthogonal to all the edges
        for (int i = 0; i < 3; ++i)
            assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
                    "Polygon's normal is not orthogonal to one of the edges");
    }

    /**
     * Test method for {@link Polygon#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Test with a square polygon for simplicity
        Polygon square = new Polygon(
                new Point(0, 0, 1),
                new Point(2, 0, 1),
                new Point(2, 2, 1),
                new Point(0, 2, 1)
        );

        // Test with a pentagon for more complex cases
        Polygon pentagon = new Polygon(
                new Point(1, 0, 0),
                new Point(0.31, 0.95, 0),
                new Point(-0.81, 0.59, 0),
                new Point(-0.81, -0.59, 0),
                new Point(0.31, -0.95, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the polygon (1 point)
        Ray ray = new Ray(new Vector(0, 0, 1), new Point(1, 1, 0));
        List<Point> result = square.findIntersections(ray);
        assertNotNull(result, "TC01: Ray inside polygon should intersect");
        assertEquals(1, result.size(), "TC01: Wrong number of points");
        assertEquals(new Point(1, 1, 1), result.get(0), "TC01: Wrong intersection point");

        // TC02: Ray outside polygon - against edge (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(3, 1, 0));
        assertNull(square.findIntersections(ray),
                "TC02: Ray outside against edge should return null");

        // TC03: Ray outside polygon - against vertex (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(-1, -1, 0));
        assertNull(square.findIntersections(ray),
                "TC03: Ray outside against vertex should return null");

        // TC04: Ray parallel to polygon plane (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 1, 0.5));
        assertNull(square.findIntersections(ray),
                "TC04: Ray parallel to plane should return null");

        // TC05: Ray in polygon plane (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 1, 1));
        assertNull(square.findIntersections(ray),
                "TC05: Ray in plane should return null");

        // TC06: Ray perpendicular to plane, starts above (1 point)
        ray = new Ray(new Vector(0, 0, -1), new Point(1, 1, 2));
        result = square.findIntersections(ray);
        assertNotNull(result, "TC06: Ray perpendicular from above should intersect");
        assertEquals(1, result.size(), "TC06: Wrong number of points");
        assertEquals(new Point(1, 1, 1), result.get(0), "TC06: Wrong intersection point");

        // TC07: Ray starts after polygon (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1, 1, 2));
        assertNull(square.findIntersections(ray),
                "TC07: Ray starting after polygon should return null");

        // =============== Boundary Values Tests ==================

        // **** Group: Ray intersects polygon boundary

        // TC11: Ray intersects on edge (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1, 0, 0));
        assertNull(square.findIntersections(ray),
                "TC11: Ray on edge should return null");

        // TC12: Ray intersects on vertex (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        assertNull(square.findIntersections(ray),
                "TC12: Ray on vertex should return null");

        // TC13: Ray on edge continuation (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(3, 0, 0));
        assertNull(square.findIntersections(ray),
                "TC13: Ray on edge continuation should return null");

        // **** Group: Ray starts on polygon

        // TC21: Ray starts inside polygon (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(1, 1, 1));
        assertNull(square.findIntersections(ray),
                "TC21: Ray starting on polygon should return null");

        // TC22: Ray starts on edge (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1, 0, 1));
        assertNull(square.findIntersections(ray),
                "TC22: Ray starting on edge should return null");

        // TC23: Ray starts on vertex (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 1));

        // **** Group: Special cases with pentagon

        // TC31: Ray through center of pentagon (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, -1));
        result = pentagon.findIntersections(ray);
        assertEquals(1, result.size(), "TC31: Wrong number of points");
        assertEquals(new Point(0, 0, 0), result.get(0), "TC31: Wrong intersection point");

        // TC32: Ray near vertex but inside (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.9, 0, -1));
        result = pentagon.findIntersections(ray);
        assertEquals(1, result.size(), "TC32: Wrong number of points");

        // TC33: Ray near edge but inside (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.5, 0.5, -1));
        result = pentagon.findIntersections(ray);
        assertEquals(1, result.size(), "TC33: Wrong number of points");
    }
}