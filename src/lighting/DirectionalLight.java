package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a directional light source in the lighting system.
 */
public class DirectionalLight extends Light implements LightSource {
    /**
     * Represents the direction of the light source.
     */
    private final Vector direction;

    /**
     * Constructs a DirectionalLight with the specified direction and intensity.
     *
     * @param direction the direction vector of the light
     * @param intensity the intensity of the light
     */
    public DirectionalLight(Vector direction, Color intensity) {
        super(intensity);
        this.direction = direction.normalize();
    }

    /**
     * Gets the intensity of the light at a given point.
     *
     * @param p the point to calculate the intensity at
     * @return the intensity of the light
     */
    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    /**
     * Gets the direction of the light at a given point.
     *
     * @param p the point to calculate the direction at
     * @return the normalized direction vector of the light
     */
    @Override
    public Vector getL(Point p) {
        return direction;
    }

    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }

}
