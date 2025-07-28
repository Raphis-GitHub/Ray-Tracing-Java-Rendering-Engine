package renderer;

import primitives.Ray;

import java.util.List;

/**
 * Interface for rendering effects that can be applied to rays.
 * Effects like anti-aliasing, depth of field, soft shadows, etc.
 */
public interface RenderEffect {

    /**
     * Applies the rendering effect to a list of rays.
     *
     * @param rays       the current list of rays
     * @param primaryRay the primary ray through the pixel center
     * @param camera     the camera instance for accessing camera parameters
     * @return the processed list of rays with the effect applied
     */
    List<Ray> applyEffect(List<Ray> rays, Ray primaryRay, Camera camera);

    /**
     * Checks if this effect is enabled and should be applied.
     *
     * @param camera the camera instance to check configuration
     * @return true if the effect should be applied, false otherwise
     */
    boolean isEnabled(Camera camera);
}