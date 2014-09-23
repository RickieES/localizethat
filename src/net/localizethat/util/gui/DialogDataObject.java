/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util.gui;

import java.awt.Component;

/**
 * A data object to transfer data from/to ModalDialog instances
 * @author rpalomares
 */
public interface DialogDataObject {
    /**
     * Implementers of DialogDataObject should use this method to transfer data
     * from the DialogDataObject concrete instance to the GUI AWT Component (for instance, a JPanel)
     * This involves knowledge of the specific Component, so it is likely that a 1:1 relationship
     * will exist between DialogDataObject implementations and concrete Component panels used to
     * build ModalDialog instances
     * @param c a AWT Component with GUI controls that can benefit from prefilling data from the
     * DialogDataObject
     */
    public void transferTo(Component c);

    /**
     * Implementers of DialogDataObject should use this method to collect data
     * to the DialogDataObject concrete instance from the GUI AWT Component (for instance, a JPanel)
     * This involves knowledge of the specific Component, so it is likely that a 1:1 relationship
     * will exist between DialogDataObject implementations and concrete Component panels used to
     * build ModalDialog instances
     * @param c a AWT Component with GUI controls that can supply expected data by this
     * DialogDataObject instance
     */
    public void collectFrom(Component c);
}
