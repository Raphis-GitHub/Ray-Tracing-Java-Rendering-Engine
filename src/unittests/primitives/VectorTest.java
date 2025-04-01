package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {
    private static final double DELTA = 0.000001;
    private static final Vector ONE = new Vector(1, 1, 1);
    private static final Vector V1 = new Vector(1, 2, 3);
    private static final Vector V2 = new Vector(4, 5, 6);
    private static final Vector V3 = new Vector(-1, -1, -3);
    private static final Vector V4 = new Vector(1e10, -1e10, 1e10);
    private static final Vector V5 = new Vector(-1e10, 1e10 + 1, -1e10);
    private static final Vector V6 = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    private static final Vector V7 = new Vector(1, -1, 0);
    private static final Vector V8 = new Vector(1e-10, 1e-10, 1e-10);
    private static final Vector V9 = new Vector(-1e-10 + 0.000001, -1e-10, -1e-10);
    private static final Vector V10 = new Vector(1e-10, 0, -1e-10);
    private static final Vector V11 = new Vector(-1e-10, 1e-10, 1e-10);

    @Test
    void constructor3double() {
        // =================EP=============
        Vector v2 = new Vector(1, 2, 3);
        assertEquals(V1, v2, "Vectors should be equal");

        assertDoesNotThrow(() -> new Vector(-1, -2, -3), "Failed to create a valid vector with negative values");
        assertDoesNotThrow(() -> V4, "Failed to create a valid vector with extreme values");
        assertDoesNotThrow(() -> new Vector(0.1, 0.2, 0.3), "Failed to create a valid vector with decimal values");
        
        //================================BVA===========================
        assertThrows(IllegalArgumentException.class, () -> new Point(1, 1, 1).subtract(new Point(1, 1, 1)),
                "Constructed a ZERO vector");
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), //
                "Constructed a ZERO vector");
    }
    @Test
    void constructorDouble3() {
        // =================EP=============
        Vector v1 = new Vector(new Double3(1, 2, 3));
        Vector v2 = new Vector(new Double3(1, 2, 3));
        assertEquals(v1, v2, "Vectors should be equal");

        assertDoesNotThrow(() -> new Vector(new Double3(-1, -2, -3)), "Failed to create a valid vector with negative values");
        assertDoesNotThrow(() -> new Vector(new Double3(1e10, -1e10, 1e-10)), "Failed to create a valid vector with extreme values");
        assertDoesNotThrow(() -> new Vector(new Double3(0.1, 0.2, 0.3)), "Failed to create a valid vector with decimal values");

        //================================BVA===========================
        assertThrows(IllegalArgumentException.class, () -> new Point(1, 1, 1).subtract(new Point(1, 1, 1)),
                "Constructed a ZERO vector");
        assertThrows(IllegalArgumentException.class, () -> new Vector(new Double3(0, 0, 0)), //
                "Constructed a ZERO vector");
    }



    @Test
    void add() {
        // =================EP====================

        // EP Test Case 1: Adding two vectors with positive values
        assertEquals(new Vector(5, 7, 9), V1.add(V2), "Add operation failed for two positive vectors");

        // EP Test Case 2: Adding a vector with a negative vector
        assertEquals(new Vector(0, 1, 0), V1.add(V3), "Add operation failed for a vector and its negative");

        // EP Test Case 3: Adding a vector with large values
        assertEquals(new Vector(0, 1, 0), V4.add(V5), "Add operation failed for vectors with large values");

        // =================BVA===================

        // BVA Test Case 1: Adding two vectors that result in boundary values
        assertEquals(new Vector(Double.MAX_VALUE + 1, Double.MAX_VALUE - 1, Double.MAX_VALUE),
                V6.add(V7), "Add operation failed near boundary limits");

        // BVA Test Case 2: Adding two vectors with very small values close to zero
        assertEquals(new Vector(0.000001, 0, 0), V8.add(V9), "Add operation failed for extremely small values");

        // BVA Test Case 3: Adding a vector to achieve near-zero results
        assertEquals(new Vector(0, 1e-10, 0), V10.add(V11), "Add operation failed for values close to zero");
    }

    @Test
    void scale() {
        // =================EP====================

        // EP Test Case 1: Scale a vector with a positive scalar
        assertEquals(new Vector(2, 4, 6), V1.scale(2), "Scaling a vector with positive scalar failed");

        // EP Test Case 2: Scale a vector with a negative scalar
        assertEquals(new Vector(-2, -4, -6), V1.scale(-2), "Scaling a vector with negative scalar failed");

        // EP Test Case 3: Scale a vector with a small fractional scalar
        assertEquals(new Vector(0.5, 1, 1.5), V1.scale(0.5), "Scaling a vector with a fractional scalar failed");

        // =================BVA===================

        // BVA Test Case 1: Scale a vector with a scalar near Double.MAX_VALUE (large value)
        double largeValue = Double.MAX_VALUE / 2; // Avoid overflow
        assertEquals(new Vector(largeValue, -largeValue, 0.0), V7.scale(largeValue), "Scaling near maximum double value failed");

        // BVA Test Case 2: Scale a vector with a scalar near Double.MIN_VALUE (small value)
        double smallValue = 0.0000001; // Avoid zero or irrelevant precision
        assertEquals(new Vector(smallValue, smallValue, smallValue), ONE.scale(smallValue), "Scaling near minimum positive double value failed");

        // BVA Test Case 3: Scale a unit vector with scalar -1 to get its inverse direction
        assertEquals(new Vector(0, 0, -1), new Vector(0,0,1).scale(-1), "Scaling a unit vector by -1 failed");

        // =================INVALID CASE===================

        // Ensure scaling by zero does not produce a zero vector
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), "Scaling by zero should throw an exception");
    }

    @Test
    void dotProduct() {
        // =================EP====================

        // EP Test Case 1: Dot product of two normal vectors
        assertEquals(32.0, V1.dotProduct(V2), DELTA, "Dot product of two normal vectors failed");


        // EP Test Case 2: Dot product of a vector with a negative vector
        assertEquals(-12.0, V1.dotProduct(V3), DELTA, "Dot product of a vector with a negative vector failed");

        // EP Test Case 3: Dot product of perpendicular vectors (result should be 0)
        assertEquals(0.0, V7.dotProduct(new Vector(1, 1, 0)), DELTA, "Dot product of perpendicular vectors failed");

        // =================BVA===================

        // BVA Test Case 1: Large magnitude vectors
        assertEquals(Double.MAX_VALUE, new Vector(Double.MAX_VALUE, 0, 0).dotProduct(new Vector(1, 0, 0)),
                DELTA, "Dot product failed for vectors with large magnitudes");

        // BVA Test Case 2: Small magnitude vectors near zero
        assertEquals(1e-20, new Vector(1e-10, 0, 0).dotProduct(new Vector(1e-10, 0, 0)),
                DELTA, "Dot product failed for vectors with small values");

        // BVA Test Case 3: Dot product of parallel vectors (angle = 0)
        assertEquals(28.0, V1.dotProduct(new Vector(2, 4, 6)), DELTA, "Dot product failed for parallel vectors");

    }

    @Test
    void crossProduct() {
    }

    @Test
    void lengthSquared() {
    }

    @Test
    void length() {
    }

    @Test
    void normalize() {
    }
}