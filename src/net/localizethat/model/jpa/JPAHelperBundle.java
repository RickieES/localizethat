/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model.jpa;

import javax.persistence.EntityManager;

/**
 * Singleton that groups the three JPAHelpers, so they can be connected among them
 * @author rpalomares
 */
public class JPAHelperBundle {
    private static final int DEFAULT_TRANSACT_MAX_COUNT = 50;
    private static JPAHelperBundle jhb;

    public static JPAHelperBundle getInstance(EntityManager em) {
        return getInstance(em, JPAHelperBundle.DEFAULT_TRANSACT_MAX_COUNT);
    }

    public static JPAHelperBundle getInstance(EntityManager em, int transactMaxCount) {
        if (jhb == null) {
            jhb = new JPAHelperBundle(em, transactMaxCount);
        }
        return jhb;
    }
    private int transactMaxCount;
    private LocaleContainerJPAHelper lcjh;
    private LocaleFileJPAHelper lfjh;
    private LocaleContentJPAHelper lcntjh;
    private EntityManager em;

    private JPAHelperBundle() {
    }

    private JPAHelperBundle(EntityManager em, int transactMaxCount) {
        this.em = em;
        this.transactMaxCount = transactMaxCount;
        this.lcjh = new LocaleContainerJPAHelper(this.em, this.transactMaxCount);
        this.lfjh = new LocaleFileJPAHelper(this.em, this.transactMaxCount);
        this.lcntjh = new LocaleContentJPAHelper(this.em, this.transactMaxCount);

        // Interconnect the helpers
        this.lcjh.setLocaleFileJPAHelper(lfjh);
        this.lcjh.setLocaleContentJPAHelper(lcntjh);
        this.lfjh.setLocaleContainerJPAHelper(lcjh);
        this.lfjh.setLocaleContentJPAHelper(lcntjh);
        this.lcntjh.setLocaleContainerJPAHelper(lcjh);
        this.lcntjh.setLocaleFileJPAHelper(lfjh);
    }
    
    public LocaleContainerJPAHelper getLocaleContainerJPAHelper() {
        return lcjh;
    }
    
    public LocaleFileJPAHelper getLocaleFileJPAHelper() {
        return lfjh;
    }

    public LocaleContentJPAHelper getLocaleContentJPAHelper() {
        return lcntjh;
    }
}
