/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.gui.dialogs.AboutDialog;

/**
 * Implements an about action that display an About...
 * @author rpalomares
 */
public class AboutAction extends AbstractAction {
    private static final String TITLE = "About...";
    private static final String DESCRIPTION = "Opens About... dialog" + TITLE;
    private AboutDialog aboutDialog;

    public AboutAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog();
        }

        aboutDialog.setVisible(true);
    }

}
