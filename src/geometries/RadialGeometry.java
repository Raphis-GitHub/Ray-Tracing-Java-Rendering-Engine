package geometries;

import primitives.Ray;

import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.alignZero;

/**
 * Abstract class representing radial geometric shapes.
 * Implements the Geometry interface.
 *
 * @author Raphael
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double radius;

    /**
     * The square of the radius of the radial geometry.
     * This is precomputed for efficiency in calculations.
     */
    protected final double radiusSquared;

    /**
     * Constructs a RadialGeometry with the given radius.
     *
     * @param radius the radius of the shape
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    /**
     * Calculates the intersection points between a ray and the radial geometry.
     *
     * @param ray         the ray to check for intersection with the radial geometry
     * @param t1          the lesser intersection distance from the ray's origin
     * @param t2          the greater intersection distance from the ray's origin
     * @param maxDistance the maximum distance to consider for intersections
     * @return List of intersection points, or {@code null} if there are none
     */
    protected List<Intersection> getIntersections(Ray ray, double t1, double t2, double maxDistance) {
        if (alignZero(t2) <= 0) return null;
        return (alignZero(t1) <= 0
                ? List.of(new Intersection(this, ray.getPoint(t2)))
                : List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2))))
                .stream()
                .filter(intersection -> alignZero(intersection.point.distanceSquared(ray.origin()) - maxDistance * maxDistance) <= 0)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? null : list
                ));
    }
}