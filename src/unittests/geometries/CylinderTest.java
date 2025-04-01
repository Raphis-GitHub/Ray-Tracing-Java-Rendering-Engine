package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

class CylinderTest {

    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     */
    @Test
    void getNormal() {
        Ray axis = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        Cylinder cylinder = new Cylinder(axis, 1, 5);

        // EP: point on side surface
        Point p1 = new Point(1, 0, 2);
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(p1), "Incorrect normal on side surface");

        // EP: point on side with diagonal
        Point p2 = new Point(1, 1, 3);
        Vector expected2 = new Vector(1, 1, 0).normalize();
        assertEquals(expected2, cylinder.getNormal(p2), "Incorrect normal on diagonal side");

        // BV: point on bottom base (t ≈ 0)
        Point p3 = new Point(0.5, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p3), "Incorrect normal on bottom base");

        // BV: point on top base (t ≈ height)
        Point p4 = new Point(0, -0.5, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p4), "Incorrect normal on top base");

        // BV: point at edge between side and bottom
        Point p5 = new Point(1, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p5), "Incorrect normal at bottom edge");

        // BV: point at edge between side and top
        Point p6 = new Point(1, 0, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p6), "Incorrect normal at top edge");
    }
}