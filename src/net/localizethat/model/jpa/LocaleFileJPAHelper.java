/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model.jpa;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import net.localizethat.Main;
import net.localizethat.model.ImageFile;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.ParseableFile;
import net.localizethat.model.TextFile;

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

    /**
     * Removes the LocaleFile lf from database and in-memory structure.
     * 
     * The "recursively" part comes because this method takes care of all internal
     * references to contents, like LocaleContent collection, or BLOB/CLOB content,
     * and to keep naming scheme consistent with LocaleContainerJPAHelper.
     * 
     * @param lf The LocaleFile to be removed
     * @return true if the operation ended successfully
     */
    public boolean removeRecursively(LocaleFile lf) {
        boolean result = true;

        try {
            if (transactCounter == 0 && !em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }

            // Ensure that the EntityManager is managing the LocaleFile to be removed
            lf = em.merge(lf);
            result = true;
            switch (lf.getClass().getName()) {
                case "DtdFile":
                case "PropertiesFile":
                    ((ParseableFile) lf).setFileLicense(null);
                    for (Iterator<LocaleContent> iterator = lf.getChildren().iterator();
                            iterator.hasNext();) {
                        LocaleContent child = iterator.next();
                        child = em.merge(child);
                        iterator.remove();
                        child.setParent(null);
                        em.remove(child);
                        if (!result) {
                            break;
                        }
                    }
                    break;
                case "ImageFile":
                    ((ImageFile) lf).clearImageData();
                    break;
                case "TextFile":
                    ((TextFile) lf).clearFileContent();
                    break;
                default:
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
                if (transactCounter > transactMaxCount) {
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

        if (em.isJoinedToTransaction() && transactCounter > 0) {
            em.getTransaction().commit();
        }

        if (this.isTransactionOpen) {
            // If needed, let the EntityManager in the same status we got it
            // (i.e., with an open transaction)
            em.getTransaction().begin();
        }
        return result;
    }

}
