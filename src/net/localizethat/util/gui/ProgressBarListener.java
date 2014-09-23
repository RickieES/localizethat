/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Property change listener that updates the JProgress of a JStatusBar based on a watched property (usually, the
 * progress property of a SwingWorker)
 * @author rpalomares
 */
public class ProgressBarListener implements PropertyChangeListener {
    private JStatusBar statusBar;
    private String observedProperty;

    /**
     * Default constructor made private to enforce creation supplying appropiate parameters
     */
    private ProgressBarListener() {
    }

    /**
     * Creates a progress bar listener for a supplied JStatusBar
     *
     * @param statusBar the JStatusBar which progress bar will be updated
     * @param observedProperty the property that this progress listener will watch
     * @throws NullPointerException if either supplied statusBar or observedProperty are null
     */
    public ProgressBarListener(JStatusBar statusBar, String observedProperty)
            throws NullPointerException {
        if (statusBar == null || observedProperty == null) {
            throw new NullPointerException();
        }
        this.statusBar = statusBar;
        this.observedProperty = observedProperty;
        this.statusBar.setProgress(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String eventProperty = evt.getPropertyName();
        if (this.observedProperty.equals(eventProperty)) {
            int newValue = (Integer) evt.getNewValue();
            this.statusBar.setProgress(newValue);
        }
    }
}
