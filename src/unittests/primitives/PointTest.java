package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing Points
 *
 * @author Raphael
 */
class PointTest {
    /**
     * accuracy ckecker
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link Point#add(Vector)}.
     */
    @Test
    void testAdd() {//todo: add proper comments
        assertEquals(new Point(5, 0, 8), new Point(1, 2, 3).add(new Vector(4, -2, 5)), "Point.add() basic addition failed");
        assertEquals(new Point(-1, -2, -3), new Point(0, 0, 0).add(new Vector(-1, -2, -3)), "Point.add() with negative values failed");
        assertEquals(Point.ZERO, new Point(1, -2, 3).add(new Vector(-1, 2, -3)), "Point.add() resulting in ZERO point failed");
    }

    /**
     * Test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        assertEquals(new Vector(2, 3, 4), new Point(3, 4, 5).subtract(new Point(1, 1, 1)), "Point.subtract() basic subtraction failed");
        assertEquals(new Vector(-1, -2, -3), new Point(0, 0, 0).subtract(new Point(1, 2, 3)), "Point.subtract() with negative results failed");
        assertEquals(new Vector(0, 0, 1), new Point(3, 4, 5).subtract(new Point(3, 4, 4)), "Subtracting identical points should give ZERO vector");
    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     */
    @Test
    void testDistanceSquared() {
        assertEquals(25, new Point(1, 2, 3).distanceSquared(new Point(4, 6, 3)), DELTA, "Point.distanceSquared() calculation failed");
        assertEquals(14, new Point(-1, -2, -3).distanceSquared(new Point(0, 0, 0)), DELTA, "Point.distanceSquared() with negative values failed");
        assertEquals(0, new Point(1, 2, 3).distanceSquared(new Point(1, 2, 3)), DELTA, "Distance squared to itself should be zero");
    }

    /**
     * Test method for {@link Point#distance(Point)}.
     */
    @Test
    void testDistance() {
        assertEquals(5, new Point(0, 0, 0).distance(new Point(0, 3, 4)), DELTA, "Point.distance() basic calculation failed");
        assertEquals(Math.sqrt(14), new Point(-1, -2, -3).distance(new Point(0, 0, 0)), DELTA, "Point.distance() with negative values failed");
        assertEquals(0, new Point(0, 0, 0).distance(new Point(0, 0, 0)), DELTA, "Distance to itself should be zero");
    }
}
