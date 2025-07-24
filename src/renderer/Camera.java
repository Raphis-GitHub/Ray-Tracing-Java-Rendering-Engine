package renderer;

import primitives.*;
import primitives.Vector;
import scene.Scene;

import java.util.*;

import static primitives.Util.*;

/**
 * Camera class for rendering scenes.
 */
public class Camera implements Cloneable {
    /**
     * ImageWriter for writing the rendered image.
     */
    private ImageWriter imageWriter;
    /**
     * RayTracerBase for tracing rays through the scene.
     */
    private RayTracerBase rayTracer;
    /**
     * Number of columns (pixels) in the view plane.
     */
    private int nX = 1;
    /**
     * Number of rows (pixels) in the view plane.
     */
    private int nY = 1;
    /**
     * Camera position
     */
    private Point p0 = null;
    /**
     * Forward direction vector
     */
    private Vector vTo = null;
    /**
     * Up direction vector
     */
    private Vector vUp = null;
    /**
     * Right direction vector
     */
    private Vector vRight = null;
    /**
     * View plane width
     */
    private double width = 0;
    /**
     * View plane height
     */
    private double height = 0;
    /**
     * Distance to view plane
     */
    private double distance = 0;
    /**
     * Blackboard configuration for rendering improvements.
     */
    private Blackboard blackboard;

    /**
     * Focus point distance for depth of field effect.
     */
    private double focusPointDistance = 100;

    /**
     * Aperture size for depth of field effect.
     */
    private double aperture = 0.5;

    /**
     * Private constructor for Camera.
     * Initializes width, height, and distance to 0.
     */
    private Camera() {
    }

    /**
     * Returns a new Camera builder.
     *
     * @return Builder instance for Camera
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a pixel in the view plane.
     *
     * @param nX number of columns (pixels) in view plane
     * @param nY number of rows (pixels) in view plane
     * @param j  pixel column index
     * @param i  pixel row index
     * @return ray from the camera through the specified pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double xJ = (j - ((nX - 1) / 2.0)) * (width / nX);
        double yI = -(i - ((nY - 1) / 2.0)) * (height / nY);

        Point pIJ = this.p0.add(this.vTo.scale(distance));
        if (!isZero(xJ)) pIJ = pIJ.add(vRight.scale(xJ));
        if (!isZero(yI)) pIJ = pIJ.add(vUp.scale(yI));

        //add a checker for the point pIJ in case fo zero vector
        return new Ray(pIJ.subtract(p0), p0);
    }

    /**
     * Renders the image by casting rays through each pixel in the view plane.
     *
     * @return the Camera instance for method chaining
     */
    public Camera renderImage() {
        int percentage = 0;
        for (int y = 0; y < nY; y++) {
            for (int x = 0; x < nX; x++) {
                castRay(x, y);
            }
            int newPercentage = (int) ((y + 1) * 100.0 / nY);
            if (newPercentage > percentage) {
                percentage = newPercentage;
                System.out.println("Rendering progress: " + percentage + "%");
            }
        }

        return this;
    }

