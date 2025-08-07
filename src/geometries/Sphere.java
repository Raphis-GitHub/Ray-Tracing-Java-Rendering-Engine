package geometries;

import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space.
 *
 * @author Raphael
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a sphere with a center point and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    /**
     * Returns the normal vector to the sphere at the given point.
     * The normal vector is the vector from the center of the sphere to the point,
     * normalized to have unit length.
     *
     * @param point the point on the sphere's surface
     * @return the normalized normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // Calculate vector from center to the point on the surface
        return point.subtract(center).normalize();
    }

    /**
     * Calculates the intersection points between a ray and the sphere.
     * <p>
     * The method computes the geometric intersections between the ray and this sphere.
     * Returns {@code null} if there are no intersections.
     * If there is one or two valid intersection points in front of the ray's origin,
     * the method returns them sorted by distance from the ray's origin.
     *
     * @param ray the ray to check for intersection with the sphere
     * @return List of intersection points, or {@code null} if there are none
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        Vector u;
        try {
            u = this.center.subtract(ray.origin());
        } catch (IllegalArgumentException e) {
            // The ray starts at the center of the sphere
            return alignZero(radius - maxDistance) >= 0 ? null
                    : List.of(new Intersection(this, ray.getPoint(radius)));
        }

        double tm = ray.direction().dotProduct(u);
        double dSquared = u.lengthSquared() - tm * tm;
        double thSquared = radiusSquared - dSquared;
        if (alignZero(thSquared) <= 0) return null;

        double th = Math.sqrt(thSquared);
        return getIntersections(ray, tm - th, tm + th, maxDistance);
    }

    /**
     * Calculates the bounding box for this sphere.
     * The bounding box extends from center ± radius in all directions.
     *
     * @return the bounding box for this sphere
     */
    @Override
    protected BoundingBox calculateBoundingBox() {
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        return new BoundingBox(
                centerX - radius, centerY - radius, centerZ - radius,
                centerX + radius, centerY + radius, centerZ + radius
        );
    }

}