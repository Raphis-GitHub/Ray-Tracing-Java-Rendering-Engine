package primitives;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for Ray.getPoint
 */
public class RayTest {

    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Vector(1, 0, 0), new Point(1, 2, 3));

        // EP: Positive t
        assertEquals(new Point(3, 2, 3), ray.getPoint(2), "Incorrect point at t = 2");

        // EP: Negative t
        assertEquals(new Point(0, 2, 3), ray.getPoint(-1), "Incorrect point at t = -1");

        // BVA: t = 0
        assertEquals(new Point(1, 2, 3), ray.getPoint(0), "Incorrect point at t = 0");
    }
}
