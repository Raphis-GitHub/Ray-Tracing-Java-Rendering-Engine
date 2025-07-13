package primitives;

import geometries.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.isZero;

/**
 * a Class Ray; Represents a ray in three-dimensional space, defined by a direction vector and an origin point.
 *
 * @author Raphael
 */
public class Ray {
    /**
     * Small offset to prevent self-intersection when constructing secondary rays.
     */
    private static final double DELTA = 0.1;

    /**
     * Direction vector of the ray
     */
    final private Vector direction;

    /**
     * Origin point of the ray
     */
    final private Point origin;

    /**
     * Constructs a Ray with the specified direction vector and origin point.
     *
     * @param direction The direction vector of the ray.
     * @param origin    The origin point of the ray.
     */
    public Ray(Vector direction, Point origin) {
        this.direction = direction.normalize();
        this.origin = origin;
    }

    /**
     * Constructs a Ray with offset head point to avoid self-intersection.
     * Used for shadow rays, reflection rays, and refraction rays.
     *
     * @param head      The point where the ray starts
     * @param direction The direction vector of the ray
     * @param normal    The normal vector at the intersection point
     */
    public Ray(Point head, Vector direction, Vector normal) {
        this.direction = direction.normalize();
        double nv = normal.dotProduct(direction);
        Vector delta = normal.scale(isZero(nv) ? 0 : (nv > 0 ? DELTA : -DELTA));
        this.origin = head.add(delta);
    }

    /**
     * Calculates a point along the ray at a given distance from the origin.
     *
     * @param t the distance from the ray's origin
     * @return the point at distance t along the ray direction
     */
    public Point getPoint(double t) {
        try {
            return origin.add(direction.scale(t));
        } catch (IllegalArgumentException e) {
            return origin;
        }
    }

    /**
     * Returns the origin point of the ray.
     *
     * @return The origin point of the ray.
     */
    public Point origin() {
        return origin;
    }

    /**
     * Returns the direction vector of the ray.
     *
     * @return The direction vector of the ray.
     */
    public Vector direction() {
        return direction;
    }

    /**
     * Returns the closest point to the ray's origin from a list of points.
     *
     * @param points list of points to check
     * @return the closest point to the ray's origin, or null
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(points.stream().map(p -> new Intersection(null, p))
                .toList()).point;
    }

    /**
     * Finds the closest intersection from a list of intersections to the ray's origin.
     *
     * @param intersections list of intersections to check
     * @return the closest intersection to the ray's origin, or null if the list is null
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null) return null;

        Intersection closest = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;
        for (Intersection intersection : intersections) {
            double distanceSquared = origin.distanceSquared(intersection.point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = intersection;
            }
        }
        return closest;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) && origin.equals(other.origin) && direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray [" + direction + "," + origin + "]";
    }
}