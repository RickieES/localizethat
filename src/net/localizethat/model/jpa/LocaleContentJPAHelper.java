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
import net.localizethat.model.LTComment;
import net.localizethat.model.LTContent;
import net.localizethat.model.LTExternalEntity;
import net.localizethat.model.LTIniSection;
import net.localizethat.model.LTKeyValuePair;
import net.localizethat.model.LTLicense;
import net.localizethat.model.LTWhitespace;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;

/**
 * This class provides helper methods to interact with LTContent persistence.
 *
 * @author rpalomares
 */
public class LocaleContentJPAHelper {
    private int transactMaxCount;
    private int transactCounter;
    private EntityManager em;
    private LocaleContainerJPAHelper lcHelper;
    private LocaleFileJPAHelper lfHelper;
    private boolean isTransactionOpen;

    private LocaleContentJPAHelper() {
        // Empty private constructor to avoid isolated construction, instead of using
        // the JPAHelperBundle methods
    }

    LocaleContentJPAHelper(EntityManager em, int transactMaxCount) {
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

    void setLocaleFileJPAHelper(LocaleFileJPAHelper lfjh) {
        this.lfHelper = lfjh;
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
     * @param defaultTwin the LTContent of the original locale
     * @param targetLocale the L10n for which we want to create the sibling
     * @param commitOnSuccess if true, sends a commit for the current transaction
     * @return true on success (this includes the case that a LTContent for the
     * targetLocale already exists), false if something went wrong (like the defaultTwin
     * not being really the defaultTwin, ie., having itself a not null defaultTwin
     * property)
     */
    public boolean createRecursively(LocaleContent defaultTwin, L10n targetLocale,
            boolean commitOnSuccess) {
        boolean result;
        LocaleContent sibling;
        LocaleFile defaultParent;
        LocaleContent newSibling;
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
                    result = result && lfHelper.createRecursively(defaultParent, targetLocale,
                            false);
                } else {
                    // We can't create a sibling of a LTContent that is not yet completely
                    // added to the datamodel (ie., an isolated LTContent with no parent)
                    result = false;
                }

                if (result) {
                    // At this point, we know that defaultTwin is really the default
                    // twin, that it has no sibling of the targetLocale and that we have
                    // both a parent of defaultTwin and a parent for the targetLocale
                    // (otherwise, the recursive call would have returned false)

                    // So, we create a new LocaleContent with the same name and a parent
                    // which is a sibling of defaultTwin parent for the target locale

                    if (!em.getTransaction().isActive()) {
                        em.getTransaction().begin();
                    }

                    if (defaultTwin instanceof LTExternalEntity) {
                        LTExternalEntity origEe = (LTExternalEntity) defaultTwin;
                        LTExternalEntity newEe = new LTExternalEntity(defaultTwin.getName(),
                                null, origEe.getTextValue());
                        newSibling = newEe;
                    } else if (defaultTwin instanceof LTComment) {
                        LTComment origLtc = (LTComment) defaultTwin;
                        LTComment newLtc = new LTComment();
                        newLtc.setCommentType(origLtc.getCommentType());
                        newLtc.setEntityName(origLtc.getEntityName());
                        newLtc.setTextValue(origLtc.getTextValue());
                        newSibling = newLtc;
                    } else if (defaultTwin instanceof LTIniSection) {
                        LTIniSection origLis = (LTIniSection) defaultTwin;
                        LTIniSection newLis = new LTIniSection();
                        newSibling = newLis;
                    } else if (defaultTwin instanceof LTKeyValuePair) {
                        LTKeyValuePair origKvp = (LTKeyValuePair) defaultTwin;
                        LTKeyValuePair newKvp = new LTKeyValuePair();
                        newKvp.setTextValue("");
                        newSibling = newKvp;
                    } else if (defaultTwin instanceof LTLicense) {
                        LTLicense origLic = (LTLicense) defaultTwin;
                        LTLicense newLic = new LTLicense();
                        newLic.setTextValue(origLic.getTextValue());
                        newSibling = newLic;
                    } else if (defaultTwin instanceof LTWhitespace) {
                        LTWhitespace origWs = (LTWhitespace) defaultTwin;
                        LTWhitespace newWs = new LTWhitespace();
                        newWs.setTextValue(origWs.getTextValue());
                        newSibling = newWs;
                    } else { // Default case
                        newSibling = new LTContent();
                    }
                    newSibling.setName(defaultTwin.getName());
                    newSibling.setParent(defaultParent.getTwinByLocale(targetLocale));
                    newSibling.setCreationDate(opTimeStamp);
                    newSibling.setDefLocaleTwin(defaultTwin);
                    newSibling.setL10nId(targetLocale);
                    newSibling.setLastUpdate(opTimeStamp);

                    // Connect the parent with newSibling
                    defaultParent.getTwinByLocale(targetLocale).addChild(newSibling);

                    // Conect defaultTwin and newSibling between them, and with the rest
                    // of twins
                    for(LocaleContent lcntTwin : defaultTwin.getTwins()) {
                        newSibling.addTwin(lcntTwin);
                        lcntTwin.addTwin(newSibling);
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

    /**
     * Removes the LTContent lcnt from database and in-memory structure.
     *
     * The "recursively" part comes because this method takes care of all internal
     * references to contents, where they might exist (like L20n in the future), and
     * to keep naming scheme consistent with LocaleContainerJPAHelper.
     *
     * @param lcnt The LTContent to be removed
     * @return true if the operation ended successfully
     */
    public boolean removeRecursively(LocaleContent lcnt) {
        // boolean result = (lc.getTwins().isEmpty());
        boolean result = true;

        if (!result) {
            return result;
        }

        try {
            if (transactCounter == 0 && !em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }

            // Ensure that the EntityManager is managing the LTContent to be removed
            lcnt = em.merge(lcnt);
            result = true;
            switch (lcnt.getClass().getName()) {
                default: // No special handling at the moment
            }

            // Now we need to update the twins to remove lcnt from their twins lists
            if (result) {
                for (LocaleContent lcntTwin : lcnt.getTwins()) {
                    lcntTwin = em.merge(lcntTwin);
                    result = result && lcntTwin.removeTwin(lcnt);
                    if (!result) {
                        break;
                    }
                }
            }

            // If lcnt is a default twin (thus, not having a default twin itself), then
            // we must remove the twins before removing lcnt itself
            if (result && (lcnt.getDefLocaleTwin() == null)) {
                for(Iterator<? extends LocaleContent> iterator = lcnt.getTwins().iterator(); iterator.hasNext(); ) {
                    LocaleContent lcntTwin = iterator.next();
                    lcntTwin = em.merge(lcntTwin);
                    iterator.remove();
                    result = removeRecursively(lcntTwin);
                    if (!result) {
                        break;
                    }
                }
            }

            if (result) {
                lcnt.setDefLocaleTwin(null);

                LocaleFile parent = lcnt.getParent();
                if (parent != null) {
                    parent.removeChild(lcnt);
                    lcnt.setParent(null);
                }

                em.remove(lcnt);
                transactCounter++;
                if (transactCounter > transactMaxCount) {
                    em.getTransaction().commit();
                    transactCounter = 0;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LocaleContentJPAHelper.class.getName()).log(Level.SEVERE, null, e);
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
