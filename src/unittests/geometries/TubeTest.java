package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tube class
 *
 * @author Raphael
 */
class TubeTest {

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        Tube tube = new Tube(axis, 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Point on the side of the tube (X direction)
        Point p1 = new Point(1, 0, 5);
        assertEquals(new Vector(1, 0, 0), tube.getNormal(p1),
                "Wrong normal on X side");

        // TC02: Point on the side of the tube (Y direction)
        Point p2 = new Point(0, 1, 10);
        assertEquals(new Vector(0, 1, 0), tube.getNormal(p2),
                "Wrong normal on Y side");

        // TC03: Point on diagonal side
        Point p3 = new Point(1, 1, 7);
        Vector expected3 = new Vector(1, 1, 0).normalize();
        assertEquals(expected3, tube.getNormal(p3),
                "Wrong normal on diagonal side");

        // =============== Boundary Values Tests ==================

        // TC11: Point exactly on axis projection origin
        Point p4 = new Point(1, 0, 0);
        assertEquals(new Vector(1, 0, 0), tube.getNormal(p4),
                "Wrong normal at axis base");

        // TC12: Point on high Z value
        Point p5 = new Point(0, -1, 1000);
        assertEquals(new Vector(0, -1, 0), tube.getNormal(p5),
                "Wrong normal at high Z");

        // TC13: Very close point with precision check
        Point p6 = new Point(0.000001, 1, 3);
        Vector expected6 = new Vector(0.000001, 1, 0).normalize();
        assertEquals(expected6, tube.getNormal(p6),
                "Wrong normal for near-axis diagonal");
    }

