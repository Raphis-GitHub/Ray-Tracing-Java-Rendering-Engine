package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube in 3D space.
 *
 * @author Raphael
 */
public class Tube extends RadialGeometry {
    protected final Ray axisRay;

    /**
     * Constructs a tube with a central axis ray and radius.
     *
     * @param axisRay the central axis ray of the tube
     * @param radius  the radius of the tube
     */
    public Tube(Ray axisRay, double radius) {
        super(radius);
        this.axisRay = axisRay;
    }


    @Override
    public Vector getNormal(Point point) {
        // This implementation will return null for now as per instructions.
        return null;
    }
}