package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

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
                "Constructed a polygon with vertex on a side");

        // TC11: Last point = first point
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                "Constructed a polygon with vertex on a side");

        // TC12: Co-located points
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
                "Constructed a polygon with vertex on a side");

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
    public void testFindIntersections() {
        Polygon poly = new Polygon(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));
        Plane plane = new Plane(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));
        // ============ Equivalence Partitions Tests ==============
        // TC01: inside Polygon
        assertEquals(List.of(new Point(1d / 3, 1d / 3, 1d / 3)), poly.findIntersections(new Ray(new Vector(-1, -1, -1), new Point(1, 1, 1))), "ERROR: bad intersect");
        // TC02: in between poly rays outside poly
        Ray ray = new Ray(new Vector(-1, -1, 0), new Point(0, 0, 2));
        // first check if it is in Plane
        assertEquals(List.of(new Point(-0.5, -0.5, 2)), plane.findIntersections(ray), "ERROR: wrong intersect with plane");
        assertNull(poly.findIntersections(ray), "ERROR: intersect when shouldn't be");
        // TC03: on other side of edge outside triangle
        // first check if it is in the Plane
        ray = new Ray(new Vector(1, 1, 0), new Point(0, 0, -1));
        assertEquals(List.of(new Point(1, 1, -1)), plane.findIntersections(ray), "ERROR: wrong intersect with plane");
        assertNull(poly.findIntersections(ray), "Bad intersection");
        // =============== Boundary Values Tests ==================
        // TC11: intersects on edge
        ray = new Ray(new Vector(1, 1, 0), new Point(-1, -1, 0));
        // check plane first
        assertEquals(List.of(new Point(0.5, 0.5, 0)), plane.findIntersections(ray), "ERROR: Wrong intersection with plane");
        assertNull(poly.findIntersections(ray), "ERROR: intersect where it shouldn't be");
        // TC12: intersect on vertex
        ray = new Ray(new Vector(1, 1, 0), new Point(-1, 0, 0));
        // check plane first
        assertEquals(List.of(new Point(0, 1, 0)), plane.findIntersections(ray), "ERROR: Wrong intersection with plane");
        assertNull(poly.findIntersections(ray), "ERROR: intersect where it shouldn't be");
        // TC13: intersect on edge continuation
        ray = new Ray(new Vector(1, 1, 0), new Point(-2, 0, 0));
        assertEquals(List.of(new Point(-0.5, 1.5, 0)), plane.findIntersections(ray), "ERROR: Wrong intersection with plane");
        assertNull(poly.findIntersections(ray), "ERROR: intersect where it shouldn't be");
    }
}