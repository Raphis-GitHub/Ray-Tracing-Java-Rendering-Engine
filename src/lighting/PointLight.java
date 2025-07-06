package lighting;

import primitives.*;

import static primitives.Util.isZero;

public class PointLight extends Light implements LightSource {
    protected final Point position;
    private double kC = 1, kL = 0, kQ = 0;

    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

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
