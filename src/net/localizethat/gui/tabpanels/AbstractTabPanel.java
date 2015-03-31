/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui.tabpanels;

/**
 *
 * @author rpalomares
 */
public abstract class AbstractTabPanel extends javax.swing.JPanel {

    public AbstractTabPanel() {
        super();
        // addComponentListener(this);
    }

    public abstract void onTabPanelAdded();

    public abstract void onTabPanelRemoved();

}
