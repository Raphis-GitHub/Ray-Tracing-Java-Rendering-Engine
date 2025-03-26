package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a cylinder in 3D space.
 * @author Raphael
 */
public class Cylinder extends Tube {
    private final double height;

    /**
     * Constructs a cylinder with a central axis ray, radius, and height.
     *
     * @param axisRay the central axis ray of the cylinder
     * @param radius  the radius of the cylinder
     * @param height  the height of the cylinder
     */
    public Cylinder(Ray axisRay, double radius, double height) {
        super( axisRay, radius);
        this.height = height;
    }


    @Override
    public Vector getNormal(Point point) {
        // This implementation will return null for now as per instructions.
        return null;
    }
}