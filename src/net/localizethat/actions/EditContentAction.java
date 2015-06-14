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
import net.localizethat.gui.dialogs.ChooseTreePanel;
import net.localizethat.gui.dialogs.DialogDataObjects.ChooseTreeDDO;
import net.localizethat.gui.tabpanels.EditContentPanel;
import net.localizethat.util.gui.ModalDialog;

/**
 * Opens a tab with the general edit view, that shows a tree view of the paths,
 * an info panel, a table and a string edit panel
 * @author rpalomares
 */
public class EditContentAction extends AbstractAction {
    private static final String TITLE = "Edit Content";
    private static final String DESCRIPTION = "Opens the Edit Content Panel";
    private static final String ICON = "show-in-chrome.png";
    private static final int MNEMONIC = java.awt.event.KeyEvent.VK_H;
    private EditContentPanel editContentPanel;
    private ChooseTreePanel chooseTreePanel;
    private ChooseTreeDDO chooseTreeDDO;

    /**
     * Action representing the launching of LocaleManager panel as a tab in main window
     */
    public EditContentAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(MNEMONIC, java.awt.event.InputEvent.CTRL_MASK));
        ImageIcon icon = new ImageIcon(LocaleManagerAction.class.getResource(
                    "/net/localizethat/resources/" + ICON));
        putValue(LARGE_ICON_KEY, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Main.mainWindow.getStatusBar().setInfoText("Creating window, please wait...");
        Thread tarea = new Thread(new Runnable() {
            @Override
            public void run(){
                if (editContentPanel == null) {
                    editContentPanel = new EditContentPanel();
                }
                Main.mainWindow.addTab(editContentPanel, TITLE);
                Main.mainWindow.getStatusBar().clearText();
            }
        });

        chooseTreePanel = new ChooseTreePanel();
        chooseTreeDDO = new ChooseTreeDDO();
        ModalDialog<ChooseTreePanel, ChooseTreeDDO> ctpDialog = new ModalDialog<>(chooseTreePanel);
        ctpDialog.initDialog(chooseTreeDDO);
        tarea.start();
        ctpDialog.showDialog();
        chooseTreePanel = null;

        if (ctpDialog.getResult()) {
            // Not really needed, since ctpDialog uses the referenced object
            chooseTreeDDO = ctpDialog.getCollectedData();

            try {
                tarea.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(EditContentAction.class.getName()).log(Level.SEVERE, null, ex);
            }

            editContentPanel.setTargetLocale(chooseTreeDDO.getLocale());
            if (chooseTreeDDO.getProduct() != null) {
                editContentPanel.refreshTree(chooseTreeDDO.getProduct());
            } else {
                editContentPanel.refreshTree(chooseTreeDDO.getColPaths());
            }
        }

        }
        


}
