package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.*;

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
     * @param ray          the ray that caused the intersection
     * @return the color intensity
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (intersection == null || !preprocessIntersection(intersection, ray.direction())) {
            return Color.BLACK;
        }
        return calcColorLocalEffects(intersection);
    }

    /**
     * Calculates the local lighting effects (diffuse + specular) at the intersection point.
     *
     * @param intersection the intersection to calculate color at
     * @return the resulting color from local effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        if (intersection == null || intersection.geometry == null || intersection.point == null) {
            return Color.BLACK;
        }

        // Start with emission color
        Color color = scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(intersection.geometry.getEmission());

        for (LightSource lightSource : scene.lights) {
            if (!SimpleRayTracer.setLightSource(intersection, lightSource)) {
                continue;
            }

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
    /**
     * Calculates the specular component at the intersection point using the Phong model.
     *
     * @param intersection the intersection to calculate specular at
     * @return the specular component as Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        if (intersection == null || intersection.geometry == null || intersection.point == null || intersection.lightSource == null) {
            return Double3.ZERO;
        }
        Material material = intersection.geometry.getMaterial();

        // FIXED: View direction should point FROM intersection TO camera (opposite of ray direction)
        Vector v = intersection.direction.scale(-1).normalize(); // view direction (from point to camera)
        Vector n = intersection.normal.normalize();
        Vector l = intersection.lightDirection.normalize();
        Vector r = n.scale(2 * l.dotProduct(n)).subtract(l).normalize(); // reflection direction

        // Now we can use v.dotProduct(r) directly since v is the correct view direction
        double vr = Math.max(0, v.dotProduct(r));
        if (Util.isZero(vr)) return Double3.ZERO;
        return material.kS.scale(Math.pow(vr, material.nSh));
    }

    /**
     * Calculates the diffusive component at the intersection point using Lambert's law.
     *
     * @param intersection the intersection to calculate diffusive at
     * @return the diffusive component as Double3
     */
    private Double3 calcDiffusive(Intersection intersection) {
        if (intersection == null || intersection.geometry == null || intersection.point == null || intersection.lightSource == null) {
            return Double3.ZERO;
        }
        Material material = intersection.geometry.getMaterial();
        Vector n = intersection.normal.normalize();
        Vector l = intersection.lightDirection.normalize();
        double nl = Math.max(0, n.dotProduct(l));
        return material.kD.scale(nl);
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
        intersection.dotProduct = intersection.normal.dotProduct(rayDirection);
        return !isZero(intersection.dotProduct);
    }

    /**
     * Sets the light source and related fields in the intersection object.
     *
     * @param intersection the intersection object to update
     * @param lightSource  the light source
     * @return false if both dot products (intersection.dotProduct and lightDotProduct) are zero, true otherwise
     */

    public static boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.lightSource = lightSource;
        if (intersection.point == null || intersection.normal == null) {
            return false;
        }
        intersection.lightDirection = lightSource.getL(intersection.point);
        intersection.lightDotProduct = alignZero(intersection.normal.dotProduct(intersection.lightDirection));

        // Only check if surface faces the light - im not sure how to fix this.....
        return alignZero(intersection.lightDotProduct) > 0;
    }
}
