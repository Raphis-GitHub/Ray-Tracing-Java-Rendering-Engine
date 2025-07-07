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
     * The narrow beam effect factor.
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
     * Sets the narrow beam effect for the spotlight.
     *
     * @param narrowBeam the narrow beam factor (1 for no effect, >1 for narrowing)
     * @return the current SpotLight instance for method chaining
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
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

        Color pointIntensity = super.getIntensity(p);

        // Calculate the direction from the light source to the point
        //maybe add try catch for subtract- unsure- ask dan
        Vector lightToPoint = p.subtract(position).normalize();

        // Calculate the cosine of the angle between light direction and light-to-point vector
        // Both vectors should point in the same direction for maximum intensity
        double cosAngle = direction.dotProduct(lightToPoint);

        // Apply directional factor (0 if pointing away, positive if within cone)
        double directionalFactor = Math.max(0, cosAngle);
        //Apply narrow beam effect by raising to a power
        if (narrowBeam > 1) {
            directionalFactor = Math.pow(directionalFactor, narrowBeam);
        }
        return pointIntensity.scale(directionalFactor);

    }

}
