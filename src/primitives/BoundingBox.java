package primitives;

import static primitives.Util.alignZero;

/**
 * BoundingBox represents an axis-aligned bounding box (AABB) for geometric objects.
 * It provides fast ray-box intersection testing using the slab method algorithm.
 * This class is used for Conservative Boundary Region (CBR) optimization in ray tracing.
 *
 * @author Eytan and Raph
 */
public class BoundingBox {
    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;

    /**
     * Constructs a bounding box with the specified minimum and maximum coordinates.
     */
    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Constructs a bounding box from two corner points.
     */
    public BoundingBox(Point min, Point max) {
        // Inline coordinate extraction for efficiency
        this.minX = min.xyz.d1();
        this.minY = min.xyz.d2();
        this.minZ = min.xyz.d3();
        this.maxX = max.xyz.d1();
        this.maxY = max.xyz.d2();
        this.maxZ = max.xyz.d3();
    }

    /**
     * Tests if a ray intersects with this bounding box using optimized slab method.
     * Focuses on the optimizations that actually matter in Java.
     */
    public boolean intersect(Ray ray, double maxDistance) {
        // Extract coordinates once - avoid repeated method calls
        double ox = ray.origin().xyz.d1();
        double oy = ray.origin().xyz.d2();
        double oz = ray.origin().xyz.d3();
        double dx = ray.direction().xyz.d1();
        double dy = ray.direction().xyz.d2();
        double dz = ray.direction().xyz.d3();

        double tMin = 0.0;
        double tMax = maxDistance;

        // X slab
        if (alignZero(dx) == 0) {
            if (ox < minX || ox > maxX) return false;
        } else {
            double invDx = 1.0 / dx;
            double t1 = (minX - ox) * invDx;
            double t2 = (maxX - ox) * invDx;
            // Branchless min/max using Math functions (JIT optimizes these well)
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return false;
        }

        // Y slab
        if (alignZero(dy) == 0) {
            if (oy < minY || oy > maxY) return false;
        } else {
            double invDy = 1.0 / dy;
            double t1 = (minY - oy) * invDy;
            double t2 = (maxY - oy) * invDy;
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return false;
        }

        // Z slab
        if (alignZero(dz) == 0) {
            if (oz < minZ || oz > maxZ) return false;
        } else {
            double invDz = 1.0 / dz;
            double t1 = (minZ - oz) * invDz;
            double t2 = (maxZ - oz) * invDz;
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return false;
        }

        return alignZero(tMax) >= 0;
    }

    /**
     * Creates a union of multiple bounding boxes.
     */
    public static BoundingBox union(BoundingBox... boxes) {
        if (boxes == null || boxes.length == 0) {
            return null;
        }

        // Find the first non-null box to initialize bounds
        BoundingBox firstBox = null;
        for (BoundingBox box : boxes) {
            if (box != null) {
                firstBox = box;
                break;
            }
        }

        if (firstBox == null) {
            return null; // All boxes are null
        }

        double minX = firstBox.minX;
        double minY = firstBox.minY;
        double minZ = firstBox.minZ;
        double maxX = firstBox.maxX;
        double maxY = firstBox.maxY;
        double maxZ = firstBox.maxZ;

        // Expand bounds to include all non-null boxes
        for (BoundingBox box : boxes) {
            if (box != null) {
                minX = Math.min(minX, box.minX);
                minY = Math.min(minY, box.minY);
                minZ = Math.min(minZ, box.minZ);
                maxX = Math.max(maxX, box.maxX);
                maxY = Math.max(maxY, box.maxY);
                maxZ = Math.max(maxZ, box.maxZ);
            }
        }

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return String.format("BoundingBox[min=(%.2f,%.2f,%.2f), max=(%.2f,%.2f,%.2f)]",
                minX, minY, minZ, maxX, maxY, maxZ);
    }
}