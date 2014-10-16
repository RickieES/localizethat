/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.L10N", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"L10NCODE"}),
    @UniqueConstraint(columnNames = {"L10NNAME"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "L10n.countAll", query = "SELECT COUNT(l) FROM L10n l"),
    @NamedQuery(name = "L10n.countById", query = "SELECT COUNT(l) FROM L10n l WHERE l.id = :id"),
    @NamedQuery(name = "L10n.countByL10ncode", query = "SELECT COUNT(l) FROM L10n l WHERE l.code = :code"),
    @NamedQuery(name = "L10n.findAll", query = "SELECT l FROM L10n l ORDER BY l.code"),
    @NamedQuery(name = "L10n.findById", query = "SELECT l FROM L10n l WHERE l.id = :id"),
    @NamedQuery(name = "L10n.findByL10ncode", query = "SELECT l FROM L10n l WHERE l.code = :code"),
    @NamedQuery(name = "L10n.findByL10nname", query = "SELECT l FROM L10n l WHERE l.name = :name"),
    @NamedQuery(name = "L10n.findByL10nteamname", query = "SELECT l FROM L10n l WHERE l.teamName = :teamname"),
    @NamedQuery(name = "L10n.findByL10nurl", query = "SELECT l FROM L10n l WHERE l.url = :url"),
    @NamedQuery(name = "L10n.findByL10ncreationdate", query = "SELECT l FROM L10n l WHERE l.creationDate = :creationdate"),
    @NamedQuery(name = "L10n.findByL10nlastupdate", query = "SELECT l FROM L10n l WHERE l.lastUpdate = :lastupdate")})
public class L10n implements Serializable, Comparable<L10n> {
    private static final long serialVersionUID = 1L;
    private static final int L10NCODE_LENGTH = 10;
    private static final int L10NNAME_LENGTH = 80;
    private static final int L10NTEAMNAME_LENGTH = 80;
    private static final int L10NURL_LENGTH = 160;
    @Transient
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @TableGenerator(name="L10N", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="L10N")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "L10NCODE", nullable = false, length = L10NCODE_LENGTH)
    private String code;
    @Column(name = "L10NNAME", length = L10NNAME_LENGTH)
    private String name;
    @Column(name = "L10NTEAMNAME", length = L10NTEAMNAME_LENGTH)
    private String teamName;
    @Column(name = "L10NURL", length = L10NURL_LENGTH)
    private String url;
    @Column(name = "L10NCREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "L10NLASTUPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column(name = "ENTITYVERSION")
    @Version
    private int entityVersion;

    public L10n() {
    }

    public L10n(Integer id) {
        this.id = id;
    }

    public L10n(Integer id, String l10ncode) {
        this.id = id;
        this.code = l10ncode.substring(0, L10NCODE_LENGTH);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        String oldL10ncode = this.code;
        String newL10ncode = code.substring(0, Math.min(code.length(), L10NCODE_LENGTH));
        this.code = newL10ncode;
        changeSupport.firePropertyChange("L10ncode", oldL10ncode, newL10ncode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldL10nname = this.name;
        String newL10nname = name.substring(0, Math.min(name.length(), L10NNAME_LENGTH));
        this.name = newL10nname;
        changeSupport.firePropertyChange("L10nname", oldL10nname, newL10nname);
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        String oldL10nteamname = this.teamName;
        String newL10nteamname = teamName.substring(0, Math.min(teamName.length(), L10NTEAMNAME_LENGTH));
        this.teamName = newL10nteamname;
        changeSupport.firePropertyChange("L10nteamname", oldL10nteamname, newL10nteamname);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        String oldL10nurl = this.url;
        String newL10nurl = url.substring(0, Math.min(url.length(), L10NURL_LENGTH));
        this.url = newL10nurl;
        changeSupport.firePropertyChange("L10nurl", oldL10nurl, newL10nurl);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        Date oldL10ncreationdate = this.creationDate;
        this.creationDate = creationDate;
        changeSupport.firePropertyChange("L10ncreationdate", oldL10ncreationdate, creationDate);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        Date oldL10nlastupdate = this.lastUpdate;
        this.lastUpdate = lastUpdate;
        changeSupport.firePropertyChange("L10nlastupdate", oldL10nlastupdate, lastUpdate);
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
        if (!(object instanceof L10n)) {
            return false;
        }
        L10n other = (L10n) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public int compareTo(L10n o) {
        int result;

        result = (this.getCode().compareTo(o.getCode()));
        if (result == 0) {
            result = (this.getCreationDate().compareTo(o.getCreationDate()));
        }
        return result;
    }
}
