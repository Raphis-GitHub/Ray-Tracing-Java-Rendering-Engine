package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import lighting.PointLight;
import primitives.*;
import scene.Scene;

import java.util.ArrayList;
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
     * Camera reference for accessing blackboard configuration.
     */
    private Camera camera = null;

    /**
     * Constructor returns super of scene
     *
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Sets the camera reference for accessing soft shadow configuration.
     *
     * @param camera the camera instance
     * @return this SimpleRayTracer instance for method chaining
     */
    public SimpleRayTracer setCamera(Camera camera) {
        this.camera = camera;
        return this;
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
     * Calculates the local lighting effects (diffuse + specular) with soft shadow support.
     *
     * @param intersection the intersection to calculate color at
     * @return the resulting color from local effects
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) continue;

            // Calculate transparency (hard or soft shadows)
            Double3 ktr;
            if (camera != null && camera.getBlackboard().useSoftShadows() && lightSource instanceof PointLight pointLight) {
                if (pointLight.getRadius() > 0) {
                    // Use soft shadow transparency calculation with safety fallbacks
                    ktr = calcSoftShadowTransparency(intersection, pointLight);
                } else {
                    // Hard shadows (radius = 0)
                    ktr = transparency(intersection);
                }
            } else {
                // Standard hard shadow calculation
                ktr = transparency(intersection);
            }
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
     * Calculates soft shadow transparency by sampling multiple shadow rays to an area light source.
     * Implementation follows the "Average ktr from all shadow rays" approach from the course material.
     *
     * @param intersection the intersection point where we're calculating shadows
     * @param areaLight    the area light source (PointLight or SpotLight with radius > 0)
     * @return averaged transparency factor representing soft shadow effect
     */
    private Double3 calcSoftShadowTransparency(Intersection intersection, PointLight areaLight) {
        // Step 1: Generate sample points on the area light source
        List<Point> lightSamplePoints = generateAreaLightSamplePoints(areaLight);

        // Step 2: Cast shadow rays to each sample point and average the transparency
        return averageTransparencyFromSampleRays(intersection, lightSamplePoints);
    }

    /**
     * Generates evenly distributed sample points across the area light source surface.
     * Creates a grid pattern scaled by the light's radius.
     *
     * @param areaLight the area light source with radius > 0
     * @return list of sample points representing the light source area
     */
    private List<Point> generateAreaLightSamplePoints(PointLight areaLight) {
        //TODO: check for cod ereuse
        double radius = areaLight.getRadius();
        Point lightCenter = areaLight.getPosition();
        int totalSamples = camera.getBlackboard().getSoftShadowSamples();
        int gridSize = (int) Math.ceil(Math.sqrt(totalSamples));

        List<Point> samplePoints = new ArrayList<>();

        // Generate grid of points from (-radius, -radius) to (+radius, +radius)
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                // Map grid indices to [-1, +1] range
                double u = (gridSize == 1) ? 0 : (2.0 * i / (gridSize - 1) - 1.0);
                double v = (gridSize == 1) ? 0 : (2.0 * j / (gridSize - 1) - 1.0);

                // Scale by radius to get actual offsets
                double dx = u * radius;
                double dy = v * radius;

                // Create sample point (offset in XY plane from light center)
                Point samplePoint = new Point(
                        lightCenter.getX() + dx,
                        lightCenter.getY() + dy,
                        lightCenter.getZ()
                );
                samplePoints.add(samplePoint);

                if (samplePoints.size() >= totalSamples) return samplePoints;
            }
        }
        return samplePoints;
    }

    /**
     * Casts shadow rays to each light sample point and calculates average transparency.
     * Each ray contributes equally to the final soft shadow factor.
     *
     * @param intersection      the surface point being shaded
     * @param lightSamplePoints sample points on the area light source
     * @return averaged transparency (0 = full shadow, 1 = no shadow)
     */
    private Double3 averageTransparencyFromSampleRays(Intersection intersection, List<Point> lightSamplePoints) {
        Double3 totalTransparency = Double3.ZERO;
        int validSamples = 0;

        for (Point lightSamplePoint : lightSamplePoints) {
            Double3 rayTransparency = calculateSingleShadowRayTransparency(intersection, lightSamplePoint);
            totalTransparency = totalTransparency.add(rayTransparency);
            validSamples++;
        }

        // Return average transparency (soft shadow factor)
        return validSamples > 0 ? totalTransparency.scale(1.0 / validSamples) : transparency(intersection);
    }

    /**
     * Calculates transparency for a single shadow ray from intersection to light sample point.
     * Handles under-horizon conditions and blocking geometry.
     *
     * @param intersection     the surface intersection point
     * @param lightSamplePoint a specific point on the area light source
     * @return transparency factor for this individual shadow ray
     */
    private Double3 calculateSingleShadowRayTransparency(Intersection intersection, Point lightSamplePoint) {
        try {
            // Calculate direction from surface to this light sample point
            Vector lightDirection = lightSamplePoint.subtract(intersection.point).normalize();
            double dotProduct = alignZero(intersection.normal.dotProduct(lightDirection));

            // Check under-horizon condition: is light sample behind the surface?
            if (dotProduct <= 0) {
                return Double3.ZERO; // Light sample is behind surface = full shadow
            }

            // Cast shadow ray from surface to light sample point
            Ray shadowRay = new Ray(intersection.point, lightDirection, intersection.normal);
            double distance = lightSamplePoint.distance(intersection.point);
            var blockingObjects = scene.geometries.calculateIntersections(shadowRay, distance);

            // Calculate accumulated transparency through all blocking objects
            Double3 rayTransparency = Double3.ONE;
            if (blockingObjects != null && !blockingObjects.isEmpty()) {
                for (Intersection blockingObject : blockingObjects) {
                    rayTransparency = rayTransparency.product(blockingObject.material.kT);
                    // Early exit if ray is completely blocked
                    if (rayTransparency.lowerThan(MIN_CALC_COLOR_K)) {
                        return Double3.ZERO;
                    }
                }
            }
            return rayTransparency;

        } catch (IllegalArgumentException e) {
            // Handle edge cases (e.g., light sample too close to intersection)
            return Double3.ZERO;
        }
    }

    /**
     * Calculates the transparency factor for partial shadows (standard hard shadows).
     * Returns the accumulated transparency of all objects between the intersection point and light source.
     *
     * @param intersection the intersection point
     * @return accumulated transparency factor (Double3.ONE = no shadow, Double3.ZERO = complete shadow)
     */
    private Double3 transparency(Intersection intersection) {
        // Standard hard shadow calculation (soft shadows handled in calcSoftShadowLighting)
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