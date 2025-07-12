package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.*;

/**
 * Represents a triangle in 3D space.
 * Inherits from Polygon.
 *
 * @author Raphael
 */
public class Triangle extends Polygon {
    /**
     * Constructs a triangle using three vertices.
     *
     * @param p1 the first vertex
     * @param p2 the second vertex
     * @param p3 the third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    /**
     * Finds the intersection point between a ray and the triangle.
     * Returns null if the ray does not intersect the triangle
     * or if the intersection point is outside the triangle or on its edge.
     * Moller-Trumbore intersection algorithm
     *
     * @param ray the ray to test
     * @return list with one intersection point or null
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        Point vertex0 = vertices.getFirst();
        Vector edge1 = vertices.get(1).subtract(vertex0);
        Vector edge2 = vertices.get(2).subtract(vertex0);
        Vector h = ray.direction().crossProduct(edge2);
        Vector s = ray.origin().subtract(vertex0);
        Vector q = s.crossProduct(edge1);

        double a = edge1.dotProduct(h);
        if (isZero(a))
            return null;    // This ray is parallel to this triangle.

        double f = 1.0 / a;
        double u = f * (s.dotProduct(h));
        if (alignZero(u) <= 0.0 || alignZero(u - 1) >= 0)
            return null;

        double v = f * ray.direction().dotProduct(q);
        if (alignZero(v) <= 0.0 || alignZero(u + v - 1) >= 0)
            return null;

        double t = f * edge2.dotProduct(q);
        return (alignZero(t) > 0 && alignZero(t - maxDistance) <= 0)
                ? List.of(new Intersection(this, ray.getPoint(t)))
                : null;
    }

}

