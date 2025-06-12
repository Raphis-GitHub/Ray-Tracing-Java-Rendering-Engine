package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.*;

/**
 * Represents a geometric plane in 3D space.
 *
 * @author Raphael
 */
public class Plane extends Geometry {
    /**
     * a point in which the plane sits on and from where
     * the direction vector sits.
     */
    protected final Point point;
    /**
     * a vector perpendicular to the plane denoted direction.
     */
    protected final Vector normal;

    /**
     * Constructs a plane using three points.
     * First, two vectors are created from the <b><i>three</i></b> points:
     * Vector `v1` from `p1` to `p2` by subtracting P<sub>1</sub> from `p2`.
     * Vector `v2` from `p1` to `p3` by subtracting `p1` from `p3`.
     * Next, the cross product of these two vectors is calculated to get a vector
     * that is orthogonal (perpendicular) to the plane defined by the three points.
     * Finally, the resulting orthogonal vector is normalized to ensure it has a length of 1,
     * making it a unit normal vector.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @throws IllegalArgumentException when the points are co-lined
     */
    public Plane(Point p1, Point p2, Point p3) {
        this.point = p1;
        this.normal = p2.subtract(p1).crossProduct(p3.subtract(p1)).normalize();
    }

    /**
     * Constructs a plane using a point and a normal vector.
     * The normal vector is normalized before storing.
     *
     * @param point  the reference point
     * @param normal the normal vector
     */
    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane using a point and a normal vector.
     * The normal vector is normalized before storing.
     *
     * @param point the reference point
     */
    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

//todo: remove this method, it is not needed in Plane class

    /**
     * Returns the point on the plane.
     *
     * @return the point on the plane
     */
    public Vector getNormal() {
        return normal;
    }

    /**
     * Calculates the intersection point(s) between the ray and the plane.
     * <p>
     * If there is no intersection (ray is parallel, lies in plane, or points away),
     * the method returns {@code null}.
     *
     * @param ray the ray to intersect with the plane
     * @return list of 1 intersection point, or {@code null} if there is none
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector u;
        try {
            u = this.point.subtract(ray.origin());
        } catch (IllegalArgumentException e) {
            // Ray direction is zero, no intersection
            return null;
        }

        // Denominator: n · v
        double nv = alignZero(normal.dotProduct(ray.direction()));
        // If ray is parallel to the plane or lies in the plane
        if (isZero(nv)) return null;

        // Numerator: n · (Q0 - P0)
        double t = alignZero(normal.dotProduct(u)) / nv;
        // If intersection is behind the ray's head
        return t <= 0 ? null : List.of(ray.getPoint(t));
    }

}