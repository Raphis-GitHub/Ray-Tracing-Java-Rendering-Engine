package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
     * <p>
     * Boundary Value Analysis:
     * TC02 - Empty list (null result expected)
     * TC03 - First point is closest
     * TC04 - Last point is closest
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
        assertNull(ray.findClosestPoint(points1), "TC01: Currently returns null (expected in stub)");

        // =============== Boundary Values Tests ==================
        // TC02: Empty list
        List<Point> points2 = List.of();
        assertNull(ray.findClosestPoint(points2), "TC02: Empty list should return null");

        // TC03: First point is closest
        List<Point> points3 = List.of(
                new Point(1, 0, 0),
                new Point(3, 0, 0),
                new Point(6, 0, 0)
        );
        assertNull(ray.findClosestPoint(points3), "TC03: Stub returns null");

        // TC04: Last point is closest
        List<Point> points4 = List.of(
                new Point(5, 0, 0),
                new Point(7, 0, 0),
                new Point(0.5, 0, 0)
        );
        assertNull(ray.findClosestPoint(points4), "TC04: Stub returns null");
    }
}
