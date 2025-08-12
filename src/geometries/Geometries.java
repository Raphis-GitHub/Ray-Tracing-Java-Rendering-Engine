package geometries;

import primitives.BoundingBox;
import primitives.Ray;

import java.util.*;

/**
 * Geometries is a composite class that groups multiple intersectable geometries.
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Default constructor for Geometries.
     */
    public Geometries() {
    }

    /**
     * Constructor that accepts an array of intersectable geometries.
     *
     * @param geometries the intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more intersectable geometries to the collection.
     *
     * @param geometries the intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Gets the list of geometries in this collection.
     * Used by GeometryHierarchyBuilderMedian for automatic hierarchy construction.
     *
     * @return an unmodifiable view of the geometries list
     */
    public List<Intersectable> getGeometries() {
        return geometries;
    }

    /**
     * Calculates intersections between a ray and all geometries in the collection.
     * Iterates through each geometry in the collection and aggregates all intersection points.
     *
     * @param ray         the ray to test for intersections
     * @param maxDistance the maximum distance to consider for intersections
     * @return a list of all intersection points from all geometries, or null if no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;
        for (Intersectable geo : geometries) {
            List<Intersection> temp = geo.calculateIntersections(ray, maxDistance);
            if (temp != null) {
                if (result == null)
                    result = new LinkedList<>(temp);
                else
                    result.addAll(temp);
            }
        }
        return result;
    }

    /**
     * Calculates the bounding box for this collection of geometries.
     * The bounding box is the union of all non-null bounding boxes of the contained geometries.
     * If any geometry has a null bounding box (infinite geometry), the entire collection
     * is considered unbounded and returns null.
     *
     * @return the union bounding box of all geometries, or null if any geometry is infinite
     */
    @Override
    protected BoundingBox calculateBoundingBox() {
        if (geometries.isEmpty()) {
            return null;
        }

        // Collect all bounding boxes
        BoundingBox[] boxes = new BoundingBox[geometries.size()];
        for (int i = 0; i < geometries.size(); i++) {
            boxes[i] = geometries.get(i).getBoundingBox();
            // If any geometry is infinite (null bounding box), entire collection is unbounded
            if (boxes[i] == null) {
                return null;
            }
        }

        // Return union of all bounding boxes
        return BoundingBox.union(boxes);
    }
}
