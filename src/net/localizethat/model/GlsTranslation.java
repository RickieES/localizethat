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
import javax.persistence.Lob;
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
 * An approved translation in one language for a glossary entry, with the GlsTranslation
 * language being different from the Glossary original language
 * @author rpalomares
 */
@Entity
@Table(name = "APP.GLSTRANSLATION", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"GLSTVALUE"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GlsTranslation.findAll", query = "SELECT gt FROM GlsTranslation gt"),
    @NamedQuery(name = "GlsTranslation.findById", query = "SELECT gt FROM GlsTranslation gt WHERE gt.id = :id"),
    @NamedQuery(name = "GlsTranslation.findByValue", query = "SELECT gt FROM GlsTranslation gt WHERE gt.value = :glstvalue"),
    @NamedQuery(name = "GlsTranslation.findByEntryAndLocale", query = "SELECT gt FROM GlsTranslation gt WHERE gt.glseId = :glseid AND gt.l10nId = :l10nid"),
    @NamedQuery(name = "GlsTranslation.findByCreationDate", query = "SELECT gt FROM GlsTranslation gt WHERE gt.creationDate = :glstcreationdate"),
    @NamedQuery(name = "GlsTranslation.findByLastUpdate", query = "SELECT gt FROM GlsTranslation gt WHERE gt.lastUpdate = :glstlastupdate")})
public class GlsTranslation implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int GLSTVALUE_LENGTH = 64;
    private static final int GLSTCOMMENT_LENGTH = 32700;
    @TableGenerator(name="GLSTRANSLATION", schema="APP", table="COUNTERS",
            pkColumnName="ENTITY", valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="GLSTRANSLATION")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "GLSTVALUE", nullable = false, length = GLSTVALUE_LENGTH)
    private String value;
    @Lob
    @Column(name = "GLSTCOMMENT", length = GLSTCOMMENT_LENGTH)
    private String comment;
    @Column(name = "GLSTCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "GLSTLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @JoinColumn(name = "GLSE_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private GlsEntry glseId;
    @Column(name = "ENTITYVERSION")
    @Version
    private int entityVersion;

    public GlsTranslation() {
    }

    public GlsTranslation(Integer id) {
        this.id = id;
    }

    public GlsTranslation(Integer id, String glstvalue) {
        this.id = id;
        this.value = glstvalue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.substring(0, Math.min(value.length(), GLSTVALUE_LENGTH));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment.substring(0, Math.min(comment.length(), GLSTCOMMENT_LENGTH));
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

    public L10n getL10nId() {
        return l10nId;
    }

    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
    }

    public GlsEntry getGlseId() {
        return glseId;
    }

    public void setGlseId(GlsEntry glseId) {
        this.glseId = glseId;
    }

    public int getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GlsTranslation)) {
            return false;
        }
        GlsTranslation other = (GlsTranslation) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    /**
     * Contrary to equals(), this method considers that two GlsTranslation objects are
     * the same if they have the same value, the same L10n and neither one has a parent
     * GlsEntry or both have it and they are equivalent. This is what a human
     * translator would understand as already containing the checked glossary translation,
     * even if the comment, creation and last update dates differ.
     *
     * @param gt the GlsTranslation to be checked against this instance
     * @return true if gt is considered to be equivalent
     */
    public boolean isEquivalent(GlsTranslation gt) {
        return (gt != null) && (this.getValue().equals(gt.getValue()))
                && (this.getL10nId().equals(gt.getL10nId()))
                && ((this.getGlseId() == null && gt.getGlseId() == null)
                    || (this.getGlseId() != null && gt.getGlseId() != null
                        && this.getGlseId().isEquivalent(gt.getGlseId())));
    }

    @Override
    public String toString() {
        return "glossarymanager.model.GlsTranslation[ id=" + id + " ]";
    }

}
