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
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author rpalomares
 */
@MappedSuperclass
public abstract class AbstractLocaleNode implements LocaleNode {
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
    @Column(name = "LNODEPARENT")
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = false)
    private LocaleNode parent;
    @Column(name = "LNODETWIN")
    @JoinColumn(name = "TWIN_ID", referencedColumnName = "ID", nullable = true)
    @OneToOne(optional = true)
    private LocaleNode defLocaleTwin;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    Collection<LocaleNode> children;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Override
    public void setName(String name) {
        this.name = name.substring(0, LOCALENODENAME_LENGTH);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParent(LocaleNode parent) {
        this.parent = parent;
    }

    @Override
    public LocaleNode getParent() {
        return parent;
    }

    @Override
    public boolean addChild(LocaleNode node) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isChild(LocaleNode node) {
        return children.contains(node);
    }

    @Override
    public boolean isChild(String name) {
        return isChild(name, false);
    }

    @Override
    public boolean isChild(String name, boolean matchCase) {
        boolean found = false;

        for(LocaleNode l : children) {
            found = (matchCase) ? (l.getName().equals(name)) : (l.getName().equalsIgnoreCase(name));
        }
        return found;
    }

    @Override
    public LocaleNode getChildByName(String name) {
        return getChildByName(name, false);
    }

    @Override
    public LocaleNode getChildByName(String name, boolean matchCase) {
        for(LocaleNode l : children) {
            boolean found = (matchCase) ? (l.getName().equals(name))
                                        : (l.getName().equalsIgnoreCase(name));
            if (found) {
                return l;
            }
        }
        return null;
    }

    @Override
    public LocaleNode removeChild(String name) {
        LocaleNode l = getChildByName(name);

        if ((l != null) && (removeChild(l))) {
            return l;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeChild(LocaleNode node) {
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
    public void setDefLocaleTwinId(LocaleNode twin) {
        this.defLocaleTwin = twin;
    }

    @Override
    public LocaleNode getDefLocaleTwin() {
        return defLocaleTwin;
    }

}
