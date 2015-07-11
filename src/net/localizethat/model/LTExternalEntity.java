/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents an external entity. External entities are used in some
 * DTD files in Mozilla Localization (at the time of this writing, netError.dtd
 * and others), and must be stored apart from ordinary entities.
 *
 * @author rpalomares
 */

@Entity
@DiscriminatorValue("LTExternalEntity")
@XmlRootElement
public class LTExternalEntity extends LTContent implements EditableLocaleContent {
    public static final int TEXTVALUE_LENGTH = 32672;

    // Currently, we don't expect external entities of type PUBLIC
    transient private String publicId;
    // We will use the text value to save the SYSTEM ID
    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = TEXTVALUE_LENGTH)
    private String systemId;

    /** Creates a new instance of ExternalEntity */
    public LTExternalEntity() {
        super();
    }

    public LTExternalEntity(String name, String publicId, String systemId) {
        super();
        setName(name);
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public String getPublicId() {
        return (publicId == null) ? "" : publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getSystemId() {
        return (systemId == null) ? "" : systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public String getTextValue() {
        return getSystemId();
    }

    @Override
    public void setTextValue(String value) {
        // Do nothing, as the SYSTEM ID value shouldn't be localizable
    }
}
