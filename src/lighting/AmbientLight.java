package lighting;

import primitives.Color;

/**
 * AmbientLight class represents the ambient light in a scene.
 */
public class AmbientLight {
    /**
     * the color itensity of the ambient light
     */
    private final Color intensity;
    /**
     * A constant representing no ambient light, initialized to black.
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an AmbientLight with the specified intensity.
     *
     * @param I_A the intensity of the ambient light
     */
    public AmbientLight(Color I_A) {
        this.intensity = I_A;
    }

    /**
     * Returns the intensity of the ambient light.
     *
     * @return the intensity of the ambient light
     */
    public Color getIntensity() {
        return intensity;
    }
}
