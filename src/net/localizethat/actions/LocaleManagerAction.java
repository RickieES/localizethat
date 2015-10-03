/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.Main;
import net.localizethat.gui.tabpanels.L10nGuiManager;

/**
 * Opens a tab with the locale manager in the main window
 * @author rpalomares
 */
public class LocaleManagerAction extends AbstractAction {
    private static final String TITLE = "Locale Manager";
    private static final String DESCRIPTION = "Opens " + TITLE;
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_L;
    private L10nGuiManager l10nGuiMgr;

    /**
     * Action representing the launching of LocaleManager panel as a tab in main window
     */
    public LocaleManagerAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main.mainWindow.getStatusBar().setInfoText("Creating window, please wait...");
        if (l10nGuiMgr == null) {
            l10nGuiMgr = new L10nGuiManager();
        }
        Main.mainWindow.addTab(l10nGuiMgr, TITLE);
    }

}
