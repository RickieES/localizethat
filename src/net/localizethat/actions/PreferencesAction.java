/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.localizethat.gui.dialogs.PreferencesDialog;
import net.localizethat.tasks.SavePreferencesWorker;

/**
 * Implements a preference action that display the Preferences dialog
 * @author rpalomares
 */
public class PreferencesAction extends AbstractAction {
    private static final String TITLE = "Preferences";
    private static final String DESCRIPTION = "Open Preferences dialog";
    private static final String ICON = "preferences.png";
    private PreferencesDialog prefDialog;

    public PreferencesAction() {
        super(TITLE);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);

        ImageIcon icon = new ImageIcon(PreferencesAction.class.getResource(
                    "/net/localizethat/resources/" + ICON));
        putValue(LARGE_ICON_KEY, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (prefDialog == null) {
            prefDialog = new PreferencesDialog();
        }

        prefDialog.showDialog();
        SavePreferencesWorker spw = new SavePreferencesWorker();
        spw.execute();
    }

}
