/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.actions;

import java.awt.event.ActionEvent;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import net.localizethat.Main;
import net.localizethat.gui.dialogs.AboutDialog;
import net.localizethat.util.gui.JStatusBar;

/**
 * Implements an about action that display an About...
 * @author rpalomares
 */
public class AboutAction extends AbstractAction {
    private static final String TITLE = "About...";
    private static final String DESCRIPTION = "Opens About... dialog" + TITLE;
    private AboutDialog aboutDialog;
    private JStatusBar statusBar;

    /**
     * Default constructor for wonderful and absolutely needed About Box dialog
     * action
     */
    public AboutAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (aboutDialog == null) {
            try {
                aboutDialog = new AboutDialog();
                aboutDialog.setVisible(true);
            } catch (URISyntaxException ex) {
                statusBar = Main.mainWindow.getStatusBar();
                statusBar.setErrorText("Can't open About dialog, there is a problem with the URL");
            }
        }

    }

}
