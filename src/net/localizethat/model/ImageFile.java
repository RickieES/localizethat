/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Image file, as a binary file representing an image in JPEG, PNG or GIF formats.
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("ImageFile")
@XmlRootElement
@SecondaryTable(name="LFILELOBS", schema = "APP",
        pkJoinColumns=@PrimaryKeyJoinColumn(name="ID"))
//@NamedQueries({
//    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
//    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
//})
public class ImageFile extends LocaleFile {
    private static final int BINARYFILE_LENGTH = 1048576; // We expect files no longer than 1 MBytes
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(table = "APP.LFILELOBS", name = "LFILEBINARYCONTENT", length = BINARYFILE_LENGTH)
    private byte[] imageData;

    @Basic(optional = false)
    @Column(name = "LFILEMD5HASH", nullable = false)
    private String md5Hash;

}
