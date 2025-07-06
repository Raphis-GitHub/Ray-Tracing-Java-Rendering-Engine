package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Ray.getPoint
 */
public class RayTest {
    /**
     * Test method for {@link Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Vector(1, 0, 0), new Point(1, 2, 3));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Positive t
        assertEquals(new Point(3, 2, 3), ray.getPoint(2), "Incorrect point at t = 2");

        // TC02: Negative t
        assertEquals(new Point(0, 2, 3), ray.getPoint(-1), "Incorrect point at t = -1");

        // =============== Boundary Values Tests ==================
        // TC01: t = is zero
        assertEquals(new Point(1, 2, 3), ray.getPoint(0), "Incorrect point at t = 0");
    }

    /**
     * Equivalence Partition:
     * TC01 - Point in the middle is the closest
     */
    @Test
    void testFindClosestPoint() {
        Ray ray = new Ray(new Vector(1, 0, 0), new Point(0, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Middle point is closest
        List<Point> points1 = List.of(
                new Point(5, 0, 0),
                new Point(2, 0, 0), // closest
                new Point(10, 0, 0)
        );
        assertEquals(new Point(2, 0, 0), ray.findClosestPoint(points1),
                "TC01: Should return the closest point (2,0,0)");

        // =============== Boundary Values Tests ==================
        // TC02: Empty list
        assertNull(ray.findClosestPoint(null), "TC02: Empty list should return null");

        // TC03: First point is closest
        List<Point> points3 = List.of(
                new Point(1, 0, 0), // closest
                new Point(3, 0, 0),
                new Point(6, 0, 0)
        );
        assertEquals(new Point(1, 0, 0), ray.findClosestPoint(points3),
                "TC03: Should return the first/closest point (1,0,0)");

        // TC04: Last point is closest
        List<Point> points4 = List.of(
                new Point(5, 0, 0),
                new Point(7, 0, 0),
                new Point(0.5, 0, 0) // closest
        );
        assertEquals(new Point(0.5, 0, 0), ray.findClosestPoint(points4),
                "TC04: Should return the last/closest point (0.5,0,0)");
    }
}
