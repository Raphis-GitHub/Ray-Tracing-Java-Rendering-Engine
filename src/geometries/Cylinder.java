package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
//TODO: write javadocs

/**
 * Represents a cylinder in 3D space.
 *
 * @author Raphael
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     * Represents the length of the cylinder along its central axis.
     * This value is immutable and defined during the construction of the cylinder.
     */
    private final double height;

    /**
     * Constructs a cylinder with a central axis ray, radius, and height.
     *
     * @param axisRay the central axis ray of the cylinder
     * @param radius  the radius of the cylinder
     * @param height  the height of the cylinder
     */
    public Cylinder(Ray axisRay, double radius, double height) {
        super(axisRay, radius);
        this.height = height;
    }


    /**
     * Returns the normal vector to the cylinder at the given point.
     * The normal vector depends on where the point is located:
     * - If the point is on the bottom base, the normal points in the negative direction of the axis
     * - If the point is on the top base, the normal points in the positive direction of the axis
     * - If the point is on the side, the normal is perpendicular to the axis and points outward
     *
     * @param point the point on the cylinder's surface
     * @return the normalized normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // Calculate vector from cylinder's origin to the point
        Vector v = point.subtract(axisRay.origin);

        // Calculate projection of this vector onto the axis direction
        double t = axisRay.direction.dotProduct(v);

        // Check if the point is on the bottom base (t ≈ 0)
        if (Util.isZero(t)) {
            return axisRay.direction.scale(-1); // Normal points opposite to the axis direction
        }

        // Check if the point is on the top base (t ≈ height)
        if (Util.isZero(t - height)) {
            return axisRay.direction; // Normal points in the axis direction
        }

        // For points on the side of the cylinder:
        // Find the closest point on the axis
        Point closestPoint = axisRay.origin.add(axisRay.direction.scale(t));

        // Calculate normal as vector from axis to surface
        return point.subtract(closestPoint).normalize();
    }
}