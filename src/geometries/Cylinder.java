package geometries;

import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.isZero;

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
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = new ArrayList<>();

        // Vector from the ray origin to the axis origin
        Vector p0ToRayOrigin = ray.origin().subtract(axisRay.origin());
        Vector axisDirection = axisRay.direction();

        // Project the ray direction onto the axis direction (this is needed for the cylinder's axis)
        double a = ray.direction().dotProduct(ray.direction()) - Math.pow(ray.direction().dotProduct(axisDirection), 2);
        double b = 2 * (ray.direction().dotProduct(p0ToRayOrigin) - (ray.direction().dotProduct(axisDirection) * p0ToRayOrigin.dotProduct(axisDirection)));
        double c = p0ToRayOrigin.dotProduct(p0ToRayOrigin) - Math.pow(p0ToRayOrigin.dotProduct(axisDirection), 2) - Math.pow(radius, 2);

        // Solve the quadratic equation for intersection points
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return intersections;  // No intersection
        }

        // Calculate the two solutions (the points where the ray intersects the infinite cylinder)
        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);

        // Calculate the intersection points for t1 and t2
        Point p1 = ray.origin().add(ray.direction().scale(t1));
        Point p2 = ray.origin().add(ray.direction().scale(t2));

        // Check if the intersection points are within the height of the cylinder
        Vector p1ToAxis = p1.subtract(axisRay.origin());
        Vector p2ToAxis = p2.subtract(axisRay.origin());
        double projection1 = p1ToAxis.dotProduct(axisDirection);
        double projection2 = p2ToAxis.dotProduct(axisDirection);

        // Check if the points are within the valid height range of the cylinder
        if (projection1 >= 0 && projection1 <= height) {
            intersections.add(p1);
        }
        if (projection2 >= 0 && projection2 <= height) {
            intersections.add(p2);
        }
        if (intersections.isEmpty()) {
            return null; // No valid intersection points found
        } else {
            return intersections;
        }

    }
}