/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Static methods collection for GUI related operations
 *
 * @author rpalomares
 */
public class GuiUtils {

    /**
     * Returns a list of available Swing Look and Feels
     * @return a collection of installed Look And Feels
     */
    public static List<String> getAvailableLookAndFeels() {
        ArrayList<String> availableLookAndFeels = new ArrayList<>(5);

        for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
            availableLookAndFeels.add(lafInfo.getName());
        }
        return availableLookAndFeels;
    }

    /**
     * Sets the best possible Look and Feel, starting with the Look and Feel
     * given in the parameter
     * @param preferredLafName the preferred Look and Feel name
     * @return true if at least one Look and Feel could be set
     */
    public static boolean setBestAvailableLookAndFeel(String preferredLafName) {
        for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
            if (lafInfo.getName().equals(preferredLafName)) {
                try {
                    UIManager.setLookAndFeel(lafInfo.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
                    // UnsupportedLookAndFeelException, ClassNotFoundException,
                    // InstantiationException or IllegalAccessException could be
                    // thrown; in any case, we fallback to the cross-platform L&F
                    try {
                        Logger.getLogger(GuiUtils.class.getName()).log(Level.WARNING,
                                "L&F {0} couldn't be applied", preferredLafName);
                        UIManager.setLookAndFeel(
                                UIManager.getCrossPlatformLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e2) {
                        Logger.getLogger(GuiUtils.class.getName()).log(Level.SEVERE,
                                "Default Metal L&F couldn't be applied, execution can't continue");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
