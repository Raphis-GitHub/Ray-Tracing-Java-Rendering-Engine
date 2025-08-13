package geometries;

import primitives.BoundingBox;
import primitives.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.isZero;

/**
 * Algorithm: For each axis, sort objects by centroid and split at median.
 * Choose axis with best balance (closest to 50-50 split).
 *
 * @author Eytan and Raph
 */
public class GeometryHierarchyBuilderMedian {

    /**
     * Global flag to enable/disable BVH (Bounding Volume Hierarchy) construction.
     * When disabled, returns the original flat geometry collection without hierarchy.
     */
    private static boolean bvhEnabled = true;

    /**
     * Maximum number of objects per leaf node in the BVH tree.
     * Smaller values create deeper trees with potentially faster intersection queries.
     */
    private static int maxObjectsPerLeaf = 8;

    /**
     * Maximum depth of the BVH tree to prevent infinite recursion.
     * Larger values allow deeper trees but may impact construction performance.
     */
    private static int maxDepth = 20;

    /**
     * Enables or disables BVH (Bounding Volume Hierarchy) construction globally.
     * When enabled (default), buildHierarchy creates spatial partitioning trees.
     * When disabled, buildHierarchy returns the original flat collection.
     *
     * @param enabled true to enable BVH, false to disable
     */
    public static void setBVHEnabled(boolean enabled) {
        bvhEnabled = enabled;
    }

    /**
     * Returns whether BVH (Bounding Volume Hierarchy) construction is currently enabled.
     *
     * @return true if BVH is enabled, false if disabled
     */
    @SuppressWarnings("unused")
    public static boolean isBVHEnabled() {
        return bvhEnabled;
    }

    /**
     * Sets the maximum number of objects per leaf node in the BVH tree.
     *
     * @param maxObjects maximum objects per leaf (must be positive)
     */
    public static void setMaxObjectsPerLeaf(int maxObjects) {
        if (maxObjects > 0) {
            maxObjectsPerLeaf = maxObjects;
        }
    }

    /**
     * Gets the current maximum objects per leaf setting.
     *
     * @return current maximum objects per leaf
     */
    @SuppressWarnings("unused")
    public static int getMaxObjectsPerLeaf() {
        return maxObjectsPerLeaf;
    }

    /**
     * Sets the maximum depth of the BVH tree.
     *
     * @param depth maximum tree depth (must be positive)
     */
    public static void setMaxDepth(int depth) {
        if (depth > 0) {
            maxDepth = depth;
        }
    }

    /**
     * Gets the current maximum depth setting.
     *
     * @return current maximum depth
     */
    @SuppressWarnings("unused")
    public static int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Builder-style configuration class for easy BVH setup.
     * Provides a fluent API for configuring BVH parameters.
     */
    public static class BVHConfig {
        /**
         * Enables BVH construction.
         *
         * @return this BVHConfig for method chaining
         */
        public BVHConfig enable() {
            setBVHEnabled(true);
            return this;
        }

        /**
         * Disables BVH construction.
         *
         * @return this BVHConfig for method chaining
         */
        public BVHConfig disable() {
            setBVHEnabled(false);
            return this;
        }

        /**
         * Sets maximum objects per leaf node.
         *
         * @param maxObjects maximum objects per leaf
         * @return this BVHConfig for method chaining
         */
        public BVHConfig maxObjectsPerLeaf(int maxObjects) {
            setMaxObjectsPerLeaf(maxObjects);
            return this;
        }

        /**
         * Sets maximum tree depth.
         *
         * @param depth maximum depth
         * @return this BVHConfig for method chaining
         */
        public BVHConfig maxDepth(int depth) {
            setMaxDepth(depth);
            return this;
        }
    }

    /**
     * Creates a new BVH configuration builder.
     *
     * @return new BVHConfig instance for fluent configuration
     */
    public static BVHConfig config() {
        return new BVHConfig();
    }

