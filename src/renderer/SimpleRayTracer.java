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
     * Maximum recursion level for color calculations (reflections/refractions).
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * Minimum color calculation coefficient threshold.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * Initial color calculation coefficient.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructor returns super of scene
     *
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene and calculates the color at the intersection point.
     * If no intersection is found, returns the scene's background color.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point or background color
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection closest = findClosestIntersection(ray);
        return closest == null ? scene.background : calcColor(closest, ray);
    }

    /**
     * Finds the closest intersection point to the ray's head.
     *
     * @param ray the ray to check intersections for
     * @return the closest intersection or null if none
     */
    private Intersection findClosestIntersection(Ray ray) {
        return ray.findClosestIntersection(scene.geometries.calculateIntersections(ray));
    }

    /**
     * Calculates color intensity at an intersection (entry point)
     *
     * @param intersection the intersection to calculate color at
     * @param ray          the ray that caused the intersection
     * @return the color intensity
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.direction()))
            return Color.BLACK;
        return scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Recursive color calculation method
     *
     * @param intersection the intersection point
     * @param level        current recursion level
     * @param k            accumulated attenuation coefficient
     * @return calculated color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcColorLocalEffects(intersection);
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates the local lighting effects (diffuse + specular) with transparency support.
     *
     * @param intersection the intersection to calculate color at
     * @return the resulting color from local effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) continue;
            Double3 ktr = transparency(intersection);
            if (!ktr.lowerThan(MIN_CALC_COLOR_K)) {
                Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);
                Double3 diffusive = calcDiffusive(intersection);
                Double3 specular = calcSpecular(intersection);
                color = color.add(lightIntensity.scale(diffusive.add(specular)));
            }
        }
        return color;
    }

    /**
     * Calculates the transparency factor for partial shadows.
     * Returns the accumulated transparency of all objects between the intersection point and light source.
     *
     * @param intersection the intersection point
     * @return accumulated transparency factor (Double3.ONE = no shadow, Double3.ZERO = complete shadow)
     */
    private Double3 transparency(Intersection intersection) {
        var intersections = getShadowRayIntersections(intersection);
        if (intersections == null) return Double3.ONE; // no blocking objects

        Double3 ktr = Double3.ONE; // accumulated transparency
        for (Intersection i : intersections) {
            ktr = ktr.product(i.material.kT);
            // Performance optimization: if transparency becomes negligible, return zero
            if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
        }
        return ktr;
    }

    /**
     * Checks if a given intersection point is not shadowed (for binary shadow - keep as required by exercise)
     *
     * @param intersection the intersection point on the geometry
     * @return true if the point is not shadowed, false otherwise
     */
    @SuppressWarnings("unused")
    private boolean unshaded(Intersection intersection) {
        var intersections = getShadowRayIntersections(intersection);
        if (intersections == null) return true;

        for (Intersection shadowIntersection : intersections)
            if (shadowIntersection.material.kT.lowerThan(MIN_CALC_COLOR_K)) return false; // blocked by non-transparent
        return true;
    }

    /**
     * Gets the intersections of the shadow ray with the scene geometries.
     * The shadow ray is cast from the intersection point towards the light source.
     *
     * @param intersection the intersection point
     * @return a list of intersections with the shadow ray, or null if no intersections found
     */
    private List<Intersection> getShadowRayIntersections(Intersection intersection) {
        Ray shadowRay = new Ray(intersection.point, intersection.pointToLight, intersection.normal);
        return scene.geometries.calculateIntersections(shadowRay, intersection.lightSource.getDistance(intersection.point));
    }

    /**
     * Calculates the diffusive component using Lambert's law.
     *
     * @param intersection the intersection point
     * @return the diffusive component as Double3
     */
    private Double3 calcDiffusive(Intersection intersection) {
        return intersection.material.kD.scale(abs(intersection.lightDotProduct));
    }

    /**
     * Calculates the specular component using the Phong model.
     *
     * @param intersection the intersection point
     * @return the specular component as Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.lightToPoint.subtract(intersection.normal.scale(2 * intersection.lightDotProduct));
        double vr = intersection.direction.dotProduct(r);
        if (alignZero(vr) >= 0) return Double3.ZERO;
        return intersection.material.kS.scale(Math.pow(-vr, intersection.material.nSh));
    }

    /**
     * Calculate the sum of global effects (reflection and transparency)
     *
     * @param intersection the intersection point
     * @param level        current recursion level
     * @param k            accumulated attenuation coefficient
     * @return combined global effects color
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        // Reflection
        return calcGlobalEffect(constructReflectedRay(intersection), level, k, intersection.material.kR)
                // Transparency
                .add(calcGlobalEffect(constructRefractedRay(intersection), level, k, intersection.material.kT));
    }

    /**
     * Calculate the global effect (reflection or transparency) recursively
     *
     * @param ray   the secondary ray
     * @param level current recursion level
     * @param k     accumulated attenuation coefficient
     * @param kx    material attenuation coefficient (kR or kT)
     * @return the color contribution from this global effect
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) return scene.background.scale(kx);

        return preprocessIntersection(intersection, ray.direction())
                ? calcColor(intersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Construct a reflected ray from the intersection point
     *
     * @param intersection the intersection point
     * @return the reflected ray
     */
    private Ray constructReflectedRay(Intersection intersection) {
        Vector r = intersection.direction.subtract(intersection.normal.scale(2 * intersection.dotProduct));
        return new Ray(intersection.point, r, intersection.normal);
    }

    /**
     * Construct a transparency (refracted) ray from the intersection point
     *
     * @param intersection the intersection point
     * @return the refracted ray
     */
    private Ray constructRefractedRay(Intersection intersection) {
        return new Ray(intersection.point, intersection.direction, intersection.normal);
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
     * @return false if both dot products are zero, true otherwise
     */
    public static boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.lightSource = lightSource;
        intersection.lightToPoint = lightSource.getL(intersection.point);
        intersection.lightDotProduct = alignZero(intersection.normal.dotProduct(intersection.lightToPoint));
        intersection.pointToLight = intersection.lightToPoint.scale(-1);
        return intersection.lightDotProduct * intersection.dotProduct > 0;
    }
}