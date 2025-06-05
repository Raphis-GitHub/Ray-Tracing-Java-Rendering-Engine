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
            // Ray starts on the axis origin - special case
            deltaP = null;
        }

        // Calculate helper vectors and scalars for the quadratic equation
        // The tube equation: (P - Pa - t*Va)² - ((P - Pa - t*Va)·Va)² = r²
        // Where P is a point on the ray: P = P0 + t*v

        double vDotVa = v.dotProduct(va);
        Vector vMinusProj = v;

        // v - (v·va)*va (perpendicular component of v to axis)
        if (!isZero(vDotVa)) {
            vMinusProj = v.subtract(va.scale(vDotVa));
        }

        // Quadratic coefficients: at² + bt + c = 0
        double a = vMinusProj.lengthSquared();

        // If a ≈ 0, ray is parallel to axis
        if (isZero(a)) {
            return null;
        }

        double b = 0;
        double c = -radiusSquared;

        if (deltaP != null) {
            double deltaPDotVa = deltaP.dotProduct(va);
            Vector deltaPMinusProj = deltaP;

            // deltaP - (deltaP·va)*va (perpendicular component of deltaP to axis)
            if (!isZero(deltaPDotVa)) {
                deltaPMinusProj = deltaP.subtract(va.scale(deltaPDotVa));
            }

            b = 2 * vMinusProj.dotProduct(deltaPMinusProj);
            c = deltaPMinusProj.lengthSquared() - radiusSquared;
        }

        // Calculate discriminant
        double discriminant = alignZero(b * b - 4 * a * c);

        // No intersection if discriminant is negative
        if (discriminant < 0) {
            return null;
        }

        // Calculate the two possible t values
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDiscriminant) / (2 * a));
        double t2 = alignZero((-b + sqrtDiscriminant) / (2 * a));

        // Check which intersections are valid (t > 0)
        if (t1 > 0 && t2 > 0) {
            Point p1 = p0.add(v.scale(t1));
            Point p2 = p0.add(v.scale(t2));
            return List.of(p1, p2);
        }

        if (t1 > 0) {
            return List.of(p0.add(v.scale(t1)));
        }

        if (t2 > 0) {
            return List.of(p0.add(v.scale(t2)));
        }

        return null;
    }

}