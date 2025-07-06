package lighting;

import primitives.*;

public class DirectionalLight extends Light implements LightSource {
    private final Vector direction;

    public DirectionalLight(Vector direction, Color intensity) {
        super(intensity);
        this.direction = direction.normalize();
    }

    /**
     * @param p
     * @return
     */
    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    /**
     * @param p
     * @return
     */
    @Override
    public Vector getL(Point p) {
        return direction;
    }
}
