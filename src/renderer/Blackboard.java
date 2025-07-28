package renderer;

import primitives.*;
import primitives.Vector;

import java.util.*;

/**
 * The Blackboard class is responsible for generating a set of points based on specified methods.
 * The points can be generated within a unit square.
 * This class is designed to be used in conjunction with a Ray and a center Point,
 * allowing for flexible point generation in 3D space.
 * The class uses a Builder pattern for construction,
 **/
public class Blackboard implements Cloneable {

    /**
     * Indicates whether to use anti-aliasing.
     * If true, the rays will be generated in a way that reduces aliasing artifacts.
     * Default is false.
     */
    private boolean antiAliasing = false;
    /**
     * Indicates whether to use depth of field.
     * If true, the rays will be generated in a way that simulates depth of field effects.
     * Default is false.
     */
    private boolean depthOfField = false;

    /**
     * Controls whether to use jittered sampling (random) or regular grid sampling.
     */
    private boolean useJitteredSampling = true;

    /**
     * Number of samples for anti-aliasing (instead of relying only on gridSize).
     */
    private int antiAliasingSamples = 9;

    /**
     * Number of samples for depth of field effect.
     */
    private int depthOfFieldSamples = 16;
    /**
     * The size of the grid for point generation.
     * This is used to determine the number of points in a grid pattern.
     * Default is 10, which corresponds to a 10x10 grid.
     */
    private int gridSize = 10;
    /**
     * Cached direction vector for orthogonal calculations.
     */
    private Vector cachedDirection = null;
    /**
     * Cached orthogonal vectors for the direction vector.
     */
    private Vector cachedU = null;
    /**
     * Cached up vector orthogonal to the direction vector.
     */
    private Vector cachedW = null;

    /**
     * Private constructor to prevent direct instantiation.
     * Use the Builder class to create an instance of Blackboard.
     */
    private Blackboard() {
    }

    /**
     * Static method to get a new Builder instance for constructing a Blackboard.
     * This method allows for fluent construction of the Blackboard with various configurations.
     *
     * @return a new instance of the Blackboard.Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * The Builder class is used to construct instances of the Blackboard class.
     * It allows for setting various configurations such as point generation method,
     * number of rays, grid size, radius, and whether to use soft shadows or anti-aliasing.
     */
    public static class Builder {
        /**
         * The Blackboard instance being built.
         * This instance is configured through the Builder methods.
         */
        private final Blackboard blackboard;

        /**
         * Default constructor for the Builder class.
         * Initializes a new Blackboard instance with default configurations.
         */
        public Builder() {
            blackboard = new Blackboard();
        }

        /**
         * Sets whether anti-aliasing should be used.
         * If true, the rays will be generated in a way that reduces aliasing artifacts.
         * Default is false.
         *
         * @param antiAliasing true to enable anti-aliasing, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setAntiAliasing(boolean antiAliasing) {
            blackboard.antiAliasing = antiAliasing;
            return this;
        }

        /**
         * Sets whether to use jittered sampling.
         *
         * @param useJittered true to enable jittered sampling, false for regular grid sampling
         * @return builder instance
         */
        public Builder setUseJitteredSampling(boolean useJittered) {
            blackboard.useJitteredSampling = useJittered;
            return this;
        }

        /**
         * Sets the number of anti-aliasing samples.
         *
         * @param samples the number of samples to use for anti-aliasing
         * @return builder instance
         */
        public Builder setAntiAliasingSamples(int samples) {
            blackboard.antiAliasingSamples = samples;
            blackboard.gridSize = (int) Math.ceil(Math.sqrt(samples));
            return this;
        }

        /**
         * Sets the number of depth of field samples.
         *
         * @param samples the number of samples to use for depth of field
         * @return builder instance
         */
        public Builder setDepthOfFieldSamples(int samples) {
            blackboard.depthOfFieldSamples = samples;
            return this;
        }

        /**
         * Sets whether depth of field should be used.
         * If true, the rays will be generated in a way that simulates depth of field effects.
         * Default is false.
         *
         * @param depthOfField true to enable depth of field, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setDepthOfField(boolean depthOfField) {
            blackboard.depthOfField = depthOfField;
            return this;
        }

        /**
         * Builds the Blackboard instance with the specified configurations.
         * This method checks if the sender point is set and throws an exception if it is not.
         *
         * @return a new instance of Blackboard with the configured properties
         * @throws MissingResourceException if the sender point is not set
         */
        public Blackboard build() {
            return blackboard.clone();
        }
    }

