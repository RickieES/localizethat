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
import java.util.Iterator;
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
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A glossary entry, ie., a term belonging to one Glossary, written in the same language
 * than it. A GlsEntry can have one or more approved translations (GlsTranslation), each
 * one in a language other than the glossary original language. A GlsEntry can have zero
 * or more GlsTranslation items for a given target language.
 * @author rpalomares
 */
@Entity
@Table(name = "APP.GLSENTRY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GlsEntry.findAll", query = "SELECT ge FROM GlsEntry ge ORDER BY ge.term"),
    @NamedQuery(name = "GlsEntry.findAllForGlossary", query = "SELECT ge FROM GlsEntry ge WHERE ge.glosId = :gid ORDER BY ge.term"),
    @NamedQuery(name = "GlsEntry.findById", query = "SELECT ge FROM GlsEntry ge WHERE ge.id = :id"),
    @NamedQuery(name = "GlsEntry.findByGlsTerm", query = "SELECT ge FROM GlsEntry ge WHERE ge.term = :glseterm"),
    @NamedQuery(name = "GlsEntry.findByGlsAndTerm", query = "SELECT ge FROM GlsEntry ge WHERE ge.glosId = :glosid AND ge.term = :glseterm"),
    @NamedQuery(name = "GlsEntry.findByGlsTermLoCase", query = "SELECT ge FROM GlsEntry ge WHERE LOWER(ge.term) = LOWER(:glseterm)"),
    @NamedQuery(name = "GlsEntry.findByGlsAndTermLoCase", query = "SELECT ge FROM GlsEntry ge WHERE ge.glosId = :glosid AND LOWER(ge.term) = LOWER(:glseterm)"),
    @NamedQuery(name = "GlsEntry.findByGlsecreationdate", query = "SELECT ge FROM GlsEntry ge WHERE ge.creationDate = :glsecreationdate"),
    @NamedQuery(name = "GlsEntry.findByGlselastupdate", query = "SELECT ge FROM GlsEntry ge WHERE ge.lastUpdate = :glselastupdate")})
public class GlsEntry implements Serializable {
    public static final int GLSTRNS_INITIAL_SIZE = 1;
    private static final long serialVersionUID = 1L;
    private static final int GLSETERM_LENGTH = 64;
    private static final int GLSECOMMENT_LENGTH = 32700;
    @TableGenerator(name="GLSENTRY", schema="APP", table="COUNTERS",
            pkColumnName="ENTITY", valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="GLSENTRY")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "GLSETERM", nullable = false, length = GLSETERM_LENGTH)
    private String term;
    @Lob
    @Column(name = "GLSECOMMENT", length = GLSECOMMENT_LENGTH)
    private String comment;
    @Column(name = "GLSEPARTOFSPEECH")
    @Enumerated(EnumType.STRING)
    private PartOfSpeech partOfSpeech;
    @Column(name = "GLSECREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "GLSELASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @JoinColumn(name = "GLOS_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Glossary glosId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "glseId")
    private Collection<GlsTranslation> glsTranslationCollection;
    @Column(name = "ENTITYVERSION")
    @Version
    private int entityVersion;

    public GlsEntry() {
        this.glsTranslationCollection = new ArrayList<>(GlsEntry.GLSTRNS_INITIAL_SIZE);
    }

    public GlsEntry(Integer id) {
        this();
        this.id = id;
    }

    public GlsEntry(Integer id, String glseterm) {
        this();
        this.id = id;
        this.term = glseterm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term.substring(0, Math.min(term.length(), GLSETERM_LENGTH));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment.substring(0, Math.min(comment.length(), GLSECOMMENT_LENGTH));
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

    public Glossary getGlosId() {
        return glosId;
    }

    public void setGlosId(Glossary glosId) {
        this.glosId = glosId;
    }

    @XmlTransient
    public Collection<GlsTranslation> getGlsTranslationCollection() {
        return glsTranslationCollection;
    }

    public void setGlsTranslationCollection(Collection<GlsTranslation> glsTranslationCollection) {
        this.glsTranslationCollection = glsTranslationCollection;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
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
        if (!(object instanceof GlsEntry)) {
            return false;
        }
        GlsEntry other = (GlsEntry) object;
        return (this.id != null || other.id == null)
                && (this.id == null || this.id.equals(other.id));
    }

    /**
     * Contrary to equals(), this method considers that two GlsEntry objects are
     * the same if they have the same term and the same PartOfSpeech. This is what
     * a human translator would understand as already containing the checked glossary
     * entry, even if the comment, creation and last update dates or even the possible
     * translation lists differ.
     * 
     * @param ge the GlsEntry to be checked against this instance
     * @return true if ge is considered to be equivalent
     */
    public boolean isEquivalent(GlsEntry ge) {
        return (ge != null) && (this.getTerm().equals(ge.getTerm()))
                && (this.getPartOfSpeech() == ge.getPartOfSpeech());
    }

    /**
     * Checks if a GlsTranslation exists in this glossary entry. Contrary to equals(),
     * this method considers that two GlsTranslation objects are the same if they have
     * the same value and the same L10n. This is what a human translator would
     * understand as already containing the checked glossary translation, even if the
     * comment, creation and last update dates or even the GlsEntry parent differ.
     *
     * @param gt the GlsTranslation to be checked against the GlsEntry translations collection
     * @return true if gt is considered to already exist in the GlsEntry translations collection, false otherwise
     */
    public boolean glsTranslationExists(GlsTranslation gt) {
        Iterator<GlsTranslation> ig = glsTranslationCollection.iterator();

        while (ig.hasNext()) {
            GlsTranslation glsTranslation = ig.next();

            if (glsTranslation.isEquivalent(gt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a GlsTranslation in this entry translations collection considered to
     * be the same than the GlsTranslation passed as a parameter, following the
     * same criteria than {@link #glsTranslationExists}
     *
     * @param gt the GlsTranslation for which we are looking for a equivalent translation in this entry
     * @return the equivalent GlsTranslation, or null if no equivalent translation is found in this Glossary
     */
    public GlsTranslation findGlsTranslation(GlsTranslation gt) {
        Iterator<GlsTranslation> ig = glsTranslationCollection.iterator();

        while (ig.hasNext()) {
            GlsTranslation glsTranslation = ig.next();

            if (glsTranslation.isEquivalent(gt)) {
                return glsTranslation;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "glossarymanager.model.GlsEntry[ id=" + id + " ]";
    }

}
