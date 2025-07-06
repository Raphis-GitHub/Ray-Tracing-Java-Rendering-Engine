package lighting;

import primitives.Color;

public abstract class Light {
    protected Color intensity;

    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    public Color getIntensity() {
        return intensity;
    }
}


