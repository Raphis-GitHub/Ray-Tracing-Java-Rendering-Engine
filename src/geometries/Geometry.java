package geometries;

import primitives.*;

/**
 * The Geometry interface represents geometric shapes.
 * It defines a method to get the normal vector at a given point on the surface of the shape.
 *
 * @author Raphael
 */
public abstract class Geometry extends Intersectable {
    public Material getMaterial() {
        return material;
    }

    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    private Material material = new Material();

    public Color getEmission() {
        return emission;
    }

    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    protected Color emission = Color.BLACK;

    /**
     * Returns the normal vector to the geometry at a given point on the geometry's surface
     *
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);

}