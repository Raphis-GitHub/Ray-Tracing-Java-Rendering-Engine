package primitives;

import geometries.Polygon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void add() {
       assertEquals(new Point(1, 2, 3), new Point(1, 1, 1).add(new Vector(0, 1, 2)), "you suck at addition");
        assertEquals(new Point(0, 0, 0), new Point(1, 1, 1).add(new Vector(-1, -1, -1)), "you suck at addition");

    }

    @Test
    void subtract() {
        assertEquals(new Point(1, 2, 3), new Point(2, 4, 6).subtract(new Vector(1, 2, 3)), "you suck at substraction");
//        assertThrows(IllegalArgumentException.class, new Point(1,1,1).subtract(new Point(-1,-1,-1)), //
//                "Constructed a polygon with wrong order of vertices");

    }

    @Test
    void distanceSquared() {
    }

    @Test
    void distance() {
    }
}