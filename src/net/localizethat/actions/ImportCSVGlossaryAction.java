/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.Main;
import net.localizethat.gui.dialogs.ImportCSVGlossaryDialog;
import net.localizethat.util.gui.ModalDialog;

/**
 * Opens a tab with the Import Glossary from CSV panel in the main window
 * @author rpalomares
 */
public class ImportCSVGlossaryAction extends AbstractAction {
    private static final String TITLE = "Import from CSV";
    private static final String DESCRIPTION = "Opens " + TITLE + " dialog";
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_I;

    private ImportCSVGlossaryDialog csvImportPanel;

    /**
     * Action representing the launching of ImportCSVGlossaryDialog panel as a modal dialog
     */
    public ImportCSVGlossaryAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        // No accelerator for this action
        // putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        csvImportPanel = new ImportCSVGlossaryDialog();
        ModalDialog csvDialog = new ModalDialog(Main.mainWindow, csvImportPanel);
        csvDialog.showDialog();
        csvImportPanel = null;
    }
}
