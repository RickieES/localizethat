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
import net.localizethat.gui.tabpanels.UpdateProductPanel;
import net.localizethat.tasks.CreateEntityManagerTask;

/**
 * Opens a tab with the Update product panel in the main window
 * @author rpalomares
 */
public class UpdateProductsAction extends AbstractAction {
    private static final String TITLE = "Update Products";
    private static final String DESCRIPTION = "Opens " + TITLE + " panel";
    private static final String ICON = "product-update.png";
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_U;
    private static final long serialVersionUID = 1L;
    private UpdateProductPanel updateProductPanel;

    /**
     * Action representing the launching of UpdateProductPanel as a tab in main window
     */
    public UpdateProductsAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC,
                java.awt.event.InputEvent.CTRL_MASK));
        ImageIcon icon = new ImageIcon(UpdateProductsAction.class.getResource(
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
            if (updateProductPanel == null) {
                updateProductPanel = new UpdateProductPanel(cemt.getEntityManager());
            } else {
                updateProductPanel.setEntityManager(cemt.getEntityManager());
            }
            Main.mainWindow.addTab(updateProductPanel, TITLE);
            Main.mainWindow.getStatusBar().clearText();
        } catch (InterruptedException ex) {
            Logger.getLogger(UpdateProductsAction.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
}
