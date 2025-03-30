package geometries;

/**
 * Abstract class representing radial geometric shapes.
 * Implements the Geometry interface.
 *
 * @author Raphael
 */
public abstract class RadialGeometry implements Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double radius;

    /**
     * The square of the radius of the radial geometry.
     * This is precomputed for efficiency in calculations.
     */
    protected final double radiusSquared;

    /**
     * Constructs a RadialGeometry with the given radius.
     *
     * @param radius the radius of the shape
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }
}