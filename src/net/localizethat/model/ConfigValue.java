/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.CONFIG")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConfigValue.findAll", query = "SELECT c FROM ConfigValue c"),
    @NamedQuery(name = "ConfigValue.findById", query = "SELECT c FROM ConfigValue c WHERE c.id = :id"),
    @NamedQuery(name = "ConfigValue.findByIdPrefix", query = "SELECT c FROM ConfigValue c WHERE c.id LIKE :idPrefix"),
    @NamedQuery(name = "ConfigValue.findByConfigValue", query = "SELECT c FROM ConfigValue c WHERE c.configValue = :configValue")})
public class ConfigValue implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int CONFIGID_LENGTH = 32;
    private static final int CONFIGVALUE_LENGTH = 128;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, length = CONFIGID_LENGTH)
    private String id;
    @Column(name = "CONFIGVALUE", length = CONFIGVALUE_LENGTH)
    private String configValue;

    public ConfigValue() {
    }

    public ConfigValue(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConfigValue)) {
            return false;
        }
        ConfigValue other = (ConfigValue) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "ConfigValue id=" + id + ", value=" + configValue;
    }

}