    /**
     * Constructs rays from the generated points based on the specified base ray and center point.
     * This method uses local variables to avoid thread safety issues.
     *
     * @param baseRay  the base ray used for direction calculation
     * @param distance the center point around which rays are constructed
     * @param size     the radius of the target zone
     * @return a list of rays constructed from the generated points
     */
    public List<Ray> constructRays(Ray baseRay, double distance, double size) {
        Point center = baseRay.getPoint(distance);
        List<Point> localPoints = createJitteredPoints(baseRay, center, size);

        List<Ray> resultRays = new LinkedList<>();
        for (Point point : localPoints) {
            try {
                Vector direction = point.subtract(baseRay.origin()).normalize();
                resultRays.add(new Ray(direction, baseRay.origin()));
            } catch (IllegalArgumentException e) {
                // Skip points that create zero vectors (point too close to origin)
                // This can happen when sampling at the ray origin (distance = 0)
            }

        }
        return resultRays;
    }

    /**
     * Computes orthogonal vectors based on the given direction vector.
     *
     * @param direction the direction vector to compute orthogonal vectors for
     */
    private void computeOrthogonalVectors(Vector direction) {
        if (direction.equals(cachedDirection)) {
            return; // Already cached
        }

        cachedDirection = direction;
        cachedW = Vector.AXIS_Y.equals(direction) ? Vector.AXIS_X
                : Vector.AXIS_Y.crossProduct(direction).normalize();
        cachedU = direction.crossProduct(cachedW).normalize();
    }

    /**
     * Helper function that generates grid points in a 2D plane
     *
     * @param center  the center point of the grid
     * @param uVector the first orthogonal vector (horizontal direction)
     * @param vVector the second orthogonal vector (vertical direction)
     * @param size    the size/radius of the grid area
     * @return a list of points including the center and grid points
     */
    private List<Point> generateGridPoints(Point center, Vector uVector, Vector vVector, double size) {
        return PointGenerator.generateGridPoints(center, uVector, vVector, size, gridSize, useJitteredSampling);
    }

    /**
     * Creates aperture points for depth of field effect
     * Points are generated in a plane perpendicular to the camera's view direction
     *
     * @param cameraPosition the position of the camera
     * @param vRight         the right vector of the camera's view direction
     * @param vUp            the up vector of the camera's view direction
     * @param apertureSize   the size of the aperture
     * @return a list of points representing the aperture
     */
    public List<Point> createAperturePoints(Point cameraPosition, Vector vRight, Vector vUp, double apertureSize) {
        return generateGridPoints(cameraPosition, vRight, vUp, apertureSize);
    }

    /**
     * Creates jittered points within grid cells, based on the base ray and center point.
     *
     * @param baseRay the base ray used for direction calculation
     * @param center  center point of pixel
     * @param size    the radius of the target zone
     * @return a list of points generated in a jittered pattern
     */
    private List<Point> createJitteredPoints(Ray baseRay, Point center, double size) {
        Vector v = baseRay.direction();
        computeOrthogonalVectors(v);
        Vector u = cachedU;
        Vector w = cachedW;

        return generateGridPoints(center, u, w, size);
    }

    /**
     * Returns whether anti-aliasing is enabled.
     *
     * @return true if anti-aliasing is used, false otherwise
     */
    public Boolean useAntiAliasing() {
        return antiAliasing;
    }

    /**
     * Returns whether depth of field is enabled.
     *
     * @return true if depth of field is used, false otherwise
     */
    public Boolean useDepthOfField() {
        return depthOfField;
    }

    /**
     * Returns the number of anti-aliasing samples.
     *
     * @return the number of samples used for anti-aliasing
     */
    @SuppressWarnings("unused")
    public int getAntiAliasingSamples() {
        return antiAliasingSamples;
    }

    /**
     * Returns the number of depth of field samples.
     *
     * @return the number of samples used for depth of field
     */
    @SuppressWarnings("unused")
    public int getDepthOfFieldSamples() {
        return depthOfFieldSamples;
    }

    /**
     * Returns whether jittered sampling is enabled.
     *
     * @return true if jittered sampling is used, false for regular grid sampling
     */
    @SuppressWarnings("unused")
    public boolean getUseJitteredSampling() {
        return useJitteredSampling;
    }

    /**
     * Creates a clone of this Blackboard instance.
     */
    @Override
    public Blackboard clone() {
        try {
            return (Blackboard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}