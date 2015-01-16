/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.awt.Color;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.PRODUCT", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"PRODNAME", "CHANNEL_ID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.countAll", query = "SELECT COUNT(p) FROM Product p"),
    @NamedQuery(name = "Product.countById", query = "SELECT COUNT(p) FROM Product p WHERE p.id = :id"),
    @NamedQuery(name = "Product.countByName", query = "SELECT COUNT(p) FROM Product p WHERE UPPER(p.name) = UPPER(:name)"),
    @NamedQuery(name = "Product.countByL10n", query = "SELECT COUNT(p) FROM Product p WHERE p.l10nId = :l10nid"),
    @NamedQuery(name = "Product.countByChannel", query = "SELECT COUNT(p) FROM Product p WHERE p.channelId = :channelid"),
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id"),
    @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE UPPER(p.name) = UPPER(:name)"),
    @NamedQuery(name = "Product.findBysrcType", query = "SELECT p FROM Product p WHERE p.srcType = :srctype"),
    @NamedQuery(name = "Product.findByL10n", query = "SELECT p FROM Product p WHERE p.l10nId = :l10nid"),
    @NamedQuery(name = "Product.findByChannel", query = "SELECT p FROM Product p WHERE p.channelId = :channelid"),
    @NamedQuery(name = "Product.findByCreationDate", query = "SELECT p FROM Product p WHERE p.creationDate = :prodcreationdate"),
    @NamedQuery(name = "Product.findByLastUpdate", query = "SELECT p FROM Product p WHERE p.lastUpdate = :prodlastupdate")})
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int PRODNAME_LENGTH = 32;
    private static final int PRODNOTES_LENGTH = 32700;
    private static final int PRODCOLOR_LENGTH = 10;
    @TableGenerator(name="PRODUCT", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 2)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="PRODUCT")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "PRODNAME", nullable = false, length = PRODNAME_LENGTH)
    private String name;
    @Basic(optional = false)
    @Column(name = "PRODSRCTYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSourceType srcType;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @JoinColumn(name = "CHANNEL_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Channel channelId;
    @Lob
    @Column(name = "PRODNOTES", length = PRODNOTES_LENGTH)
    private String notes;
    @Column(name = "PRODCOLOR", length = PRODCOLOR_LENGTH)
    private String color;
    @Column(name = "PRODCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "PRODLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @ManyToMany
    @JoinTable(name="APP.PRODUCT_PATH",
            joinColumns=@JoinColumn(name="PRODUCT_ID"),
            inverseJoinColumns=@JoinColumn(name="LOCALEPATH_ID"))
    private Collection<LocalePath> pathList;

    public Product() {
    }

    public Product(Integer id) {
        this.id = id;
    }

    public Product(Integer id, String name, ProductSourceType srcType) {
        this.id = id;
        this.name = name;
        this.srcType = srcType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.substring(0, Math.min(name.length(), Product.PRODNAME_LENGTH));
    }

    public ProductSourceType getSrcType() {
        return srcType;
    }

    public void setSrcType(ProductSourceType srcType) {
        this.srcType = srcType;
    }

    public L10n getL10nId() {
        return l10nId;
    }

    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
    }

    public Channel getChannelId() {
        return channelId;
    }

    public void setChannelId(Channel channelId) {
        this.channelId = channelId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes.substring(0, Math.min(notes.length(), Product.PRODNOTES_LENGTH));
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color.substring(0, Math.min(color.length(), Product.PRODCOLOR_LENGTH));
    }

    public Color returnAwtColor() {
        int colorValue = new BigInteger(getColor(), 16).intValue();
        return new Color(colorValue);
    }

    public boolean addChild(LocalePath lp) {
        return addLocalePath(lp);
    }

    public boolean addLocalePath(LocalePath lp) {
        if (!Product.this.hasChild(lp)) {
            pathList.add(lp);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasChild(LocalePath lp) {
        return Product.this.hasLocalePath(lp);
    }

    public boolean hasLocalePath(LocalePath lp) {
        return pathList.contains(lp);
    }

    public boolean hasChild(String path) {
        return hasLocalePath(path, false);
    }

    public boolean hasLocalePath(String path) {
        return hasLocalePath(path, false);
    }

    public boolean hasChild(String path, boolean matchCase) {
        return hasLocalePath(path, matchCase);
    }

    public boolean hasLocalePath(String path, boolean matchCase) {
        boolean found = false;

        for (LocalePath lp : pathList) {
            found = (matchCase) ? (lp.getRawPath().equals(path)) : (lp.getRawPath().equalsIgnoreCase(path));
        }
        return found;
    }

    public LocalePath getChildByName(String path) {
        return getLocalePath(path, false);
    }

    public LocalePath getLocalePath(String path) {
        return getLocalePath(path, false);
    }

    public LocalePath getChildByName(String path, boolean matchCase) {
        return getLocalePath(path, matchCase);
    }

    public LocalePath getLocalePath(String path, boolean matchCase) {
        for(LocalePath lp : pathList) {
            boolean found = (matchCase) ? (lp.getRawPath().equals(path))
                                        : (lp.getRawPath().equalsIgnoreCase(path));
            if (found) {
                return lp;
            }
        }
        return null;
    }

    public Collection<LocalePath> getChildren() {
        return getPathList();
    }

    @XmlTransient
    public Collection<LocalePath> getPathList() {
        return pathList;
    }

    public LocalePath removeChild(String path) {
        return removeLocalePath(path, false);
    }

    public LocalePath removeLocalePath(String path) {
        return removeLocalePath(path, false);
    }

    public LocalePath removeChild(String path, boolean matchCase) {
        return removeLocalePath(path, matchCase);
    }

    public LocalePath removeLocalePath(String path, boolean matchCase) {
        LocalePath lp = getLocalePath(path, matchCase);

        if ((lp != null) && (removeLocalePath(lp))) {
            return lp;
        } else {
            return null;
        }
    }

    public boolean removeChild(LocalePath lp) {
        return removeLocalePath(lp);
    }

    public boolean removeLocalePath(LocalePath lp) {
        return pathList.remove(lp);
    }

    public boolean clearChildren() {
        return clearLocalePathCollection();
    }

    public boolean clearLocalePathCollection() {
        try {
            pathList.clear();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return name;
    }

}