    /**
     * Prints a grid on the image with specified interval and color.
     *
     * @param interval the interval at which to print the grid lines
     * @param color    the color of the grid lines
     * @return the Camera instance for method chaining
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nX; i++)
            for (int j = 0; j < nY; j++)
                if (i % interval == 0 || j % interval == 0)
                    imageWriter.writePixel(i, j, color);
        return this;
    }

    /**
     * Writes the rendered image to a file.
     *
     * @param fileName the name of the file to write the image to
     */
    public void writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
    }

    /**
     * Casts a ray through the specified pixel and writes the resulting color to the image.
     * Supports anti-aliasing and depth of field if configured in blackboard.
     *
     * @param x the pixel column index
     * @param y the pxel row index
     */
    private void castRay(int x, int y) {
        Ray primaryRay = constructRay(nX, nY, x, y);
        //split into 3 functions!! - refacter score 7/10
        // Generate anti-aliasing rays if enabled
        List<Ray> aaRays = List.of(primaryRay);
        if (blackboard != null && blackboard.useAntiAliasing()) {
            double pixelSize = Math.max(width / nX, height / nY);
            aaRays = blackboard.constructRays(primaryRay, distance, pixelSize);
        }

        Color finalColor = Color.BLACK;

        // Process each anti-aliasing ray
        for (Ray aaRay : aaRays) {
            List<Ray> dofRays = List.of(aaRay);

            // Generate depth of field rays if enabled
            if (blackboard != null && blackboard.useDepthOfField()) {
                // Calculate focal point along the primary ray (not AA ray)
                Point focalPoint = primaryRay.getPoint(focusPointDistance);

                // Generate aperture points around the camera position
                List<Point> aperturePoints = blackboard.createAperturePoints(p0, vRight, vUp, aperture);
                dofRays = new ArrayList<>();

                for (Point aperturePoint : aperturePoints) {
                    try {
                        Vector direction = focalPoint.subtract(aperturePoint).normalize();
                        dofRays.add(new Ray(direction, aperturePoint));
                    } catch (IllegalArgumentException e) {
                        // Skip if direction is zero (aperture point = focal point)
                        dofRays.add(aaRay); // Use original ray as fallback
                    }
                }
            }

            // Trace all depth of field rays and average their colors
            Color aaColor = Color.BLACK;
            for (Ray dofRay : dofRays) {
                aaColor = aaColor.add(rayTracer.traceRay(dofRay));
            }
            aaColor = aaColor.reduce(dofRays.size());

            finalColor = finalColor.add(aaColor);
        }

        // Average across all anti-aliasing samples
        finalColor = finalColor.reduce(aaRays.size());

        imageWriter.writePixel(x, y, finalColor);
    }

    /**
     * Creates a shallow copy of this Camera object.
     * Uses the Object.clone() method to create a copy.
     *
     * @return a cloned Camera object
     * @throws RuntimeException if cloning fails (should not happen)
     */
    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Must not happen, Camera is Cloneable", e);
        }
    }

    /**
     * Builder for Camera.
     */
    public static class Builder {
        /**
         * Camera instance being built
         */
        private final Camera camera = new Camera();

        /**
         * Sets the ray tracer for the camera.
         *
         * @param scene         the scene to be rendered
         * @param rayTracerType the type of ray tracer to use
         * @return Builder instance
         */
        public Builder setRayTracer(Scene scene, RayTracerType rayTracerType) {
            if (rayTracerType == RayTracerType.SIMPLE)
                camera.rayTracer = new SimpleRayTracer(scene);
            return this;
        }

        /**
         * Sets camera position.
         *
         * @param p0 camera position
         * @return Builder instance
         */
        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets direction vectors.
         *
         * @param vTo forward direction vector
         * @param vUp up direction vector
         * @return Builder instance
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Sets direction using target point and up vector.
         *
         * @param target target point
         * @param vUp    up direction vector
         * @return Builder instance
         */
        public Builder setDirection(Point target, Vector vUp) {
            camera.vTo = target.subtract(camera.p0).normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Sets direction using target point.
         *
         * @param target target point
         * @return Builder instance
         */
        public Builder setDirection(Point target) {
            camera.vTo = target.subtract(camera.p0).normalize();
            camera.vUp = Vector.AXIS_Y; // Default up vector
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        /**
         * Sets the blackboard configuration.
         *
         * @param blackboard the blackboard to use for rendering improvements
         * @return Builder instance
         */
        public Builder setBlackboard(Blackboard blackboard) {
            camera.blackboard = blackboard;
            return this;
        }

        /**
         * Sets the focus point distance for depth of field.
         *
         * @param focusPointDistance distance to the focus point
         * @return Builder instance
         */
        public Builder setFocusPointDistance(double focusPointDistance) {
            camera.focusPointDistance = focusPointDistance;
            return this;
        }

        /**
         * Sets the aperture size for depth of field.
         *
         * @param aperture aperture size
         * @return Builder instance
         */
        public Builder setAperture(double aperture) {
            camera.aperture = aperture;
            return this;
        }

        /**
         * Sets view plane size.
         *
         * @param width  view plane width
         * @param height view plane height
         * @return Builder instance
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;

        }

        /**
         * Sets view plane distance.
         *
         * @param distance distance to view plane
         * @return Builder instance
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;

        }

        /**
         * Sets camera resolution (not implemented).
         *
         * @param nX number of columns (pixels) in view plane
         * @param nY number of rows (pixels) in view plane
         * @return Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Builds and returns the Camera.
         * Checks for missing parameters and calculates right vector.
         *
         * @return Camera instance
         */
        public Camera build() {
            final String MISSING = "Missing rendering data";
            final String CAM = "Camera";
            if (camera.p0 == null)
                throw new MissingResourceException(MISSING, CAM, "location");
            if (camera.vTo == null)
                throw new MissingResourceException(MISSING, CAM, "vTo");
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();

            if (alignZero(camera.width) <= 0)
                throw new MissingResourceException(MISSING, CAM, "width > 0");
            if (alignZero(camera.height) <= 0)
                throw new MissingResourceException(MISSING, CAM, "height > 0");
            if (alignZero(camera.distance) <= 0)
                throw new MissingResourceException(MISSING, CAM, "distance > 0");

            if (camera.nX <= 0)
                throw new MissingResourceException(MISSING, CAM, "nX > 0");
            if (camera.nY <= 0)
                throw new MissingResourceException(MISSING, CAM, "nY > 0");
            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);

            if (camera.rayTracer == null)
                camera.rayTracer = new SimpleRayTracer(null);

            // Set default blackboard if none provided
            if (camera.blackboard == null) {
                camera.blackboard = Blackboard.getBuilder().build();
            }

            return camera.clone();
        }
    }
}
