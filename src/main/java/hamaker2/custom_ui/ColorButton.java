/*
 * ColorButton.java
 *
 * Created on 14. august 2007, 07:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hamaker2.custom_ui;

import java.awt.Graphics;
import javax.swing.JButton;

/**
 * This class implements a button filled with its foreground color for color
 * choosing
 *
 * @author aschauer
 */
public class ColorButton extends JButton {

    /**
     * The routine to do the actual painting into the graphics context g
     *
     * @param g The graphics context to paint the button in
     */
    @Override
    protected void paintComponent(Graphics g) {
        //if the button is disabled paint it a bit brighter otherwise with the actual color
        if (!this.isEnabled()) {
            g.setColor(this.getForeground().brighter());
        } else {
            g.setColor(this.getForeground());
        }

        //draw a beveled rectangle filled with the chosen color
        g.fill3DRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, true);

        //draw a frame with the background color
        g.setColor(this.getBackground());
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    }
}
