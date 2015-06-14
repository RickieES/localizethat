/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "APP.LOCALEPATH", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"L10NPATH"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocalePath.countAll", query = "SELECT COUNT(lp) FROM LocalePath lp"),
    @NamedQuery(name = "LocalePath.countById", query = "SELECT COUNT(lp) FROM LocalePath lp WHERE lp.id = :id"),
    @NamedQuery(name = "LocalePath.countByPath", query = "SELECT COUNT(lp) FROM LocalePath lp WHERE lp.path = :path"),
    @NamedQuery(name = "LocalePath.findByL10n", query = "SELECT lp FROM LocalePath lp WHERE lp.l10nId = :l10nid ORDER by lp.path"),
    @NamedQuery(name = "LocalePath.findOriginals",
            query = "SELECT lp FROM LocalePath lp WHERE lp.localeContainer.defLocaleTwin is null ORDER by lp.path"),
    @NamedQuery(name = "LocalePath.findByProductAndL10n",
            query = "SELECT lp FROM Product p JOIN p.pathList lp WHERE p.id = :productid AND lp.l10nId = :l10nid ORDER BY lp.path"),
    @NamedQuery(name = "LocalePath.findByProductAndNotL10n",
            query = "SELECT lp FROM Product p JOIN p.pathList lp WHERE p.id = :productid AND lp.l10nId != :l10nid ORDER BY lp.path"),
    @NamedQuery(name = "LocalePath.findByLocaleContainer",
            query = "SELECT lp FROM LocalePath lp WHERE lp.localeContainer = :localecontainer"),
    @NamedQuery(name = "LocalePath.findByAssociatedLocaleContainer",
            query = "SELECT lp FROM LocalePath lp WHERE lp.localeContainer.defLocaleTwin = :localecontainer "
                    + "ORDER by lp.l10nId, lp.path"),
    /*
    @NamedQuery(name = "LocalePath.countByProduct", query = "SELECT COUNT(pl) FROM LocalePath pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "LocalePath.countByLocale", query = "SELECT COUNT(pl) FROM LocalePath pl WHERE pl.l10nId = :l10nid"),
    @NamedQuery(name = "LocalePath.findAll", query = "SELECT pl FROM LocalePath pl"),
    @NamedQuery(name = "LocalePath.findById", query = "SELECT pl FROM LocalePath pl WHERE pl.id = :id"),
    @NamedQuery(name = "LocalePath.findByProduct", query = "SELECT pl FROM LocalePath pl WHERE pl.productId = :productid"),
    @NamedQuery(name = "LocalePath.findByCreationDate", query = "SELECT pl FROM LocalePath pl WHERE pl.creationDate = :prodcreationdate"),
    @NamedQuery(name = "LocalePath.findByLastUpdate", query = "SELECT pl FROM LocalePath pl WHERE pl.lastUpdate = :prodlastupdate") */
})
public class LocalePath implements Serializable, Comparable<LocalePath> {
    private static final long serialVersionUID = 1L;
    private static final int LPATHPATH_LENGTH = 255;
    @TableGenerator(name="LOCALEPATH", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="LOCALEPATH")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "L10NPATH", nullable = false, length = LPATHPATH_LENGTH)
    private String path;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @JoinColumn(name = "LCONTAINER_ID", referencedColumnName = "ID", nullable = true)
    @OneToOne(optional = true)
    private LocaleContainer localeContainer;
    @ManyToMany(mappedBy="pathList")
    private List<Product> productList;
    @Column(name = "PRODPCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "PRODPLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    public LocalePath() {
        productList = new ArrayList<>(5);
    }

    public LocalePath(Integer id) {
        this();
        this.id = id;
    }

    public LocalePath(Integer id, String path) {
        this(id);
        this.path = path;
    }

    public LocalePath(Integer id, String path, LocaleContainer localeContainer) {
        this(id, path);
        this.localeContainer = localeContainer;
    }

    public LocalePath(String path) {
        this();
        this.path = path;
    }

    public LocalePath(String path, LocaleContainer localeContainer) {
        this(path);
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

    /**
     * Returns the path "as is", without any processing, ie. without replacing channel or base
     * directory tags
     * @return the path value without any processing
     */
    public String getRawPath() {
        return path;
    }

    /**
     * Returns the operational path, ie. after replacing possible channel or base directory tags.
     * In other words, a path that represents a real file path in the user filesystem.
     * @return the operational path
     */
    public String getFilePath() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(getRawPath());

        // TODO process channel and base directory substitutions before returning the value
        return sb.toString();
    }

    public String getPathLastComponent() {
        int i = path.lastIndexOf(System.getProperty("file.separator"));
        if (i != -1) {
            return path.substring(i + 1);
        } else {
            return path;
        }
    }

    public void setPath(String path) {
        this.path = path.substring(0, Math.min(path.length(), LPATHPATH_LENGTH));
    }

    public L10n getL10nId() {
        return l10nId;
    }

    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
    }

    public LocaleContainer getLocaleContainer() {
        return localeContainer;
    }

    public void setLocaleContainer(LocaleContainer localeContainer) {
        this.localeContainer = localeContainer;
    }

    public boolean addChild(Product product) {
        return addProduct(product);
    }

    public boolean addProduct(Product product) {
        if (!hasProduct(product)) {
            productList.add(product);
            Collections.sort(productList);
            return true;
        } else {
            return false;
        }
    }

    public boolean hasChild(Product product) {
        return hasProduct(product);
    }

    public boolean hasProduct(Product product) {
        return productList.contains(product);
    }

    public boolean hasChild(String name) {
        return hasProduct(name, false);
    }

    public boolean hasProduct(String name) {
        return hasProduct(name, false);
    }

    public boolean hasChild(String name, boolean matchCase) {
        return hasProduct(name, matchCase);
    }

    public boolean hasProduct(String name, boolean matchCase) {
        boolean found = false;

        for(Product p : productList) {
            found = (matchCase) ? (p.getName().equals(name)) : (p.getName().equalsIgnoreCase(name));
        }
        return found;
    }

    public Product getChildByName(String productName) {
        return getProductByName(productName, false);
    }

    public Product getProductByName(String productName) {
        return getProductByName(productName, false);
    }

    public Product getChildByName(String productName, boolean matchCase) {
        return getProductByName(productName, matchCase);
    }

    public Product getProductByName(String productName, boolean matchCase) {
        for(Product p : productList) {
            if ((matchCase) ? (p.getName().equals(productName))
                                        : (p.getName().equalsIgnoreCase(productName))) {
                return p;
            }
        }
        return null;
    }

    public Collection<Product> getChildren() {
        return getProductList();
    }

    public Collection<Product> getProductList() {
        return productList;
    }

    public Product removeChild(String productName) {
        return removeProduct(productName, false);
    }

    public Product removeProduct(String productName) {
        return removeProduct(productName, false);
    }

    public Product removeChild(String productName, boolean matchCase) {
        return removeProduct(productName, matchCase);
    }

    public Product removeProduct(String productName, boolean matchCase) {
        Product p = getProductByName(productName, matchCase);

        if ((p != null) && (removeProduct(p))) {
            return p;
        } else {
            return null;
        }
    }

    public boolean removeChild(Product product) {
        return removeProduct(product);
    }

    public boolean removeProduct(Product product) {
        return productList.remove(product);
    }

    public boolean clearChildren() {
        return clearProducts();
    }

    public boolean clearProducts() {
        try {
            productList.clear();
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
        final LocalePath other = (LocalePath) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return Objects.equals(this.lastUpdate, other.lastUpdate);
    }

    @Override
    public String toString() {
        return this.getRawPath();
    }

    @Override
    public int compareTo(LocalePath o) {
        return getRawPath().compareTo(o.getRawPath());
    }
}
