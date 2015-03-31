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
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;

/**
 * This class provides helper methods to interact with LocaleFile persistence.
 *
 * @author rpalomares
 */
public class LocaleFileJPAHelper {
    private static final int DEFAULT_TRANSACT_MAX_COUNT = 50;
    private int transactMaxCount;
    private int transactCounter;
    private final EntityManager em;
    private boolean isTransactionOpen;
    
    public LocaleFileJPAHelper(EntityManager em, int transactMaxCount) {
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

    public LocaleFileJPAHelper(EntityManager em) {
        this(em, LocaleFileJPAHelper.DEFAULT_TRANSACT_MAX_COUNT);
    }

    public boolean removeRecursively(LocaleFile lf) {
        return removeRecursively(lf, 0);
    }

    private boolean removeRecursively(LocaleFile lf, int depth) {
        boolean result = true;

        try {
            if (transactCounter == 0 && !em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }

            lf = em.merge(lf);
            for(LocaleContent child : lf.getChildren()) {
                // TODO remove the LocaleContent instance from persistence and memory
                // result = removeRecursively(child, depth + 1);
                if (!result) {
                    break;
                }
                lf.removeChild(child);
            }
            
            if (result) {
                lf.setDefLocaleTwin(null);

                LocaleContainer parent = lf.getParent();
                if (parent != null) {
                    parent.removeFileChild(lf);
                    lf.setParent(null);
                }

                em.remove(lf);
                transactCounter++;
                if ((transactCounter > transactMaxCount) && (depth > 0)) {
                    em.getTransaction().commit();
                    transactCounter = 0;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LocaleFileJPAHelper.class.getName()).log(Level.SEVERE, null, e);
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
