/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import net.localizethat.Main;

/**
 * LocaleNode implementation representing a LocaleContainer (a directory/folder)
 * @author rpalomares
 */
@Entity
@Table(name = "APP.LOCALECONTAINER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleContainer.countAll", query = "SELECT COUNT(lc) FROM LocaleContainer lc"),
    @NamedQuery(name = "LocaleContainer.count", query = "SELECT COUNT(lc) FROM LocaleContainer lc")
})
public class LocaleContainer implements LocaleNode, Serializable {
    private static final int LOCALENODENAME_LENGTH = 128;
    @TableGenerator(name="LOCALENODE", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="LOCALENODE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "LNODENAME", nullable = false, length = LOCALENODENAME_LENGTH)
    private String name;
    @JoinColumn(name = "LNODEPARENT", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = false)
    private LocaleContainer parent;
    @JoinColumn(name = "LNODETWIN", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true)
    private LocaleContainer defLocaleTwin;
    @OneToMany(mappedBy="defLocaleTwin")
    private Collection<LocaleContainer> twins;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    protected Collection<LocaleContainer> children;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private Collection<LocaleFile> fileChildren;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODECREATIONDATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODELASTUPDATE", nullable = false)
    private Date lastUpdate;

    public LocaleContainer() {
      super();
      children = new ArrayList<>(5);
      fileChildren = new ArrayList<>(5);
    }

    public LocaleContainer(Integer id) {
        this();
        this.id = id;
    }

    public LocaleContainer(Integer id, String name) {
        this(id);
        this.name = name.substring(0, Math.min(name.length(), LOCALENODENAME_LENGTH));
    }

    public LocaleContainer(Integer id, String name, LocaleContainer parent) {
        this(id, name);
        this.parent = parent;
    }

    public LocaleContainer(String name) {
        this();
        this.name = name.substring(0, Math.min(name.length(), LOCALENODENAME_LENGTH));
    }

    public LocaleContainer(String name, LocaleContainer parent) {
        this(name);
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public int getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }

    @Override
    public void setName(String name) {
        this.name = name.substring(0, Math.min(name.length(), LOCALENODENAME_LENGTH));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParent(LocaleNode parent) {
        if (parent instanceof LocaleContainer) {
            this.parent = (LocaleContainer) parent;
        }
    }

    @Override
    public LocaleNode getParent() {
        return parent;
    }

    @Override
    public boolean addChild(LocaleNode node) {
        if ((node instanceof LocaleContainer) && !hasChild(node)) {
            children.add((LocaleContainer) node);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasChild(LocaleNode node) {
        if (node instanceof LocaleContainer) {
            return children.contains((LocaleContainer) node);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasChild(String name) {
        return hasChild(name, false);
    }

    @Override
    public boolean hasChild(String name, boolean matchCase) {
        boolean found = false;

        for(LocaleContainer l : children) {
            found = found || ((matchCase) ? (l.getName().equals(name)) : (l.getName().equalsIgnoreCase(name)));
        }
        return found;
    }

    @Override
    public LocaleContainer getChildByName(String name) {
        return getChildByName(name, false);
    }

    @Override
    public LocaleContainer getChildByName(String name, boolean matchCase) {
        if (name == null) {
            return null;
        }

        for(LocaleContainer l : children) {
            boolean found = (matchCase) ? (name.equals(l.getName()))
                                        : (name.equalsIgnoreCase(l.getName()));
            if (found) {
                return l;
            }
        }
        return null;
    }

    @Override
    public Collection<LocaleContainer> getChildren() {
        return children;
    }

    @Override
    public LocaleContainer removeChild(String name) {
        return removeChild(name, false);
    }


    @Override
    public LocaleContainer removeChild(String name, boolean matchCase) {
        LocaleContainer l = getChildByName(name, matchCase);

        if ((l != null) && (removeChild(l))) {
            return l;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        if (node instanceof LocaleContainer) {
            return children.remove((LocaleContainer) node);
        } else {
            return false;
        }
    }

    @Override
    public boolean clearChildren() {
        try {
            children.clear();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    @Override
    public void setDefLocaleTwin(LocaleNode twin) {
        if ((twin != null) && (twin instanceof LocaleContainer)) {
            this.defLocaleTwin = (LocaleContainer) twin;
            twin.addTwin(this);
        } else {
            this.defLocaleTwin = null;
        }
    }

    @Override
    public LocaleNode getDefLocaleTwin() {
        return defLocaleTwin;
    }
    
    @Override
    public boolean addTwin(LocaleNode twin) {
        boolean result;

        result = ((twin != null) && (twin instanceof LocaleContainer) && (twin.getDefLocaleTwin() == this));

        if (result) {
            this.twins.add((LocaleContainer) twin);
        }
        return result;
    }

    @Override
    public boolean removeTwin(LocaleNode twin) {
        boolean result;

        result = (twin != null)
                && (twin instanceof LocaleContainer)
                &&(twin.getDefLocaleTwin() == null)
                && (twins.contains((LocaleContainer) twin))
                && twins.remove((LocaleContainer) twin);
        return result;
    }

    @Override
    public boolean isATwin(LocaleNode possibleTwin) {
        for(LocaleNode s : twins) {
            if (s.equals(possibleTwin)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LocaleNode getTwinByLocale(L10n locale) {
        for(LocaleNode s : twins) {
            if (s.getL10nId().equals(locale)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Collection<LocaleContainer> getTwins() {
        return twins;
    }

    @Override
    public L10n getL10nId() {
        return l10nId;
    }

    @Override
    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String getFilePath() {
        StringBuilder sb = new StringBuilder(64);
        LocaleContainer lc = (LocaleContainer) getParent();

        if (lc != null) {
            sb.append(lc.getFilePath());
            // We use the "/" literal instead of file.separator to avoid mixing of separators
            sb.append("/").append(getName());
        } else {
            // TODO how can we do this without creating an EntityManager here?
            EntityManager entityManager = Main.emf.createEntityManager();
            TypedQuery<LocalePath> localePathQuery = entityManager.createNamedQuery(
                    "LocalePath.findByLocaleContainer", LocalePath.class);
            localePathQuery.setParameter("localecontainer", this);
            LocalePath lp = localePathQuery.getSingleResult();
            sb.append(lp.getFilePath());
        }
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
            found = found || ((matchCase) ? (lf.getName().equals(name)) : (lf.getName().equalsIgnoreCase(name)));
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

    public Collection<LocaleFile> getFileChildren() {
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

    @Override
    public int compareTo(LocaleNode o) {
        return this.toString().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
