package renderer;

import primitives.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Depth of field render effect that simulates camera aperture by
 * generating rays from multiple points on the aperture plane.
 */
public class DepthOfFieldEffect implements RenderEffect {

    @Override
    public List<Ray> applyEffect(List<Ray> rays, Ray primaryRay, Camera camera) {
        if (!isEnabled(camera)) {
            return rays;
        }

        List<Ray> allRays = new ArrayList<>();
        Point focalPoint = primaryRay.getPoint(camera.getFocusPointDistance());

        int samples = camera.getBlackboard().getDepthOfFieldSamples();
        int gridSize = (int) Math.ceil(Math.sqrt(samples));
        boolean useJittered = camera.getBlackboard().getUseJitteredSampling();

        List<Point> aperturePoints = PointGenerator.generateGridPoints(
                camera.getP0(), camera.getVRight(), camera.getVUp(),
                camera.getAperture(), gridSize, useJittered);

        for (Ray ray : rays) {
            for (Point aperturePoint : aperturePoints) {
                try {
                    Vector direction = focalPoint.subtract(aperturePoint).normalize();
                    allRays.add(new Ray(direction, aperturePoint));
                } catch (IllegalArgumentException e) {
                    allRays.add(ray);
                }
            }
        }
        return allRays;
    }

    @Override
    public boolean isEnabled(Camera camera) {
        return camera.getBlackboard().useDepthOfField();
    }
}