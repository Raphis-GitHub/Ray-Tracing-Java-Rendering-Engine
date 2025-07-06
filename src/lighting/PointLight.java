package lighting;

import primitives.*;

import static primitives.Util.isZero;

/**
 * PointLight class represents a point light source in a scene.
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the point light in 3D space.
     */
    protected final Point position;
    /**
     * The constant, linear, and quadratic attenuation factors for the light.
     */
    private double kC = 1, kL = 0, kQ = 0;

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
        double d = position.distance(p);
        double attenuationFactor = kC + kL * d + kQ * d * d;
        return isZero(attenuationFactor)
                ? intensity.scale(Double.POSITIVE_INFINITY)
                : intensity.scale(1d / attenuationFactor);
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
        //maybe add try catch to handle zero vector
    }

}
