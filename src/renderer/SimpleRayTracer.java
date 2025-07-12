package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static java.lang.Math.abs;
import static primitives.Util.alignZero;

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

    /**
     * Small offset to prevent self-shadowing when casting shadow rays.
     */
    private static final double DELTA = 0.1;

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
     * @param ray          the ray that caused the intersection
     * @return the color intensity
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return preprocessIntersection(intersection, ray.direction())
                ? scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(calcColorLocalEffects(intersection))
                : Color.BLACK;
    }

    /**
     * Calculates the local lighting effects (diffuse + specular) at the intersection point.
     *
     * @param intersection the intersection to calculate color at
     * @return the resulting color from local effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        // Start with emission color
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if (!unshaded(intersection, lightSource)) continue; // Skip this light if the point is in shadow
            if (!setLightSource(intersection, lightSource)) continue;

            Color lightIntensity = lightSource.getIntensity(intersection.point);
            Double3 diffusive = calcDiffusive(intersection);
            Double3 specular = calcSpecular(intersection);

            color = color.add(lightIntensity.scale(diffusive.add(specular)));
        }

        return color;
    }

    /**
     * Calculates the specular component at the intersection point using the Phong model.
     *
     * @param intersection the intersection to calculate specular at
     * @return the specular component as Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        // reflection direction
        Vector r = intersection.lightToPoint.subtract(intersection.normal.scale(2 * intersection.lightDotProduct));

        // Now we can use v.dotProduct(r) directly since v is the correct view direction
        double vr = intersection.direction.dotProduct(r);
        if (alignZero(vr) >= 0)
            return Double3.ZERO; // No specular reflection if vr is zero or negative

        return intersection.material.kS.scale(Math.pow(-vr, intersection.material.nSh));
    }

    /**
     * Calculates the diffusive component at the intersection point using Lambert's law.
     *
     * @param intersection the intersection to calculate diffusive at
     * @return the diffusive component as Double3
     */
    private Double3 calcDiffusive(Intersection intersection) {
        return intersection.material.kD.scale(abs(intersection.lightDotProduct));
    }

    /**
     * Preprocesses an intersection by initializing direction, normal, and dot product fields.
     *
     * @param intersection the intersection object to preprocess
     * @param rayDirection the direction vector of the intersecting ray
     * @return false if the dot product is 0, true otherwise
     */
    public static boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        intersection.direction = rayDirection;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.dotProduct = alignZero(intersection.normal.dotProduct(rayDirection));
        return intersection.dotProduct != 0;
    }

    /**
     * Sets the light source and related fields in the intersection object.
     *
     * @param intersection the intersection object to update
     * @param lightSource  the light source
     * @return false if both dot products (intersection.dotProduct and lightDotProduct) are zero, true otherwise
     */
    public static boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.lightToPoint = lightSource.getL(intersection.point);
        intersection.lightDotProduct = alignZero(intersection.normal.dotProduct(intersection.lightToPoint));
        intersection.lightDirection = intersection.lightToPoint.scale(-1);
        return intersection.lightDotProduct * intersection.dotProduct > 0;
    }

    /**
     * Checks if a given intersection point is not shadowed (i.e., has a direct line of sight to the light source).
     * Uses the bonus solution with max distance parameter for better performance.
     *
     * @param intersection the intersection point on the geometry
     * @param light        the light source
     * @return true if the point is not shadowed, false otherwise
     */
    private boolean unshaded(Intersection intersection, LightSource light) {
        Vector lightToPoint = light.getL(intersection.point);
        Vector pointToLight = lightToPoint.scale(-1); // from point to light source

        // Calculate the dot product to determine the direction of the delta offset
        double lightDotProduct = intersection.normal.dotProduct(lightToPoint);
        Vector delta = intersection.normal.scale(lightDotProduct < 0 ? DELTA : -DELTA);
        Point rayOrigin = intersection.point.add(delta);

        // Create shadow ray from the intersection point towards the light
        Ray shadowRay = new Ray(pointToLight, rayOrigin);

        // Get distance to light source
        double lightDistance = light.getDistance(intersection.point);

        // Use the bonus solution: only get intersections up to the light distance
        List<Intersection> intersections = scene.geometries.calculateIntersections(shadowRay, lightDistance);

        // If no intersections found within light distance, point is unshaded
        return intersections == null;
    }

}
