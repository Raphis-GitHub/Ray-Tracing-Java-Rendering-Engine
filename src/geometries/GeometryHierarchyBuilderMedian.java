package geometries;

import primitives.BoundingBox;
import primitives.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.isZero;

/**
 * Simplified GeometryHierarchyBuilderMedian using Median Split algorithm.
 * Much simpler than SAH but still provides good performance improvements.
 * <p>
 * Algorithm: For each axis, sort objects by centroid and split at median.
 * Choose axis with best balance (closest to 50-50 split).
 *
 * @author Eytan and Raph
 */
public class GeometryHierarchyBuilderMedian {

    private static final int MAX_OBJECTS_PER_LEAF = 8;
    private static final int MAX_DEPTH = 20;

    /**
     * Builds a hierarchical spatial data structure from a flat collection of geometries
     * using the median split algorithm for Bounding Volume Hierarchy (BVH) construction.
     * <p>
     * This method creates a binary tree where each internal node represents a spatial
     * partition and leaf nodes contain actual geometry objects. The algorithm works by:
     * <ol>
     *   <li>Extracting all leaf geometries from the input collection</li>
     *   <li>Separating finite objects (with bounding boxes) from infinite objects</li>
     *   <li>Recursively partitioning finite objects using median splits</li>
     *   <li>Adding infinite objects at the top level</li>
     * </ol>
     *
     * @param flatGeometries the input collection of geometries to hierarchically organize
     * @return a hierarchically organized Geometries collection for faster intersection queries,
     * or null if input is null
     * @see #buildRecursive(List, int)
     * @see #extractLeafGeometries(Geometries)
     */
    public static Geometries buildHierarchy(Geometries flatGeometries) {
        if (flatGeometries == null) {
            return null;
        }

        List<Intersectable> leafGeometries = extractLeafGeometries(flatGeometries);

        if (leafGeometries.size() <= MAX_OBJECTS_PER_LEAF) {
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

        if (finiteObjects.size() <= MAX_OBJECTS_PER_LEAF) {
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
     * <p>
     * Termination conditions:
     * <ul>
     *   <li>Number of objects ≤ MAX_OBJECTS_PER_LEAF</li>
     *   <li>Maximum depth reached (MAX_DEPTH)</li>
     *   <li>No suitable split axis found (all objects have same centroid)</li>
     * </ul>
     *
     * @param objects list of ObjectInfo containing geometries and their spatial information
     * @param depth   current recursion depth (used to prevent infinite recursion)
     * @return a Geometries node representing either an internal node with children
     * or a leaf node containing the actual geometry objects
     * @see #findBestSplitAxis(List)
     * @see #createLeafNode(List)
     */
    private static Geometries buildRecursive(List<ObjectInfo> objects, int depth) {
        if (objects.size() <= MAX_OBJECTS_PER_LEAF || depth >= MAX_DEPTH) {
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
     * <p>
     * The algorithm evaluates each axis by calculating the coordinate range
     * (max - min) of object centroids along that axis. The axis with the largest
     * range is selected as it provides the best spatial separation.
     * <p>
     * This is simpler than Surface Area Heuristic (SAH) but still effective
     * for most cases and much faster to compute.
     *
     * @param objects list of ObjectInfo to analyze for splitting
     * @return the best axis index (0=X, 1=Y, 2=Z) for splitting,
     * or -1 if no suitable axis found (all objects at same position)
     * @see ObjectInfo#getCentroidCoordinate(int)
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
     * <p>
     * Leaf nodes are the terminal nodes of the BVH tree that contain the
     * actual renderable geometry objects. This method extracts the geometry
     * from each ObjectInfo wrapper and adds them to a new Geometries collection.
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
     * <p>
     * This method flattens any existing hierarchy to get individual geometry objects.
     * It's necessary because the input might already contain nested Geometries collections,
     * and we need to work with the actual primitive geometries for BVH construction.
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
     * <p>
     * This helper method performs a depth-first traversal of nested Geometries collections,
     * adding only the actual primitive geometry objects to the output list.
     * If the current object is a Geometries collection, it recursively processes its children.
     * Otherwise, it adds the object as a leaf geometry.
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
     * <p>
     * This helper class pre-computes and caches spatial data (bounding box and centroid)
     * for each geometry object to avoid repeated calculations during BVH construction.
     * The centroid is used for sorting and splitting during the median split algorithm.
     */
    private static class ObjectInfo {
        final Intersectable geometry;
        final BoundingBox boundingBox;
        final Point centroid;

        /**
         * Constructs an ObjectInfo wrapper for the given geometry.
         * <p>
         * Pre-computes and caches the bounding box and centroid for efficient
         * access during BVH construction. The centroid is calculated as the
         * center point of the bounding box.
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
         * <p>
         * This method provides efficient access to centroid coordinates
         * used for sorting during the median split algorithm.
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
         * <p>
         * The centroid is computed as the average of the minimum and maximum
         * coordinates for each axis. This point represents the geometric center
         * of the bounding box and is used for spatial partitioning.
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