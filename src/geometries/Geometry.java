package geometries;

import primitives.*;

/**
 * The Geometry interface represents geometric shapes.
 * It defines a method to get the normal vector at a given point on the surface of the shape.
 *
 * @author Raphael
 */
public abstract class Geometry extends Intersectable {
    /**
     * Represents the material of the geometry.
     */
    private Material material = new Material();

    /**
     * Represents the emission color of the geometry.
     */
    protected Color emission = Color.BLACK;

    /**
     * Gets the emission color of the geometry.
     *
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission the emission color to set
     * @return the geometry instance
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Returns the material of the geometry.
     *
     * @return the material of the geometry
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material of the geometry.
     *
     * @param material the material to set
     * @return the geometry instance
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Returns the normal vector to the geometry at a given point on the geometry's surface
     *
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);

}