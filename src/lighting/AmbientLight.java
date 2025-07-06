package lighting;

import primitives.Color;

/**
 * AmbientLight class represents the ambient light in a scene.
 */
public class AmbientLight extends Light {

    /**
     * A constant representing no ambient light, initialized to black.
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an AmbientLight with the specified intensity.
     *
     * @param intensity the intensity of the ambient light
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }
}
