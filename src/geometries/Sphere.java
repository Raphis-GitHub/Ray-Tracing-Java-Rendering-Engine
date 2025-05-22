package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space.
 *
 * @author Raphael
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a sphere with a center point and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    /**
     * Returns the normal vector to the sphere at the given point.
     * The normal vector is the vector from the center of the sphere to the point,
     * normalized to have unit length.
     *
     * @param point the point on the sphere's surface
     * @return the normalized normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // Calculate vector from center to the point on the surface
        return point.subtract(center).normalize();
    }

    /**
     * Calculates the intersection points between a ray and the sphere.
     * <p>
     * The method computes the geometric intersections between the ray and this sphere.
     * Returns {@code null} if there are no intersections.
     * If there is one or two valid intersection points in front of the ray's origin,
     * the method returns them sorted by distance from the ray's origin.
     *
     * @param ray the ray to check for intersection with the sphere
     * @return List of intersection points, or {@code null} if there are none
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point o = this.center;

        Vector u;
        try {
            u = o.subtract(p0);
        } catch (IllegalArgumentException e) {
            // The ray starts at the center of the sphere
            return List.of(p0.add(v.scale(radius)));
        }

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double rSquared = alignZero(radius * radius);

        if (dSquared > rSquared) return null;

        double th = alignZero(Math.sqrt(rSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0 && t2 > 0) {
            Point p1 = p0.add(v.scale(t1));
            Point p2 = p0.add(v.scale(t2));
            return t1 < t2 ? List.of(p1, p2) : List.of(p2, p1);
        }
        if (t1 > 0)
            return List.of(p0.add(v.scale(t1)));
        if (t2 > 0)
            return List.of(p0.add(v.scale(t2)));

        return null;
    }


}