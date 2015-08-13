/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import static javax.swing.Action.LARGE_ICON_KEY;
import javax.swing.ImageIcon;
import net.localizethat.Main;
import net.localizethat.gui.tabpanels.ExportProductPanel;
import net.localizethat.tasks.CreateEntityManagerTask;

/**
 * Opens a tab with the Export product panel in the main window
 * @author rpalomares
 */
public class ExportProductAction extends AbstractAction {
    private static final String TITLE = "Export Products";
    private static final String DESCRIPTION = "Opens " + TITLE + " panel";
    private static final String ICON = "view-refresh.png"; // TODO change icon
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_X;
    private ExportProductPanel exportProductPanel;

    /**
     * Action representing the launching of ExportProductPanel as a tab in main window
     */
    public ExportProductAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        // putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
        ImageIcon icon = new ImageIcon(ExportProductAction.class.getResource(
                    "/net/localizethat/resources/" + ICON));
        putValue(LARGE_ICON_KEY, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CreateEntityManagerTask cemt = new CreateEntityManagerTask();
        Thread t = new Thread(cemt);

        Main.mainWindow.getStatusBar().setInfoText("Creating window, please wait...");
        t.start();

        try {
            t.join();
            if (exportProductPanel == null) {
                exportProductPanel = new ExportProductPanel(cemt.getEntityManager());
            } else {
                exportProductPanel.setEntityManager(cemt.getEntityManager());
            }
            Main.mainWindow.addTab(exportProductPanel, TITLE);
            Main.mainWindow.getStatusBar().clearText();
        } catch (InterruptedException ex) {
            Logger.getLogger(ExportProductAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
