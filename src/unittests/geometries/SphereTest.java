package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * unit tests for sphere
 */
class SphereTest {


    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void getNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // EP: point on +X axis
        assertEquals(new Vector(1, 0, 0), sphere.getNormal(new Point(1, 0, 0)), "Incorrect normal for point on +X axis");

        // EP: point on -X axis
        assertEquals(new Vector(-1, 0, 0), sphere.getNormal(new Point(-1, 0, 0)), "Incorrect normal for point on -X axis");

        // EP: point on +Y axis
        assertEquals(new Vector(0, 1, 0), sphere.getNormal(new Point(0, 1, 0)), "Incorrect normal for point on +Y axis");

        // EP: point on -Y axis
        assertEquals(new Vector(0, -1, 0), sphere.getNormal(new Point(0, -1, 0)), "Incorrect normal for point on -Y axis");

        // EP: point on +Z axis
        assertEquals(new Vector(0, 0, 1), sphere.getNormal(new Point(0, 0, 1)), "Incorrect normal for point on +Z axis");

        // EP: point on -Z axis
        assertEquals(new Vector(0, 0, -1), sphere.getNormal(new Point(0, 0, -1)), "Incorrect normal for point on -Z axis");
    }

    /**
     * Test method for {@link Sphere#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections_noIntersection() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);
        Ray ray = new Ray(new Vector(1, 0, 0),new Point(2, 2, 2));
        assertEquals(null, sphere.findIntersections(ray), "Ray outside sphere should return null");
    }

    /**
     * Test method for {@link Sphere#findIntersections(Ray)}.
     * Based on the example provided in lab instructions (EP + BVA).
     */
    @Test
    void testFindIntersections_variousCases() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        Ray ray = new Ray(new Vector(1, 1, 0),new Point(-1, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC01: Ray outside sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        ray = new Ray(new Vector(3, 0, 0), new Point(-1, 0, 0));
        List<Point> result = sphere.findIntersections(ray);
        assertNotNull(result, "TC02: Expected 2 intersection points");
        assertEquals(2, result.size(), "TC02: Wrong number of points");
        assertTrue(result.contains(new Point(0, 0, 0)), "TC02: Missing first expected point");
        assertTrue(result.contains(new Point(2, 0, 0)), "TC02: Missing second expected point");

        // TC03: Ray starts inside the sphere (1 point)
        ray = new Ray(new Vector(0, 1, 0),new Point(1, 0.5, 0));
        result = sphere.findIntersections(ray);
        assertNotNull(result, "TC03: Expected 1 intersection point");
        assertEquals(1, result.size(), "TC03: Wrong number of points");
        assertEquals(new Point(1, Math.sqrt(0.75), 0), result.get(0), "TC03: Intersection point incorrect");

        // TC04: Ray starts after the sphere (0 points)
        ray = new Ray(new Vector(1, 0, 0),new Point(3, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC04: Ray starts after sphere");

        // =============== Boundary Values Tests ==================

        // TC11: Ray starts at sphere and goes inside (1 point)
        ray = new Ray(new Vector(1, 0, 0),new Point(0, 0, 0));
        result = sphere.findIntersections(ray);
        assertNotNull(result, "TC11: Expected 1 intersection point");
        assertEquals(1, result.size(), "TC11: Wrong number of points");

        // TC12: Ray starts at sphere and goes outside (0 points)
        ray = new Ray(new Vector(-1, 0, 0),new Point(0, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC12: Ray goes away from sphere");

        // TC13: Ray starts at center (1 point)
        ray = new Ray(new Vector(1, 0, 0),new Point(1, 0, 0));
        result = sphere.findIntersections(ray);
        assertNotNull(result, "TC13: Expected 1 intersection point");
        assertEquals(1, result.size(), "TC13: Wrong number of points");

        // TC14: Ray is tangent and starts before (0 points)
        ray = new Ray(new Vector(0, 0, 1),new Point(0, 1, -1));
        assertNull(sphere.findIntersections(ray), "TC14: Tangent, before");

        // TC15: Ray is tangent and starts at point of contact (0 points)
        ray = new Ray(new Vector(0, 0, 1),new Point(0, 1, 0));
        assertNull(sphere.findIntersections(ray), "TC15: Tangent, on");

        // TC16: Ray is tangent and starts after contact (0 points)
        ray = new Ray(new Vector(0, 0, 1),new Point(0, 1, 1));
        assertNull(sphere.findIntersections(ray), "TC16: Tangent, after");
    }


}