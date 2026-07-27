/*
 * Matrix3.java
 *
 * Created on February 28, 2007, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2;

/**
 * Class that implements simple 3x3 matrix functionality
 *
 * @author uli
 */
public class Matrix3D {

    private final double m_elements[][] = new double[3][3];

    /**
     * Initialize the matrix to the identity matrix
     */
    public Matrix3D() {
        m_elements[0][0] = 1.0;
        m_elements[0][1] = 0.0;
        m_elements[0][2] = 0.0;

        m_elements[1][0] = 0.0;
        m_elements[1][1] = 1.0;
        m_elements[1][2] = 0.0;

        m_elements[2][0] = 0.0;
        m_elements[2][1] = 0.0;
        m_elements[2][2] = 1.0;
    }

    /**
     * Initialize the matrix with the given elements
     *
     * @param e0 element[0][0]
     * @param e1 element[0][1]
     * @param e2 element[0][2]
     * @param e3 element[1][0]
     * @param e4 element[1][1]
     * @param e5 element[1][2]
     * @param e6 element[2][0]
     * @param e7 element[2][1]
     * @param e8 element[2][2]
     */
    public Matrix3D(double e0, double e1, double e2,
            double e3, double e4, double e5,
            double e6, double e7, double e8) {
        m_elements[0][0] = e0;
        m_elements[0][1] = e1;
        m_elements[0][2] = e2;

        m_elements[1][0] = e3;
        m_elements[1][1] = e4;
        m_elements[1][2] = e5;

        m_elements[2][0] = e6;
        m_elements[2][1] = e7;
        m_elements[2][2] = e8;
    }

    /**
     * Print the matrix to stdout
     */
    public void print() {
        System.out.println("----------------------------------------");
        System.out.println(m_elements[0][0] + ", " + m_elements[0][1] + ", " + m_elements[0][2]);
        System.out.println(m_elements[1][0] + ", " + m_elements[1][1] + ", " + m_elements[1][2]);
        System.out.println(m_elements[2][0] + ", " + m_elements[2][1] + ", " + m_elements[2][2]);
        System.out.println("----------------------------------------");
    }

    /**
     * Initialize the matrix from an axis vector and rotation angle description
     *
     * @param v Rotation axis
     * @param a Rotation angle
     */
    public Matrix3D(Vector3D v, double a) {
        Matrix3D m = new Matrix3D();

        Matrix3D s = new Matrix3D(0.0, -v.getZ(), v.getY(),
                v.getZ(), 0.0, -v.getX(),
                -v.getY(), v.getX(), 0.0);

        m = m.addMatrix(s.multiplyWithScalar(Math.sin(a))).addMatrix((s.multiplyWithMatrix(s)).multiplyWithScalar(1.0 - Math.cos(a)));

        for (int i = 0; i < 3; i++) {
            System.arraycopy(m.m_elements[i], 0, m_elements[i], 0, 3);
        }
    }

    /**
     * Return the element of the matrix
     *
     * @param row Row (0-2) to be returned
     * @param col Column (0-2) to be returned
     * @return The element or -1 if invalid indices
     */
    public double get(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            return m_elements[row][col];
        }

        return -1.0;
    }

    /**
     * Return the sum of this matrix with another matrix
     *
     * @param m Matrix to be added to the present one
     * @return Sum of the two matrices
     */
    public Matrix3D addMatrix(Matrix3D m) {
        Matrix3D result = new Matrix3D();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.m_elements[i][j] = this.m_elements[i][j] + m.m_elements[i][j];
            }
        }

        return result;
    }

    /**
     * Multiply all elements of this matrix with a scalar
     *
     * @param s Scalar number
     * @return Product of the matrix and the scalar
     */
    public Matrix3D multiplyWithScalar(double s) {
        Matrix3D result = new Matrix3D();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.m_elements[i][j] = this.m_elements[i][j] * s;
            }
        }

        return result;
    }

    /**
     * Multiply this matrix on the right with another matrix
     *
     * @param m Matrix to be multiplied onto the right of this matrix
     * @return Product of the two matrices
     */
    public Matrix3D multiplyWithMatrix(Matrix3D m) {
        Matrix3D result = new Matrix3D();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.m_elements[i][j] = 0.0;
                for (int k = 0; k < 3; k++) {
                    result.m_elements[i][j] += this.m_elements[i][k] * m.m_elements[k][j];
                }
            }
        }

        return result;
    }
}
