/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import net.localizethat.gui.dialogs.PreferencesDialog;

/**
 * Implements a preference action that display the Preferences dialog
 * @author rpalomares
 */
public class PreferencesAction extends AbstractAction {
    private static final String TITLE = "Preferences";
    private static final String DESCRIPTION = "Opens Preferences dialog" + TITLE;
    private PreferencesDialog prefDialog;

    public PreferencesAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (prefDialog == null) {
            prefDialog = new PreferencesDialog();
        }

        prefDialog.setVisible(true);
    }

}
