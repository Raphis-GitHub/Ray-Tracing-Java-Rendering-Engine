package lighting;

import primitives.*;

public class SpotLight extends PointLight {

    /**
     * The direction of the spotlight.
     */
    private final Vector direction;

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

}
