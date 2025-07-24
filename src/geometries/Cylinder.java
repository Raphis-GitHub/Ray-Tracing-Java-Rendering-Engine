package geometries;

import primitives.*;

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
     * The bottom base plane of the cylinder.
     * This plane is defined by the axis origin and direction.
     * It represents the circular base at the bottom of the cylinder.
     */
    private final Plane bottomBase;
    /**
     * The top base plane of the cylinder.
     */
    private final Plane topBase;
    /**
     * The center point of the top base of the cylinder.
     */
    private final Point topCenter;

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
        topCenter = axisRay.getPoint(height);
        bottomBase = new Plane(axisOrigin, axisDirection);
        topBase = new Plane(topCenter, axisDirection);
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
        Vector v = point.subtract(axisOrigin);

        // Calculate projection of this vector onto the axis direction
        double t = axisDirection.dotProduct(v);
        // Check if the point is on the bottom base (t ≈ 0)
        if (isZero(t)) return axisDirection.scale(-1); // Normal points opposite to the axis direction
        // Check if the point is on the top base (t ≈ height)
        if (isZero(t - height))
            return axisDirection; // Normal points in the axis direction

        // Calculate normal as vector from axis to surface
        return point.subtract(axisRay.getPoint(t)).normalize();
    }

    /**
     * Calculates intersections between a ray and the cylinder including both curved sides and flat bases.
     * The method first finds intersections with the infinite cylinder, then filters them by height,
     * and finally checks for intersections with the bottom and top circular bases.
     *
     * @param ray         the ray to find intersections with
     * @param maxDistance the maximum distance to find intersections
     * @return a list of intersection points, or null if no intersections found
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        var bottomBaseIntersections = bottomBase.calculateIntersections(ray, maxDistance);
        var bottomBaseIntersection = bottomBaseIntersections == null ? null : bottomBaseIntersections.getFirst().point;
        if (bottomBaseIntersection != null && alignZero(axisOrigin.distanceSquared(bottomBaseIntersection) - radiusSquared) >= 0)
            bottomBaseIntersection = null;

        var topBaseIntersections = topBase.calculateIntersections(ray, maxDistance);
        var topBaseIntersection = topBaseIntersections == null ? null : topBaseIntersections.getFirst().point;
        if (topBaseIntersection != null && alignZero(topCenter.distanceSquared(topBaseIntersection) - radiusSquared) >= 0)
            topBaseIntersection = null;

        if (bottomBaseIntersection != null && topBaseIntersection != null)
            return axisDirection.dotProduct(ray.direction()) > 0
                    ? List.of(new Intersection(this, bottomBaseIntersection), new Intersection(this, topBaseIntersection))
                    : List.of(new Intersection(this, topBaseIntersection), new Intersection(this, bottomBaseIntersection));

        var baseIntersection = bottomBaseIntersection == null ? topBaseIntersection : bottomBaseIntersection;

        // Find intersections with the infinite cylinder
        var tubeIntersections = super.calculateIntersectionsHelper(ray, maxDistance);
        if (tubeIntersections == null)
            return baseIntersection == null ? null : List.of(new Intersection(this, baseIntersection));

        var point1 = tubeIntersections.getFirst().point;
        double projection1 = axisDirection.dotProduct(point1.subtract(axisOrigin));
        if (alignZero(projection1) < 0 || alignZero(projection1 - height) > 0)
            // If the first intersection is outside the cylinder's height, ignore it
            point1 = null;

        var point2 = tubeIntersections.size() > 1 ? tubeIntersections.getLast().point : null;
        if (point2 != null) {
            double projection2 = axisDirection.dotProduct(point2.subtract(axisOrigin));
            if (alignZero(projection2) < 0 || alignZero(projection2 - height) > 0)
                // If the first intersection is outside the cylinder's height, ignore it
                point2 = null;
        }

        if (point1 == null && point2 != null) {
            point1 = point2;
            point2 = null;
        }

        if (baseIntersection != null)
            return point1 == null ? List.of(new Intersection(this, baseIntersection)) :
                    List.of(new Intersection(this, point1), new Intersection(this, baseIntersection));

        return point1 == null ? null :
                point2 == null ? List.of(new Intersection(this, point1)) :
                        List.of(new Intersection(this, point1), new Intersection(this, point2));
    }

}
