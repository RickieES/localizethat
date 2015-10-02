/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.Main;
import net.localizethat.gui.tabpanels.GlsEntryGuiManager;

/**
 * Opens a tab with the Glossary Entries manager in the main window
 * @author rpalomares
 */
public class GlsEntriesManagerAction extends AbstractAction {
    private static final String TITLE = "Glossary Entries Manager";
    private static final String DESCRIPTION = "Opens " + TITLE;
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_E;
    private GlsEntryGuiManager glsEntryGuiMgr;

    /**
     * Action representing the launching of the GlsEntriesManager panel as a tab in main window
     */
    public GlsEntriesManagerAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (glsEntryGuiMgr == null) {
            glsEntryGuiMgr = new GlsEntryGuiManager();
        }
        Main.mainWindow.addTab(glsEntryGuiMgr, TITLE);
    }

}
