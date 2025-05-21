package geometries;

import primitives.Ray;
import primitives.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * Geometries is a composite class that groups multiple intersectable geometries.
 */
public class Geometries implements Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();

    public Geometries() {}

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    public void add(Intersectable... geometries) {
        for (Intersectable g : geometries) {
            this.geometries.add(g);
        }
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable geo : geometries) {
            List<Point> temp = geo.findIntersections(ray);
            if (temp != null) {
                if (result == null) result = new LinkedList<>();
                result.addAll(temp);
            }
        }

        return result;
    }
}
