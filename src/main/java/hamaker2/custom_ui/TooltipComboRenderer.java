/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamaker2.custom_ui;

import java.awt.*;
import javax.swing.*;

/**
 * Class to render tooltips for items in a ComboBox
 * @author aschauer
 */
public class TooltipComboRenderer<E> extends JLabel implements ListCellRenderer<E> {

    public TooltipComboRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            String tooltip = ((TooltipComboBoxModel) list.getModel()).getTooltip(index);
            if (index >= 0) {
                list.setToolTipText(tooltip);
            }
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setText((value == null) ? "" : value.toString());

        return this;
    }
}
