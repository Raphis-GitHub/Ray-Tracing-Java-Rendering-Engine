package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
