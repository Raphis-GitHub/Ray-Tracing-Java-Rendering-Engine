package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 * @author Raphael
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    /**
     * Constructs a sphere with a center point and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }


    @Override
    public Vector getNormal(Point point) {
        // This implementation will return null for now as per instructions.
        return null;
    }
}