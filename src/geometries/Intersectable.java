package geometries;

import lighting.LightSource;
import primitives.*;

import java.util.List;

/**
 * Intersectable is an interface for all geometric objects that can be intersected by a ray.
 * Each class that implements this interface must provide logic to find intersection points with a ray.
 * <p>
 * If no intersections exist — return null (not an empty list).
 * <p>
 * Implements Conservative Boundary Region (CBR) optimization using bounding boxes for fast ray rejection.
 *
 * @author Eytan and Raph
 */
public abstract class Intersectable {

    /**
     * Global flag to enable/disable Conservative Boundary Region (CBR) optimization.
     * When disabled, all intersection calculations proceed directly without bounding box checks.
     */
    private static boolean cbrEnabled = true;

    /**
     * Cached bounding box for this geometry (lazy initialization with thread safety).
     */
    protected volatile BoundingBox boundingBox;

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
        public Material material;

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
        public Vector pointToLight;

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
     * Calculates the bounding box for this geometry.
     * Returns null for infinite geometries (Plane, Tube).
     *
     * @return the bounding box for this geometry, or null if infinite
     */
    protected abstract BoundingBox calculateBoundingBox();

    /**
     * Gets the bounding box for this geometry using thread-safe lazy initialization.
     * Uses double-checked locking pattern for optimal performance in multithreaded environments.
     *
     * @return the bounding box for this geometry, or null if infinite
     */
    public BoundingBox getBoundingBox() {
        if (boundingBox == null) {
            synchronized (this) {
                if (boundingBox == null) {
                    boundingBox = calculateBoundingBox();
                }
            }
        }
        return boundingBox;
    }

    /**
     * Enables or disables Conservative Boundary Region (CBR) optimization globally.
     * When enabled (default), rays are tested against bounding boxes for early rejection.
     * When disabled, all intersection calculations proceed directly without CBR checks.
     *
     * @param enabled true to enable CBR, false to disable
     */
    public static void setCBREnabled(boolean enabled) {
        cbrEnabled = enabled;
    }

    /**
     * Returns whether Conservative Boundary Region (CBR) optimization is currently enabled.
     *
     * @return true if CBR is enabled, false if disabled
     */
    public static boolean isCBREnabled() {
        return cbrEnabled;
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
     * @param ray         the ray to intersect with
     * @param maxDistance for calculations
     * @return a list of Intersection objects, or null if no intersections
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance);

    /**
     * Calculates intersections between a ray and the geometry using the NVI pattern.
     * This method is final and delegates to the protected helper method.
     *
     * @param ray the ray to intersect with
     * @return a list of Intersection objects, or null if no intersections
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Calculates intersections between a ray and the geometry with a maximum distance constraint.
     * Implements Conservative Boundary Region (CBR) optimization for early ray rejection when enabled.
     *
     * @param ray         the ray to intersect with
     * @param maxDistance the maximum distance to consider for intersections
     * @return a list of Intersection objects, or null if no intersections
     */
    public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
        // CBR CHECK - Conservative Boundary Region optimization for early rejection (if enabled)
        if (cbrEnabled) {
            BoundingBox bbox = getBoundingBox();
            if (bbox != null && !bbox.intersect(ray, maxDistance)) {
                return null; // Ray doesn't hit bounding box - early rejection
            }
        }

        // Proceed to actual intersection calculation
        return calculateIntersectionsHelper(ray, maxDistance);
    }
}
