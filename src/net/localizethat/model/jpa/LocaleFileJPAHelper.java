/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model.jpa;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import net.localizethat.Main;
import net.localizethat.model.ImageFile;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LTContent;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.ParseableFile;
import net.localizethat.model.TextFile;

/**
 * This class provides helper methods to interact with LocaleFile persistence.
 *
 * @author rpalomares
 */
public class LocaleFileJPAHelper {
    private int transactMaxCount;
    private int transactCounter;
    private EntityManager em;
    private LocaleContainerJPAHelper lcHelper;
    private LocaleContentJPAHelper lcntHelper;
    private boolean isTransactionOpen;

    private LocaleFileJPAHelper() {
        // Empty private constructor to avoid isolated construction, instead of using
        // the JPAHelperBundle methods
    }

    LocaleFileJPAHelper(EntityManager em, int transactMaxCount) {
        transactCounter = 0;
        if (em == null) {
            this.em = Main.emf.createEntityManager();
            isTransactionOpen = false;
        } else {
            this.em = em;
            // Does the passed EntityManager have a transaction open?
            this.isTransactionOpen = this.em.isJoinedToTransaction();
        }
        this.transactMaxCount = transactMaxCount;
    }

    void setLocaleContainerJPAHelper(LocaleContainerJPAHelper lcjh) {
        this.lcHelper = lcjh;
    }

    void setLocaleContentJPAHelper(LocaleContentJPAHelper lcntjh) {
        this.lcntHelper = lcntjh;
    }

    /**
     * Creates a sibling of defaultTwin for the targetLocale, including all needed
     * parents up to either an existing LocaleContainer or to the base, referencing it
     * in that case from the associated LocalePath.
     * @param defaultTwin the LocaleFile of the original locale
     * @param targetLocale the L10n for which we want to create the sibling
     * @param commitOnSuccess if true, sends a commit for the current transaction
     * @return true on success (this includes the case that a LocaleFile for the
     * targetLocale already exists), false if something went wrong (like the defaultTwin
     * not being really the defaultTwin, ie., having itself a not null defaultTwin
     * property)
     */
    public boolean createRecursively(LocaleFile defaultTwin, L10n targetLocale,
            boolean commitOnSuccess) {
        boolean result;
        LocaleFile sibling;
        LocaleContainer defaultParent;
        LocaleFile newSibling;


        // Only the real defaultTwin has no DefLocaleTwin; we can only process defaultTwins
        result = (defaultTwin.getDefLocaleTwin() == null);

        if (result) {
            // Let's find out the sibling of this level defaultTwin
            sibling = defaultTwin.getTwinByLocale(targetLocale);
            defaultParent = defaultTwin.getParent();
            // If no sibling
            if (sibling == null) {
                if (defaultParent != null) {
                    result = result && lcHelper.createRecursively(defaultParent, targetLocale,
                            false);
                } else {
                    // We can't create a sibling of a LocaleFile that is not yet completely
                    // added to the datamodel (ie., an isolated LocaleFile with no parent)
                    result = false;
                }

                if (result) {
                    // At this point, we know that defaultTwin is really the default
                    // twin, that it has no sibling of the targetLocale and that we have
                    // both a parent of defaultTwin and a parent for the targetLocale
                    // (otherwise, ther recursive call would have returned false)
                    newSibling = LocaleFile.createFile(defaultTwin.getName(),
                            defaultParent.getTwinByLocale(targetLocale));
                    newSibling.setCreationDate(new Date());
                    newSibling.setDefLocaleTwin(defaultTwin);
                    newSibling.setL10nId(targetLocale);
                    newSibling.setLastUpdate(newSibling.getCreationDate());
                    em.persist(newSibling);
                    if (commitOnSuccess) {
                        em.getTransaction().commit();
                    }
                }
            }
        }

        /*
         * check if there is already a sibling of targetLocale
         * if not,
         *     if there is a not null parent,
         *         result = result && call createRecursively with the parent of defaultTwin
         *     else
         *         // we have reached the base LocaleContainer, and if it does not have a sibling
         *         // of targetLocale is because there is no LocalePath for it, and therefore we
         *         // can't create the associated LocaleContainer
         *         return false;
         *     endif
         *     find the sibling for targetLocale of the parent
         *     create a new LocaleContainer with the targetLocale and the parent sibling
         * endif
         *
        */
        return result;
    }

    /**
     * Removes the LocaleFile lf from database and in-memory structure.
     *
     * The "recursively" part comes because this method takes care of all internal
 references to contents, like LTContent collection, or BLOB/CLOB content,
 and to keep naming scheme consistent with LocaleContainerJPAHelper.
     *
     * @param lf The LocaleFile to be removed
     * @return true if the operation ended successfully
     */
    public boolean removeRecursively(LocaleFile lf) {
        boolean result = (lf.getTwins().isEmpty());

        if (!result) {
            return result;
        }

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
                    for (Iterator<LTContent> iterator = lf.getChildren().iterator();
                            iterator.hasNext();) {
                        LTContent child = iterator.next();
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
