package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
        // Main tube for most tests: axis along Z, radius 1
        Tube tube = new Tube(
                new Ray(new Vector(0, 0, 1), new Point(0, 0, 0)),
                1
        );

        // ============ Equivalence Partitions Tests ==============

        // **** Group 1: Ray parallel to tube axis

        // TC01: Ray outside tube, parallel to axis (0 points)
        Ray ray = new Ray(new Vector(0, 0, 1), new Point(2, 0, 0));

        // TC02: Ray inside tube, parallel to axis (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.5, 0, 0));

        // TC03: Ray near tube surface, parallel to axis (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1.001, 0, 0));

        // TC04: Ray on axis (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));

        // **** Group 2: Ray perpendicular to tube axis

        // TC11: Ray outside tube, perpendicular to axis (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(2, 2, 1));

        // TC12: Ray outside tube, perpendicular, aimed at tube (2 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 0, 1));
        List<Point> result = tube.findIntersections(ray);
        assertEquals(2, result.size(), "TC12: Wrong number of points");
        assertEquals(new Point(-1, 0, 1), result.get(0), "TC12: Wrong first point");
        assertEquals(new Point(1, 0, 1), result.get(1), "TC12: Wrong second point");

        // TC13: Ray inside tube, perpendicular to axis (1 point)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 1));
        result = tube.findIntersections(ray);
        assertEquals(1, result.size(), "TC13: Wrong number of points");
        assertEquals(new Point(1, 0, 1), result.get(0), "TC13: Wrong intersection point");

        // TC14: Ray starts after tube, perpendicular (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(2, 0, 1));

        // **** Group 3: Ray at angle to axis

        // TC21: Ray outside, at angle, misses tube (0 points)
        ray = new Ray(new Vector(1, 0, 1), new Point(-2, 2, 0));

        // TC22: Ray outside, at angle, intersects tube (2 points)
        ray = new Ray(new Vector(0, 1, 1).normalize(), new Point(0, -2, -2));
        result = tube.findIntersections(ray);
        assertEquals(2, result.size(), "TC22: Wrong number of points");

        // TC23: Ray inside, at angle (1 point)
        ray = new Ray(new Vector(1, 0, 1), new Point(0, 0, 0));
        result = tube.findIntersections(ray);
        assertEquals(1, result.size(), "TC23: Wrong number of points");

        // TC24: Ray starts near axis, goes out at angle (1 point)
        ray = new Ray(new Vector(1, 0, 0), new Point(0.001, 0, 1));
        result = tube.findIntersections(ray);
        assertEquals(1, result.size(), "TC24: Wrong number of points");

        // =============== Boundary Values Tests ==================

        // **** Group: Ray starts on tube surface

        // TC31: Ray starts on surface, goes out perpendicular (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(1, 0, 1));
        assertNull(tube.findIntersections(ray),
                "TC31: Ray from surface going out should return null");

        // TC32: Ray starts on surface, goes in perpendicular (1 point)
        ray = new Ray(new Vector(-1, 0, 0), new Point(1, 0, 1));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC32: Ray from surface going in should intersect");
        assertEquals(1, result.size(), "TC32: Wrong number of points");
        assertEquals(new Point(-1, 0, 1), result.get(0), "TC32: Wrong intersection point");

        // TC33: Ray starts on surface, tangent perpendicular (0 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0, 1));
        assertNull(tube.findIntersections(ray),
                "TC33: Tangent ray perpendicular should return null");

        // TC34: Ray starts on surface, goes out at angle (0 points)
        ray = new Ray(new Vector(1, 0, 1), new Point(1, 0, 0));
        assertNull(tube.findIntersections(ray),
                "TC34: Ray from surface at angle out should return null");

        // TC35: Ray starts on surface, goes in at angle (1 point)
        ray = new Ray(new Vector(-1, 0, 1), new Point(1, 0, 0));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC35: Ray from surface at angle in should intersect");
        assertEquals(1, result.size(), "TC35: Wrong number of points");

        // **** Group: Ray tangent to tube

        // TC41: Ray tangent to tube, outside (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1.001, 0, 0));
        assertNull(tube.findIntersections(ray),
                "TC41: Ray tangent outside should return null");

        // TC42: Ray tangent perpendicular to axis (0 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0, 1));
        assertNull(tube.findIntersections(ray),
                "TC42: Tangent ray perpendicular should return null");

        // TC43: Ray near-tangent at angle (0 points)
        Vector tangentDir = new Vector(0, 1, 1).normalize();
        Point startPoint = new Point(1.001, -1, -1);
        ray = new Ray(tangentDir, startPoint);
        assertNull(tube.findIntersections(ray),
                "TC43: Near-tangent ray at angle should return null");

        // **** Group: Ray through axis

        // TC51: Ray through axis perpendicular (2 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 0, 5));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC51: Ray through axis should intersect");
        assertEquals(2, result.size(), "TC51: Wrong number of points");
        assertEquals(new Point(-1, 0, 5), result.get(0), "TC51: Wrong first point");
        assertEquals(new Point(1, 0, 5), result.get(1), "TC51: Wrong second point");

        // TC52: Ray through axis at angle (2 points)
        ray = new Ray(new Vector(1, 0, 1).normalize(), new Point(-2, 0, 3));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC52: Ray through axis at angle should intersect");
        assertEquals(2, result.size(), "TC52: Wrong number of points");

        // **** Group: Different tube orientations

        // TC61: Tube with axis along X direction
        Tube tubeX = new Tube(
                new Ray(new Vector(1, 0, 0), new Point(0, 0, 0)),
                1
        );
        ray = new Ray(new Vector(0, 1, 0), new Point(5, -2, 0));
        result = tubeX.findIntersections(ray);
        assertNotNull(result, "TC61: Ray through X-axis tube should intersect");
        assertEquals(2, result.size(), "TC61: Wrong number of points");

        // TC62: Tube with axis along Y direction
        Tube tubeY = new Tube(
                new Ray(new Vector(0, 1, 0), new Point(0, 0, 0)),
                1
        );
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 5, 0));
        result = tubeY.findIntersections(ray);
        assertNotNull(result, "TC62: Ray through Y-axis tube should intersect");
        assertEquals(2, result.size(), "TC62: Wrong number of points");

        // TC63: Tube with diagonal axis
        Tube tubeDiag = new Tube(
                new Ray(new Vector(1, 1, 1).normalize(), new Point(0, 0, 0)),
                1
        );
        ray = new Ray(new Vector(1, -1, 0).normalize(), new Point(-2, 2, 0));
        result = tubeDiag.findIntersections(ray);
        assertNotNull(result, "TC63: Ray through diagonal tube should intersect");

        // **** Group: Special numeric cases

        // TC71: Very small radius tube
        Tube smallTube = new Tube(
                new Ray(new Vector(0, 0, 1), new Point(0, 0, 0)),
                0.001
        );
        ray = new Ray(new Vector(1, 0, 0), new Point(-0.002, 0, 1));
        result = smallTube.findIntersections(ray);
        assertNotNull(result, "TC71: Ray through small tube should intersect");
        assertEquals(2, result.size(), "TC71: Wrong number of points");

        // TC72: Very large radius tube
        Tube largeTube = new Tube(
                new Ray(new Vector(0, 0, 1), new Point(0, 0, 0)),
                1000
        );
        ray = new Ray(new Vector(1, 0, 0), new Point(-2000, 0, 1));
        result = largeTube.findIntersections(ray);
        assertNotNull(result, "TC72: Ray through large tube should intersect");
        assertEquals(2, result.size(), "TC72: Wrong number of points");

        // TC73: Ray very close to being parallel to axis
        ray = new Ray(new Vector(0.0001, 0, 1).normalize(), new Point(0, 0, -10));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC73: Nearly parallel ray should intersect");
        assertEquals(2, result.size(), "TC73: Wrong number of points");

        // TC74: Ray very close to being tangent
        ray = new Ray(new Vector(1, 0.0001, 0).normalize(), new Point(-2, 0.9999, 1));
        result = tube.findIntersections(ray);
        assertNotNull(result, "TC74: Nearly tangent ray should intersect");
        assertEquals(2, result.size(), "TC74: Wrong number of points");
    }
}