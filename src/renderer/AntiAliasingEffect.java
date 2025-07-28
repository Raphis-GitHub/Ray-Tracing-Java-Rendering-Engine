package renderer;

import primitives.Ray;

import java.util.List;

/**
 * Anti-aliasing render effect that generates multiple rays per pixel
 * to reduce aliasing artifacts through supersampling.
 */
public class AntiAliasingEffect implements RenderEffect {

    @Override
    public List<Ray> applyEffect(List<Ray> rays, Ray primaryRay, Camera camera) {
        if (!isEnabled(camera) || rays.size() != 1) {
            return rays; // Only apply AA to single primary ray
        }
        
        double pixelSize = Math.max(camera.getWidth() / camera.getNX(), camera.getHeight() / camera.getNY());

        // Use Blackboard's cached vector computation instead of PointGenerator
        return camera.getBlackboard().constructRays(primaryRay, camera.getDistance(), pixelSize);
    }

    @Override
    public boolean isEnabled(Camera camera) {
        return camera.getBlackboard().useAntiAliasing();
    }
}