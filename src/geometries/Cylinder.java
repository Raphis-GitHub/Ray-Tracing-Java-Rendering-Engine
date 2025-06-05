package geometries;

import primitives.*;

import java.util.LinkedList;
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
    public List<Point> findIntersections(Ray ray) {
        // Collect valid intersection points
        List<Point> intersections = new LinkedList<>();

        // 1. Check intersections with the cylinder sides
        List<Point> tubeIntersections = super.findIntersections(ray);
        if (tubeIntersections != null) {
            for (Point p : tubeIntersections) {
                double t = alignZero(axisRay.direction().dotProduct(p.subtract(axisRay.origin())));
                if (t >= 0 && t <= height) {
                    intersections.add(p);
                }
            }
        }

        // 2. Check intersections with caps (bottom at t=0, top at t=height)
        Point[] capCenters = {
                axisRay.origin(),                                           // bottom cap
                axisRay.origin().add(axisRay.direction().scale(height))   // top cap
        };

        Vector[] capNormals = {
                axisRay.direction().scale(-1),  // bottom cap normal (opposite to axis)
                axisRay.direction()             // top cap normal (same as axis)
        };

        // Check both caps
        for (int i = 0; i < 2; i++) {
            double nv = alignZero(capNormals[i].dotProduct(ray.direction()));
            if (!isZero(nv)) {
                double t = alignZero(capNormals[i].dotProduct(capCenters[i].subtract(ray.origin()))) / nv;
                if (t > 0) {
                    Point p = ray.getPoint(t);
                    if (alignZero(p.distanceSquared(capCenters[i]) - radiusSquared) <= 0) {
                        intersections.add(p);
                    }
                }
            }
        }

        // Return result based on number of intersections
        return intersections.isEmpty() ? null :
                intersections.size() == 1 ? List.of(intersections.get(0)) :
                        List.of(intersections.get(0), intersections.get(1));
    }
}