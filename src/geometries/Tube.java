package geometries;

import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.*;

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
        Point p0 = this.axisRay.origin();
        double t = point.subtract(p0).dotProduct(axisRay.direction());
        Point o = isZero(t) ? p0 : p0.add(axisRay.direction().scale(t));
        return point.subtract(o).normalize();
    }

    /**
     * Finds intersection points between the ray and the geometry.
     * <p>
     * If no intersections are found, the method returns {@code null} (not an empty list).
     *
     * @param ray the ray to test for intersection
     * @return a list of intersection points, or {@code null} if there are none
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point pa = axisRay.origin();
        Vector va = axisRay.direction();

        // Vector from axis origin to ray origin
        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            // Ray starts at axis origin
            deltaP = null;
        }

        // Check if ray direction is parallel to axis
        double vDotVa = alignZero(v.dotProduct(va));

        // Calculate perpendicular component of ray direction to axis
        Vector vPerp;
        if (isZero(vDotVa)) {
            vPerp = v; // Ray direction is already perpendicular to axis
        } else {
            try {
                vPerp = v.subtract(va.scale(vDotVa));
            } catch (IllegalArgumentException e) {
                // v is parallel to va (vPerp would be zero vector)
                return null;
            }
        }

        // Quadratic coefficient a = ||vPerp||²
        double a = vPerp.lengthSquared();

        // If a ≈ 0, ray direction is parallel to axis (no intersection for infinite tube)
        if (isZero(a)) {
            return null;
        }

        // Calculate b and c coefficients for quadratic equation at² + bt + c = 0
        double b = 0;
        double c = -radiusSquared;

        if (deltaP != null) {
            double deltaPDotVa = alignZero(deltaP.dotProduct(va));

            // Calculate perpendicular component of deltaP to axis
            Vector deltaPPerp;
            if (isZero(deltaPDotVa)) {
                deltaPPerp = deltaP; // deltaP is already perpendicular to axis
            } else {
                
                deltaPPerp = deltaP.subtract(va.scale(deltaPDotVa));
            }

            b = 2 * alignZero(vPerp.dotProduct(deltaPPerp));
            c = deltaPPerp.lengthSquared() - radiusSquared;
        }

        // Solve quadratic equation
        double discriminant = alignZero(b * b - 4 * a * c);

        if (discriminant < 0) {
            return null; // No real solutions
        }

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDisc) / (2 * a));
        double t2 = alignZero((-b + sqrtDisc) / (2 * a));

        // Collect valid intersections (t > 0 and not duplicate)
        List<Point> intersections = new LinkedList<>();

        if (t1 > 0) {
            intersections.add(p0.add(v.scale(t1)));
        }

        if (t2 > 0 && !isZero(t2 - t1)) { // Avoid duplicate points when discriminant ≈ 0
            intersections.add(p0.add(v.scale(t2)));
        }

        return intersections.isEmpty() ? null : intersections;
    }

}