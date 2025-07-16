package lighting;

import primitives.*;

import static primitives.Util.alignZero;

/**
 * PointLight class represents a point light source in a scene.
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the point light in 3D space.
     */
    protected final Point position;
    /**
     * The constant attenuation factors for the light.
     */
    private double kC = 1;
    /**
     * The linear attenuation factors for the light.
     */
    private double kL = 0;
    /**
     * The quadratic attenuation factors for the light.
     */
    private double kQ = 0;

    /**
     * Constructs a PointLight with the specified intensity and position.
     *
     * @param intensity the intensity of the light
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation factor for the point light.
     *
     * @param kC the constant attenuation factor
     * @return the current PointLight instance for method chaining
     */
    public PointLight setKc(double kC) {
        if (alignZero(kC) <= 0)
            throw new IllegalArgumentException("kC must be positive");
        this.kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor for the point light.
     *
     * @param kL the linear attenuation factor
     * @return the current PointLight instance for method chaining
     */
    public PointLight setKl(double kL) {
        if (kL < 0)
            throw new IllegalArgumentException("kL must be non-negative");
        this.kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor for the point light.
     *
     * @param kQ the quadratic attenuation factor
     * @return the current PointLight instance for method chaining
     */
    public PointLight setKq(double kQ) {
        if (kQ < 0)
            throw new IllegalArgumentException("kQ must be non-negative");
        this.kQ = kQ;
        return this;
    }

    /**
     * calculates the intensity of the light at a given point.
     *
     * @param p the point at which the intensity is calculated
     * @return the intensity of the light at the given point
     */
    @Override
    public Color getIntensity(Point p) {
        double dSquared = position.distanceSquared(p);
        double d = Math.sqrt(dSquared);
        double attenuationFactor = kC + kL * d + kQ * dSquared;
        return intensity.scale(1d / attenuationFactor);
    }

    /**
     * calculates the vector from the light source to a given point.
     *
     * @param point the point at which the vector is calculated
     * @return the vector from the light source to the given point
     */
    @Override
    public Vector getL(Point point) {
        return point.subtract(position).normalize();

    }

    /**
     * Calculates the distance from the point light to a given point.
     *
     * @param point the point to calculate distance to
     * @return the distance from the light source to the given point
     */
    @Override
    public double getDistance(Point point) {
        return point.distance(position);
    }

}
