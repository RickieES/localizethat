/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("ImageFile")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
//    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
//})
public class ImageFile extends LocaleFile {
    @Lob
    @Basic(fetch=FetchType.LAZY)
    byte[] imageData;

}
