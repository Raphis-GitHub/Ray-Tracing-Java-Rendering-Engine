package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * unit tests for tube
 */
class TubeTest {

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     */
    @Test
    void getNormal() {
        Ray axis = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        Tube tube = new Tube(axis, 1);

        // EP: point on the side of the tube (X direction)
        Point p1 = new Point(1, 0, 5);
        assertEquals(new Vector(1, 0, 0), tube.getNormal(p1), "Incorrect normal on X side");

        // EP: point on the side of the tube (Y direction)
        Point p2 = new Point(0, 1, 10);
        assertEquals(new Vector(0, 1, 0), tube.getNormal(p2), "Incorrect normal on Y side");

        // EP: point on diagonal side
        Point p3 = new Point(1, 1, 7);
        Vector expected3 = new Vector(1, 1, 0).normalize();
        assertEquals(expected3, tube.getNormal(p3), "Incorrect normal on diagonal side");

        // BV: point exactly on axis projection origin
        Point p4 = new Point(1, 0, 0);
        assertEquals(new Vector(1, 0, 0), tube.getNormal(p4), "Incorrect normal at axis base");

        // BV: point on high Z value
        Point p5 = new Point(0, -1, 1000);
        assertEquals(new Vector(0, -1, 0), tube.getNormal(p5), "Incorrect normal at high Z");

        // BV: very close point with precision check
        Point p6 = new Point(0.000001, 1, 3);
        Vector expected6 = new Vector(0.000001, 1, 0).normalize();
        assertEquals(expected6, tube.getNormal(p6), "Incorrect normal for near-axis diagonal");
    }
}