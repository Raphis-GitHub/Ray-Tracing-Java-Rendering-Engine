package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 *
 * @author Raphael
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
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

    /**
     * Returns the normal vector to the sphere at the given point.
     * The normal vector is the vector from the center of the sphere to the point,
     * normalized to have unit length.
     *
     * @param point the point on the sphere's surface
     * @return the normalized normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // Calculate vector from center to the point on the surface
        return point.subtract(center).normalize();
    }
}