package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * The Geometry interface represents geometric shapes.
 * It defines a method to get the normal vector at a given point on the surface of the shape.
 *
 * @author Raphael
 */
public abstract class Geometry implements Intersectable {
    /**
     * Returns the normal vector to the geometry at a given point on the geometry's surface
     *
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);

}