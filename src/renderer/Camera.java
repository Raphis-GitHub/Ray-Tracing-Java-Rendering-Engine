package renderer;

import primitives.*;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

/**
 * Camera class for rendering scenes.
 */
public class Camera implements Cloneable {
    /**
     * Camera position
     */
    private Point p0;
    /**
     * Forward direction vector
     */
    private Vector vTo;
    /**
     * Up direction vector
     */
    private Vector vUp;
    /**
     * Right direction vector
     */
    private Vector vRight;
    /**
     * View plane width
     */
    private double width;
    /**
     * View plane height
     */
    private double height;
    /**
     * Distance to view plane
     */
    private double distance;

    /**
     * Gets camera position.
     *
     * @return camera position
     */
    public Point getP0() {
        return p0;
    }

    /**
     * Gets forward direction vector.
     *
     * @return forward direction vector
     */
    public Vector getVTo() {
        return vTo;
    }

    /**
     * Gets up direction vector.
     *
     * @return up direction vector
     */
    public Vector getVUp() {
        return vUp;
    }

    /**
     * Gets right direction vector.
     *
     * @return right direction vector
     */
    public Vector getVRight() {
        return vRight;
    }

    /**
     * Gets view plane width.
     *
     * @return view plane width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets view plane height.
     *
     * @return view plane height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets distance to view plane.
     *
     * @return distance to the view plane
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Private constructor for Camera.
     * Initializes width, height, and distance to 0.
     */
    private Camera() {
        height = 0;
        width = 0;
        distance = 0;
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
     * @return Ray from camera through the specified pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double xJ = (j - ((nX - 1) / 2.0)) * (width / nX);
        double yI = -(i - ((nY - 1) / 2.0)) * (height / nY);

        Point pIJ = this.p0.add(this.vTo.scale(distance));
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(yI));
        }
        //add a checker for the point pIJ in case fo zero vector
        Vector dir = pIJ.subtract(p0).normalize();
        return new Ray(dir, p0);
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
            // This method is not implemented
            // It could be used to set the resolution of the camera view
            return this;
        }

        /**
         * Builds and returns the Camera.
         * Checks for missing parameters and calculates right vector.
         *
         * @return Camera instance
         * @throws MissingResourceException   if required parameters are missing
         * @throws CloneNotSupportedException if cloning is not supported
         */
        public Camera build() throws CloneNotSupportedException {
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            final String MISSING = "Missing rendering data";
            final String CAM = "Camera";
            if (camera.p0 == null)
                throw new MissingResourceException(MISSING, CAM, "location");
            if (camera.vTo == null)
                throw new MissingResourceException(MISSING, CAM, "vTo");
            if (camera.vUp == null)
                throw new MissingResourceException(MISSING, CAM, "vUp");
            if (camera.width <= 0)
                throw new MissingResourceException(MISSING, CAM, "width > 0");
            if (camera.height <= 0)
                throw new MissingResourceException(MISSING, CAM, "height > 0");
            if (camera.distance <= 0)
                throw new MissingResourceException(MISSING, CAM, "distance > 0");
            return (Camera) camera.clone();

        }
    }
}
