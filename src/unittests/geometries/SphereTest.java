package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * unit tests for sphere
 */
class SphereTest {


    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void getNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // EP: point on +X axis
        assertEquals(new Vector(1, 0, 0), sphere.getNormal(new Point(1, 0, 0)), "Incorrect normal for point on +X axis");

        // EP: point on -X axis
        assertEquals(new Vector(-1, 0, 0), sphere.getNormal(new Point(-1, 0, 0)), "Incorrect normal for point on -X axis");

        // EP: point on +Y axis
        assertEquals(new Vector(0, 1, 0), sphere.getNormal(new Point(0, 1, 0)), "Incorrect normal for point on +Y axis");

        // EP: point on -Y axis
        assertEquals(new Vector(0, -1, 0), sphere.getNormal(new Point(0, -1, 0)), "Incorrect normal for point on -Y axis");

        // EP: point on +Z axis
        assertEquals(new Vector(0, 0, 1), sphere.getNormal(new Point(0, 0, 1)), "Incorrect normal for point on +Z axis");

        // EP: point on -Z axis
        assertEquals(new Vector(0, 0, -1), sphere.getNormal(new Point(0, 0, -1)), "Incorrect normal for point on -Z axis");
    }
}