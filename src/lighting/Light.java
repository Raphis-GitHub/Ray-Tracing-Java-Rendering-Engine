package lighting;

import primitives.Color;

/**
 * Abstract base class for all types of lights in a scene.
 */
public abstract class Light {
    /**
     * The color intensity of the light.
     */
    protected Color intensity;

    /**
     * Constructor for Light that initializes the intensity of the light.
     *
     * @param intensity the color intensity of the light
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Gets the intensity of the light.
     *
     * @return the color intensity of the light
     */
    public Color getIntensity() {
        return intensity;
    }
}


