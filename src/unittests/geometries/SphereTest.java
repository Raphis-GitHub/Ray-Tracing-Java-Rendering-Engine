package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

    @Test
    void getNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);
        Point p = new Point(1, 0, 0); // נקודה על פני הכדור

        assertNull(sphere.getNormal(p), "Expected getNormal to return null as it's not implemented yet");
    }
}