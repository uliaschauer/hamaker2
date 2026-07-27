/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.custom_ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.DefaultComboBoxModel;

/**
 * Class to show tooltips for items in a ComboBox
 * @author uli
 * @param <E> Object of the ComboBox model
 */
public class TooltipComboBoxModel<E> extends DefaultComboBoxModel<E> {

    public TooltipComboBoxModel(final E items[]) {
        super(items);
    }
    
    /**
     * Get the tooltip text for the item at the given index
     * @param i Index of the item
     * @return Tooltip text for item at given index
     */
    public String getTooltip(int i) {
        E current = (E)getElementAt(i);
                
        try {
            Method m = current.getClass().getMethod("reference", (Class<?>[]) null);
            return (String)m.invoke(current, (Object[]) null);
            
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return "";
        }
    }
    
}
