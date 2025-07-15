package geometries;

import primitives.*;
import primitives.Vector;

import java.util.*;

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
        // Initialize intersections list
        List<Point> intersections = new ArrayList<>();

        // Find intersections with the infinite cylinder
        Tube tube = new Tube(axisRay, radius);
        List<Point> infiniteCylinderIntersections = tube.findIntersections(ray);
        if (infiniteCylinderIntersections != null) {
            intersections.addAll(infiniteCylinderIntersections);
        }

        // Remove intersections outside the cylinder height
        Iterator<Point> iterator = intersections.iterator();
        while (iterator.hasNext()) {
            Point intersection = iterator.next();
            double t = axisRay.direction().dotProduct(intersection.subtract(axisRay.getPoint(0d)));
            if (t <= 0d || t >= height || alignZero(intersection.distanceSquared(ray.getPoint(0)) - maxDistance * maxDistance) > 0d) {
                iterator.remove();
            }
        }

        // Define planes for the bottom and top bases
        Plane bottomBase = new Plane(axisRay.getPoint(0d), axisRay.direction());
        Plane topBase = new Plane(axisRay.getPoint(height), axisRay.direction());

        // Return intersections if there are exactly 2 (so they are on the sides of the cylinder)
        if (intersections.size() == 2) {
            return List.of(new Intersection(this, intersections.get(0)), new Intersection(this, intersections.get(1)));
        }

        // Find intersections with the bottom base
        List<Point> bottomBaseIntersections = bottomBase.findIntersections(ray);
        if (bottomBaseIntersections != null && alignZero(bottomBaseIntersections.getFirst().distanceSquared(ray.getPoint(0)) - maxDistance) <= 0d) {
            Point intersection = bottomBaseIntersections.getFirst();
            if (axisRay.getPoint(0d).distanceSquared(intersection) <= radius * radius) {
                intersections.add(intersection);
            }
        }

        // Find intersections with the top base
        List<Point> topBaseIntersections = topBase.findIntersections(ray);
        if (topBaseIntersections != null && alignZero(topBaseIntersections.getFirst().distanceSquared(ray.getPoint(0)) - maxDistance) <= 0d) {
            Point intersection = topBaseIntersections.getFirst();
            if (axisRay.getPoint(height).distanceSquared(intersection) <= radius * radius) {
                intersections.add(intersection);
            }
        }

        // if the ray is tangent to the cylinder
        if (intersections.size() == 2 && axisRay.getPoint(0).distanceSquared(intersections.get(0)) == radius * radius &&
                axisRay.getPoint(height).distanceSquared(intersections.get(1)) == radius * radius) {
            Vector v = intersections.get(1).subtract(intersections.get(0));
            if (v.normalize().equals(axisRay.direction()) || v.normalize().equals(axisRay.direction().scale(-1d)))
                return null;
        }

        // Return null if no valid intersections found
        List<Intersection> geoPoints = new ArrayList<>();
        for (Point p : intersections) {
            geoPoints.add(new Intersection(this, p));
        }

        return geoPoints.isEmpty() ? null : geoPoints;
    }

}
