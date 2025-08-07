package primitives;

/**
 * Class Point that uses Double3 to store 3 doubles in the form (x, y, z) and contains methods
 * that add, subtract, calculates distances and esc.
 *
 * @author Raphael
 */
public class Point {

    /**
     * A constant representing the origin point (0, 0, 0).
     */

    public static final Point ZERO = new Point(0, 0, 0);
    /**
     * An object of Double3, obj xyz is a collection of 3 doubles
     * representing the xyz in a cartesian coordinate system.
     */
    protected final Double3 xyz;

    /**
     * a constructor that takes a singe parameter and assigns it to the xyz field
     *
     * @param otherXyz is an object of Double3 and is set as the field xyz.
     */
    protected Point(Double3 otherXyz) {
        this.xyz = otherXyz;
    }

    /**
     * a constructor that takes in 3 doubles and assigns them to
     * be the corresponding values in a 3 Dimensional point.
     *
     * @param x double representing the X-value
     * @param y double representing the Y-value
     * @param z double representing the Z-value
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * a method that takes in a vector and adds it to the point. it uses add function from Double3
     *
     * @param obj Vector used for addition
     * @return a new point
     */
    public Point add(Vector obj) {
        return new Point(xyz.add(obj.xyz));
    }

    /**
     * a method that computes the difference between a Point and a Vector.
     * if the point shares the same vales as the vector or vise-versa, it throws an error.
     *
     * @param otherPoint an object of Point used
     * @return a new Vector which is the difference between the vector and the point
     */
    public Vector subtract(Point otherPoint) {
        return new Vector(xyz.subtract(otherPoint.xyz));
    }

    /**
     * a method that finds out the squared distance between 2 points
     * using the formula: (x2-x1)^2 + (y2-y1)^2
     *
     * @param otherPoint the second point in the equation
     * @return a double representing output of the formula( the distance between 2 points squared)
     */
    public double distanceSquared(Point otherPoint) {
        double d1 = xyz.d1() - otherPoint.xyz.d1();
        double d2 = xyz.d2() - otherPoint.xyz.d2();
        double d3 = xyz.d3() - otherPoint.xyz.d3();
        return d1 * d1 + d2 * d2 + d3 * d3;
    }

    /**
     * a method that roots the product of distanceSquared
     *
     * @param otherPoint the second point in the equation
     * @return a double representing the distance between 2 points
     */
    public double distance(Point otherPoint) {
        return Math.sqrt(distanceSquared(otherPoint));
    }

    /**
     * Checks if this Point object is equal to another object.
     * Two Point objects are considered equal if their xyz coordinates are equal.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Point other && xyz.equals(other.xyz);
    }

    public double getX() {
        return xyz.d1();
    }

    public double getY() {
        return xyz.d2();
    }

    public double getZ() {
        return xyz.d3();
    }

    /**
     * Returns a string representation of this Point object.
     * The format is based on the underlying xyz Double3 representation.
     *
     * @return a string representation of this Point object
     */
    @Override
    public String toString() {
        return "" + xyz;
    }
}