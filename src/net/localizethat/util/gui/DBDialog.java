/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util.gui;

import javax.persistence.EntityManager;

/**
 * Interface for dialogs dealing with database and therefore using an EntityManager
 * which needs to be closed when closing the dialog
 * @author rpalomares
 */
public interface DBDialog {

    /**
     * Returns the EntityManager instance used by the dialog
     * @return the EntityManager instance used by the dialog, or null if there is no one active
     */
    public EntityManager getEntityManager();

    /**
     * Refresh the EntityManager instance reference to the object used by the dialog
     * @param o the object to be refreshed in the EntityManager
     */
    public void refreshEntityManager(Object o);

    /**
     * Closes the EntityManager used by the dialog
     */
    public void closeEntityManager();
}
