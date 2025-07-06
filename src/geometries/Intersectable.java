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
 * @author Eytan & Raph
 */
public abstract class Intersectable {
    public static class Intersection {
        public Intersection(Geometry geometry, Point point
        ) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry != null ? geometry.getMaterial() : null;

        }

        public final Material material;
        public Vector direction;
        public Vector normal;
        public double dotProduct;
        public LightSource lightSource;
        public Vector lightDirection;
        public double lightDotProduct;

        public Geometry geometry;
        public Point point;

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
