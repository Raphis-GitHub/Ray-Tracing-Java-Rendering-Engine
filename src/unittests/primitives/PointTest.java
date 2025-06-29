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
     * accuracy checker
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link Point#add(Vector)}.
     */
    @Test
    void testAdd() {
        // TC01: Basic addition of positive values
        assertEquals(new Point(5, 0, 8), new Point(1, 2, 3).add(new Vector(4, -2, 5)), "Point.add() basic addition failed");

        // TC02: Addition with negative vector values
        assertEquals(new Point(-1, -2, -3), new Point(0, 0, 0).add(new Vector(-1, -2, -3)), "Point.add() with negative values failed");

        // TC03: Addition resulting in ZERO point
        assertEquals(Point.ZERO, new Point(1, -2, 3).add(new Vector(-1, 2, -3)), "Point.add() resulting in ZERO point failed");
    }

    /**
     * Test method for {@link Point#subtract(Point)}.
     */
    @Test
    void testSubtract() {
        // TC01: Basic subtraction resulting in positive vector
        assertEquals(new Vector(2, 3, 4), new Point(3, 4, 5).subtract(new Point(1, 1, 1)), "Point.subtract() basic subtraction failed");

        // TC02: Subtraction resulting in negative vector
        assertEquals(new Vector(-1, -2, -3), new Point(0, 0, 0).subtract(new Point(1, 2, 3)), "Point.subtract() with negative results failed");

        // TC03: Subtraction of identical points resulting in ZERO vector
        assertEquals(new Vector(0, 0, 1), new Point(3, 4, 5).subtract(new Point(3, 4, 4)), "Subtracting identical points should give ZERO vector");
    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     */
    @Test
    void testDistanceSquared() {
        // TC01: Basic calculation of distance squared between two distinct points
        assertEquals(25, new Point(1, 2, 3).distanceSquared(new Point(4, 6, 3)), DELTA, "Point.distanceSquared() calculation failed");

        // TC02: Calculation of distance squared with negative point values
        assertEquals(14, new Point(-1, -2, -3).distanceSquared(new Point(0, 0, 0)), DELTA, "Point.distanceSquared() with negative values failed");

        // TC03: Distance squared to the same point should be zero
        assertEquals(0, new Point(1, 2, 3).distanceSquared(new Point(1, 2, 3)), DELTA, "Distance squared to itself should be zero");
    }//TODO: add BVA and EP partitions

    /**
     * Test method for {@link Point#distance(Point)}.
     */
    @Test
    void testDistance() {
        // TC01: Basic calculation of Euclidean distance
        assertEquals(5, new Point(0, 0, 0).distance(new Point(0, 3, 4)), DELTA, "Point.distance() basic calculation failed");

        // TC02: Calculation of distance with negative point values
        assertEquals(Math.sqrt(14), new Point(-1, -2, -3).distance(new Point(0, 0, 0)), DELTA, "Point.distance() with negative values failed");

        // TC03: Distance to the same point should be zero
        assertEquals(0, new Point(0, 0, 0).distance(new Point(0, 0, 0)), DELTA, "Distance to itself should be zero");
    }

}
