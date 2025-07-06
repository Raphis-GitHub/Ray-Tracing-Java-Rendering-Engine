package renderer;

import geometries.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * Simple ray tracer that traces rays through a scene
 */
public class SimpleRayTracer extends RayTracerBase {
    /**
     * constructor returns super of scene
     *
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        return intersections == null
                ? scene.background
                : calcColor(ray.findClosestIntersection(intersections), ray);
    }

    /**
     * calculates color intensity at an intersection
     *
     * @param intersection the intersection to calculate color at
     * @param ray          the ray that caused the intersection (optional, for future use)
     * @return the color intensity
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (intersection == null) {
            return scene.background;
        }
        // Get the emission color of the intersected geometry
        Color objectColor = intersection.geometry.getEmission();
        // Multiply ambient light intensity by the material's kA (attenuation factor)
        Color ambient = scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA);
        // Add the emission color to the ambient component
        return ambient.add(objectColor);
    }
}
