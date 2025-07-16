package primitives;

/**
 * Material class represents the material properties of an object in a scene.
 */
public class Material {
    /**
     * Ambient coefficient - the proportion of ambient light reflected by the material.
     * Default value is 1.0 (fully reflective).
     */
    public Double3 kA = Double3.ONE;
    /**
     * Diffuse coefficient - the proportion of diffuse light reflected by the material.
     * Default value is 0.0 (no diffuse reflection).
     */
    public Double3 kD = Double3.ZERO;
    /**
     * Specular coefficient - the proportion of specular light reflected by the material.
     * Default value is 0.0 (no specular reflection).
     */
    public Double3 kS = Double3.ZERO;
    /**
     * Shininess coefficient - controls the shininess of the material.
     * A higher value results in a shinier surface.
     * Default value is 0 (no shininess).
     */
    public int nSh = 0;
    /**
     * Transparency coefficient for the material.
     * Controls how much light passes through the material.
     */
    public Double3 kT = Double3.ZERO;
    /**
     * Reflection coefficient for the material.
     * Controls how much light is reflected by the material.
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Default constructor for Material.
     *
     * @param kA ambient coefficient
     * @return the current Material instance for method chaining
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient coefficient to a double value.
     *
     * @param kA ambient coefficient as a double
     * @return the current Material instance for method chaining
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the diffuse coefficient.
     *
     * @param kD diffuse coefficient
     * @return the current Material instance for method chaining
     */
    public Material setKd(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse coefficient to a double value.
     *
     * @param kD diffuse coefficient as a double
     * @return the current Material instance for method chaining
     */
    public Material setKd(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular coefficient.
     *
     * @param kS specular coefficient
     * @return the current Material instance for method chaining
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular coefficient to a double value.
     *
     * @param kS specular coefficient as a double
     * @return the current Material instance for method chaining
     */
    public Material setKs(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess coefficient.
     *
     * @param nSh shininess coefficient (must be non-negative)
     * @return the current Material instance for method chaining
     * @throws IllegalArgumentException if nSh is negative
     */
    public Material setShininess(int nSh) {
        if (nSh < 0) throw new IllegalArgumentException("nSh must be non-negative");
        this.nSh = nSh;
        return this;

    }

    /**
     * Sets the transparency coefficient using a Double3 value.
     *
     * @param kT transparency coefficient as a Double3
     * @return the current Material instance for method chaining
     */
    public Material setKt(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the transparency coefficient using a double value.
     *
     * @param kT transparency coefficient as a double
     * @return the current Material instance for method chaining
     */
    public Material setKt(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the reflection coefficient using a Double3 value.
     *
     * @param kR reflection coefficient as a Double3
     * @return the current Material instance for method chaining
     */
    public Material setKr(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets the reflection coefficient using a double value.
     *
     * @param kR reflection coefficient as a double
     * @return the current Material instance for method chaining
     */
    public Material setKr(double kR) {
        this.kR = new Double3(kR);
        return this;
    }

}
