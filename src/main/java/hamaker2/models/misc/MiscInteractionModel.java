/*
 * MiscInteractionModel.java
 *
 * Created on March 24, 2007, 3:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hamaker2.models.misc;

import hamaker2.models.InteractionModel;

/**
 * Interface to identify misc interaction models
 * @author uli
 */
public interface MiscInteractionModel extends InteractionModel {
    public boolean getActive();
    public void setActive(boolean active);
}
