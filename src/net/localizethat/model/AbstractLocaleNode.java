/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @param <P> the parent class
 * @param <S> the sibling class
 * @param <D> the descendent class
 * @author rpalomares
 */
@MappedSuperclass
public abstract class AbstractLocaleNode<P extends LocaleNode, S extends LocaleNode, D extends LocaleNode>
        implements LocaleNode<P, S, D> {
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
    private P parent;
    @JoinColumn(name = "LNODETWIN", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true)
    private S defLocaleTwin;
    @OneToMany(mappedBy="defLocaleTwin")
    private Collection<S> twins;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    Collection<D> children;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODECREATIONDATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODELASTUPDATE", nullable = false)
    private Date lastUpdate;

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
    public void setParent(P parent) {
        this.parent = parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public boolean hasChild(D node) {
        return children.contains(node);
    }

    @Override
    public boolean hasChild(String name) {
        return hasChild(name, false);
    }

    @Override
    public boolean hasChild(String name, boolean matchCase) {
        boolean found = false;

        for(D l : children) {
            found = (matchCase) ? (l.getName().equals(name)) : (l.getName().equalsIgnoreCase(name));
        }
        return found;
    }

    @Override
    public D getChildByName(String name) {
        return getChildByName(name, false);
    }

    @Override
    public D getChildByName(String name, boolean matchCase) {
        for(D l : children) {
            boolean found = (matchCase) ? (l.getName().equals(name))
                                        : (l.getName().equalsIgnoreCase(name));
            if (found) {
                return l;
            }
        }
        return null;
    }

    @Override
    public Collection<D> getChildren() {
        return children;
    }

    @Override
    public D removeChild(String name) {
        return removeChild(name, false);
    }


    @Override
    public D removeChild(String name, boolean matchCase) {
        D l = getChildByName(name, matchCase);

        if ((l != null) && (removeChild(l))) {
            return l;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeChild(D node) {
        return children.remove(node);
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
    public void setDefLocaleTwin(S twin) {
        if (twin != null) {
            this.defLocaleTwin = twin;
            twin.addTwin(this);
        } else {
            S defLT = this.defLocaleTwin;
            this.defLocaleTwin = null;
            // defLT.removeTwin(this);
        }
    }

    @Override
    public S getDefLocaleTwin() {
        return defLocaleTwin;
    }
    
    @Override
    public boolean addTwin(S twin) {
        boolean result;

        result = (twin.getDefLocaleTwin() == this);

        if (result) {
            this.twins.add(twin);
        }
        return result;
    }

    @Override
    public boolean removeTwin(S twin) {
        boolean result;

        result = (twin.getDefLocaleTwin() == null)
                && (twins.contains(twin))
                && twins.remove(twin);
        return result;
    }

    @Override
    public boolean isATwin(S possibleTwin) {
        for(S s : twins) {
            if (s.equals(possibleTwin)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<S> getTwins() {
        return twins;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
