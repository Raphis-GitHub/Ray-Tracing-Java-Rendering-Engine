package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.Comparator;
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
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Finds the closest intersection point to the ray's head.
     *
     * @param ray the ray to check intersections for
     * @return the closest intersection or null if none
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        return intersections == null ? null :
                intersections.stream()
                        .min(Comparator.comparingDouble(intersection ->
                                ray.origin().distanceSquared(intersection.point)))
                        .orElse(null);
    }

    @Override
    public Color traceRay(Ray ray) {
        Intersection closest = findClosestIntersection(ray);
        return closest == null ? scene.background : calcColor(closest, ray);
    }

    /**
     * calculates color intensity at an intersection
     *
     * @param intersection the intersection to calculate color at
     * @param ray          the ray that caused the intersection
     * @return the color intensity
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    private Color calcColor(Intersection intersection, int level, Double3 k) {
        if (intersection == null)
            return scene.background;
        Color color = scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(calcColorLocalEffects(intersection));
        if (level == 1)
            return color;
        Color globalEffects = calcGlobalEffects(intersection, level, k);
        return color.add(globalEffects);
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
        double lightDotProduct = intersection.normal.dotProduct(lightToPoint);
        Vector delta = intersection.normal.scale(lightDotProduct < 0 ? DELTA : -DELTA);
        Point rayOrigin = intersection.point.add(delta);
        Ray shadowRay = new Ray(pointToLight, rayOrigin);
        double lightDistance = light.getDistance(intersection.point);
        List<Intersection> intersections = scene.geometries.calculateIntersections(shadowRay, lightDistance);
        if (intersections == null) return true;
        for (Intersection i : intersections) {
            if (!i.material.kT.lowerThan(MIN_CALC_COLOR_K)) continue; // transparent, does not block
            return false; // blocked by non-transparent
        }
        return true;
    }

    /**
     * Construct a reflected ray from the intersection point
     */
    private Ray constructReflectedRay(Intersection intersection) {
        Vector v = intersection.direction;
        Vector n = intersection.normal;
        double nv = n.dotProduct(v);
        if (alignZero(nv) == 0) return null;
        Vector r = v.subtract(n.scale(2 * nv));
        Vector delta = n.scale(nv > 0 ? DELTA : -DELTA);
        Point newOrigin = intersection.point.add(delta);
        return new Ray(r, newOrigin);
    }

    /**
     * Construct a transparency (refracted) ray from the intersection point
     */
    private Ray constructRefractedRay(Intersection intersection) {
        Vector v = intersection.direction;
        Vector n = intersection.normal;
        double nv = n.dotProduct(v);
        Vector delta = n.scale(nv > 0 ? DELTA : -DELTA);
        Point newOrigin = intersection.point.add(delta);
        return new Ray(v, newOrigin);
    }

    /**
     * Calculate the global effect (reflection or transparency) recursively
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        if (level <= 1 || k.product(kx).lowerThan(MIN_CALC_COLOR_K))
            return Color.BLACK;
        Intersection closest = findClosestIntersection(ray);
        return closest == null ? scene.background : calcColor(closest, level - 1, k.product(kx)).scale(kx);
    }

    /**
     * Calculate the sum of global effects (reflection and transparency)
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = Color.BLACK;
        // Reflection
        Ray reflectedRay = constructReflectedRay(intersection);
        if (reflectedRay != null && !intersection.material.kR.lowerThan(MIN_CALC_COLOR_K)) {
            color = color.add(calcGlobalEffect(reflectedRay, level, k, intersection.material.kR));
        }
        // Transparency
        Ray refractedRay = constructRefractedRay(intersection);
        if (!intersection.material.kT.lowerThan(MIN_CALC_COLOR_K)) {
            color = color.add(calcGlobalEffect(refractedRay, level, k, intersection.material.kT));
        }
        return color;
    }

}
