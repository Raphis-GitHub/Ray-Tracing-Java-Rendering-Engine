package renderer;

import primitives.*;

import java.util.MissingResourceException;

import static primitives.Util.isZero;

public class Camera implements Cloneable {
    private Point p0;
    private Vector vTo, vUp, vRight;
    private double width, height, distance;

    public Point getP0() {
        return p0;
    }

    public Vector getVTo() {
        return vTo;
    }

    public Vector getVUp() {
        return vUp;
    }

    public Vector getVRight() {
        return vRight;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDistance() {
        return distance;
    }

    private Camera() {
        height = 0;
        width = 0;
        distance = 0;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

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

    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            camera.vTo = target.subtract(camera.p0).normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        public Builder setDirection(Point target) {
            camera.vTo = target.subtract(camera.p0).normalize();
            camera.vUp = new Vector(0, 1, 0); // Default up vector
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;

        }

        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;

        }

        public Builder setResolution(int nX, int nY) {
            // This method is not implemented
            // It could be used to set the resolution of the camera view
            return this;
        }

        /**
         * Build the Camera object after validation
         *
         * @return Camera copy
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
