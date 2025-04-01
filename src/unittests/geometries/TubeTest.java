package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

class TubeTest {

    @Test
    void getNormal() {
        Ray axisRay = new Ray(new Vector(0, 0, 1),new Point(0, 0, 0));
        Tube tube = new Tube(axisRay, 1);
        Point p = new Point(1, 0, 5); // נקודה על מעטפת הגליל

        assertNull(tube.getNormal(p), "Expected getNormal to return null as it's not implemented yet");
    }
}