package geometries;

import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.*;

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
        Vector v = point.subtract(axisRay.origin());

        // Calculate projection of this vector onto the axis direction
        double t = axisRay.direction().dotProduct(v);

        // Check if the point is on the bottom base (t ≈ 0)
        if (isZero(t)) {
            return axisRay.direction().scale(-1); // Normal points opposite to the axis direction
        }

        // Check if the point is on the top base (t ≈ height)
        if (isZero(t - height)) {
            return axisRay.direction(); // Normal points in the axis direction
        }

        // For points on the side of the cylinder:
        // Find the closest point on the axis
        Point closestPoint = axisRay.getPoint(t);

        // Calculate normal as vector from axis to surface
        return point.subtract(closestPoint).normalize();
    }

    /**
     * Finds intersection points between the ray and the geometry.
     * <p>
     * If no intersections are found, the method returns {@code null} (not an empty list).
     *
     * @param ray the ray to test for intersection
     * @return a list of intersection points, or {@code null} if there are none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> intersections = new ArrayList<>();

        Point axisOrigin = axisRay.origin();
        Vector axisDirection = axisRay.direction();
        Point rayOrigin = ray.origin();
        Vector rayDirection = ray.direction();

        // === SIDE INTERSECTIONS ===
        // Use tube intersection logic but filter by height bounds
        List<Intersection> tubeIntersections = super.calculateIntersectionsHelper(ray);
        if (tubeIntersections != null) {
            for (Intersection intersection : tubeIntersections) {
                Vector toPoint = intersection.point.subtract(axisOrigin);
                double heightPos = alignZero(toPoint.dotProduct(axisDirection));

                // Only include if within cylinder height bounds
                if (heightPos >= 0 && heightPos <= height) {
                    intersections.add(intersection);
                }
            }
        }

        // === CAP INTERSECTIONS ===
        double vDotAxis = alignZero(rayDirection.dotProduct(axisDirection));

        if (!isZero(vDotAxis)) { // Ray not parallel to axis

            // Bottom cap (at height = 0)
            Vector rayToAxis = axisOrigin.subtract(rayOrigin);
            double tBottom = alignZero(rayToAxis.dotProduct(axisDirection) / vDotAxis);

            if (tBottom > 0) {
                Point capPoint = ray.getPoint(tBottom);
                double distanceSquared = axisOrigin.distanceSquared(capPoint);

                if (alignZero(distanceSquared - radiusSquared) <= 0) {
                    intersections.add(new Intersection(this, capPoint));
                }
            }

            // Top cap (at height = this.height)
            Point topCenter = axisOrigin.add(axisDirection.scale(height));
            Vector rayToTopAxis = topCenter.subtract(rayOrigin);
            double tTop = alignZero(rayToTopAxis.dotProduct(axisDirection) / vDotAxis);

            if (tTop > 0) {
                Point capPoint = ray.getPoint(tTop);
                double distanceSquared = topCenter.distanceSquared(capPoint);

                if (alignZero(distanceSquared - radiusSquared) <= 0) {
                    intersections.add(new Intersection(this, capPoint));
                }
            }
        }

        return intersections.isEmpty() ? null : intersections;
    }
}