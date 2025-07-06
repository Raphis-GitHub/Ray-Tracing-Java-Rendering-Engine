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
    private static final Vector v1 = new Vector(1, 2, 3);
    /**
     * nothing
     */
    private static final Vector v2 = new Vector(4, 5, 6);
    /**
     * nothing
     */
    private static final Vector v3 = new Vector(-1, -1, -3);
    /**
     * nothing
     */
    private static final Vector v4 = new Vector(1e10, -1e10, 1e10);
    /**
     * nothing
     */
    private static final Vector v5 = new Vector(-1e10, 1e10 + 1, -1e10);
    /**
     * nothing
     */
    private static final Vector v6 = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    /**
     * nothing
     */
    private static final Vector v7 = new Vector(1, -1, 0);
    /**
     * nothing
     */
    private static final Vector v8 = new Vector(1e-10, 1e-10, 1e-10);
    /**
     * nothing
     */
    private static final Vector v9 = new Vector(-1e-10 + 0.000001, -1e-10, -1e-10);
    /**
     * nothing
     */
    private static final Vector v10 = new Vector(1e-10, 0, -1e-10);
    /**
     * nothing
     */
    private static final Vector v11 = new Vector(-1e-10, 1e-10, 1e-10);

    /**
     * test for cinstructor double 3
     */
    @Test
    void constructor3double() {
        // =================EP=============
        Vector v2 = new Vector(1, 2, 3);
        //TC01: Create a vector
        assertEquals(v1, v2, "Vectors should be equal");
        //TC01: Create a vector with NEGETIVE values
        assertDoesNotThrow(() -> new Vector(-1, -2, -3), "Failed to create a valid vector with negative values");

        //================================BVA===========================
        //TC11: CREATES ZERO VECTOR
        assertThrows(IllegalArgumentException.class, () -> new Point(1, 1, 1).subtract(new Point(1, 1, 1)),
                "Constructed a ZERO vector");
        //TC12: CREATES ZERO VECTOR
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
        //TC01: Create a vector
        assertEquals(v1, v2, "Vectors should be equal");

        //================================BVA===========================
        //TC11: CREATES ZERO VECTOR
        assertThrows(IllegalArgumentException.class, () -> new Vector(new Double3(0, 0, 0)), //
                "Constructed a ZERO vector");
    }

    /**
     * Tests the {@link Vector#add(Vector)} method to ensure its correctness.
     */
    @Test
    void add() {
        Vector v12 = new Vector(1, 2, 3);
        // =================EP====================

        //TC01: Adding two vectors with positive values
        assertEquals(new Vector(5, 7, 9), v12.add(v2), "Add operation failed for two positive vectors");

        //TC02: Adding a vector with a negative vector
        assertEquals(new Vector(0, 1, 0), v12.add(v3), "Add operation failed for a vector and its negative");

        // =================BVA===================
        //TC11: adding a vector with a negetive of itself chould throw an exception
        assertThrows(IllegalArgumentException.class, () -> v12.add(v12.scale(-1)),
                "Constructed a ZERO vector");

    }

    /**
     * Tests the {@link Vector#scale(double)} method to ensure its correctness.
     */
    @Test
    void scale() {
        // =================EP====================
        //TC01: Scale a vector with a positive scalar
        assertEquals(new Vector(2, 4, 6), v1.scale(2), "Scaling a vector with positive scalar failed");

        //TC02: Scale a vector with a negative scalar
        assertEquals(new Vector(-2, -4, -6), v1.scale(-2), "Scaling a vector with negative scalar failed");

        //TC03: Scale a vector with a small fractional scalar
        assertEquals(new Vector(0.5, 1, 1.5), v1.scale(0.5), "Scaling a vector with a fractional scalar failed");

        // =================BVA===================

        // TC11: Scale a unit vector with scalar -1 to get its inverse direction
        assertEquals(new Vector(0, 0, -1), new Vector(0, 0, 1).scale(-1), "Scaling a unit vector by -1 failed");

        //TC12: Ensure scaling by zero does not produce a zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0), "Scaling by zero should throw an exception");
    }

    /**
     * Tests the {@link Vector#dotProduct(Vector)} method to ensure its correctness.
     */
    @Test
    void dotProduct() {
        // =================Equivalence partitions====================

        //TC01: Dot product of two normal vectors
        assertEquals(32.0, v1.dotProduct(v2), DELTA, "Dot product of two normal vectors failed");

        //TC02 2: Dot product of a vector with a negative vector
        assertEquals(-12.0, v1.dotProduct(v3), DELTA, "Dot product of a vector with a negative vector failed");

        //TC03: Dot product of perpendicular vectors (result should be 0)
        assertEquals(0.0, v7.dotProduct(new Vector(1, 1, 0)), DELTA, "Dot product of perpendicular vectors failed");

        // =================BVA===================

        // TC11: Dot product of parallel vectors (angle = 0)
        assertEquals(28.0, v1.dotProduct(new Vector(2, 4, 6)), DELTA, "Dot product failed for parallel vectors");

    }

    /**
     * Tests the {@link Vector#crossProduct(Vector)} method to ensure its correctness.
     */
    @Test
    void crossProduct() {
        // =================EP====================

        // TC01: Cross product of two typical vectors
        assertEquals(new Vector(-3, 6, -3), v1.crossProduct(v2), "Cross product of typical vectors failed");

        //TC02: Cross product of orthogonal vectors
        assertEquals(new Vector(0, 0, 1), new Vector(1, 0, 0).crossProduct(new Vector(0, 1, 0)),
                "Cross product of orthogonal vectors failed");

        // =================BVA===================

        //TC11: Cross product orthogonality property
        Vector result = v1.crossProduct(v2);
        assertEquals(0, result.dotProduct(v1), DELTA, "Cross product not orthogonal to first vector");
        assertEquals(0, result.dotProduct(v2), DELTA, "Cross product not orthogonal to second vector");

        //TC12: Cross product of parallel vectors should throw exception
        Vector v = new Vector(2, 4, 6); // Parallel to V1
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v),
                "Cross product of parallel vectors should throw an exception due to zero vector result");
    }

    /**
     * Tests the {@link Vector#lengthSquared()} method to ensure its correctness in computing the
     */
    @Test
    void lengthSquared() {
        // =================EP====================

        //TC01: Length squared of a typical vector
        assertEquals(14, v1.lengthSquared(), DELTA, "Length squared calculation failed for a typical vector");

        // =================BVA===================

        //TC11: Length squared of a unit vector
        assertEquals(1, new Vector(0, 1, 0).lengthSquared(), DELTA, "Length squared of a unit vector should be 1");

    }

    /**
     * Tests the {@link Vector#length()} method to ensure its accuracy in computing the length of a vector.
     */
    @Test
    void length() {
        // =================EP====================

        //TC01: Length of a typical vector
        assertEquals(Math.sqrt(14), v1.length(), DELTA, "Length calculation failed for a typical vector");

        //TC02: Length for a negative vector
        assertEquals(Math.sqrt(11), v3.length(), DELTA, "Length calculation failed for a negative vector");

        // =================BVA===================

        //TC01: Length of a unit vector
        assertEquals(1, new Vector(0, 1, 0).length(), DELTA, "Length of a unit vector should be 1");

    }

    /**
     * Tests the {@link Vector#subtract(Point)} method to ensure its correctness.
     */
    @Test
    void subtract() {
        // =================EP====================

        //TC01: Subtracting two vectors with positive values
        assertEquals(new Vector(3, 3, 3), v2.subtract(v1), "Subtract operation failed for two positive vectors");

        //TC02: Subtracting a vector with a negative vector
        assertEquals(new Vector(2, 3, 6), v1.subtract(v3), "Subtract operation failed when subtracting negative vector");

        // =================BVA===================

        //TC11: Subtracting a vector with itself should throw an exception
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1), "Subtracting a vector with itself should throw an exception");
    }

    /**
     * Tests the {@link Vector#normalize()} method to ensure its correctness.
     */
    @Test
    void normalize() {
        // =================EP====================

        //TC01: Normalize a typical vector
        Vector normalizedV1 = v1.normalize();
        assertEquals(1, normalizedV1.length(), DELTA, "Normalized vector should have length 1");
        assertEquals(new Vector(1 / Math.sqrt(14), 2 / Math.sqrt(14), 3 / Math.sqrt(14)), normalizedV1,
                "Normalized vector direction is incorrect");

        //TC02: Normalize vector with negative components
        Vector normalizedV3 = v3.normalize();
        assertEquals(1, normalizedV3.length(), DELTA, "Normalized negative vector should have length 1");
        assertEquals(new Vector(-1 / Math.sqrt(11), -1 / Math.sqrt(11), -3 / Math.sqrt(11)), normalizedV3,
                "Normalized negative vector direction is incorrect");

        // =================BVA===================

        //TC01: Normalize already a unit vector
        Vector unitVector = new Vector(0, 1, 0);
        assertEquals(unitVector, unitVector.normalize(), "Normalizing a unit vector should return the same vector");

    }

}