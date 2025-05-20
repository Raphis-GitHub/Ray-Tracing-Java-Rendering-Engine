package geometries;

import primitives.Ray;
import primitives.Point;
import primitives.Vector;


import java.util.LinkedList;
import java.util.List;

/**
 * The Geometry interface represents geometric shapes.
 * It defines a method to get the normal vector at a given point on the surface of the shape.
 *
 * @author Raphael
 */
public abstract class Geometry implements Intersectable{
    /**
     * Returns the normal vector to the geometry at a given point on the geometry's surface
     *
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    abstract Vector getNormal(Point point);

    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Default constructor – creates an empty collection.
     */
    public Geometries() {}

    /**
     * Constructor – adds geometries to the collection.
     *
     * @param geometries one or more geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the internal collection.
     *
     * @param geometries one or more geometries to add
     */
    public void add(Intersectable... geometries) {
        for (Intersectable g : geometries) {
            this.geometries.add(g);
        }
    }

    /**
     * Finds all intersection points between a ray and the geometries in the collection.
     *
     * @param ray the ray to check
     * @return list of intersection points, or null if none
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable geo : geometries) {
            List<Point> temp = geo.findIntersections(ray);
            if (temp != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(temp);
            }
        }

        return result;
    }

}