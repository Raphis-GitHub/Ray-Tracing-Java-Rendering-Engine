package geometries;

import lighting.LightSource;
import primitives.*;

import java.util.List;

/**
 * Intersectable is an interface for all geometric objects that can be intersected by a ray.
 * Each class that implements this interface must provide logic to find intersection points with a ray.
 * <p>
 * If no intersections exist — return null (not an empty list).
 *
 * @author Eytan and Raph
 */
public abstract class Intersectable {
    /**
     * geoPoint
     */
    public static class Intersection {

        /**
         * Represents the geometry involved in the intersection.
         */
        public final Geometry geometry;

        /**
         * Represents the point of intersection.
         */
        public final Point point;

        /**
         * Represents the material of the intersected geometry.
         */
        public final Material material;

        /**
         * Represents the direction vector of the intersecting ray.
         */
        public Vector direction;

        /**
         * Represents the normal vector at the intersection point.
         */
        public Vector normal;

        /**
         * Represents the dot product of the normal vector and the ray direction.
         */
        public double dotProduct;

        /**
         * Represents the light source affecting the intersection point.
         */
        public LightSource lightSource;

        /**
         * Represents the direction vector from the light source.
         */
        public Vector lightToPoint;

        /**
         * Represents the direction vector to the light source.
         */
        public Vector lightDirection;

        /**
         * Represents the dot product of the normal vector and the light direction.
         */
        public double lightDotProduct;

        /**
         * default constructor
         *
         * @param geometry body
         * @param point    point of intersection
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry != null ? geometry.getMaterial() : null;
        }

        /**
         * Checks if this intersection is equal to another object.
         *
         * @param obj the object to compare with
         * @return true if the objects are equal, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Intersection other = (Intersection) obj;
            return geometry == other.geometry && point.equals(other.point);
        }

        @Override
        public String toString() {
            return "Intersection{geometry=" + geometry + ", point=" + point + "}";
        }
    }

    /**
     * Finds intersection points between a ray and the geometry.
     *
     * @param ray the ray to intersect with
     * @return a list of intersection points, or null if no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }

    /**
     * Helper method to calculate intersections between a ray and the geometry.
     * According to NVI, this should be private, but Java does not allow private abstract methods.
     * Therefore, it is protected.
     *
     * @param ray the ray to intersect with
     * @return a list of Intersection objects, or null if no intersections
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Calculates intersections between a ray and the geometry using the NVI pattern.
     * This method is final and delegates to the protected helper method.
     *
     * @param ray the ray to intersect with
     * @return a list of Intersection objects, or null if no intersections
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }
}
