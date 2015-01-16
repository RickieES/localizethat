/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import net.localizethat.Main;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.LOCALECONTAINER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleContainer.countAll", query = "SELECT COUNT(lc) FROM LocaleContainer lc"),
    @NamedQuery(name = "LocaleContainer.count", query = "SELECT COUNT(lc) FROM LocaleContainer lc")
})

public class LocaleContainer extends AbstractLocaleNode<LocaleContainer, LocaleContainer, LocaleContainer> implements Serializable {
    // TODO write the annotations that describe how the fileChildren collection is persisted
    private Collection<LocaleFile> fileChildren;

    public LocaleContainer() {
      super();
      children = new ArrayList<>(5);
    }

    @Override
    public String getFilePath() {
        StringBuilder sb = new StringBuilder(64);
        LocaleContainer lc = getParent();

        if (lc != null) {
            sb.append(lc.getFilePath());
        } else {
            // TODO how can we do this without creating an EntityManager here?
            EntityManager entityManager = Main.emf.createEntityManager();
            TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                    "LocalePath.findByLocaleContainer", LocalePath.class);
            localePathQuery.setParameter("localecontainer", lc);
            LocalePath lp = localePathQuery.getSingleResult();
            sb.append(lp.getFilePath());
        }
        // We use the "/" literal instead of file.separator to avoid mixing of separators
        sb.append("/").append(getName());
        return sb.toString();
    }


    public boolean addFileChild(LocaleFile node) {
        if (!hasFileChild(node)) {
            fileChildren.add(node);
            return true;
        }
        return false;
    }

    public boolean hasFileChild(LocaleFile node) {
        return fileChildren.contains(node);
    }

    public boolean hasFileChild(String name) {
        return hasFileChild(name, false);
    }

    public boolean hasFileChild(String name, boolean matchCase) {
        boolean found = false;

        for(LocaleFile lf : fileChildren) {
            found = (matchCase) ? (lf.getName().equals(name)) : (lf.getName().equalsIgnoreCase(name));
        }
        return found;
    }

    public LocaleFile getFileChildByName(String name) {
        return getFileChildByName(name, false);
    }

    public LocaleFile getFileChildByName(String name, boolean matchCase) {
        for(LocaleFile lf : fileChildren) {
            boolean found = (matchCase) ? (lf.getName().equals(name))
                                        : (lf.getName().equalsIgnoreCase(name));
            if (found) {
                return lf;
            }
        }
        return null;
    }

    public Collection<? extends LocaleFile> getFileChildren() {
        return fileChildren;
    }

    public LocaleFile removeFileChild(String name) {
        return removeFileChild(name, false);
    }

    public LocaleFile removeFileChild(String name, boolean matchCase) {
        LocaleFile lf = getFileChildByName(name, matchCase);

        if ((lf != null) && (removeFileChild(lf))) {
            return lf;
        } else {
            return null;
        }
    }

    public boolean removeFileChild(LocaleFile node) {
        return fileChildren.remove(node);
    }

    public boolean clearFileChildren() {
        try {
            fileChildren.clear();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
