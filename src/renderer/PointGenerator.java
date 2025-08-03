package renderer;

import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Simple utility class for generating points in 2D grids.
 * Handles both regular and jittered sampling patterns.
 */
public class PointGenerator {

    /**
     * Generates grid points in a 2D plane.
     *
     * @param center      the center point of the grid
     * @param uVector     the first orthogonal vector (horizontal direction)
     * @param vVector     the second orthogonal vector (vertical direction)
     * @param size        the size/radius of the grid area
     * @param gridSize    the number of grid cells per dimension
     * @param useJittered whether to use jittered (random) or regular sampling
     * @return list of points including the center and grid points
     */
    public static List<Point> generateGridPoints(Point center, Vector uVector, Vector vVector,
                                                 double size, int gridSize, boolean useJittered) {
        List<Point> pointsList = new ArrayList<>();
        pointsList.add(center); // Include center point

        if (size == 0) return pointsList;

        double cellSize = size / gridSize;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double x, y;
                if (useJittered) {
                    x = (col + current().nextDouble()) * cellSize - size;
                    y = (row + current().nextDouble()) * cellSize - size;
                } else {
                    x = (col + 0.5) * cellSize - size;
                    y = (row + 0.5) * cellSize - size;
                }
                Point point = center.add(uVector.scale(x)).add(vVector.scale(y));
                pointsList.add(point);
            }
        }
        return pointsList;
    }

}