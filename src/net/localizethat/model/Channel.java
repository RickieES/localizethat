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
import javax.persistence.Lob;
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
 * Channel entity
 * @author rpalomares
 */
@Entity
@Table(name = "APP.CHANNEL", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"CHNLNAME"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Channel.countAll", query = "SELECT COUNT(c) FROM Channel c"),
    @NamedQuery(name = "Channel.countById", query = "SELECT COUNT(c) FROM Channel c WHERE c.id = :id"),
    @NamedQuery(name = "Channel.countByName", query = "SELECT COUNT(c) FROM Channel c WHERE c.name = :name"),
    @NamedQuery(name = "Channel.findAll", query = "SELECT c FROM Channel c"),
    @NamedQuery(name = "Channel.findById", query = "SELECT c FROM Channel c WHERE c.id = :id"),
    @NamedQuery(name = "Channel.findByName", query = "SELECT c FROM Channel c WHERE c.name = :name"),
    @NamedQuery(name = "Channel.findByReplacementTag", query = "SELECT c FROM Channel c WHERE c.replacementTag = :replacementtag"),
    @NamedQuery(name = "Channel.findByReplacementText", query = "SELECT c FROM Channel c WHERE c.replacementText = :replacementtext"),
    @NamedQuery(name = "Channel.findByCreationDate", query = "SELECT c FROM Channel c WHERE c.creationDate = :creationdate"),
    @NamedQuery(name = "Channel.findByLastUpdate", query = "SELECT c FROM Channel c WHERE c.lastUpdate = :lastupdate")})
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int CHNLNAME_LENGTH = 32;
    private static final int CHNLDESCRIPTION_LENGTH = 32700;
    private static final int CHNLREPLACEMENTTAG_LENGTH = 16;
    private static final int CHNLREPLACEMENTTEXT_LENGTH = 64;
    @TableGenerator(name="CHANNEL", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 2)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="CHANNEL")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "CHNLNAME", nullable = false, length = CHNLNAME_LENGTH)
    private String name;
    @Lob
    @Column(name = "CHNLDESCRIPTION", length = CHNLDESCRIPTION_LENGTH)
    private String description;
    @Column(name = "CHNLREPLACEMENTTAG", length = CHNLREPLACEMENTTAG_LENGTH)
    private String replacementTag;
    @Column(name = "CHNLREPLACEMENTTEXT", length = CHNLREPLACEMENTTEXT_LENGTH)
    private String replacementText;
    @Column(name = "CHNLCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "CHNLLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    public Channel() {
    }

    public Channel(Integer id) {
        this.id = id;
    }

    public Channel(Integer id, String chnlname) {
        this.id = id;
        this.name = chnlname;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReplacementTag() {
        return replacementTag;
    }

    public void setReplacementTag(String replacementTag) {
        this.replacementTag = replacementTag;
    }

    public String getReplacementText() {
        return replacementText;
    }

    public void setReplacementText(String replacementText) {
        this.replacementText = replacementText;
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
        if (!(object instanceof Channel)) {
            return false;
        }
        Channel other = (Channel) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return name;
    }

}
