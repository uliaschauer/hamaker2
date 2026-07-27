/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

import com.jogamp.opengl.util.gl2.GLUT;
import hamaker2.Project.PlotQuantity;
import java.awt.Color;
import java.text.DecimalFormat;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 *
 * @author uli
 */
public class Plot3D {

    /**
     * Draw a 2D plot of the current project into the given drawable with mouse
     * at x and y positions
     *
     * @param drawable Drawable to plot into
     * @param project Project to be plotted
     * @param trans_x X translation
     * @param trans_y Y translation
     * @param scale scaling (zoom) factor
     * @param trackball Rotation description via trackball
     */
    public static void plot(GLAutoDrawable drawable, Project project, double trans_x, double trans_y, double scale, Trackball trackball) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        //setup lighting etc
        gl.glLoadIdentity();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        float lightPosition[] = {-1.0f, -1.0f, -1.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPosition, 0);
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glShadeModel(GL2.GL_SMOOTH);

        //set the viewport
        gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());

        //get model dimensions
        Vector3D min = new Vector3D(project.getPrimaryAxisMin(), project.getSecondaryAxisMin(), project.getInteractionAxisMin());
        Vector3D max = new Vector3D(project.getPrimaryAxisMax(), project.getSecondaryAxisMax(), project.getInteractionAxisMax());

        //setup factor so that all graphics fall within -1 to +1
        Vector3D factor = (max.subtractVector(min)).elementwiseInverse();

        //scale min and max accordingly
        min = min.elementwiseMultiply(factor);
        max = max.elementwiseMultiply(factor);

        //setup the view
        setupView((max.subtractVector(min)).maxElement(), drawable, gl, trans_x, trans_y, scale, trackball, (min.addVector(max)).multiplyWithScalar(0.5));

        //draw the x axis
        drawAxes(gl, min, max);

        //enable the z range clipping planes
        enableClipPlanes(min, max, gl);

        //draw the surface
        drawSurface(gl, project, factor);
        drawGrid(gl, project, factor);

        //disable the clipping planes
        disableClipPlanes(gl);

        //draw colored limiter planes
        drawAxisPlanes(gl, project, min, max, factor);

        //draw axes
        drawAxisTitles(gl, min, max, glut, project, scale);
        drawAxisScale(project, gl, factor, min, glut, scale);

        gl.glDisable(GL2.GL_DEPTH_TEST);

    }

    private static void drawAxes(GL2 gl, Vector3D min, Vector3D max) {
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(1.0, 0.0, 0.0);
        gl.glVertex3d(min.getX(), min.getY(), 0.0);
        gl.glVertex3d(max.getX(), min.getY(), 0.0);

        gl.glColor3d(0.0, 1.0, 0.0);
        gl.glVertex3d(min.getX(), min.getY(), 0.0);
        gl.glVertex3d(min.getX(), max.getZ(), 0.0);

        gl.glColor3d(0.0, 0.0, 1.0);
        gl.glVertex3d(min.getX(), min.getY(), min.getZ());
        gl.glVertex3d(min.getX(), min.getY(), max.getZ());
        gl.glEnd();

    }

    private static void drawSurface(GL2 gl, Project data, Vector3D factor) {
        //draw the plot
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

        float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_shininess[] = {100.0f};
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, mat_shininess, 0);

        //ArrayList points = data.getPlotPoints();
        for (int s = 0; s < data.getSeriesCount(); s++) {
            //ArrayList serie = (ArrayList) points.get(s);
            Color c = data.getSerie(s).getColor();
            if (data.getSerie(s).getVisible()) {
                gl.glBegin(GL2.GL_QUADS);
                for (int x = 0; x < data.getPrimary3DPlotPointCount(s) - 1; x++) {

                    for (int y = 0; y < data.getSecondary3DPlotPointCount(s) - 1 && y < data.getSecondary3DPlotPointCount(s) - 1; y++) {

                        Vector3D p1 = (data.get3DPlotPoint(s, x, y)).elementwiseMultiply(factor);
                        Vector3D p2 = (data.get3DPlotPoint(s, x, y + 1)).elementwiseMultiply(factor);
                        Vector3D p3 = (data.get3DPlotPoint(s, x + 1, y)).elementwiseMultiply(factor);
                        Vector3D p4 = (data.get3DPlotPoint(s, x + 1, y + 1)).elementwiseMultiply(factor);

                        Vector3D normal = getNormal(data, s, x, y, factor);

                        gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());

                        gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, c.getAlpha() / 255.0);

                        gl.glVertex3d(p1.getX(), p1.getY(), p1.getZ());
                        gl.glVertex3d(p2.getX(), p2.getY(), p2.getZ());
                        gl.glVertex3d(p4.getX(), p4.getY(), p4.getZ());
                        gl.glVertex3d(p3.getX(), p3.getY(), p3.getZ());
                    }
                }
                gl.glEnd();
            }
        }

        gl.glDisable(GL2.GL_LIGHTING);
    }

    private static Vector3D getNormal(Project data, int s, int x, int y, Vector3D factor) {

        //create vectors initialized to zero
        Vector3D vxm = new Vector3D();
        Vector3D vxp = new Vector3D();
        Vector3D vym = new Vector3D();
        Vector3D vyp = new Vector3D();

        Vector3D here = data.get3DPlotPoint(s, x, y);

        //now get the valid points
        if (x > 0) {
            vxm = (data.get3DPlotPoint(s, x - 1, y).subtractVector(here)).elementwiseMultiply(factor);
        }
        if (y > 0) {
            vym = (data.get3DPlotPoint(s, x, y - 1).subtractVector(here)).elementwiseMultiply(factor);
        }
        if (x < data.getPrimary3DPlotPointCount(s) - 1) {
            vxp = (data.get3DPlotPoint(s, x + 1, y).subtractVector(here)).elementwiseMultiply(factor);
        }
        if (y < data.getSecondary3DPlotPointCount(s) - 1) {
            vyp = (data.get3DPlotPoint(s, x, y + 1).subtractVector(here)).elementwiseMultiply(factor);
        }

        Vector3D n1 = vxm.cross(vym);
        Vector3D n2 = vyp.cross(vxm);
        Vector3D n3 = vxp.cross(vyp);
        Vector3D n4 = vym.cross(vxp);

        Vector3D n = n1.addVector(n2).addVector(n3).addVector(n4);
        n.normalize();

        return n;
    }

    private static void drawGrid(GL2 gl, Project data, Vector3D factor) {
        //now draw the grid on top
        int kGridStep = 20;
        for (int s = 0; s < data.getSeriesCount(); s++) {

            if (data.getSerie(s).getVisible()) {
                Color c = data.getSerie(s).getGridColor();
                gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, c.getAlpha() / 255.0);

                //first draw the lines with x=const
                for (int x = 0; x < data.getPrimary3DPlotPointCount(s); x += kGridStep) {
                    gl.glBegin(GL2.GL_LINE_STRIP);
                    for (int y = 0; y < data.getSecondary3DPlotPointCount(s); y++) {
                        Vector3D point = (data.get3DPlotPoint(s, x, y)).elementwiseMultiply(factor);
                        gl.glVertex3d(point.getX(), point.getY(), point.getZ());
                    }
                    gl.glEnd();
                }

                //second draw the lines with y=const
                for (int y = 0; y < data.getSecondary3DPlotPointCount(s); y += kGridStep) {
                    gl.glBegin(GL2.GL_LINE_STRIP);
                    for (int x = 0; x < data.getPrimary3DPlotPointCount(s); x++) {
                        Vector3D point = (data.get3DPlotPoint(s, x, y)).elementwiseMultiply(factor);
                        gl.glVertex3d(point.getX(), point.getY(), point.getZ());
                    }
                    gl.glEnd();
                }
            }
        }
    }

    private static void setupView(double max_extent, GLAutoDrawable drawable, GL2 gl, double trans_x, double trans_y, double scale, Trackball trackball, Vector3D center) {
        //compute the view frustum
        double left = -2 * max_extent;
        double right = 2 * max_extent;
        double bottom = -2 * max_extent;
        double top = 2 * max_extent;
        double near = -10 * max_extent;
        double far = 10 * max_extent;
        double aspect = drawable.getSurfaceWidth() / (double) drawable.getSurfaceHeight();
        if (aspect < 1) {
            bottom /= aspect;
            top /= aspect;
        } else {
            left *= aspect;
            right *= aspect;
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        //apply the transform
        gl.glLoadIdentity();
        gl.glTranslated(trans_x, trans_y, 0.0);
        gl.glScaled(scale, scale, scale);
        gl.glMultMatrixd(trackball.getOrientation(), 0);
        gl.glTranslated(-center.getX(), -center.getY(), -center.getZ());
    }

    private static void drawAxisPlanes(GL2 gl, Project data, Vector3D min, Vector3D max, Vector3D factor) {
        //plot axis planes
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glColor4d(0.0, 0.0, 1.0, 0.2);
        gl.glVertex3d(min.getX(), min.getY(), 0.0);
        gl.glVertex3d(max.getX(), min.getY(), 0.0);
        gl.glVertex3d(max.getX(), max.getY(), 0.0);
        gl.glVertex3d(min.getX(), max.getY(), 0.0);

        gl.glColor4d(1.0, 0.0, 0.0, 0.2);
        gl.glVertex3d(min.getX(), min.getY(), min.getZ());
        gl.glVertex3d(max.getX(), min.getY(), min.getZ());
        gl.glVertex3d(max.getX(), min.getY(), max.getZ());
        gl.glVertex3d(min.getX(), min.getY(), max.getZ());

        gl.glColor4d(0.0, 1.0, 0.0, 0.2);
        gl.glVertex3d(min.getX(), min.getY(), min.getZ());
        gl.glVertex3d(min.getX(), min.getY(), max.getZ());
        gl.glVertex3d(min.getX(), max.getY(), max.getZ());
        gl.glVertex3d(min.getX(), max.getY(), min.getZ());

        gl.glEnd();

        //grid on planes
        //xy plane
        double log = Math.log10(data.getPrimaryAxisMax() - data.getPrimaryAxisMin());
        double floor = Math.floor(log);
        double frac = log - floor;
        double x_tick_step = Math.pow(10, floor);
        if (frac
                <= Math.log10(
                        2) + 1e-6) {
            x_tick_step = Math.pow(10, floor - 1);
        }
        double x_tick;

        if (data.getPrimaryAxisMin()
                >= 0) {
            x_tick = x_tick_step;
        } else {
            x_tick = -10 * x_tick_step;
        }

        gl.glColor4d(0.5, 0.5, 0.9, 1.0);

        while (x_tick < data.getPrimaryAxisMax()) {
            if (x_tick > data.getPrimaryAxisMin()) {
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(x_tick * factor.getX(), min.getY(), 0.0);
                gl.glVertex3d(x_tick * factor.getX(), max.getY(), 0.0);
                gl.glEnd();
            }
            x_tick += x_tick_step;
        }
        log = Math.log10(data.getSecondaryAxisMax() - data.getSecondaryAxisMin());
        floor = Math.floor(log);
        frac = log - floor;
        double y_tick_step = Math.pow(10, floor);
        if (frac
                <= Math.log10(
                        2) + 1e-6) {
            y_tick_step = Math.pow(10, floor - 1);
        }
        double y_tick;

        if (data.getSecondaryAxisMin()
                >= 0) {
            y_tick = y_tick_step;
        } else {
            y_tick = -10 * y_tick_step;
        }

        while (y_tick < data.getSecondaryAxisMax()) {
            if (y_tick > data.getSecondaryAxisMin()) {
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(min.getX(), y_tick * factor.getY(), 0.0);
                gl.glVertex3d(max.getX(), y_tick * factor.getY(), 0.0);
                gl.glEnd();
            }
            y_tick += y_tick_step;
        }
        //xz and yz plane
        log = Math.log10(data.getInteractionAxisMax() - data.getInteractionAxisMin());
        floor = Math.floor(log);
        frac = log - floor;
        double z_tick_step = Math.pow(10, floor);
        if (frac
                <= Math.log10(
                        2) + 1e-6) {
            z_tick_step = Math.pow(10, floor - 1);
        }
        double z_tick;

        if (data.getInteractionAxisMin()
                >= 0) {
            z_tick = z_tick_step;
        } else {
            z_tick = -10 * z_tick_step;
        }

        while (z_tick < data.getInteractionAxisMax()) {
            if (z_tick > data.getInteractionAxisMin()) {
                gl.glBegin(GL2.GL_LINES);
                gl.glColor4d(0.9, 0.5, 0.5, 1.0);
                gl.glVertex3d(min.getX(), min.getY(), z_tick * factor.getZ());
                gl.glVertex3d(max.getX(), min.getY(), z_tick * factor.getZ());

                gl.glColor4d(0.5, 0.9, 0.5, 1.0);
                gl.glVertex3d(min.getX(), min.getY(), z_tick * factor.getZ());
                gl.glVertex3d(min.getX(), max.getY(), z_tick * factor.getZ());
                gl.glEnd();
            }
            z_tick += z_tick_step;
        }
    }

    private static void enableClipPlanes(Vector3D min, Vector3D max, GL2 gl) {
        //setup clipping planes to limit the visible plot in the z range
        double[] plane1 = {0.0, 0.0, 1.0, -min.getZ()};
        double[] plane2 = {0.0, 0.0, -1.0, max.getZ()};
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, plane1, 0);
        gl.glEnable(GL2.GL_CLIP_PLANE0);
        gl.glClipPlane(GL2.GL_CLIP_PLANE1, plane2, 0);
        gl.glEnable(GL2.GL_CLIP_PLANE1);
    }

    private static void disableClipPlanes(GL2 gl) {
        //disable the clipping planes
        gl.glDisable(GL2.GL_CLIP_PLANE0);
        gl.glDisable(GL2.GL_CLIP_PLANE1);
    }

    private static void drawAxisTitles(GL2 gl, Vector3D min, Vector3D max, GLUT glut, Project data, double scale) {
        //gl.glDisable(GL2.GL_DEPTH_TEST);
        //axis titles
        gl.glColor3d(0.3, 0.3, 0.3);
        gl.glRasterPos3d(max.getX(), min.getY() - 0.05 / scale * 2.6, 0.0);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, data.getSelectedVariable1Name() + " [" + data.getSelectedVariable1Unit() + "]");

        gl.glRasterPos3d(min.getX() - 0.1 / scale * 2.6, max.getY(), 0.0);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, data.getSelectedVariable2Name() + " [" + data.getSelectedVariable2Unit() + "]");

        gl.glRasterPos3d(min.getX() - 0.1 / scale * 2.6, min.getY() - 0.05 / scale * 2.6, max.getZ() + 0.05);
        if (data.getPlotQuantity() == PlotQuantity.Potential) {
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Potential [kT]");
        } else if (data.getPlotQuantity() == PlotQuantity.Force_neg || data.getPlotQuantity() == PlotQuantity.Force_pos) {
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Force [kT/nm]");
        } else {
            System.out.println("Error with plot quantity!");
        }
    }

    private static void drawAxisScale(Project data, GL2 gl, Vector3D factor, Vector3D min, GLUT glut, double scale) {
        //x axis
        DecimalFormat xscale_format = data.getSelectedVariable1().getFormat();
        double xscale_factor = data.getSelectedVariable1().getDisplayUnitFactor();

        double log = Math.log10(data.getPrimaryAxisMax() - data.getPrimaryAxisMin());
        double floor = Math.floor(log);
        double frac = log - floor;
        double x_tick_step = Math.pow(10, floor);
        double x_tick;
        if (frac <= Math.log10(2) + 1e-6) {
            x_tick_step = Math.pow(10, floor - 1);
        }

        if (data.getPrimaryAxisMin() >= 0) {
            x_tick = x_tick_step;
        } else {
            x_tick = -10 * x_tick_step;
        }

        while (x_tick < data.getPrimaryAxisMax()) {
            if (x_tick > data.getPrimaryAxisMin()) {
                String text = xscale_format.format(x_tick * xscale_factor);
                gl.glRasterPos3d(x_tick * factor.getX(), min.getY() - 0.05 / scale * 2.6, 0.0);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, text);
            }
            x_tick += x_tick_step;
        }

        //y axis
        DecimalFormat yscale_format = data.getSelectedVariable2().getFormat();
        double yscale_factor = data.getSelectedVariable2().getDisplayUnitFactor();

        log = Math.log10(data.getSecondaryAxisMax() - data.getSecondaryAxisMin());
        floor = Math.floor(log);
        frac = log - floor;
        double y_tick_step = Math.pow(10, floor);
        double y_tick;
        if (frac
                <= Math.log10(
                        2) + 1e-6) {
            y_tick_step = Math.pow(10, floor - 1);
        }

        if (data.getSecondaryAxisMin()
                >= 0) {
            y_tick = y_tick_step;
        } else {
            y_tick = -10 * y_tick_step;
        }

        while (y_tick < data.getSecondaryAxisMax()) {
            if (y_tick > data.getSecondaryAxisMin()) {
                gl.glRasterPos3d(min.getX() - 0.1 / scale * 2.6, y_tick * factor.getY(), 0.0);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, yscale_format.format(y_tick * yscale_factor));
            }
            y_tick += y_tick_step;
        }
        log = Math.log10(data.getInteractionAxisMax() - data.getInteractionAxisMin());
        floor = Math.floor(log);
        frac = log - floor;
        double z_tick_step = Math.pow(10, floor);
        double z_tick;
        if (frac
                <= Math.log10(
                        2) + 1e-6) {
            z_tick_step = Math.pow(10, floor - 1);
        }

        if (data.getInteractionAxisMin()
                >= 0) {
            z_tick = z_tick_step;
        } else {
            z_tick = -10 * z_tick_step;
        }

        while (z_tick < data.getInteractionAxisMax()) {
            if (z_tick > data.getInteractionAxisMin()) {
                gl.glRasterPos3d(min.getX() - 0.1 / scale * 2.6, min.getY() - 0.05 / scale * 2.6, z_tick * factor.getZ());
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(z_tick));
            }
            z_tick += z_tick_step;
        }
    }

}
