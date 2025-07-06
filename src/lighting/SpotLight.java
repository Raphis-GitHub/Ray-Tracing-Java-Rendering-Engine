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
     * Narrow beam effect factor. A value greater than 1 makes the beam narrower.
     * A value of 1 means no narrowing effect.
     */
    private double narrowBeam = 1;

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
     * Gets the direction of the spotlight.
     *
     * @return the normalized direction vector of the spotlight
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        if (narrowBeam < 1) {
            throw new IllegalArgumentException("Narrow beam factor must be >= 1");
        }
        this.narrowBeam = narrowBeam;
        return this;
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
        // Get base intensity from PointLight (includes distance attenuation)
        Color pointIntensity = super.getIntensity(p);

        // Get the direction from light to point
        Vector l = getL(p);

        // Calculate the cosine of the angle between light direction and vector to point
        double cosAngle = direction.normalize().dotProduct(l);

        // Apply directional factor (0 if pointing away, positive if within cone)
        double directionalFactor = Math.max(0, cosAngle);

        // Apply narrow beam effect by raising to a power
        if (narrowBeam > 1) {
            directionalFactor = Math.pow(directionalFactor, narrowBeam);
        }

        return pointIntensity.scale(directionalFactor);
    }

}