    /**
     * Builds a hierarchical spatial data structure from a flat collection of geometries
     * using the median split algorithm for Bounding Volume Hierarchy (BVH) construction.
     *
     * @param flatGeometries the input collection of geometries to hierarchically organize
     * @return a hierarchically organized Geometries collection for faster intersection queries,
     * or the original collection if BVH is disabled, or null if input is null
     */
    public static Geometries buildHierarchy(Geometries flatGeometries) {
        if (flatGeometries == null) {
            return null;
        }

        // If BVH is disabled, return the original flat collection
        if (!bvhEnabled) {
            return flatGeometries;
        }

        List<Intersectable> leafGeometries = extractLeafGeometries(flatGeometries);

        if (leafGeometries.size() <= maxObjectsPerLeaf) {
            return flatGeometries;
        }

        // Filter finite objects
        List<ObjectInfo> finiteObjects = leafGeometries.stream()
                .map(ObjectInfo::new)
                .filter(obj -> obj.boundingBox != null)
                .collect(Collectors.toList());

        List<Intersectable> infiniteObjects = leafGeometries.stream()
                .filter(geo -> geo.getBoundingBox() == null)
                .toList();

        if (finiteObjects.size() <= maxObjectsPerLeaf) {
            return flatGeometries;
        }

        Geometries hierarchicalGeometries = buildRecursive(finiteObjects, 0);

        if (!infiniteObjects.isEmpty()) {
            Geometries topLevel = new Geometries(hierarchicalGeometries);
            infiniteObjects.forEach(topLevel::add);
            return topLevel;
        }

        return hierarchicalGeometries;
    }

    /**
     * Recursively constructs the BVH tree using median split partitioning.
     * <p>
     * This is the core recursive function that builds the spatial hierarchy. At each level,
     * it determines the best axis for splitting (X, Y, or Z), sorts objects by their
     * centroid coordinates along that axis, and splits at the median point.
     *
     * @param objects list of ObjectInfo containing geometries and their spatial information
     * @param depth   current recursion depth (used to prevent infinite recursion)
     * @return a Geometries node representing either an internal node with children
     * or a leaf node containing the actual geometry objects
     */
    private static Geometries buildRecursive(List<ObjectInfo> objects, int depth) {
        if (objects.size() <= maxObjectsPerLeaf || depth >= maxDepth) {
            return createLeafNode(objects);
        }

        // Find best axis using median split
        int bestAxis = findBestSplitAxis(objects);
        if (bestAxis == -1) {
            return createLeafNode(objects);
        }

        // Sort by centroid on best axis
        final int axis = bestAxis;
        objects.sort((a, b) -> Double.compare(
                a.getCentroidCoordinate(axis),
                b.getCentroidCoordinate(axis)
        ));

        // Split at median
        int mid = objects.size() / 2;
        List<ObjectInfo> leftObjects = objects.subList(0, mid);
        List<ObjectInfo> rightObjects = objects.subList(mid, objects.size());

        // Recursively build subtrees
        Geometries leftChild = buildRecursive(new ArrayList<>(leftObjects), depth + 1);
        Geometries rightChild = buildRecursive(new ArrayList<>(rightObjects), depth + 1);

        return new Geometries(leftChild, rightChild);
    }

    /**
     * Determines the optimal axis (X, Y, or Z) for splitting the given objects
     * using a simple median split heuristic.
     *
     * @param objects list of ObjectInfo to analyze for splitting
     * @return the best axis index (0=X, 1=Y, 2=Z) for splitting,
     * or -1 if no suitable axis found (all objects at same position)
     */
    private static int findBestSplitAxis(List<ObjectInfo> objects) {
        double bestBalance = Double.POSITIVE_INFINITY;
        int bestAxis = -1;

        for (int axis = 0; axis < 3; axis++) {
            final int currentAxis = axis;

            // Get coordinate range for this axis
            double min = objects.stream()
                    .mapToDouble(obj -> obj.getCentroidCoordinate(currentAxis))
                    .min().orElse(0);
            double max = objects.stream()
                    .mapToDouble(obj -> obj.getCentroidCoordinate(currentAxis))
                    .max().orElse(0);

            if (isZero(max - min)) continue; // Skip if all same

            // Calculate balance metric (how close to 50-50 split)
            double balance = max - min; // Larger range = better for splitting

            if (balance < bestBalance) {
                bestBalance = balance;
                bestAxis = axis;
            }
        }

        return bestAxis;
    }

