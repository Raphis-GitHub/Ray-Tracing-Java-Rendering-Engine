package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracers.
 */
public abstract class RayTracerBase {
    /**
     * The scene in which rays will be traced.
     */
    protected final Scene scene;

    /**
     * Constructor for RayTracerBase that initializes the scene.
     *
     * @param scene the scene to trace rays in
     */
    protected RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a ray through the scene and returns the color at the intersection point.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point, or the background color if no intersection occurs
     */
    public abstract Color traceRay(Ray ray);
}
