/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

/**
 *
 * @author rpalomares
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"PRODUCT_ID", "L10N_ID"}),
    @UniqueConstraint(columnNames = {"L10N_ID", "PRODUCT_ID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductLocale.countAll", query = "SELECT COUNT(pl) FROM ProductLocale pl"),
    @NamedQuery(name = "ProductLocale.countById", query = "SELECT COUNT(pl) FROM ProductLocale pl WHERE pl.id = :id"),
    @NamedQuery(name = "ProductLocale.countByProduct", query = "SELECT COUNT(pl) FROM ProductLocale pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "ProductLocale.countByLocale", query = "SELECT COUNT(pl) FROM ProductLocale pl WHERE pl.l10nId = :l10nid"),
    @NamedQuery(name = "ProductLocale.findAll", query = "SELECT pl FROM ProductLocale pl"),
    @NamedQuery(name = "ProductLocale.findById", query = "SELECT pl FROM ProductLocale pl WHERE pl.id = :id"),
    @NamedQuery(name = "ProductLocale.findByProduct", query = "SELECT pl FROM ProductLocale pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "ProductLocale.findByL10n", query = "SELECT pl FROM ProductLocale pl WHERE pl.l10nId = :l10nid"),
    @NamedQuery(name = "ProductLocale.findByCreationDate", query = "SELECT pl FROM ProductLocale pl WHERE pl.creationDate = :prodcreationdate"),
    @NamedQuery(name = "ProductLocale.findByLastUpdate", query = "SELECT pl FROM ProductLocale pl WHERE pl.lastUpdate = :prodlastupdate")})
public class ProductLocale implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableGenerator(name="PRODUCTLOCALE", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="PRODUCTLOCALE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Product productId;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @Column(name = "PRODLCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "PRODLLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    public ProductLocale() {
    }

    public ProductLocale(Integer id) {
        this.id = id;
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

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }

    public L10n getL10nId() {
        return l10nId;
    }

    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductLocale)) {
            return false;
        }
        ProductLocale other = (ProductLocale) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return this.productId.getName() + "-(" + this.l10nId.getName() + ")";
    }

}
