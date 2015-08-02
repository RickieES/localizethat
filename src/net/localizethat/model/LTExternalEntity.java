/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.model;

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
public class LTExternalEntity extends LTContent {
    private static final long serialVersionUID = 1L;

    // Currently, we don't expect external entities of type PUBLIC
    transient private String publicId;

    /** Creates a new instance of ExternalEntity */
    public LTExternalEntity() {
        super();
    }

    public LTExternalEntity(String name, String publicId, String systemId) {
        super();
        setName(name);
        this.publicId = publicId;
        this.setTextValue(systemId);
    }

    public String getPublicId() {
        return (publicId == null) ? "" : publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
