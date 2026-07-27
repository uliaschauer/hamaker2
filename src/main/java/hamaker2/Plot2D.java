/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 * Method to draw 2D plots
 *
 * @author uli
 */
public class Plot2D {

    /**
     * Draw a 2D plot of the current project into the given drawable with mouse
     * at x and y positions
     *
     * @param drawable Drawable to plot into
     * @param project Project to be plotted
     * @param mouseX X position of the mouse (for crosshairs)
     * @param mouseY Y position of the mouse (for crosshairs)
     */
    public static void plot(GLAutoDrawable drawable, Project project, int mouseX, int mouseY) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();

        gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());

        //define the margin in pixels to be left blank outside the axes
        final int kMargin = 20;

        //compute conversion ratios from pixel to physical data
        double x_pixel_to_physical = (drawable.getSurfaceWidth() - 2 * kMargin) / project.getPrimaryAxisLength();
        double y_pixel_to_physical = (drawable.getSurfaceHeight() - 2 * kMargin) / project.getInteractionAxisLength();

        //setup the projection
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        //double margin_x = kMargin / x_pixel_to_physical;
        //double margin_y = kMargin / y_pixel_to_physical;
        glu.gluOrtho2D(project.getPrimaryAxisMin() * x_pixel_to_physical - kMargin,
                project.getPrimaryAxisMax() * x_pixel_to_physical + kMargin,
                project.getInteractionAxisMin() * y_pixel_to_physical - kMargin,
                project.getInteractionAxisMax() * y_pixel_to_physical + kMargin);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        //apply the transform
        gl.glLoadIdentity();

        drawAxes(project, x_pixel_to_physical, y_pixel_to_physical, gl, glut);

        //draw the curve
        ArrayList<ArrayList<Vector3D>> series = project.getPlotPoints2D();
        for (int s = 0; s < series.size(); s++) {
            if (project.getSerie(s).getVisible()) {
                Color c = project.getSerie(s).getColor();
                gl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, c.getAlpha() / 255.0);
                gl.glBegin(GL2.GL_LINE_STRIP);
                for (Vector3D point : series.get(s)) {
                    gl.glVertex2d(point.getX() * x_pixel_to_physical, point.getY() * y_pixel_to_physical);
                }
                gl.glEnd();

                if (project.getSerie(s).getShowStability()) {
                    gl.glPushAttrib(GL2.GL_LINE_BIT);
                    gl.glEnable(GL2.GL_LINE_STIPPLE);
                    double barrier = project.getSerie(s).getRequiredBarrier();
                    gl.glLineStipple(3, (short) 0xAAAA);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex2d(project.getPrimaryAxisMin() * x_pixel_to_physical, barrier * y_pixel_to_physical);
                    gl.glVertex2d(project.getPrimaryAxisMax() * x_pixel_to_physical, barrier * y_pixel_to_physical);
                    gl.glEnd();
                    gl.glDisable(GL2.GL_LINE_STIPPLE);
                    gl.glPopAttrib();
                }
            }
        }

        ////////////////////////////////////////
        //draw the crosshair
        ////////////////////////////////////////
        //compute mouse pointer in openGL coordinate system
        double x3 = project.getPrimaryAxisMin() * x_pixel_to_physical + mouseX - kMargin;
        //double y3 = -m_mouseY + 0.5 * drawable.getHeight();
        double y3 = project.getInteractionAxisMax() * y_pixel_to_physical - mouseY + kMargin;
        boolean debugCrosshair = false;
        //draw it for checking
        if (debugCrosshair) {
            System.out.println("Mouse: (" + String.valueOf(x3) + ", " + String.valueOf(y3) + ")");
            gl.glColor4d(0.0, 0.0, 1.0, 1.0);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2d(x3 - 1, y3 - 1);
            gl.glVertex2d(x3 + 1, y3 - 1);
            gl.glVertex2d(x3 + 1, y3 + 1);
            gl.glVertex2d(x3 - 1, y3 + 1);
            gl.glEnd();
        }
        //find the visible curve within 20px closest to the mouse pointer
        double min_x = 0.0, min_y = 0.0;
        double min_dist_sqr = 1000000;
        int min_series = -1;
        for (int s = 0; s < series.size(); s++) {
            if (project.getSerie(s).getVisible()) {
                ArrayList serie = (ArrayList) series.get(s);
                for (int p = 0; p < serie.size() - 1; p++) {
                    Vector3D p1 = (Vector3D) serie.get(p);
                    Vector3D p2 = (Vector3D) serie.get(p + 1);
                    double x1 = p1.getX() * x_pixel_to_physical;
                    double y1 = p1.getY() * y_pixel_to_physical;
                    double x2 = p2.getX() * x_pixel_to_physical;
                    double y2 = p2.getY() * y_pixel_to_physical;

                    double u = ((x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1)) / ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
                    if (u >= 0 && u <= 1) {
                        double x = x1 + u * (x2 - x1);
                        double y = y1 + u * (y2 - y1);
                        double dist_sqr = (x3 - x) * (x3 - x) + (y3 - y) * (y3 - y);

                        if (dist_sqr < min_dist_sqr && dist_sqr < 400) {
                            min_dist_sqr = dist_sqr;
                            min_series = s;
                            min_x = x;
                            min_y = y;
                        }
                    }
                }
            }
        }

        //draw for checking
        if (min_series != -1) {
            if (debugCrosshair) {
                gl.glColor4d(1.0, 0.0, 0.0, 1.0);
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex2d(min_x - 1, min_y - 1);
                gl.glVertex2d(min_x + 1, min_y - 1);
                gl.glVertex2d(min_x + 1, min_y + 1);
                gl.glVertex2d(min_x - 1, min_y + 1);
                gl.glEnd();
            }

            //now get draw the crosshair at the mouse x position and the corresponding series y position
            ArrayList serie = (ArrayList) series.get(min_series);
            //find the segment mouse x is on
            double y = -100000.0;
            for (int p = 0; p < serie.size() - 1; p++) {
                Vector3D p1 = (Vector3D) serie.get(p);
                Vector3D p2 = (Vector3D) serie.get(p + 1);
                double x1 = p1.getX() * x_pixel_to_physical;
                double y1 = p1.getY() * y_pixel_to_physical;
                double x2 = p2.getX() * x_pixel_to_physical;
                double y2 = p2.getY() * y_pixel_to_physical;
                if (x3 >= x1 && x3 < x2) {
                    double a = (y2 - y1) / (x2 - x1);
                    double b = y1 - a * x1;
                    y = a * x3 + b;
                }
            }

            //if one was found then draw the crosshair
            if (y != -100000.0) {
                gl.glColor4d(0.2, 0.2, 0.2, 0.8);
                gl.glPushAttrib(GL2.GL_LINE_BIT);
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                gl.glLineStipple(3, (short) 0xAAAA);

                gl.glBegin(GL2.GL_LINES);
                //x line
                gl.glVertex2d(x3, 0);
                gl.glVertex2d(x3, y);
                //y line
                gl.glVertex2d(0, y);
                gl.glVertex2d(x3, y);
                gl.glEnd();
                gl.glDisable(GL2.GL_LINE_STIPPLE);
                gl.glPopAttrib();

                //draw the numeric values
                gl.glRasterPos2d(x3 + 5, y);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "(" + new DecimalFormat("0.00").format(x3 / x_pixel_to_physical * project.getSelectedVariable1().getDisplayUnitFactor()/*1E9*/) + ", " + new DecimalFormat("0.00").format(y / y_pixel_to_physical) + ")");
            }
        }
    }

    private static void drawAxes(Project project, double x_pixel_to_physical, double y_pixel_to_physical, GL2 gl, GLUT glut) {

        final int kTick_length = 3;

        //determine the axis crossing point (keep it on screen)
        double x_pos = Math.max(project.getPrimaryAxisMin() * x_pixel_to_physical, Math.min(0.0, project.getPrimaryAxisMax() * x_pixel_to_physical));
        double y_pos = Math.max(project.getInteractionAxisMin() * y_pixel_to_physical, Math.min(0.0, project.getInteractionAxisMax() * y_pixel_to_physical));

        //draw the axes
        gl.glColor3d(0.0, 0.0, 0.0);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(project.getPrimaryAxisMin() * x_pixel_to_physical, y_pos);
        gl.glVertex2d(project.getPrimaryAxisMax() * x_pixel_to_physical, y_pos);
        gl.glVertex2d(x_pos, project.getInteractionAxisMin() * y_pixel_to_physical);
        gl.glVertex2d(x_pos, project.getInteractionAxisMax() * y_pixel_to_physical);
        gl.glEnd();

        String xAxisLabel = project.getSelectedVariable1().getName() + " [" + project.getSelectedVariable1().getDisplayUnit() + "]";
        double xAxisDelta = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, xAxisLabel) / 2.0;
        int pos = -30;
        if (project.getInteractionAxisMin() >= 0.0) {
            pos = 8;
        }
        gl.glRasterPos2d(project.getPrimaryAxisMin() * x_pixel_to_physical + 0.5 * (project.getPrimaryAxisMax() * x_pixel_to_physical - project.getPrimaryAxisMin() * x_pixel_to_physical) - xAxisDelta, pos);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, xAxisLabel);

        //draw x marks
        DecimalFormat x_format = project.getSelectedVariable1().getFormat();
        double factor = project.getSelectedVariable1().getDisplayUnitFactor();
        double log = Math.log10(project.getPrimaryAxisMax() - project.getPrimaryAxisMin());
        double floor = Math.floor(log);
        double frac = log - floor;
        double tick_step = Math.pow(10, floor);
        if (frac <= Math.log10(2) + 1e-6) {
            tick_step = Math.pow(10, floor - 1);
        }
        double tick;
        if (project.getPrimaryAxisMin() >= 0) {
            tick = tick_step;
        } else {
            tick = -10 * tick_step;
        }

        while (tick < project.getPrimaryAxisMax()) {
            if (tick > project.getPrimaryAxisMin()) {
                //draw the tick
                String text = x_format.format(tick * factor);
                int shift = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_10, text) / 2;
                gl.glRasterPos2d(tick * x_pixel_to_physical - shift, y_pos - 15);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, text);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex2d(tick * x_pixel_to_physical, y_pos - kTick_length);
                gl.glVertex2d(tick * x_pixel_to_physical, y_pos + kTick_length);
                gl.glEnd();
            }
            tick += tick_step;
        }

        //draw y marks
        DecimalFormat y_format = project.getSelectedVariable1().getFormat();
        log = Math.log10(project.getInteractionAxisMax() - project.getInteractionAxisMin());
        floor = Math.floor(log);
        frac = log - floor;
        tick_step = Math.pow(10, floor);
        if (frac <= Math.log10(2) + 1e-6) {
            tick_step = Math.pow(10, floor - 1);
        }
        if (project.getInteractionAxisMin() >= 0) {
            tick = tick_step;
        } else {
            tick = -10 * tick_step;
        }

        while (tick < project.getInteractionAxisMax()) {
            if (tick > project.getInteractionAxisMin()) {
                //draw the tick
                String text = y_format.format(tick);
                //int shift = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_10, text) / 2;
                gl.glRasterPos2d(x_pos + 5, tick * y_pixel_to_physical - 3);
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, text);
                gl.glBegin(GL2.GL_LINES);
                //System.out.println(String.valueOf(x_pos * x_pixel_to_physical) + ", " + String.valueOf(tick * y_pixel_to_physical));
                gl.glVertex2d(x_pos - kTick_length, tick * y_pixel_to_physical);
                gl.glVertex2d(x_pos + kTick_length, tick * y_pixel_to_physical);
                gl.glEnd();
            }
            tick += tick_step;
        }
    }
}
