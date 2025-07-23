package renderer;

import primitives.*;
import primitives.Vector;

import java.util.*;

/**
 * The Blackboard class is responsible for generating a set of points based on specified methods.
 * The points can be generated within a unit circle or a unit square based on the useCircle flag.
 * This class is designed to be used in conjunction with a Ray and a center Point,
 * allowing for flexible point generation in 3D space.
 * The class uses a Builder pattern for construction,
 **/
public class Blackboard implements Cloneable {

    /**
     * Indicates whether to use soft shadows.
     * If true, the rays will be generated in a way that simulates soft shadows.
     * Default is false.
     */
    private boolean softShadows = false;
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
     * Indicates whether to use blurry and glossy effects.
     * If true, the rays will be generated in a way that simulates blurry and glossy effects.
     * Default is false.
     */
    private boolean blurryAndGlossy = false;

    /**
     * The size of the grid for point generation.
     * This is used to determine the number of points in a grid pattern.
     * Default is 10, which corresponds to a 10x10 grid.
     */
    private int gridSize = 10;
    /**
     * Default diameter/width/height for point generation.
     * This is used to define the size of the area in which points are generated.
     * Default is 1, which corresponds to a unit circle or square of size 1x1.
     */
    private static final double DEFAULT_SIZE = 1;

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
         * Sets the grid size for the underlying blackboard configuration.
         * The grid size determines the spacing between grid points in the blackboard.
         *
         * @param gridSize the size of the grid, represented as a double
         * @return this Builder instance for method chaining
         */
        public Builder setGridSize(int gridSize) {
            blackboard.gridSize = gridSize;
            return this;
        }

        /**
         * Sets whether soft shadows should be used.
         * If true, the rays will be generated in a way that simulates soft shadows.
         * Default is false.
         *
         * @param softShadows true to enable soft shadows, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setSoftShadows(boolean softShadows) {
            blackboard.softShadows = softShadows;
            return this;
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
         * Sets whether to use blurry and glossy effects.
         * If true, the rays will be generated in a way that simulates blurry and glossy effects.
         * Default is false.
         *
         * @param blurryAndGlossy true to enable blurry and glossy effects, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setBlurryAndGlossy(boolean blurryAndGlossy) {
            blackboard.blurryAndGlossy = blurryAndGlossy;
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
     * Uses a default radius size
     * This method uses local variables to avoid thread safety issues.
     *
     * @param baseRay  the base ray used for direction calculation
     * @param distance the center point around which rays are constructed
     * @return a list of rays constructed from the generated points
     */
    public List<Ray> constructRays(Ray baseRay, double distance) {
        return constructRays(baseRay, distance, DEFAULT_SIZE);
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
            Vector direction = point.subtract(baseRay.origin()).normalize();
            resultRays.add(new Ray(direction, baseRay.origin()));
        }
        return resultRays;
    }

    /**
     * Creates jittered points within grid cells, based on the base ray and center point.
     */
    private List<Point> createJitteredPoints(Ray baseRay, Point center, double size) {
        List<Point> pointsList = new LinkedList<>();

        pointsList.add(center);
        if (size == 0) return pointsList;

        Vector v = baseRay.direction();//fow
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();//right
        Vector u = v.crossProduct(w).normalize();//to
        //might take from camera- refactor score 3/10

        double cellSize = size / gridSize;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double x = (col + Math.random()) * cellSize - size;
                double y = (row + Math.random()) * cellSize - size;

                Point point = center.add(u.scale(x)).add(w.scale(y));
                pointsList.add(point);
            }
        }
        return pointsList;
    }

    /**
     * Returns whether soft shadows are enabled.
     */
    public Boolean useSoftShadows() {
        return softShadows;
    }

    /**
     * Returns whether anti-aliasing is enabled.
     */
    public Boolean useAntiAliasing() {
        return antiAliasing;
    }

    /**
     * Returns whether depth of field is enabled.
     */
    public Boolean useDepthOfField() {
        return depthOfField;
    }

    /**
     * Returns whether blurry and glossy effects are enabled.
     */
    public Boolean useBlurryAndGlossy() {
        return blurryAndGlossy;
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