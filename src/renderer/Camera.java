package renderer;

import primitives.*;
import scene.Scene;

import java.util.MissingResourceException;

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
        for (int y = 0; y < nY; y++)
            for (int x = 0; x < nX; x++)
                castRay(x, y);
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
     *
     * @param x the x index of the pixel (column)
     * @param y the y index of the pixel (row)
     */
    private void castRay(int x, int y) {
        // Construct the ray through the pixel
        Ray ray = constructRay(nX, nY, x, y);
        // Trace the ray to get the color
        Color color = rayTracer.traceRay(ray);
        // Write the color to the image
        imageWriter.writePixel(x, y, color);
    }

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
            camera.vUp = new Vector(0, 1, 0); // Default up vector
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
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

            return camera.clone();
        }
    }
}
