package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * The Geometry interface represents geometric shapes.
 * It defines a method to get the normal vector at a given point on the surface of the shape.
 *
 * @author Raphael
 */
public interface Geometry {
    /**
     * Returns the normal vector to the geometry at the given point.
     *
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    Vector getNormal(Point point);


}