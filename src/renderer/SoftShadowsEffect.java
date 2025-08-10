package renderer;

import primitives.Ray;

import java.util.List;

/**
 * Soft shadows render effect that generates multiple shadow rays
 * from area light sources to reduce hard shadow edges through supersampling.
 * Only affects PointLight and SpotLight sources with radius > 0.
 */
public class SoftShadowsEffect implements RenderEffect {

    @Override
    public List<Ray> applyEffect(List<Ray> rays, Ray primaryRay, Camera camera) {
        // Soft shadows are handled during shadow ray generation in SimpleRayTracer
        // This effect is more of a configuration enabler for the ray tracer
        // The actual soft shadow ray generation happens in the lighting calculations
        return rays;
    }

    @Override
    public boolean isEnabled(Camera camera) {
        return camera.getBlackboard().useSoftShadows();
    }
}