    /**
     * Creates a leaf node containing the actual geometry objects.
     *
     * @param objects list of ObjectInfo containing the geometries to include in the leaf
     * @return a Geometries collection containing all the actual geometry objects
     */
    private static Geometries createLeafNode(List<ObjectInfo> objects) {
        Geometries leaf = new Geometries();
        objects.forEach(obj -> leaf.add(obj.geometry));
        return leaf;
    }

    /**
     * Extracts all leaf geometry objects from a potentially nested Geometries structure.
     *
     * @param geometries the input Geometries collection that may contain nested structures
     * @return a flat list of individual Intersectable geometry objects
     * @see #extractLeafGeometriesRecursive(Intersectable, List)
     */
    private static List<Intersectable> extractLeafGeometries(Geometries geometries) {
        List<Intersectable> leafGeometries = new ArrayList<>();
        extractLeafGeometriesRecursive(geometries, leafGeometries);
        return leafGeometries;
    }

    /**
     * Recursively traverses a Geometries hierarchy to extract individual leaf geometries.
     *
     * @param intersectable  the current object being processed (may be Geometries or primitive)
     * @param leafGeometries the output list to accumulate individual geometry objects
     */
    private static void extractLeafGeometriesRecursive(Intersectable intersectable, List<Intersectable> leafGeometries) {
        if (intersectable instanceof Geometries geometries) {
            for (Intersectable child : geometries.getGeometries()) {
                extractLeafGeometriesRecursive(child, leafGeometries);
            }
        } else {
            leafGeometries.add(intersectable);
        }
    }

    /**
     * Wrapper class that encapsulates geometry objects with their spatial information.
     */
    private static class ObjectInfo {
        /**
         * The geometry object being wrapped.
         */
        final Intersectable geometry;
        /**
         * The bounding box of the geometry, used for spatial partitioning.
         */
        final BoundingBox boundingBox;
        /**
         * The centroid of the bounding box, used for median split calculations.
         */
        final Point centroid;

        /**
         * Constructs an ObjectInfo wrapper for the given geometry.
         *
         * @param geometry the geometry object to wrap
         */
        ObjectInfo(Intersectable geometry) {
            this.geometry = geometry;
            this.boundingBox = geometry.getBoundingBox();
            this.centroid = calculateCentroid(boundingBox);
        }

        /**
         * Gets the centroid coordinate for the specified axis.
         *
         * @param axis the axis index (0=X, 1=Y, 2=Z)
         * @return the centroid coordinate for the specified axis, or 0 if centroid is null
         */
        double getCentroidCoordinate(int axis) {
            if (centroid == null) return 0;
            return switch (axis) {
                case 0 -> centroid.getX();
                case 1 -> centroid.getY();
                case 2 -> centroid.getZ();
                default -> 0;
            };
        }

        /**
         * Calculates the centroid (center point) of a bounding box.
         *
         * @param box the bounding box to calculate centroid for
         * @return the centroid point, or Point.ZERO if box is null
         */
        private static Point calculateCentroid(BoundingBox box) {
            if (box == null) return Point.ZERO;
            return new Point(
                    (box.getMinX() + box.getMaxX()) * 0.5,
                    (box.getMinY() + box.getMaxY()) * 0.5,
                    (box.getMinZ() + box.getMaxZ()) * 0.5
            );
        }
    }
}