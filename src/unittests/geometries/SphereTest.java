package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static geometries.Intersectable.Intersection;
import static org.junit.jupiter.api.Assertions.*;

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
        // ============ Equivalence Partitions Tests ==============
        // TC01: point on +X axis
        assertEquals(new Vector(1, 0, 0), sphere.getNormal(new Point(1, 0, 0)), "Incorrect normal for point on +X axis");
    }

    /**
     * Test method for {@link Sphere#findIntersections(Ray)}.
     * Based on the example provided in lab instructions (EP + BVA).
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        Ray ray = new Ray(new Vector(1, 1, 0), new Point(-1, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC01: Ray outside sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        ray = new Ray(new Vector(3, 0, 0), new Point(-1, 0, 0));
        List<Point> result = sphere.findIntersections(ray);
        assertEquals(List.of(new Point(0, 0, 0), new Point(2, 0, 0)), result, "TC02: Missing first expected point");

        // TC03: Ray starts inside the sphere (1 point)
        ray = new Ray(new Vector(0, 1, 0), new Point(1, 0.5, 0));
        result = sphere.findIntersections(ray);
        assertEquals(List.of(new Point(1, 1, 0)), result, "TC03: Intersection point incorrect");

        // TC04: Ray starts after the sphere (0 points)
        ray = new Ray(new Vector(1, 0, 0), new Point(3, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC04: Ray starts after sphere");

        // =============== Boundary Values Tests ==================

        // TC10: Ray starts at sphere and goes inside (0 point)

        Sphere sphere1 = new Sphere(new Point(0, 0, 0), 1);
        Ray ray1 = new Ray(new Vector(1, 0, 0), new Point(2, 2, 2));
        assertNull(sphere1.findIntersections(ray1), "Ray outside sphere should return null");

        // TC11: Ray starts at sphere and goes inside (1 point)
        ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 0));
        result = sphere.findIntersections(ray);
        assertNotNull(result, "TC11: Expected 1 intersection point");
        assertEquals(List.of(new Point(2.0, 0.0, 0.0)), result, "TC11: Wrong points");

        // TC12: Ray starts at sphere and goes outside (0 points)
        ray = new Ray(new Vector(-1, 0, 0), new Point(0, 0, 0));
        assertNull(sphere.findIntersections(ray), "TC12: Ray goes away from sphere");

        // TC13: Ray starts at center (1 point)
        ray = new Ray(new Vector(1, 0, 0), new Point(1, 0, 0));
        result = sphere.findIntersections(ray);
        assertEquals(List.of(new Point(2.0, 0.0, 0.0)), result, "TC13: Wrong points");

        // TC14: Ray is tangent and starts before (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 1, -1));
        assertNull(sphere.findIntersections(ray), "TC14: Tangent, before");

        // TC15: Ray is tangent and starts at point of contact (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 1, 0));
        assertNull(sphere.findIntersections(ray), "TC15: Tangent, on");

        // TC16: Ray is tangent and starts after contact (0 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 1, 1));
        assertNull(sphere.findIntersections(ray), "TC16: Tangent, after");
    }

    /**
     * Test method for {@link Sphere#calculateIntersectionsHelper(Ray, double)} IntersectionsHelper(Ray, double)}.
     */
    @Test
    void testFindIntersectionsWithMaxDistance() {
        Sphere s = new Sphere(new Point(5, 3, 0), 2);
        //============ Equivalence Partitions Tests ==============
        // TC01: Ray doesn't intersect sphere within max distance
        assertNull(s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(0, 4, 0)), 2), "TC01: Ray doesn't intersect sphere within max distance");

        // TC02: Ray intersects sphere within max distance
        assertEquals(List.of(new Intersection(s, new Point(3.0635083268962915, 3.5, 0.0))), s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(2.5, 3.5, 0)), 2), "TC02: Ray intersects sphere within max distance");

        // TC03: Ray intersects sphere but beyond max distance
        assertNull(s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(3.5, 3, 0)), 2), "TC03: Ray intersects sphere but beyond max distance");

        // TC04: Ray intersects sphere within max distance from inside
        assertEquals(List.of(new Intersection(s, new Point(6.936491673103708, 2.5, 0.0))), s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(5.5, 2.5, 0)), 2), "TC04: Ray intersects sphere within max distance from inside");

        // TC05: Ray intersects sphere within max distance at close range
        assertEquals(List.of(new Intersection(s, new Point(6.732050807568877, 2.0, 0.0))), s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(6.5, 2, 0)), 2), "TC05: Ray intersects sphere within max distance at close range");

        // TC06: Ray is far from sphere beyond max distance
        assertNull(s.calculateIntersections(new Ray(new Vector(1, 0, 0), new Point(9, 1.5, 0)), 2), "TC06: Ray is far from sphere beyond max distance");
    }

}