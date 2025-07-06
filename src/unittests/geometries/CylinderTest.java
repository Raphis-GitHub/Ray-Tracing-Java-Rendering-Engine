package geometries;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cylinder class
 *
 * @author Raphael
 */
class CylinderTest {
    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        Cylinder cylinder = new Cylinder(axis, 1, 5);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Point on side surface
        Point p1 = new Point(1, 0, 2);
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(p1),
                "Wrong normal on side surface");

        // TC02: Point on side with diagonal
        Point p2 = new Point(1, 1, 3);
        Vector expected2 = new Vector(1, 1, 0).normalize();
        assertEquals(expected2, cylinder.getNormal(p2),
                "Wrong normal on diagonal side");

        // =============== Boundary Values Tests ==================

        // TC11: Point on bottom base (t ≈ 0)
        Point p3 = new Point(0.5, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p3),
                "Wrong normal on bottom base");

        // TC12: Point on top base (t ≈ height)
        Point p4 = new Point(0, -0.5, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p4),
                "Wrong normal on top base");

        // TC13: Point at edge between side and bottom
        Point p5 = new Point(1, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p5),
                "Wrong normal at bottom edge");

        // TC14: Point at edge between side and top
        Point p6 = new Point(1, 0, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p6),
                "Wrong normal at top edge");
    }

    /**
     * Test method for {@link Cylinder#findIntersections(Ray)}.
     */
    @Test
    @Disabled
    void testFindIntersections() {
        Cylinder cylinder = new Cylinder(
                new Ray(new Vector(0, 0, 1), new Point(0, 0, 0)),
                1,
                2
        );
 
// ============ Equivalence Partitions Tests ==============

// TC01: Ray outside cylinder and parallel to axis (0 points)
        Ray ray = new Ray(new Vector(0, 0, 1), new Point(2, 0, 0));
        assertNull(cylinder.findIntersections(ray),
                "TC01: Ray outside cylinder parallel to axis should return null");

// TC02: Ray inside cylinder and parallel to axis (2 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.5, 0, -1));
        List<Point> result = cylinder.findIntersections(ray);
        assertEquals(2, result.size(), "TC02: Wrong number of points");
        assertEquals(new Point(0.5, 0, 0), result.get(0), "TC02: Wrong first point");
        assertEquals(new Point(0.5, 0, 2), result.get(1), "TC02: Wrong second point");

// TC03: Ray perpendicular to axis, misses cylinder (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 2, 1));
        assertNull(cylinder.findIntersections(ray),
                "TC03: Ray perpendicular to axis missing cylinder should return null");

// TC04: Ray perpendicular to axis, through cylinder (2 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 0, 1));
        result = cylinder.findIntersections(ray);
        assertEquals(2, result.size(), "TC04: Wrong number of points");
        assertEquals(new Point(-1, 0, 1), result.get(0), "TC04: Wrong first point");
        assertEquals(new Point(1, 0, 1), result.get(1), "TC04: Wrong second point");

// TC05: Ray at angle, intersects sides (2 points)
        ray = new Ray(new Vector(1, 0, 1), new Point(-2, 0, -1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC05: Ray at angle should intersect");
        assertEquals(2, result.size(), "TC05: Wrong number of points");
        assertEquals(new Point(-1, 0, 0), result.get(0), "TC05: Wrong first point");
        assertEquals(new Point(1, 0, 2), result.get(1), "TC05: Wrong second point");

// TC06: Ray at angle, intersects caps (2 points)
        ray = new Ray(new Vector(0.5, 0, 1), new Point(-1, 0, -1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC06: Ray through caps should intersect");
        assertEquals(2, result.size(), "TC06: Wrong number of points");
        assertEquals(new Point(-0.5, 0, 0), result.get(0), "TC06: Wrong first point");
        assertEquals(new Point(0.5, 0, 2), result.get(1), "TC06: Wrong second point");

// TC07: Ray inside cylinder at angle (1 point)
        ray = new Ray(new Vector(1, 0, 1), new Point(0, 0, 1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC07: Ray from inside should intersect");
        assertEquals(1, result.size(), "TC07: Wrong number of points");
        assertEquals(new Point(1, 0, 2), result.get(0), "TC07: Wrong intersection point");

// TC08: Ray starts after cylinder (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 3));
        assertNull(cylinder.findIntersections(ray),
                "TC08: Ray starting after cylinder should return null");

// =============== Boundary Values Tests ==================

// **** Group: Ray parallel to axis

// TC11: Ray near cylinder surface parallel to axis (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1.001, 0, -1));
        assertNull(cylinder.findIntersections(ray),
                "TC11: Ray near surface parallel to axis should return null");

// TC12: Ray on axis (2 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, -1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC12: Ray on axis should intersect caps");
        assertEquals(2, result.size(), "TC12: Wrong number of points");
        assertEquals(new Point(0, 0, 0), result.get(0), "TC12: Wrong first cap point");
        assertEquals(new Point(0, 0, 2), result.get(1), "TC12: Wrong second cap point");

// **** Group: Ray starts on cylinder

// TC21: Ray starts on bottom cap, goes inside (1 point)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.5, 0, 0));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC21: Ray from bottom cap should intersect");
        assertEquals(1, result.size(), "TC21: Wrong number of points");
        assertEquals(new Point(0.5, 0, 2), result.get(0), "TC21: Wrong intersection point");

// TC22: Ray starts on top cap, goes outside (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0.5, 0, 2));
        assertNull(cylinder.findIntersections(ray),
                "TC22: Ray from top cap going out should return null");

// TC23: Ray starts on side, goes outside (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(1, 0, 1));
        assertNull(cylinder.findIntersections(ray),
                "TC23: Ray from side going out should return null");

// TC24: Ray starts on side, goes inside (1 point)
        ray = new Ray(new Vector(-1, 0, 0), new Point(1, 0, 1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC24: Ray from side going in should intersect");
        assertEquals(1, result.size(), "TC24: Wrong number of points");
        assertEquals(new Point(-1, 0, 1), result.get(0), "TC24: Wrong intersection point");

// **** Group: Ray tangent to cylinder

// TC31: Ray tangent to side surface (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(1, 0, -1));
        assertNull(cylinder.findIntersections(ray),
                "TC31: Tangent ray should return null");

// TC32: Ray tangent to cap edge (0 points)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, -2, 0));
        assertNull(cylinder.findIntersections(ray),
                "TC32: Ray tangent to cap edge should return null");

// **** Group: Special cases

// TC41: Ray through axis perpendicular to it (2 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(-2, 0, 1));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC41: Ray through axis should intersect");
        assertEquals(2, result.size(), "TC41: Wrong number of points");
        assertEquals(new Point(-1, 0, 1), result.get(0), "TC41: Wrong first point");
        assertEquals(new Point(1, 0, 1), result.get(1), "TC41: Wrong second point");

// TC42: Ray starts at cap center (1 point)
        ray = new Ray(new Vector(1, 1, 1), new Point(0, 0, 0));
        result = cylinder.findIntersections(ray);
        assertNotNull(result, "TC42: Ray from cap center should intersect");
        assertEquals(1, result.size(), "TC42: Wrong number of points");
        double sqrt2 = Math.sqrt(2);
        assertEquals(new Point(1 / sqrt2, 1 / sqrt2, 1 / sqrt2), result.get(0), "TC42: Wrong intersection point");
    }
}