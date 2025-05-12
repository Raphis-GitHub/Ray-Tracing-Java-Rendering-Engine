package primitives;

/**
 * Class Vector represents a vector in three-dimensional space.
 * Extends the Point class to inherit x, y, and z coordinates.
 *
 * @author Raphael
 */
public class Vector extends Point {

    /**
     * Constructs a Vector with the specified x, y, and z coordinates.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     * @param z The z-coordinate of the vector.
     * @throws IllegalArgumentException if attempting to create a zero vector.
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (this.xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("You may not create a ZERO vector.");
        }
    }

    /**
     * Constructs a Vector with the specified Double3 object.
     *
     * @param xyz The Double3 object containing x, y, and z coordinates.
     * @throws IllegalArgumentException if attempting to create a zero vector.
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("You may not create a ZERO vector.");
        }
    }

    /**
     * Adds another Vector to this Vector.
     *
     * @param other The Vector to add.
     * @return The resulting Vector after addition.
     * @throws IllegalArgumentException if attempting to create a zero vector.
     */
    public Vector add(Vector other) {
        if (this.xyz.equals(other.xyz.scale(-1))) {
            throw new IllegalArgumentException("You may not create a ZERO vector.");
        }
        return new Vector(xyz.add(other.xyz));
    }

    /**
     * Scales this Vector by a scalar value.
     *
     * @param scalar The scalar value to scale the Vector.
     * @return The resulting scaled Vector.
     */
    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this Vector with another Vector.
     *
     * @param other The Vector to compute the dot product with.
     * @return The dot product of the two Vectors.
     */
    public double dotProduct(Vector other) {
        return (this.xyz.d1() * other.xyz.d1() +
                this.xyz.d2() * other.xyz.d2() +
                this.xyz.d3() * other.xyz.d3());
    }

    /**
     * Computes the cross product of this Vector with another Vector.
     *
     * @param other The Vector to compute the cross product with.
     * @return The cross product Vector.
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
                this.xyz.d2() * other.xyz.d3() - this.xyz.d3() * other.xyz.d2(),
                this.xyz.d3() * other.xyz.d1() - this.xyz.d1() * other.xyz.d3(),
                this.xyz.d1() * other.xyz.d2() - this.xyz.d2() * other.xyz.d1()
        );
    }

    /**
     * Computes the squared length of this Vector.
     * using the formula V dotproduct V = |V|^2
     *
     * @return The squared length of the Vector.
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Computes the length of this Vector by finding the square root of lengthSquared method
     *
     * @return The length of the Vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes this Vector to have unit length.
     *
     * @return The normalized Vector.
     */
    public Vector normalize() {
        return new Vector(this.xyz.reduce(length()));
    }

    /**
     * Checks if this Vector is equal to another Object.
     *
     * @param obj The Object to compare with.
     * @return true if the Objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Vector other && super.equals(other);
    }

    /**
     * Returns a string representation of this Vector.
     *
     * @return The string representation of the Vector.
     */
    @Override
    public String toString() {
        return "->" + super.toString();
    }
}