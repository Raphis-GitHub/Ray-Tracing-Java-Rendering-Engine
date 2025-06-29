package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests for vector
 */
class VectorTest {
    /**
     * nothing
     */ //@SuppressWarnings({"javadoc", "JavadocBlankLines"}
    private static final double DELTA = 0.000001;
    /**
     * nothing
     */
    private static final Vector ONE = new Vector(1, 1, 1);
    /**
     * nothing
     */
    private static final Vector V1 = new Vector(1, 2, 3);
    /**
     * nothing
     */
    private static final Vector V2 = new Vector(4, 5, 6);
    /**
     * nothing
     */
    private static final Vector V3 = new Vector(-1, -1, -3);
    /**
     * nothing
     */
    private static final Vector V4 = new Vector(1e10, -1e10, 1e10);
    /**
     * nothing
     */
    private static final Vector V5 = new Vector(-1e10, 1e10 + 1, -1e10);
    /**
     * nothing
     */
    private static final Vector V6 = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    /**
     * nothing
     */
    private static final Vector V7 = new Vector(1, -1, 0);
    /**
     * nothing
     */
    private static final Vector V8 = new Vector(1e-10, 1e-10, 1e-10);
    /**
     * nothing
     */
    private static final Vector V9 = new Vector(-1e-10 + 0.000001, -1e-10, -1e-10);
    /**
     * nothing
     */
    private static final Vector V10 = new Vector(1e-10, 0, -1e-10);
    /**
     * nothing
     */
    private static final Vector V11 = new Vector(-1e-10, 1e-10, 1e-10);

    /**
     * test for cinstructor double 3
     */
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

    /**
     * tests for other constructpr
     */
    @Test
    void constructorDouble3() {
        // =================EP=============
        Vector v1 = new Vector(new Double3(1, 2, 3));
        Vector v2 = new Vector(new Double3(1, 2, 3));
        assertEquals(v1, v2, "Vectors should be equal");

        //================================BVA===========================
        assertThrows(IllegalArgumentException.class, () -> new Vector(new Double3(0, 0, 0)), //
                "Constructed a ZERO vector");
    }

    /**
     * Tests the {@link Vector#add(Vector)} method to ensure its correctness.
     */
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

    /**
     * Tests the {@link Vector#scale(double)} method to ensure its correctness.
     */
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
        assertEquals(new Vector(0, 0, -1), new Vector(0, 0, 1).scale(-1), "Scaling a unit vector by -1 failed");

        // =================INVALID CASE===================

