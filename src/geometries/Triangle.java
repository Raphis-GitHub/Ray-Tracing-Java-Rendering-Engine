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
    public List<Point> findIntersections(Ray ray) {
        Point vertex0 = vertices.getFirst();
        Point vertex1 = vertices.get(1);
        Point vertex2 = vertices.get(2);
        Vector edge1 = vertex1.subtract(vertex0);
        Vector edge2 = vertex2.subtract(vertex0);
        Vector h = ray.direction().crossProduct(edge2);
        Vector s = ray.origin().subtract(vertex0);
        Vector q = s.crossProduct(edge1);
        double a, f, u, v;
        a = alignZero(edge1.dotProduct(h));

        if (isZero(a)) {
            return null;    // This ray is parallel to this triangle.
        }

        f = 1.0 / a;
        u = f * (s.dotProduct(h));

        if (u <= 0.0 || u >= 1.0) {
            return null;
        }

        v = f * ray.direction().dotProduct(q);

        if (v <= 0.0 || u + v >= 1.0) {
            return null;
        }

        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dotProduct(q);
        if (!isZero(t) && t > 0) // ray intersection
        {
            return plane.findIntersections(ray);
        } else // This means that there is a line intersection but not a ray intersection.
        {
            return null;
        }
    }

}