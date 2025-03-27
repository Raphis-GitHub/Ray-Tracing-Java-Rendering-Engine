package geometries;

/**
 * Abstract class representing radial geometric shapes.
 * Implements the Geometry interface.
 *
 * @author Raphael and Orel
 */
public abstract class RadialGeometry implements Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double radius;

    /**
     * Constructs a RadialGeometry with the given radius.
     *
     * @param radius the radius of the shape
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }
}