package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * Represents a tube in 3D space.
 *
 * @author Raphael
 */
public class Tube extends RadialGeometry {
    /**
     * The central  axis ray of the tube.
     */
    protected final Ray axisRay;

    /**
     * Constructs a tube with a central axis ray and radius.
     *
     * @param axisRay the central axis ray of the tube
     * @param radius  the radius of the tube
     */
    public Tube(Ray axisRay, double radius) {
        super(radius);
        this.axisRay = axisRay;
    }

    /**
     * Returns the normal vector to the tube at the given point.
     * The normal vector is perpendicular to the axis of the tube and points outward.
     * It is calculated by finding the closest point on the axis to the given point
     * and then creating a vector from that axis point to the given point.
     *
     * @param point the point on the tube's surface
     * @return the normalized normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        Vector u = point.subtract(this.axisRay.origin);
        double t = u.dotProduct(axisRay.direction);
        if (isZero(t))
            return ((point.subtract(axisRay.origin)).normalize());
        Point o = axisRay.origin.add(axisRay.direction.scale(t));
        return point.subtract(o).normalize();
    }
}