/*
 * Trackball.java
 *
 * Created on February 28, 2007, 8:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hamaker2;

/**
 * Class that implements trackball rotation
 * @author uli
 */
public class Trackball {
    
    private double m_prev_x, m_prev_y;
    private double m_window_width, m_window_height, m_smaller;
    private double m_radius;
    private Matrix3D m_orientation;
    
    /**
     * Create a new instance of trackball with default radius of 0.8 times the smaller window dimension
     */
    public Trackball() {
        m_radius = 0.8;
        m_orientation = new Matrix3D();
    }
    
    /**
     * Reset the rotation matrix to identity
     */
    public void reset() {
        m_orientation = new Matrix3D();
    }
    
    /**
     * Return the radius of the trackball
     * @return Radius of the trackball
     */
    public double getRadius() {
        return m_radius;
    }
    
    /**
     * Set the radius of the trackball
     * @param radius Radius of the trackball
     */
    public void setRadius(double radius) {
        m_radius = radius;
    }
    
    /**
     * Inform trackball about window resizes
     * @param width New width of the window
     * @param height New height of the window
     */
    public void resizeWindow(double width, double height) {
        m_window_width = width;
        m_window_height = height;
        
        m_smaller = width<height?width:height;
    }
    
    /**
     * Inform the trackball about mouse down event
     * @param x X position of the click
     * @param y Y position of the click
     */
    public void mouseDown(double x, double y) {
        m_prev_x = (2 * x - m_window_width) / m_smaller;
        m_prev_y = (m_window_height - 2 * y) / m_smaller;
    }
    
    /**
     * Inform the trackball about mouse up event
     * @param x X position of the release
     * @param y Y position of the release
     */
    public void mouseUp(double x, double y) {
        m_prev_x = 0.0;
        m_prev_y = 0.0;
    }
    
    /**
     * Inform the trackball about mouse drag event
     * @param x X position of the cursor
     * @param y Y position of the cursor
     */
    public void mouseDrag(double x, double y) {
        double curr_x = (2 * x - m_window_width) / m_smaller;
        double curr_y = (m_window_height - 2 * y) / m_smaller;
        
        Vector3D from = projectOnSphere(new Vector3D(m_prev_x, m_prev_y, 0.0));
        Vector3D to = projectOnSphere(new Vector3D(curr_x, curr_y, 0.0));
        
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            m_orientation = m_orientation.multiplyWithMatrix(rotationFromDrag(from, to));
        }
        
        
        m_prev_x = curr_x;
        m_prev_y = curr_y;
    }
    
    /**
     * Return the orientation matrix
     * @return Orientation matrix
     */
    public double[] getOrientation() {
        double[] matrix = new double[4*4];
        
        matrix[0] = m_orientation.get(0, 0);
        matrix[1] = m_orientation.get(0, 1);
        matrix[2] = m_orientation.get(0, 2);
        matrix[3] = 0.0;
        
        matrix[4] = m_orientation.get(1, 0);
        matrix[5] = m_orientation.get(1, 1);
        matrix[6] = m_orientation.get(1, 2);
        matrix[7] = 0.0;
        
        matrix[8] = m_orientation.get(2, 0);
        matrix[9] = m_orientation.get(2, 1);
        matrix[10] = m_orientation.get(2, 2);
        matrix[11] = 0.0;
        
        matrix[12] = 0.0;
        matrix[13] = 0.0;
        matrix[14] = 0.0;
        matrix[15] = 1.0;
        
        return matrix;
    }
    
    private Vector3D projectOnSphere(Vector3D v) {
        Vector3D out = v;
        
        double rsqr = m_radius * m_radius;
        double dsqr = v.getX() * v.getX() + v.getY() * v.getY();
        
        if (dsqr <= rsqr) {
            out.setZ(Math.sqrt(rsqr - dsqr));
        }
        else {
            out.normalize();
            out = out.multiplyWithScalar(m_radius);
            out.setZ(0.0);
        }
        
        return out;
    }
    
    private Matrix3D rotationFromDrag(Vector3D from, Vector3D to) {
        Vector3D axis = to.unitCross(from);
        
        if (axis.length() < 1E-6) {
            axis.setX(1.0);
            axis.setY(0.0);
            axis.setZ(0.0);
        }
        
        double phi = Math.acos(to.dot(from) / (to.length() * from.length()));
        
        return new Matrix3D(axis, phi);
    }
}