        // Ensure scaling by zero does not produce a zero vector
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), "Scaling by zero should throw an exception");
    }

    /**
     * Tests the {@link Vector#dotProduct(Vector)} method to ensure its correctness.
     */
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

    /**
     * Tests the {@link Vector#crossProduct(Vector)} method to ensure its correctness.
     */
    @Test
    void crossProduct() {
        // =================EP====================

        // EP Test Case 1: Cross product of two typical vectors
        assertEquals(new Vector(-3, 6, -3), V1.crossProduct(V2), "Cross product of typical vectors failed");

        // EP Test Case 2: Cross product of a vector with a negative vector
        assertEquals(new Vector(-3, 0, 1), V1.crossProduct(V3), "Cross product with negative vector failed");

        // EP Test Case 3: Cross product of orthogonal vectors
        assertEquals(new Vector(0, 0, 1), new Vector(1, 0, 0).crossProduct(new Vector(0, 1, 0)),
                "Cross product of orthogonal vectors failed");

        // =================BVA===================

        // BVA Test Case 1: Cross product orthogonality property
        Vector result = V1.crossProduct(V2);
        assertEquals(0, result.dotProduct(V1), DELTA, "Cross product not orthogonal to first vector");
        assertEquals(0, result.dotProduct(V2), DELTA, "Cross product not orthogonal to second vector");

        // BVA Test Case 2: Cross product of vectors with mixed signs
        assertEquals(new Vector(-1, -1, 2), V7.crossProduct(ONE), "Cross product of vectors with mixed signs failed");

        // BVA Test Case 3: Cross product of parallel vectors should throw exception
        Vector v = new Vector(2, 4, 6); // Parallel to V1
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(v),
                "Cross product of parallel vectors should throw an exception due to zero vector result");
    }

    /**
     * Tests the {@link Vector#lengthSquared()} method to ensure its correctness in computing the
     */
    @Test
    void lengthSquared() {
        // =================EP====================

        // EP Test Case 1: Length squared of a typical vector
        assertEquals(14, V1.lengthSquared(), DELTA, "Length squared calculation failed for a typical vector");

        // EP Test Case 2: Length squared of a unit vector
        assertEquals(1, new Vector(0, 1, 0).lengthSquared(), DELTA, "Length squared of a unit vector should be 1");

        // EP Test Case 3: Length squared for a negative vector
        assertEquals(11, V3.lengthSquared(), DELTA, "Length squared calculation failed for a negative vector");

        // =================BVA===================

        // BVA Test Case 1: Length squared for a vector with extreme values
        assertEquals(3 * Math.pow(1e10, 2), V4.lengthSquared(), DELTA, "Length squared calculation failed for extreme values");

        // BVA Test Case 2: Length squared for a vector with values close to zero
        assertEquals(3 * Math.pow(1e-10, 2), V8.lengthSquared(), DELTA, "Length squared calculation failed for values close to zero");

        // BVA Test Case 3: Length squared of a vector with mixed components
        assertEquals(2, V7.lengthSquared(), DELTA, "Length squared calculation failed for mixed component vector");
    }

    /**
     * Tests the {@link Vector#length()} method to ensure its accuracy in computing the length of a vector.
     */
    @Test
    void length() {
        // =================EP====================

        // EP Test Case 1: Length of a typical vector
        assertEquals(Math.sqrt(14), V1.length(), DELTA, "Length calculation failed for a typical vector");

        // EP Test Case 2: Length of a unit vector
        assertEquals(1, new Vector(0, 1, 0).length(), DELTA, "Length of a unit vector should be 1");

        // EP Test Case 3: Length for a negative vector
        assertEquals(Math.sqrt(11), V3.length(), DELTA, "Length calculation failed for a negative vector");

        // =================BVA===================

        // BVA Test Case 1: Length for a vector with extreme values
        double expectedLength = Math.sqrt(3) * 1e10;
        // Use a proportional delta for large values
        double largeDelta = expectedLength * 1e-10; // Allows 0.0000000001% error
        assertEquals(expectedLength, V4.length(), largeDelta,
                "Length calculation failed for extreme values");

        // BVA Test Case 2: Length for a vector with values close to zero
        assertEquals(Math.sqrt(3) * 1e-10, V8.length(), DELTA, "Length calculation failed for values close to zero");

        // BVA Test Case 3: Length of a vector with mixed components
        assertEquals(Math.sqrt(2), V7.length(), DELTA, "Length calculation failed for mixed component vector");
    }

    /**
     * Tests the {@link Vector#subtract(Point)} method to ensure its correctness.
     */
    @Test
    void subtract() {
        // =================EP====================

        // EP Test Case 1: Subtracting two vectors with positive values
        assertEquals(new Vector(3, 3, 3), V2.subtract(V1), "Subtract operation failed for two positive vectors");

        // EP Test Case 2: Subtracting a vector with a negative vector
        assertEquals(new Vector(2, 3, 6), V1.subtract(V3), "Subtract operation failed when subtracting negative vector");

        // EP Test Case 3: Subtracting a vector with large values
        assertEquals(new Vector(-2e10, 2e10 + 1, -2e10), V5.subtract(V4), "Subtract operation failed for vectors with large values");

        // =================BVA===================

        // BVA Test Case 1: Subtracting two vectors that result in boundary values
        assertEquals(new Vector(Double.MAX_VALUE - 1, Double.MAX_VALUE - 1, Double.MAX_VALUE - 1), V6.subtract(ONE), "Subtract operation failed when result should be near maximum boundary");

        // BVA Test Case 2: Subtracting two vectors with very small values close to zero
        assertEquals(new Vector(-2e-10 + 0.000001, -2e-10, -2e-10), V9.subtract(V8), "Subtract operation failed for very small values close to zero");

        // BVA Test Case 3: Subtracting a vector to achieve near-zero results
        assertEquals(new Vector(2e-10, -1e-10, -2e-10), V10.subtract(V11), "Subtract operation failed when achieving near-zero results");
    }

    /**
     * Tests the {@link Vector#normalize()} method to ensure its correctness.
     */
    @Test
    void normalize() {
        // =================EP====================

        // EP Test Case 1: Normalize a typical vector
        Vector normalizedV1 = V1.normalize();
        assertEquals(1, normalizedV1.length(), DELTA, "Normalized vector should have length 1");
        assertEquals(new Vector(1 / Math.sqrt(14), 2 / Math.sqrt(14), 3 / Math.sqrt(14)), normalizedV1,
                "Normalized vector direction is incorrect");

        // EP Test Case 2: Normalize already a unit vector
        Vector unitVector = new Vector(0, 1, 0);
        assertEquals(unitVector, unitVector.normalize(), "Normalizing a unit vector should return the same vector");

        // EP Test Case 3: Normalize vector with negative components
        Vector normalizedV3 = V3.normalize();
        assertEquals(1, normalizedV3.length(), DELTA, "Normalized negative vector should have length 1");
        assertEquals(new Vector(-1 / Math.sqrt(11), -1 / Math.sqrt(11), -3 / Math.sqrt(11)), normalizedV3,
                "Normalized negative vector direction is incorrect");

        // =================BVA===================

        // BVA Test Case 1: Normalize a vector with extreme values
        Vector normalizedV4 = V4.normalize();
        assertEquals(1, normalizedV4.length(), DELTA, "Normalized vector with extreme values should have length 1");
        assertEquals(new Vector(1 / Math.sqrt(3), -1 / Math.sqrt(3), 1 / Math.sqrt(3)), normalizedV4,
                "Normalized vector with extreme values direction is incorrect");

        // BVA Test Case 2: Normalize a vector with values close to zero
        Vector normalizedV8 = V8.normalize();
        assertEquals(1, normalizedV8.length(), DELTA, "Normalized vector with values close to zero should have length 1");
        assertEquals(new Vector(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)), normalizedV8,
                "Normalized vector with values close to zero direction is incorrect");

        // BVA Test Case 3: Normalize a vector with mixed components
        Vector normalizedV7 = V7.normalize();
        assertEquals(1, normalizedV7.length(), DELTA, "Normalized vector with mixed components should have length 1");
        assertEquals(new Vector(1 / Math.sqrt(2), -1 / Math.sqrt(2), 0), normalizedV7,
                "Normalized vector with mixed components direction is incorrect");
    }

}