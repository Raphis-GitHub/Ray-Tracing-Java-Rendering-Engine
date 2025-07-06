package lighting;

import primitives.*;

/**
 * Interface for light sources in a scene.
 * Provides methods to get the intensity of the light at a point and the direction of the light.
 */
public interface LightSource {
    /**
     * Gets the intensity of the light at a specific point in space.
     *
     * @param p the point in space where the intensity is calculated
     * @return the color intensity of the light at that point
     */
    Color getIntensity(Point p);

    /**
     * Gets the direction of the light at a specific point in space.
     *
     * @param p the point in space where the direction is calculated
     * @return the vector representing the direction of the light at that point
     */
    Vector getL(Point p);

}