    /**
     * Test method for {@link Tube#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        Tube tube1 = new Tube(new Ray(new Vector(0, 1, 0), new Point(1, 0, 0)), 1d);
        Vector vAxis = new Vector(0, 0, 1);
        Tube tube2 = new Tube(new Ray(vAxis, new Point(1, 1, 1)), 1d);
        Ray ray;

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray's line is outside the tube (0 points)
        ray = new Ray(new Vector(1, 1, 0), new Point(1, 1, 2));
        assertNull(tube1.findIntersections(ray), "Must not be intersections");

        // TC02: Ray's crosses the tube (2 points)
        ray = new Ray(new Vector(2, 1, 1), new Point(0, 0, 0));
        List<Point> result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC03: Ray's starts within tube and crosses the tube (1 point)
        ray = new Ray(new Vector(2, 1, 1), new Point(1, 0.5, 0.5));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // =============== Boundary Values Tests ==================

        // Ray's line is parallel to the axis (0 points)
        // TC11: Ray is inside the tube (0 points)
        ray = new Ray(vAxis, new Point(0.5, 0.5, 0.5));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC12: Ray is outside the tube
        ray = new Ray(vAxis, new Point(0.5, -0.5, 0.5));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC13: Ray is at the tube surface
        ray = new Ray(vAxis, new Point(2, 1, 0.5));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC14: Ray is inside the tube and starts against axis head
        ray = new Ray(vAxis, new Point(0.5, 0.5, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC15: Ray is outside the tube and starts against axis head
        ray = new Ray(vAxis, new Point(0.5, -0.5, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC16: Ray is at the tube surface and starts against axis head
        ray = new Ray(vAxis, new Point(2, 1, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC17: Ray is inside the tube and starts at axis head
        ray = new Ray(vAxis, new Point(1, 1, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // Ray is orthogonal but does not begin against the axis head
        // TC21: Ray starts outside and the line is outside (0 points)
        ray = new Ray(new Vector(1, 1, 0), new Point(0, 2, 2));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC23: Ray starts before (2 points)
        ray = new Ray(new Vector(2, 1, 0), new Point(0, 0, 2));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC24: Ray starts at the surface and goes inside (1 point)
        ray = new Ray(new Vector(2, 1, 0), new Point(0.4, 0.2, 2));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC25: Ray starts inside (1 point)
        ray = new Ray(new Vector(2, 1, 0), new Point(1, 0.5, 2));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC26: Ray starts before and crosses the axis (2 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, -1, 2));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC27: Ray starts inside and the line crosses the axis (1 point)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0.5, 2));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC28: Ray starts at the surface and goes outside and the line crosses the axis (0 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 2, 2));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC29: Ray starts after and crosses the axis (0 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 3, 2));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // Ray is orthogonal to axis and begins against the axis head
        // TC40: Ray starts outside and the line is outside (0 Points)
        ray = new Ray(new Vector(1, 1, 0), new Point(0, 2, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC41: The line is tangent and the ray starts at the tube
        ray = new Ray(new Vector(1, 0, 0), new Point(1, 2, 1));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC42: The line is tangent and the ray starts after the tube
        ray = new Ray(new Vector(1, 0, 0), new Point(2, 2, 2));
        assertNull(tube2.findIntersections(ray), "Must not be intersections");

        // TC43: Ray starts before
        ray = new Ray(new Vector(2, 1, 0), new Point(0, 0, 1));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC44: Ray starts at the surface and goes inside
        ray = new Ray(new Vector(2, 1, 0), new Point(0.4, 0.2, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC45: Ray starts inside
        ray = new Ray(new Vector(2, 1, 0), new Point(1, 0.5, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC46: Ray starts after
        ray = new Ray(new Vector(2, 1, 0), new Point(4, 2, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC47: Ray starts before and goes through the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, -1, 1));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC48: Ray starts at the surface and goes inside and goes through the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC49: Ray starts inside and the line goes through the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0.5, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC50: Ray starts at the surface and the line goes outside and goes through
        // the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 2, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC51: Ray starts after and the line goes through the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 3, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC52: Ray start at the axis head
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 1, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // Ray's line is neither parallel nor orthogonal to the axis and begins against axis head
        Point p0 = new Point(0, 2, 1);
        // TC60: Ray's line is outside the tube
        ray = new Ray(new Vector(1, 1, 1), p0);
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC61: Ray's line crosses the tube and begins before
        ray = new Ray(new Vector(2, -1, 1), p0);
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC62: Ray's line crosses the tube and begins at surface and goes inside
        ray = new Ray(new Vector(2, -1, 1), new Point(0.4, 1.8, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC63: Ray's line crosses the tube and begins inside
        ray = new Ray(new Vector(2, -1, 1), new Point(1, 1.5, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC64: Ray's line crosses the tube and begins at the axis head
        ray = new Ray(new Vector(0, 1, 1), new Point(1, 1, 1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC65: Ray's line crosses the tube and begins at surface and goes outside
        ray = new Ray(new Vector(2, -1, 1), new Point(2, 1, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC66: Ray's line is tangent and begins before
        ray = new Ray(new Vector(0, 2, 1), p0);
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC67: Ray's line is tangent and begins at the tube surface
        ray = new Ray(new Vector(1, 0, 1), new Point(1, 2, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // TC68: Ray's line is tangent and begins after
        ray = new Ray(new Vector(1, 0, 1), new Point(2, 2, 1));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");

        // Group: Ray's line is neither parallel nor orthogonal to the axis and
        // does not begin against axis head
        double sqrt2 = Math.sqrt(2);
        double denomSqrt2 = 1 / sqrt2;
        double value1 = 1 - denomSqrt2;

        // TC70: Ray's crosses the tube and the axis
        ray = new Ray(new Vector(1, 1, 1), new Point(0, 0, 2));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC71: Ray's crosses the tube and the axis head
        ray = new Ray(new Vector(1, 1, 1), new Point(0, 0, 0));
        result = tube2.findIntersections(ray);
        assertEquals(2, result.size(), "must be 2 intersections");

        // TC72: Ray's begins at the surface and goes inside
        ray = new Ray(new Vector(1, 0, 1), new Point(value1, value1, 2 + value1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC73: Ray's begins at the surface and goes inside crossing the axis
        ray = new Ray(new Vector(1, 1, 1), new Point(value1, value1, 2 + value1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC74: Ray's begins at the surface and goes inside crossing the axis head
        ray = new Ray(new Vector(1, 1, 1), new Point(value1, value1, value1));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC75: Ray's begins inside and the line crosses the axis
        ray = new Ray(new Vector(1, 1, 1), new Point(0.5, 0.5, 2.5));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC76: Ray's begins inside and the line crosses the axis head
        ray = new Ray(new Vector(1, 1, 1), new Point(0.5, 0.5, 0.5));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC77: Ray's begins at the axis
        ray = new Ray(new Vector(1, 1, 1), new Point(1, 1, 3));
        result = tube2.findIntersections(ray);
        assertEquals(1, result.size(), "must be 1 intersection");

        // TC78: Ray's begins at the surface and goes outside
        ray = new Ray(new Vector(2, 1, 1), new Point(2, 1, 2));
        result = tube2.findIntersections(ray);
        assertNull(result, "Bad intersections");
    }
}