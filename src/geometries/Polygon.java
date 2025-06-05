package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 *
 * @author Dan
 */
public class Polygon extends Geometry {
    /**
     * List of polygon's vertices
     */
    protected final List<Point> vertices;
    /**
     * Associated plane in which the polygon lays
     */
    protected final Plane plane;
    /**
     * The size of the polygon - the amount of the vertices in the polygon
     */
    private final int size;

    /**
     * Polygon constructor based on vertices list. The list must be ordered by edge
     * path. The polygon must be convex.
     *
     * @param vertices list of vertices according to their order by
     *                 edge path
     * @throws IllegalArgumentException in any case of illegal combination of
     *                                  vertices:
     *                                  <ul>
     *                                  <li>Less than 3 vertices</li>
     *                                  <li>Consequent vertices are in the same
     *                                  point
     *                                  <li>The vertices are not in the same
     *                                  plane</li>
     *                                  <li>The order of vertices is not according
     *                                  to edge path</li>
     *                                  <li>Three consequent vertices lay in the
     *                                  same line (180&#176; angle between two
     *                                  consequent edges)
     *                                  <li>The polygon is concave (not convex)</li>
     *                                  </ul>
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        this.vertices = List.of(vertices);
        size = vertices.length;

        // Generate the plane according to the first three vertices and associate the
        // polygon with this plane.
        // The plane holds the invariant normal (orthogonal unit) vector to the polygon
        plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (size == 3) return; // no need for more tests for a Triangle

        Vector n = plane.getNormal(vertices[0]);
        // Subtracting any subsequent points will throw an IllegalArgumentException
        // because of Zero Vector if they are in the same point
        Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[size - 1]);

        // Cross Product of any subsequent edges will throw an IllegalArgumentException
        // because of Zero Vector if they connect three vertices that lay in the same
        // line.
        // Generate the direction of the polygon according to the angle between last and
        // first edge being less than 180deg. It is hold by the sign of its dot product
        // with the normal. If all the rest consequent edges will generate the same sign
        // - the polygon is convex ("kamur" in Hebrew).
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < size; ++i) {
            // Test that the point is in the same plane as calculated originally
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            // Test the consequent edges have
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return plane.getNormal(point);
    }

    /**
     * Finds intersection points between the ray and the polygon.
     * First checks intersection with the polygon's plane, then verifies
     * if the intersection point is inside the polygon boundaries.
     *
     * @param ray the ray to test for intersection
     * @return a list of intersection points, or {@code null} if there are none
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        // First, find intersection with the polygon's plane
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) {
            return null;
        }

        // Get the intersection point with the plane
        Point p = planeIntersections.getFirst();
        Vector v = ray.direction();

        // Check if the point is inside the polygon using the same technique as Triangle
        // For each edge, create a vector from vertex to point and check cross product
        Vector[] normals = new Vector[size];

        for (int i = 0; i < size; i++) {
            // Get current vertex and next vertex (wrapping around)
            Point vi = vertices.get(i);
            Point viNext = vertices.get((i + 1) % size);

            // Edge vector
            Vector edge = viNext.subtract(vi);

            // Vector from vertex to intersection point
            Vector toPoint;
            try {
                toPoint = p.subtract(vi);
            } catch (IllegalArgumentException e) {
                // Point is on vertex → no intersection
                return null;
            }

            // Calculate cross product to get normal
            try {
                normals[i] = edge.crossProduct(toPoint);
            } catch (IllegalArgumentException e) {
                // Cross product is zero → point is on edge → no intersection
                return null;
            }
        }

        // Check if all dot products have the same sign
        double firstSign = alignZero(v.dotProduct(normals[0]));

        // If first sign is zero, point is on edge
        if (firstSign == 0) {
            return null;
        }

        // Check all other normals
        for (int i = 1; i < size; i++) {
            double sign = alignZero(v.dotProduct(normals[i]));

            // If sign is zero, point is on edge
            if (sign == 0) {
                return null;
            }

            // If signs don't match, point is outside
            if ((firstSign > 0 && sign < 0) || (firstSign < 0 && sign > 0)) {
                return null;
            }
        }

        // All signs match → point is inside the polygon
        return List.of(p);
    }

}
