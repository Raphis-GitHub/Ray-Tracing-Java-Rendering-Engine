package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cylinder class
 *
 * @author Raphael
 */
class CylinderTest {
    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Vector(0, 0, 1), new Point(0, 0, 0));
        Cylinder cylinder = new Cylinder(axis, 1, 5);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Point on side surface
        Point p1 = new Point(1, 0, 2);
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(p1),
                "Wrong normal on side surface");

        // TC02: Point on side with diagonal
        Point p2 = new Point(1, 1, 3);
        Vector expected2 = new Vector(1, 1, 0).normalize();
        assertEquals(expected2, cylinder.getNormal(p2),
                "Wrong normal on diagonal side");

        // =============== Boundary Values Tests ==================

        // TC11: Point on bottom base (t ≈ 0)
        Point p3 = new Point(0.5, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p3),
                "Wrong normal on bottom base");

        // TC12: Point on top base (t ≈ height)
        Point p4 = new Point(0, -0.5, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p4),
                "Wrong normal on top base");

        // TC13: Point at edge between side and bottom
        Point p5 = new Point(1, 0, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p5),
                "Wrong normal at bottom edge");

        // TC14: Point at edge between side and top
        Point p6 = new Point(1, 0, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p6),
                "Wrong normal at top edge");
    }

    /**
     * Test method for{@link geometries.Cylinder#calculateIntersectionsHelper(primitives.Ray)}
     */
    @Test
    void testCalculateIntersectionsHelper() {
        Cylinder cylinder = new Cylinder(new Ray(Vector.AXIS_Z, new Point(2, 2, 0)), 1, 1);
        final Point p220 = new Point(2, 2, 0);
        final Point p011 = new Point(0, 1, 1);
        final Point p11_1 = new Point(1, 1, -1);
        final Point p110 = new Point(1, 1, 0);
        final Point p112 = new Point(1, 1, 2);
        final Point p1515_05 = new Point(1.5, 1.5, -0.5);
        final Point p15150 = new Point(1.5, 1.5, 0);
        final Point p151505 = new Point(1.5, 1.5, 0.5);
        final Point p15152 = new Point(1.5, 1.5, 2);
        final Point p1205 = new Point(1, 2, 0.5);
        final Point p12_1 = new Point(1, 2, -1);
        final Point p22_05 = new Point(2, 2, -0.5);
        final Point p331 = new Point(3, 3, 1);
        final Point p332 = new Point(3, 3, 2);
        final Point p440 = new Point(4, 4, 0);
        final Vector v001 = Vector.AXIS_Z;
        final Vector v100 = Vector.AXIS_X;
        final Vector v110 = new Vector(1, 1, 0);
        final Vector v112 = new Vector(1, 1, 2);
        final Vector v111 = new Vector(1, 1, 1);
        final var exp04 = List.of(new Point(1.5, 1.5, 0), new Point(2, 2, 1));
        final var exp21 = List.of(new Point(2.7071067811865475, 2.7071067811865475, 0.7071067811865475), p220);
        final var exp22 = List.of(new Point(2.7071067811865475244008443621048490, 2.7071067811865475244008443621048490, 0.7071067811865475244008443621048490), new Point(2, 2, 0));
        final var exp32 = List.of(new Point(2, 2, 1));
        final var exp31 = List.of(new Point(1.2928932188134525, 1.2928932188134525, 0.2928932188134525), new Point(2, 2, 1));
        final var exp43 = List.of(new Point(1.5, 1.5, 0), new Point(1.5, 1.5, 1));
        final var exp44 = List.of(new Point(2, 2, 0), new Point(2, 2, 1));
        final var exp51 = List.of(new Point(1.5, 2.5, 1));
        final var exp52 = List.of(new Point(3, 2, 0.5));
        final var exp53 = List.of(new Point(2.5, 2.5, 1));
        final var exp54 = List.of(new Point(1.5, 1.5, 1));
        // ============ Equivalence Partitions Tests ==============

        // Group 0: ray intersect the tube but not the cylinder - acute angle
        //TC01 ray intersected in acute angle - over the cylinder (0 points)
        assertNull(cylinder.findIntersections(new Ray(v111, p011)), "TC01 failed");

        //TC02 ray intersected in acute angle  - over the cylinder (0 point)
        assertNull(cylinder.findIntersections(new Ray(v111, p15152)), "TC02 failed");

        //TC03 ray intersected in acute angle but starts after the tube (0 points)
        assertNull(cylinder.findIntersections(new Ray(v111, p440)), "TC03 failed");

        //TC04 ray intersect the two bases in an acute angle
        assertEquals(exp04, cylinder.findIntersections(new Ray(v112, p11_1)), "TC04 failed");

        //Grope 1: ray intersect the tube in a straight angle, but not the cylinder

        //TC11 ray intersect the tube twice but is too high for the cylinder
        assertNull(cylinder.findIntersections(new Ray(v110, p112)), "TC11 failed");

        //TC12 ray intersect the tube once but is too high for the cylinder
        assertNull(cylinder.findIntersections(new Ray(v110, p15152)), "TC12 failed");

        //Group 2: the ray (or its continuation) intersects the cylinder twice,
        // once in one of the bases and once on the tube
        // the first point is on the base

        //TC21 two intersection points
        assertEquals(exp21, cylinder.findIntersections(new Ray(v111, p11_1)), "TC21 failed");

        //TC22 one intersection point
        assertEquals(exp22, cylinder.findIntersections(new Ray(v111, p1515_05)), "TC22 failed");

        //TC23 no intersection point
        assertNull(cylinder.findIntersections(new Ray(v111, p331)), "TC23 failed");

        //Group 3: the ray (or its continuation) intersects the cylinder twice,
        // once in one of the bases and once on the tube
        // the first point is on the tube

        //TC31 two intersection points
        assertEquals(exp31, cylinder.findIntersections(new Ray(v111, p110)), "TC31 failed");

        //TC32 one intersection point
        assertEquals(exp32, cylinder.findIntersections(new Ray(v111, p151505)), "TC31 failed");

        //TC23 no intersection point
        assertNull(cylinder.findIntersections(new Ray(v111, p332)), "TC23 failed");

        // =============== Boundary Values Tests ==================

        //Grope 4: the same direction vector for the ray and the axis

        //TC41 ray is outside the cylinder
        assertNull(cylinder.findIntersections(new Ray(v001, p11_1)), "TC41 failed");
        //TC42 ray is on the cylinder
        assertNull(cylinder.findIntersections(new Ray(v001, p12_1)), "TC41 failed");
        //TC43 ray is inside the cylinder
        assertEquals(exp43, cylinder.findIntersections(new Ray(v001, p1515_05)), "TC43 failed");
        //TC44 ray is on the axis
        assertEquals(exp44, cylinder.findIntersections(new Ray(v001, p22_05)), "TC44   failed");

        //Group 5: starts on the cylinder
        //TC51 on the tube, acute angle
        assertEquals(exp51, cylinder.findIntersections(new Ray(v111, p1205)), "TC51 failed");
        //TC52 on the tube, straight angle
        assertEquals(exp52, cylinder.findIntersections(new Ray(v100, p1205)), "TC52 failed");
        //TC53 on the base, acute angle
        assertEquals(exp53, cylinder.findIntersections(new Ray(v111, p15150)), "TC51 failed");
        //TC54 on the base, straight angle
        assertEquals(exp54, cylinder.findIntersections(new Ray(v001, p15150)), "TC54 failed");
    }
}