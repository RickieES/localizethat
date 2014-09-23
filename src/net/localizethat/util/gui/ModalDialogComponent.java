/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util.gui;

/**
 * Interface for Component objects that want to be added to a ModalDialog object.
 * This interface defines a method so the ModalDialog object can add a reference to itself to
 * the Component object, which will be used by that one to report back to the ModalDialog
 * @author rpalomares
 */
public interface ModalDialogComponent {
    /**
     * Sets a reference to the ModalDialog object containing this ModalDialogComponent
     * @param md the ModalDialog reference
     */
    public void setModalDialogReference(ModalDialog md);
}
