/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.tasks;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import net.localizethat.Main;
import net.localizethat.system.AppSettings;
import net.localizethat.util.gui.JStatusBar;

/**
 *
 * @author rpalomares
 */
public class SavePreferencesWorker extends SwingWorker<Boolean, Void> {
    private final JStatusBar statusBar;

    public SavePreferencesWorker() {
        statusBar = Main.mainWindow.getStatusBar();
    }

    @Override
    protected Boolean doInBackground() {
        AppSettings settings = Main.appSettings;
        boolean result = settings.save();

        return result;
    }

    @Override
    protected void done() {
        try {
            boolean result = get();
            statusBar.setInfoText("Preferences saved");
        } catch (InterruptedException | ExecutionException ex) {
            statusBar.logMessage(JStatusBar.LogMsgType.ERROR,
                    "Error saving preferences",
                    "Something has prevented from successfully saving preferences",
                    ex);
        }
    }

}
