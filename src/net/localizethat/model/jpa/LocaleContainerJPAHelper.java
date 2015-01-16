/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import net.localizethat.Main;
import net.localizethat.model.LocaleContainer;

/**
 * This class provides helper methods to interact with LocaleContainer persistence.
 *
 * @author rpalomares
 */
public class LocaleContainerJPAHelper {
    private static final int DEFAULT_TRANSACT_MAX_COUNT = 50;
    private int transactMaxCount;
    private int transactCounter;
    private final EntityManager em;
    private boolean isTransactionOpen;
    
    public LocaleContainerJPAHelper(EntityManager em, int transactMaxCount) {
        transactCounter = 0;
        if (em == null) {
            this.em = Main.emf.createEntityManager();
            isTransactionOpen = false;
        } else {
            this.em = em;
            // Does the passed EntityManager have a transaction open?
            isTransactionOpen = this.em.isJoinedToTransaction();
        }
        this.transactMaxCount = transactMaxCount;
    }

    public LocaleContainerJPAHelper(EntityManager em) {
        this(em, LocaleContainerJPAHelper.DEFAULT_TRANSACT_MAX_COUNT);
    }

    public boolean removeRecursively(LocaleContainer lc) {
        return removeRecursively(lc, 0);
    }

    private boolean removeRecursively(LocaleContainer lc, int depth) {
        boolean result = true;

        try {
            if (transactCounter == 0 && !em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }

            for(LocaleContainer child : lc.getChildren()) {
                result = removeRecursively(child, depth + 1);
                if (!result) {
                    break;
                }
                lc.removeChild(child);
            }
            
            if (result) {
                // TODO Iterate and remove LocaleFile children, once defined in LocaleContainer

                lc.setDefLocaleTwin(null);

                LocaleContainer parent = lc.getParent();
                if (parent != null) {
                    parent.removeChild(lc);
                    lc.setParent(null);
                }

                em.remove(lc);
                transactCounter++;
                if ((transactCounter > transactMaxCount) && (depth > 0)) {
                    em.getTransaction().commit();
                    transactCounter = 0;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LocaleContainerJPAHelper.class.getName()).log(Level.SEVERE, null, e);
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            result = false;
        }

        if (em.isJoinedToTransaction() && transactCounter > 0 && depth > 0) {
            em.getTransaction().commit();
        }

        if (this.isTransactionOpen && depth > 0) {
            // If needed, let the EntityManager in the same status we got it
            // (i.e., with an open transaction)
            em.getTransaction().begin();
        }
        return result;
    }

}
