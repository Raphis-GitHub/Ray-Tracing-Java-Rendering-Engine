package primitives;

import geometries.Intersectable.Intersection;

import java.util.List;

/**
 * a Class Ray; Represents a ray in three-dimensional space, defined by a direction vector and an origin point.
 *
 * @author Raphael
 */
public class Ray {
    /**
     * Direction vector of the ray
     */
    final private Vector direction;

    /**
     * Origin point of the ray
     */
    final private Point origin; // Origin point of the ray

    /**
     * Constructs a Ray with the specified direction vector and origin point.
     *
     * @param direction The direction vector of the ray.
     * @param origin    The origin point of the ray.
     */
    public Ray(Vector direction, Point origin) {
        this.direction = direction.normalize(); // Normalize the direction vector
        this.origin = origin;
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
     * Returns a string representation of the ray.
     *
     * @return The string representation of the ray.
     */
    @Override
    public String toString() {
        return "Ray [" + direction + "," + origin + "]";
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
     * <p>
     * Current implementation: stub returning null.
     *
     * @param points list of points to check
     * @return the closest point to the ray's origin, or null
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(points.stream().map(p -> new Intersection(null, p)).toList()).point;
    }

    /**
     * Checks if this ray is equal to another object.
     *
     * @param obj the object to compare with
     * @return true if the object is a Ray with the same head and direction, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) && origin.equals(other.origin) && direction.equals(other.direction);
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
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distance = origin.distanceSquared(intersection.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = intersection;
            }
        }
        return closest;
    }
}
