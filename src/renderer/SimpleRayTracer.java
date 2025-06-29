package renderer;

import primitives.*;
import scene.Scene;

public class SimpleRayTracer extends RayTracerBase {
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Find intersections between the ray and the scene's geometries
        var intersections = scene.geometries.findIntersections(ray);
        // If no intersections, return the background color
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        }
        // Find the closest intersection point to the ray's origin
        Point closestPoint = ray.findClosestPoint(intersections);
        // Return the color at this point using calcColor
        return calcColor(closestPoint);
    }

    private Color calcColor(Point p) {
        return scene.ambientLight.getIntensity();
    }
}
