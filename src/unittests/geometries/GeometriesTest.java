package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Geometries.findIntersections
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

        // ============ Boundary Values Tests ==============

        // TC01: Empty collection → null
        Geometries empty = new Geometries();
        assertNull(empty.findIntersections(new Ray(new Vector(1, 0, 0), new Point(0, 0, 0))), "TC01: Expected null for empty collection");

        // TC02: No geometry intersects → null
        Ray ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 0));
        assertNull(geometries.findIntersections(ray), "TC02: Expected null when no geometries intersect");

        // TC03: One geometry intersects → list with 1 point
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        List<Point> result = new Geometries(sphere).findIntersections(ray);
        assertNotNull(result, "TC03: Expected 1 intersection");
        assertEquals(2, result.size(), "TC03: Expected 2 intersection points with sphere");

        // TC04: Some geometries intersect → return all
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        result = new Geometries(sphere, triangle).findIntersections(ray);
        assertNotNull(result, "TC04: Expected intersection");
        assertEquals(2, result.size(), "TC04: Expected 2 intersection points");

        // TC05: All geometries intersect
        ray = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        result = geometries.findIntersections(ray);
        assertNotNull(result, "TC05: Expected intersection");
        assertEquals(4, result.size(), "TC05: Expected 4 intersection points total");
    }
}
