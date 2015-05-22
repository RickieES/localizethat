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
 *
 * @author rpalomares
 */
@Entity
@DiscriminatorValue("TextFile")
@XmlRootElement
@SecondaryTable(name="LFILELOBS", schema ="APP",
        pkJoinColumns=@PrimaryKeyJoinColumn(name="ID"))
//@NamedQueries({
//    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
//    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
//})
public class TextFile extends LocaleFile {
    private static final int TEXTFILE_LENGTH = 512*1024; // We expect files no longer than 512 KBytes
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(table = "APP.LFILELOBS", name="LFILECLOB", length = TEXTFILE_LENGTH)
    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void clearFileContent() {
        this.fileContent = "";
    }



}
