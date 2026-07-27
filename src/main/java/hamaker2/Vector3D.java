/*
 * Vector3.java
 *
 * Created on February 28, 2007, 7:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hamaker2;

/**
 * A class that implements simple 3D vector maths
 * @author uli
 */
public class Vector3D {
    
    private double m_x, m_y, m_z;
    
    /**
     * Create a new zero vector
     */
    public Vector3D() {
        m_x = 0.0;
        m_y = 0.0;
        m_z = 0.0;
    }
    
    /**
     * Create a new vector with given x, y and z elements
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public Vector3D(double x, double y, double z) {
        m_x = x;
        m_y = y;
        m_z = z;
    }
    
    /**
     * Create a new vector with given x and y elements
     * @param x x component
     * @param y y component
     */
    public Vector3D(double x, double y) {
        m_x = x;
        m_y = y;
        m_z = 0.0;
    }
    
    /**
     * Set the x component
     * @param x x component
     */
    public void setX(double x) {
        m_x = x;
    }
    
    /**
     * Set the y component
     * @param y y component
     */
    public void setY(double y) {
        m_y = y;
    }
    
    /**
     * Set the y component
     * @param z z component
     */
    public void setZ(double z) {
        m_z = z;
    }
    
    /**
     * Return the x component
     * @return x component
     */
    public double getX() {
        return m_x;
    }
    
    /**
     * Return the y component
     * @return y component
     */
    public double getY() {
        return m_y;
    }
    
    /**
     * Return the z component
     * @return z component
     */
    public double getZ() {
        return m_z;
    }
    
    /**
     * Return the length of the vector
     * @return Length
     */
    public double length() {
        return Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
    }
    
    /**
     * Add a vector
     * @param v Vector to be added
     * @return Sum of the two vectors
     */
    public Vector3D addVector(Vector3D v) {
        return new Vector3D(m_x + v.getX(), m_y + v.getY(), m_z + v.getZ());
    }
    
    /**
     * Subtract a vector
     * @param v vector to be subtracted
     * @return Difference of the two vectors
     */
    public Vector3D subtractVector(Vector3D v) {
        return new Vector3D(m_x - v.getX(), m_y - v.getY(), m_z - v.getZ());
    }
    
    /**
     * Multiply vector by a scalar
     * @param s Scalar
     * @return Scaled vector
     */
    public Vector3D multiplyWithScalar(double s) {
        return new Vector3D(m_x * s, m_y * s, m_z * s);
    }
    
    /**
     * Multiply two vectors element by element
     * @param v Vector to be multiplied
     * @return Elementwise product of the two vectors
     */
    public Vector3D elementwiseMultiply(Vector3D v) {
        return new Vector3D(m_x * v.m_x, m_y * v.m_y, m_z * v.m_z);
    }
    
    /**
     * Invert all elements of the vector
     * @return Vector with inverted elements
     */
    public Vector3D elementwiseInverse() {
        return new Vector3D(1.0 / m_x, 1.0 / m_y, 1.0 / m_z);
    }
    
    /**
     * Return the longest dimension of the vector
     * @return Longest dimension
     */
    public double maxElement() {
        return Math.max(m_x, Math.max(m_y, m_z));
    }
    
    /**
     * Dot product with a given vector
     * @param v Vector to be multiplied
     * @return Dot product of the two vectors
     */
    public double dot(Vector3D v) {
        return m_x * v.getX() + m_y * v.getY() + m_z * v.getZ();
    }
    
    /**
     * Cross product with a given vector
     * @param v Vector to be multiplied
     * @return Cross product of the two vectors
     */
    public Vector3D cross(Vector3D v) {
        return new Vector3D(m_y * v.getZ() - m_z * v.getY(),
                           m_z * v.getX() - m_x * v.getZ(),
                           m_x * v.getY() - m_y * v.getX());
    }
    
    /**
     * Normalize the vector
     */
    public void normalize() {
        double length = length();
        if (length != 0.0) {
            double inv_length = 1.0 / length;
        
            m_x *= inv_length;
            m_y *= inv_length;
            m_z *= inv_length;
        }
    }
    
    /**
     * Perform cross product with given vector and normalize the result
     * @param v Vector to be multiplied
     * @return Normalized cross product
     */
    public Vector3D unitCross(Vector3D v) {
        Vector3D c = cross(v);
        
        c.normalize();
        return c;
    }
}
