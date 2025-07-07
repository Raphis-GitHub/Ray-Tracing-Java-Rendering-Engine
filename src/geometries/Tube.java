package geometries;

import primitives.*;

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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Point pa = axisRay.origin();
        Vector va = axisRay.direction();

        // Handle ray origin relative to axis origin
        Vector deltaP;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            // Ray starts exactly at axis origin
            deltaP = null;
        }

        // Calculate perpendicular component of the ray direction
        double vDotVa = alignZero(v.dotProduct(va));
        Vector vPerp;
        if (isZero(vDotVa)) {
            // Ray is perpendicular to axis
            vPerp = v;
        } else {
            try {
                vPerp = v.subtract(va.scale(vDotVa));
            } catch (IllegalArgumentException e) {
                // Ray is exactly parallel to axis - no side intersections
                return null;
            }
        }

        // Initialize quadratic equation coefficients
        double a = vPerp.lengthSquared();
        double b = 0;
        double c = -radiusSquared;

        // Calculate coefficients when ray doesn't start at axis origin
        if (deltaP != null) {
            double deltaPDotVa = alignZero(deltaP.dotProduct(va));
            Vector deltaPPerp;

            if (isZero(deltaPDotVa)) {
                // deltaP is perpendicular to axis
                deltaPPerp = deltaP;
            } else {
                try {
                    deltaPPerp = deltaP.subtract(va.scale(deltaPDotVa));
                } catch (IllegalArgumentException e) {
                    // deltaP is exactly parallel to axis - ray starts on axis
                    deltaPPerp = null;
                }
            }

            if (deltaPPerp != null) {
                b = 2 * vPerp.dotProduct(deltaPPerp);
                c += deltaPPerp.lengthSquared();
            }
        }

        // Solve quadratic equation
        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double a2 = 2 * a;
        double t1 = (-b - sqrtDisc) / a2;
        double t2 = (-b + sqrtDisc) / a2;
        // t1 is always less than t2
        return getIntersections(ray, t1, t2);
    }

}