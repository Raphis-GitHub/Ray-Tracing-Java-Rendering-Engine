package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

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
     *
     * @param ray the ray to test
     * @return list with one intersection point or null
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Vector v = ray.direction();
        Point p = planeIntersections.getFirst();

        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        Vector v1v2 = v2.subtract(v1);
        Vector v2v3 = v3.subtract(v2);
        Vector v3v1 = v1.subtract(v3);

        Vector n1, n2, n3;
        try {
            n1 = v1v2.crossProduct(p.subtract(v1));
            n2 = v2v3.crossProduct(p.subtract(v2));
            n3 = v3v1.crossProduct(p.subtract(v3));
        } catch (IllegalArgumentException e) {
            // One of the vectors was ZERO => point is on edge or vertex → no intersection
            return null;
        }

        double sign1 = alignZero(v.dotProduct(n1));
        double sign2 = alignZero(v.dotProduct(n2));
        double sign3 = alignZero(v.dotProduct(n3));

        if ((sign1 > 0 && sign2 > 0 && sign3 > 0) || (sign1 < 0 && sign2 < 0 && sign3 < 0)) {
            return List.of(p);
        }

        return null;
    }


}