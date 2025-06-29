package scene;

import geometries.Geometry;
import lighting.AmbientLight;
import primitives.Color;

public class scene {
    public String name;
    public Color backround;
    public AmbientLight ambientLight = AmbientLight.NONE;
    public Geometry geometries;

    public scene(String name) {
        this.name = name;
    }

    public scene setBackground(Color background) {
        this.backround = background;
        return this;
    }

    public scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public scene setGeometries(Geometry geometries) {
        this.geometries = geometries;
        return this;
    }
}
