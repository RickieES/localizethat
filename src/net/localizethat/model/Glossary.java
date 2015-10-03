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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
 * An object representing a glossary, ie., a named collection of terms in a given language,
 * each of one has one or more approved translations
 * @author rpalomares
 */
@Entity
@Table(name = "APP.GLOSSARY", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"GLOSNAME"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Glossary.countByL10n", query = "SELECT COUNT(g) FROM Glossary g WHERE g.l10nId = :l10nid"),
    @NamedQuery(name = "Glossary.findAll", query = "SELECT g FROM Glossary g ORDER BY g.name"),
    @NamedQuery(name = "Glossary.findById", query = "SELECT g FROM Glossary g WHERE g.id = :id"),
    @NamedQuery(name = "Glossary.findByGlosname", query = "SELECT g FROM Glossary g WHERE g.name = :name"),
    @NamedQuery(name = "Glossary.findByGlosversion", query = "SELECT g FROM Glossary g WHERE g.version = :version"),
    @NamedQuery(name = "Glossary.findByGloscreationdate", query = "SELECT g FROM Glossary g WHERE g.creationDate = :creationdate"),
    @NamedQuery(name = "Glossary.findByGloslastupdate", query = "SELECT g FROM Glossary g WHERE g.lastUpdate = :lastupdate")})
public class Glossary implements Serializable {
    public static final int GLS_INITIAL_SIZE = 50;
    private static final long serialVersionUID = 1L;
    private static final int GLOSNAME_LENGTH = 64;
    private static final int GLOSVERSION_LENGTH = 10;
    @TableGenerator(name="GLOSSARY", schema="APP", table="COUNTERS",
            pkColumnName="ENTITY", valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="GLOSSARY")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "GLOSNAME", nullable = false, length = GLOSNAME_LENGTH)
    private String name;
    @Basic(optional = false)
    @Column(name = "GLOSVERSION", nullable = false, length = GLOSVERSION_LENGTH)
    private String version;
    @Column(name = "GLOSCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "GLOSLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "glosId")
    private Collection<GlsEntry> glsEntryCollection;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @Column(name = "ENTITYVERSION")
    @Version
    private int entityVersion;

    public Glossary() {
        this.glsEntryCollection = new ArrayList<>(Glossary.GLS_INITIAL_SIZE);
    }

    public Glossary(Integer id) {
        this();
        this.id = id;
    }

    public Glossary(Integer id, String glosname, String glosversion) {
        this();
        this.id = id;
        this.name = glosname;
        this.version = glosversion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.substring(0, Math.min(name.length(), GLOSNAME_LENGTH));
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version.substring(0, Math.min(version.length(), GLOSVERSION_LENGTH));
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
    public Collection<GlsEntry> getGlsEntryCollection() {
        return glsEntryCollection;
    }

    public void setGlsEntryCollection(Collection<GlsEntry> glsEntryCollection) {
        this.glsEntryCollection = glsEntryCollection;
    }

    public L10n getL10nId() {
        return l10nId;
    }

    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
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
        if (!(object instanceof Glossary)) {
            return false;
        }
        Glossary other = (Glossary) object;
        return (this.id != null || other.id == null)
                && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getVersion() + ")";
    }
    
    /**
     * Checks if a GlsEntry exists in this glossary. Contrary to equals(), this
     * method considers that two GlsEntry are the same if they have the same term
     * and the same PartOfSpeech. This is what a human translator would understand
     * as already containing the checked glossary entry, even if the comment,
     * creation and last update dates the comments or even the possible translation
     * lists differ.
     * 
     * @param ge the GlsEntry to be checked against the Glossary entries collection
     * @return true if ge is considered to already exist in the Glossary entries collection, false otherwise
     */
    public boolean glsEntryExists(GlsEntry ge) {
        Iterator<GlsEntry> ig = glsEntryCollection.iterator();
        
        while (ig.hasNext()) {
            GlsEntry glsEntry = ig.next();
            
            if (glsEntry.isEquivalent(ge)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds a GlsEntry in this glossary entries collection considered to be the same
     * than the GlsEntry passed as a parameter, following the same criteria than
     * {@link #glsEntryExists}
     * 
     * @param ge the GlsEntry for which we are looking for a equivalent entry in this Glossary
     * @return the equivalent GlsEntry, or null if no entry is found in this Glossary
     */
    public GlsEntry findGlsEntry(GlsEntry ge) {
        Iterator<GlsEntry> ig = glsEntryCollection.iterator();
        
        while (ig.hasNext()) {
            GlsEntry glsEntry = ig.next();
            
            if (glsEntry.isEquivalent(ge)) {
                return glsEntry;
            }
        }
        return null;
    }

}
