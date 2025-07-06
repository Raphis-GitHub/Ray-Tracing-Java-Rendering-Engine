package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * Scene class represents a 3D scene with a name, background color, ambient light, and geometries.
 */
public class Scene {
    /**
     * The name of the scene.
     */
    public String name;
    /**
     * The background color of the scene.
     */
    public Color background = Color.BLACK;
    //TODO: check- doesnt mention in english doc
    /**
     * The ambient light in the scene.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;
    /**
     * The geometries in the scene.
     * This is a collection of all the geometrical shapes that make up the scene.
     */
    public Geometries geometries = new Geometries();

    public Scene setLightSources(List<LightSource> lightSources) {
        this.lights = lightSources;
        return this;
    }

    public List<LightSource> lights = new LinkedList<>();

    /**
     * Constructor for Scene with a name.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background of the scene.
     *
     * @param background the color to set
     * @return this Scene instance for method chaining
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light to set
     * @return this Scene instance for method chaining
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries of the scene.
     *
     * @param geometries the geometries to set
     * @return this Scene instance for method chaining
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
