package lighting;

import primitives.*;

/**
 * SpotLight class represents a spotlight in a scene.
 * It extends PointLight and adds a direction vector to define the spotlight's direction.
 */
public class SpotLight extends PointLight {

    /**
     * The direction of the spotlight.
     */
    private final Vector direction;

    /**
     * Constructs a SpotLight with the specified intensity, position, and direction.
     *
     * @param intensity the intensity of the light
     * @param position  the position of the light source
     * @param direction the direction of the light beam
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the constant attenuation factor for the spotlight.
     *
     * @param kC the constant attenuation factor
     * @return the current SpotLight instance for method chaining
     */
    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * Sets the linear attenuation factor for the spotlight.
     *
     * @param kL the linear attenuation factor
     * @return the current SpotLight instance for method chaining
     */
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation factor for the spotlight.
     *
     * @param kQ the quadratic attenuation factor
     * @return the current SpotLight instance for method chaining
     */
    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    /**
     * Calculates the vector from the spotlight to a given point.
     *
     * @param p the point at which the intensity is calculated
     * @return the vector from the spotlight to the given point
     */
    @Override
    public Color getIntensity(Point p) {
        Color pointIntensity = super.getIntensity(p);
        Vector l = getL(p);
        double factor = Math.max(0, direction.normalize().dotProduct(l));
        return pointIntensity.scale(factor);
    }

}
