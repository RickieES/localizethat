/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Table(name = "APP.PRODUCTPATH", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"PRODPATH"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductPath.countAll", query = "SELECT COUNT(pp) FROM ProductPath pp"),
    @NamedQuery(name = "ProductPath.countById", query = "SELECT COUNT(pp) FROM ProductPath pp WHERE pp.id = :id"),
    /*
    @NamedQuery(name = "ProductPath.countByProduct", query = "SELECT COUNT(pl) FROM ProductLocale pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "ProductPath.countByLocale", query = "SELECT COUNT(pl) FROM ProductLocale pl WHERE pl.l10nId = :l10nid"),
    @NamedQuery(name = "ProductPath.findAll", query = "SELECT pl FROM ProductLocale pl"),
    @NamedQuery(name = "ProductPath.findById", query = "SELECT pl FROM ProductLocale pl WHERE pl.id = :id"),
    @NamedQuery(name = "ProductPath.findByProduct", query = "SELECT pl FROM ProductLocale pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "ProductPath.findByL10n", query = "SELECT pl FROM ProductLocale pl WHERE pl.l10nId = :l10nid"),
    @NamedQuery(name = "ProductPath.findByCreationDate", query = "SELECT pl FROM ProductLocale pl WHERE pl.creationDate = :prodcreationdate"),
    @NamedQuery(name = "ProductPath.findByLastUpdate", query = "SELECT pl FROM ProductLocale pl WHERE pl.lastUpdate = :prodlastupdate") */
})
public class ProductPath implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int PPATHPATH_LENGTH = 255;
    @TableGenerator(name="PRODUCTPATH", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="PRODUCTPATH")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "PRODPATH", nullable = false, length = PPATHPATH_LENGTH)
    private String path;
    @JoinColumn(name = "LCONTAINER_ID", referencedColumnName = "ID", nullable = true)
    @OneToOne(optional = true)
    private LocaleContainer localeContainer;
    @ManyToMany(mappedBy="pathList")
    private Collection<ProductLocale> productLocaleList;
    @Column(name = "PRODPCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "PRODPLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    public ProductPath() {
    }

    public ProductPath(Integer id) {
        this.id = id;
    }

    public ProductPath(Integer id, String path) {
        this.id = id;
        this.path = path;
    }

    public ProductPath(Integer id, String path, LocaleContainer localeContainer) {
        this.id = id;
        this.path = path;
        this.localeContainer = localeContainer;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocaleContainer getLocaleContainer() {
        return localeContainer;
    }

    public void setLocaleContainer(LocaleContainer localeContainer) {
        this.localeContainer = localeContainer;
    }

    public boolean addProductLocale(ProductLocale productLocale) {
        if (!hasProductLocale(productLocale)) {
            productLocaleList.add(productLocale);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasProductLocale(ProductLocale productLocale) {
        return productLocaleList.contains(productLocale);
    }

    public boolean hasProductLocale(Product p, L10n l) {
        boolean found = false;

        if ((p == null) || (l == null)) {
            return false;
        }

        for(ProductLocale pl : productLocaleList) {
            found = (p.equals(pl.getProductId())) && (l.equals(pl.getL10nId()));
        }
        return found;
    }

    public ProductLocale getProductLocale(Product p, L10n l) {
        for(ProductLocale pl : productLocaleList) {
            if ((p.equals(pl.getProductId())) && (l.equals(pl.getL10nId()))) {
                return pl;
            }
        }
        return null;
    }

    public ProductLocale removeProductLocale(Product p, L10n l) {
        ProductLocale pl = getProductLocale(p, l);

        if ((pl != null) && (removeProductLocale(pl))) {
            return pl;
        } else {
            return null;
        }
    }

    public boolean removeProductLocale(ProductLocale productLocale) {
        return productLocaleList.remove(productLocale);
    }

    public boolean clearPaths() {
        try {
            productLocaleList.clear();
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
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.path);
        hash = 97 * hash + Objects.hashCode(this.lastUpdate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductPath other = (ProductPath) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return Objects.equals(this.lastUpdate, other.lastUpdate);
    }

}
