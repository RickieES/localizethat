/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.tasks;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.localizethat.Main;

/**
 *
 * @author rpalomares
 */
public class CreateEntityManagerTask implements Runnable {
    private EntityManagerFactory emf;
    private EntityManager entityManager;

    @Override
    public void run() {
        emf = Main.emf;
        entityManager = emf.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
