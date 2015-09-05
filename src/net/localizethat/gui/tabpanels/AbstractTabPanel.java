/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

/**
 * JPanel extension class that mandates subclasses to implement two methods
 * to be executed whenever the tabpanel is added or removed to the UI. Those
 * two methods work as "visual" initializers and finalizers
 * @author rpalomares
 */
public abstract class AbstractTabPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;

    public AbstractTabPanel() {
        super();
    }

    public abstract void onTabPanelAdded();

    public abstract void onTabPanelRemoved();

}
