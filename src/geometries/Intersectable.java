package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Intersectable is an interface for all geometric objects that can be intersected by a ray.
 * Each class that implements this interface must provide logic to find intersection points with a ray.
 * <p>
 * If no intersections exist — return null (not an empty list).
 *
 * @author Eytan
 */
public interface Intersectable {
    /**
     * Finds intersection points between a ray and the geometry.
     *
     * @param ray the ray to intersect with
     * @return a list of intersection points, or null if no intersections
     */
    List<Point> findIntersections(Ray ray);
}
