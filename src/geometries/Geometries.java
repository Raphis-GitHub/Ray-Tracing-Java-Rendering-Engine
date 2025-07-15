package geometries;

import primitives.Ray;

import java.util.*;

/**
 * Geometries is a composite class that groups multiple intersectable geometries.
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Default constructor for Geometries.
     */
    public Geometries() {
    }

    /**
     * Constructor that accepts an array of intersectable geometries.
     *
     * @param geometries the intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more intersectable geometries to the collection.
     *
     * @param geometries the intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Calculates intersections between a ray and all geometries in the collection.
     * Iterates through each geometry in the collection and aggregates all intersection points.
     *
     * @param ray the ray to test for intersections
     * @param maxDistance the maximum distance to consider for intersections
     * @return a list of all intersection points from all geometries, or null if no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;
        for (Intersectable geo : geometries) {
            List<Intersection> temp = geo.calculateIntersections(ray, maxDistance);
            if (temp != null) {
                if (result == null)
                    result = new ArrayList<>(temp);
                else
                    result.addAll(temp);
            }
        }
        return result;
    }
}
