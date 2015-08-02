/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util.gui;

import java.awt.Component;

/**
 * Interface for Component objects that want to be added to a ModalDialog object.
 * This interface defines a method so the ModalDialog object can add a reference to itself to
 * the Component object, which will be used by that one to report back to the ModalDialog
 * @author rpalomares
 * @param <C> a Component subclass instance, usually a JPanel, that will be displayed as the
 *              content of the Dialog
 * @param <D> a DialogDataObject subclass instance used to transfer data to and from C instance
 */
public interface ModalDialogComponent<C extends Component, D extends DialogDataObject> {
    /**
     * Sets a reference to the ModalDialog object containing this ModalDialogComponent
     * @param md the ModalDialog reference
     */
    public void setModalDialogReference(ModalDialog<C, D> md);
}
