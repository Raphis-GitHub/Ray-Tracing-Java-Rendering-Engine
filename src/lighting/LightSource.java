package lighting;

import primitives.*;

public interface LightSource {
    Color getIntensity(Point p);

    Vector getL(Point p);

}
