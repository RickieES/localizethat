/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.Main;
import net.localizethat.gui.tabpanels.ChannelGuiManager;

/**
 * Opens a tab with the locale manager in the main window
 * @author rpalomares
 */
public class ChannelManagerAction extends AbstractAction {
    private static final String TITLE = "Channel Manager";
    private static final String DESCRIPTION = "Opens " + TITLE;
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_C;
    private ChannelGuiManager channelGuiMgr;

    /**
     * Action representing the launching of LocaleManager panel as a tab in main window
     */
    public ChannelManagerAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        // No accel key for this action, since it will not be used often
        // putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main.mainWindow.getStatusBar().setInfoText("Creating window, please wait...");
        if (channelGuiMgr == null) {
            channelGuiMgr = new ChannelGuiManager();
        }
        Main.mainWindow.addTab(channelGuiMgr, TITLE);
    }

}
