package primitives;

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
        return origin.add(direction.scale(t));
    }

    /**
     * Returns a string representation of the ray.
     *
     * @return The string representation of the ray.
     */
    @Override
    public String toString() {
        return "Ray [direction=" + direction + ", origin=" + origin + "]";
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
}
