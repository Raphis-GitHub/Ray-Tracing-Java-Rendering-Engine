package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Geometries class
 *
 * @author Raphael
 */
public class GeometriesTest {
    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(0, 0, 2), 1);
        Plane plane = new Plane(new Point(0, 0, 3), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(new Point(1, 0, 3), new Point(-1, 0, 3), new Point(0, 1, 3));

        Geometries geometries = new Geometries(sphere, plane, triangle);

        // =============== Boundary Values Tests ==================

        // TC01: Empty collection (0 points)
        Geometries empty = new Geometries();
        assertNull(empty.findIntersections(new Ray(new Vector(1, 0, 0), new Point(0, 0, 0))),
                "TC01: Empty collection should return null");

        // TC02: No geometry intersects (0 points)
        Ray ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 0));
        assertNull(geometries.findIntersections(ray),
                "TC02: No geometries intersect should return null");

        // TC03: Only one geometry intersects (2 points from sphere)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        List<Point> result = new Geometries(sphere).findIntersections(ray);
        assertNotNull(result, "TC03: One geometry should intersect");
        assertEquals(2, result.size(), "TC03: Wrong number of points");

        // TC05: All geometries intersect (3 points total)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        result = geometries.findIntersections(ray);
        assertNotNull(result, "TC05: All geometries should intersect");
        assertEquals(3, result.size(), "TC05: Wrong number of points");

        // ============ Equivalence Partitions Tests ==============

        // TC04: Several (but not all) geometries intersect (2 points)
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        result = new Geometries(sphere, triangle).findIntersections(ray);
        assertNotNull(result, "TC04: Several geometries should intersect");
        assertEquals(2, result.size(), "TC04: Wrong number of points");
    }
}