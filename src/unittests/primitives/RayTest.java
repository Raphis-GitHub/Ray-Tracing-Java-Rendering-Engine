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

        // EP: Positive t
        assertEquals(new Point(3, 2, 3), ray.getPoint(2), "Incorrect point at t = 2");

        // EP: Negative t
        assertEquals(new Point(0, 2, 3), ray.getPoint(-1), "Incorrect point at t = -1");

        // BVA: t = close to zero
        assertEquals(new Point(1.00000000001, 2, 3), ray.getPoint(0.00000000001), "Incorrect point at t = 0");
    }
}
