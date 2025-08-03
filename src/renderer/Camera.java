package renderer;

import primitives.*;
import primitives.Vector;
import scene.Scene;

import java.util.*;
import java.util.stream.*;

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
     * List of render effects to apply during ray generation.
     */
    private List<RenderEffect> renderEffects = new ArrayList<>();

    /**
     * Focus point distance for depth of field effect.
     */
    private double focusPointDistance = 100;

    /**
     * Aperture size for depth of field effect.
     */
    private double aperture = 0.5;

    /**
     * Number of threads for multithreading.
     */
    private int threadsCount = 0;

    /**
     * Number of spare threads to keep available for the system.
     */
    private static final int SPARE_THREADS = 2;

    /**
     * Progress print interval for debugging.
     */
    private double printInterval = 0;

    /**
     * Pixel manager for thread-safe pixel processing.
     */
    private PixelManager pixelManager;

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
        return new Ray(pIJ.subtract(p0).normalize(), p0);
    }

    /**
     * Renders the image by casting rays through each pixel in the view plane.
     *
     * @return the Camera instance for method chaining
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
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
     * Casts rays through the specified pixel and writes the resulting color to the image.
     *
     * @param x the pixel column index
     * @param y the pixel row index
     */
    private void castRay(int x, int y) {
        Ray primaryRay = constructRay(nX, nY, x, y);
        List<Ray> rays = generateRays(primaryRay);
        Color finalColor = traceRays(rays);
        imageWriter.writePixel(x, y, finalColor);
        pixelManager.pixelDone();
    }

    /**
     * Generates rays for a pixel based on enabled effects.
     * Processes rays through all enabled ray generation steps.
     *
     * @param primaryRay the main ray through the pixel center
     * @return list of rays to trace for this pixel
     */
    private List<Ray> generateRays(Ray primaryRay) {
        List<Ray> rays = List.of(primaryRay);

        // Apply each render effect
        for (RenderEffect effect : renderEffects) {
            if (effect.isEnabled(this)) {
                rays = effect.applyEffect(rays, primaryRay, this);
            }
        }

        return rays;
    }

    /**
     * Traces all rays and computes the average color.
     *
     * @param rays the rays to trace
     * @return the average color from all rays
     */
    private Color traceRays(List<Ray> rays) {
        Color totalColor = Color.BLACK;
        for (Ray ray : rays) {
            totalColor = totalColor.add(rayTracer.traceRay(ray));
        }
        return totalColor.reduce(rays.size());
    }

    /**
     * Renders image using parallel streams for multithreading.
     *
     * @return the Camera instance for method chaining
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
            .forEach(i -> IntStream.range(0, nX).parallel()
                .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders image with no threading (single-threaded).
     *
     * @return the Camera instance for method chaining
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRay(j, i);
        return this;
    }

    /**
     * Renders image using manual thread management.
     *
     * @return the Camera instance for method chaining
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}
        return this;
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
         * Sets the render effects to apply during ray generation.
         *
         * @param effects the list of render effects
         * @return Builder instance
         */
        @SuppressWarnings("unused")
        public Builder setRenderEffects(List<RenderEffect> effects) {
            camera.renderEffects = new ArrayList<>(effects);
            return this;
        }

        /**
         * Adds a render effect to the list of effects.
         *
         * @param effect the render effect to add
         * @return Builder instance
         */
        @SuppressWarnings("unused")
        public Builder addRenderEffect(RenderEffect effect) {
            camera.renderEffects.add(effect);
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
         * Sets multithreading configuration.
         *
         * @param threads number of threads to use (-2 for auto, -1 for streams, 0 for no threading, >0 for specific count)
         * @return Builder instance
         */
        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            } else
                camera.threadsCount = threads;
            return this;
        }

        /**
         * Sets debug print interval for progress display.
         *
         * @param interval print interval percentage (0 to disable)
         * @return Builder instance
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("Interval must be non-negative");
            camera.printInterval = interval;
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

            // Initialize render effects
            if (camera.renderEffects.isEmpty()) {
                camera.renderEffects.add(new AntiAliasingEffect());
                camera.renderEffects.add(new DepthOfFieldEffect());
            }

            return camera.clone();
        }
    }

    // Getter methods for render effects access
    public Blackboard getBlackboard() {
        return blackboard;
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

    public int getNX() {
        return nX;
    }

    public int getNY() {
        return nY;
    }

    public Point getP0() {
        return p0;
    }

    public Vector getVRight() {
        return vRight;
    }

    public Vector getVUp() {
        return vUp;
    }

    public double getFocusPointDistance() {
        return focusPointDistance;
    }

    public double getAperture() {
        return aperture;
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
            Camera cloned = (Camera) super.clone();
            cloned.renderEffects = new ArrayList<>(this.renderEffects);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Must not happen, Camera is Cloneable", e);
        }
    }
}
