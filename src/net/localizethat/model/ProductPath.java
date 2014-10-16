/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author rpalomares
 */
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
    @Column(nullable = false, length = PPATHPATH_LENGTH)
    private String path;
    private LocaleContainer localeContainer;

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
