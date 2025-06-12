package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.*;

/**
 * Geometries is a composite class that groups multiple intersectable geometries.
 */
public class Geometries implements Intersectable {
    /**
     * List of intersectable geometries.
     */
    private final List<Intersectable> geometries = new LinkedList<>();

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
     * Adds a list of intersectable geometries to the collection.
     *
     * @param ray the list of intersectable geometries to add
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable geo : geometries) {
            List<Point> temp = geo.findIntersections(ray);
            if (temp != null) {
                if (result == null)
                    result = new LinkedList<>(temp);
                else
                    result.addAll(temp);
            }
        }

        return result;
    }
}
