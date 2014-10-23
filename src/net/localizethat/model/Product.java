/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Product.countByName", query = "SELECT COUNT(p) FROM Product p WHERE p.name = :name"),
    @NamedQuery(name = "Product.countByL10n", query = "SELECT COUNT(p) FROM Product p WHERE p.l10nId = :l10nid"),
    @NamedQuery(name = "Product.countByChannel", query = "SELECT COUNT(p) FROM Product p WHERE p.channelId = :channelid"),
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id"),
    @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name"),
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
            valueColumnName="COUNTERVALUE", allocationSize = 5)
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<ProductLocale> productLocaleCollection;

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
        this.name = name;
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
        this.notes = notes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @XmlTransient
    public Collection<ProductLocale> getProductLocaleCollection() {
        return productLocaleCollection;
    }

    public void setProductLocaleCollection(Collection<ProductLocale> productLocaleCollection) {
        this.productLocaleCollection = productLocaleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return name + " (channel: " + ")";
    }

}
