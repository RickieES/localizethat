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
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;

/**
 * This class provides helper methods to interact with LocaleContainer persistence.
 *
 * @author rpalomares
 */
public class LocaleContainerJPAHelper {
    private int transactMaxCount;
    private int transactCounter;
    private EntityManager em;
    private boolean isTransactionOpen;
    private LocaleFileJPAHelper lfHelper;
    private LocaleContentJPAHelper lcntHelper;

    private LocaleContainerJPAHelper() {
        // Empty private constructor to avoid isolated construction, instead of using
        // the JPAHelperBundle methods
    }

    LocaleContainerJPAHelper(EntityManager em, int transactMaxCount) {
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

    void setLocaleFileJPAHelper(LocaleFileJPAHelper lfjh) {
        this.lfHelper = lfjh;
    }

    void setLocaleContentJPAHelper(LocaleContentJPAHelper lcntjh) {
        this.lcntHelper = lcntjh;
    }

    void setTransactMaxCount(int transactMaxCount) {
        this.transactMaxCount = transactMaxCount;
    }

    void setEm(EntityManager em) {
        this.em = em;
    }


    /**
     * Creates a sibling of defaultTwin for the targetLocale, including all needed
     * parents up to either an existing LocaleContainer or to the base, referencing it
     * in that case from the associated LocalePath.
     * @param defaultTwin the LocaleContainer of the original locale
     * @param targetLocale the L10n for which we want to create the sibling
     * @param commitOnSuccess if true, sends a commit for the current transaction
     * @return true on success (this includes the case that a LocaleContainer for the
     * targetLocale already exists), false if something went wrong (like the defaultTwin
     * not being really the defaultTwin, ie., having itself a not null defaultTwin
     * property)
     */
    public boolean createRecursively(LocaleContainer defaultTwin, L10n targetLocale,
            boolean commitOnSuccess) {
        boolean result;
        LocaleContainer sibling;
        LocaleContainer defaultParent;
        LocaleContainer newSibling;
        Date opTimeStamp = new Date();

        // Only the real defaultTwin has no DefLocaleTwin; we can only process defaultTwins
        result = (defaultTwin.getDefLocaleTwin() == null);

        if (result) {
            // Let's find out the sibling of this level defaultTwin
            sibling = defaultTwin.getTwinByLocale(targetLocale);
            defaultParent = defaultTwin.getParent();
            // If no sibling
            if (sibling == null) {
                if (defaultParent != null) {
                    result = result && createRecursively(defaultParent, targetLocale,
                            false);
                } else {
                    // We have reached the base LocaleContainer, and if it does not
                    // have a sibling of targetLocale is because there is no LocalePath
                    // for that locale, and therefore we can't create the associated
                    // LocaleContainer
                    result = false;
                }

                if (result) {
                    // At this point, we know that defaultTwin is really the default
                    // twin, that it has no sibling of the targetLocale and that we have
                    // both a parent of defaultTwin and a parent for the targetLocale
                    // (otherwise, ther recursive call would have returned false)

                    // So, we create a new LocaleContainer with the same name and a parent
                    // which is a sibling of defaultTwin parent for the target locale

                    if (!em.getTransaction().isActive()) {
                        em.getTransaction().begin();
                    }

                    newSibling = new LocaleContainer(defaultTwin.getName(),
                            defaultParent.getTwinByLocale(targetLocale));
                    newSibling.setCreationDate(opTimeStamp);
                    newSibling.setDefLocaleTwin(defaultTwin);
                    newSibling.setL10nId(targetLocale);
                    newSibling.setLastUpdate(opTimeStamp);

                    // Connect the parent with newSibling
                    defaultParent.getTwinByLocale(targetLocale).addChild(newSibling);

                    // Conect defaultTwin and newSibling between them, and with the rest
                    // of twins
                    for(LocaleContainer lcTwin : defaultTwin.getTwins()) {
                        newSibling.addTwin(lcTwin);
                        lcTwin.addTwin(newSibling);
                    }
                    newSibling.addTwin(defaultTwin);
                    defaultTwin.addTwin(newSibling);

                    em.persist(newSibling);

                    if (commitOnSuccess) {
                        em.getTransaction().commit();
                    }
                }
            }
        }
        return result;
    }

    public boolean removeRecursively(LocaleContainer lc) {
        return removeRecursively(lc, 0);
    }

    private boolean removeRecursively(LocaleContainer lc, int depth) {
        // boolean result = (lc.getTwins().isEmpty());
        boolean result = true;

        if (!result) {
            return result;
        }

        try {
            if (transactCounter == 0 && !em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }

            // Ensure that the EntityManager is managing the LocaleContainer to be removed
            lc = em.merge(lc);

            // Walk through the list of children and remove them recursively from the DB, and
            // also from the children List
            for (Iterator<LocaleContainer> iterator = lc.getChildren().iterator(); iterator.hasNext();) {
                LocaleContainer child = iterator.next();
                child = em.merge(child);
                iterator.remove();
                result = removeRecursively(child, depth + 1);
                if (!result) {
                    break;
                }
            }

            // LocaleContainer have two kind of children, so we need to repeat the process with
            // LocaleFile children
            if (result) {
                for(Iterator<LocaleFile> iterator = lc.getFileChildren().iterator(); iterator.hasNext(); ) {
                    LocaleFile lfChild = iterator.next();
                    lfChild = em.merge(lfChild);
                    iterator.remove();
                    result = lfHelper.removeRecursively(lfChild);
                    if (!result) {
                        break;
                    }
                }
            }

            // Now we need to update the twins to remove lc from their twins lists
            if (result) {
                // The next line should be redundant with the for loop below, but
                // it could be that some entries in the DB might miss the default
                // twin being included in the twins collection
                if (lc.getDefLocaleTwin() != null) {
                    lc.getDefLocaleTwin().removeTwin(lc);
                }

                for (LocaleContainer lcTwin : lc.getTwins()) {
                    lcTwin = em.merge(lcTwin);
                    result = result && lcTwin.removeTwin(lc);
                    if (!result) {
                        break;
                    }
                }
            }

            // If lc is a default twin (thus, not having a default twin itself), then
            // we must remove the twins before removing lc itself
            if (result && (lc.getDefLocaleTwin() == null)) {
                for(Iterator<LocaleContainer> iterator = lc.getTwins().iterator(); iterator.hasNext(); ) {
                    LocaleContainer lcTwin = iterator.next();
                    lcTwin = em.merge(lcTwin);
                    iterator.remove();
                    result = removeRecursively(lcTwin);
                    if (!result) {
                        break;
                    }
                }
            }

            if (result) {
                lc.setDefLocaleTwin(null);

                LocaleContainer parent = (LocaleContainer) lc.getParent();
                if (parent != null) {
                    // Only if the node has a parent that it is not being removed, we
                    // remove this container as a child from it: if not, the upper call
                    // will take care of it
                    if (depth == 0) {
                        parent.removeChild(lc);
                    }
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
