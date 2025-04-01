package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Testing Planes
 * @author Raphael & Eitan
 */
class PlaneTest {

    private static final double DELTA = 0.000001;

    /** Test method for {@link Plane#Plane(Point, Point, Point)}. */
    @Test
    void testConstructorThreePoints() {
        Point P0 = new Point(0, 0, 0);
        Point P1 = new Point(1, 1, 1);

        // Equivalence Partitions Test
        assertDoesNotThrow(() -> {
            Plane plane = new Plane(
                    P0,
                    new Point(1, 0, 0),
                    new Point(0, 1, 0)
            );

            Vector normal = plane.getNormal();

            // Ensure normal is perpendicular to at least two vectors on the plane
            Vector v1 = new Vector(1, 0, 0);
            Vector v2 = new Vector(0, 1, 0);

            assertEquals(0, normal.dotProduct(v1), DELTA, "Normal is not perpendicular to first vector");
            assertEquals(0, normal.dotProduct(v2), DELTA, "Normal is not perpendicular to second vector");

            // Ensure normal is normalized (length = 1)
            assertEquals(1, normal.length(), DELTA, "Normal vector is not normalized");
        }, "Plane constructor with three points failed");

        // Boundary Values Tests - Collinear or coinciding points

        // Case 1: First and second points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P0, P1),
                "Plane constructor allowed first and second points to coincide");

        // Case 2: First and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P1, P0),
                "Plane constructor allowed first and third points to coincide");

        // Case 3: Second and third points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P1, P0, P0),
                "Plane constructor allowed second and third points to coincide");

        // Case 4: All points coincide
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P1, P1, P1),
                "Plane constructor allowed all points to coincide");

        // Case 5: Points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        P0, P1, new Point(2, 2, 2)),
                "Plane constructor allowed collinear points");
    }

    /** Test method for {@link Plane#Plane(Point, Vector)}. */
    @Test
    void testConstructorPointVector() {
        Point P0 = new Point(0, 0, 0);
        Vector normal = new Vector(0, 0, 1);

        assertDoesNotThrow(() -> {
            Plane plane = new Plane(P0, normal);
            assertEquals(1, plane.getNormal().length(), DELTA, "Normal vector is not normalized");
            assertEquals(normal.normalize(), plane.getNormal(), "Plane constructor with point and vector failed to set normal correctly");
        }, "Plane constructor with point and vector threw an unexpected exception");
    }

}
