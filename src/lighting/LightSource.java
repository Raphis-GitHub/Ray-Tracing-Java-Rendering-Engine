package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

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

    /**
     * Returns the distance from a point to the light source.
     * For directional lights, returns positive infinity.
     *
     * @param point the point to compute distance from
     * @return the distance to the light source
     */
    double getDistance(Point point);


}
