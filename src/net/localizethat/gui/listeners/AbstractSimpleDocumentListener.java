/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.gui.listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This abstact class can be used to perform a single task both when text is inserted
 * or removed. This is useful when we want to trigger some task whenever the text in
 * a text field (in the broad sense, including JTextField, JTextArea, JTPasswordField
 * o JFormattedTextField. The task to accomplish must be implemented in subclasses
 * @author rpalomares
 */
public abstract class AbstractSimpleDocumentListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
        doTask(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        doTask(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // This isn't used in simple text fields
    }

    protected abstract void doTask(DocumentEvent e);
}
