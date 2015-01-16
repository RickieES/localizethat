/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rpalomares
 */
@Entity
@Table(name = "APP.LOCALEFILE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="LF_TYPE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
    @NamedQuery(name = "LocaleFile.count", query = "SELECT COUNT(lf) FROM LocaleFile lf")
})
public class LocaleFile extends AbstractLocaleNode<LocaleContainer, LocaleFile, LocaleContent> implements Serializable {

    public static LocaleFile createFile(String fileName, LocaleContainer parent) {
        LocaleFile newFile;
        String extension;
        int extStart = fileName.lastIndexOf('.');

        if (extStart == -1) {
            extension = "(none)";
        } else {
            extension = fileName.substring(extStart +1).toLowerCase();
        }

        switch (extension) {
            case "dtd":
                newFile = new DtdFile();
                break;
            case "properties":
                newFile = new PropertiesFile();
                break;
            case "ini": // TODO perhaps a different class is needed later
                newFile = new PropertiesFile();
                break;
            case "gif":
            case "jpeg":
            case "jpg":
            case "png":
                newFile = new ImageFile();
                break;
            case "css":
                newFile = new TextFile();
                break;
            case "html": // TODO perhaps a real HTML class & parser could be created in the future
            case "xhtml":
                newFile = new TextFile();
                break;
            case "inc":
                newFile = new TextFile();
                break;
            case "js":
                newFile = new TextFile();
                break;
            case "txt":
                newFile = new TextFile();
                break;
            case "xml": // TODO perhaps a real XML class & parser could be created in the future
                newFile = new TextFile();
                break;
            default:
                newFile = new TextFile();
        }
        Date creationDate = new Date();
        newFile.setCreationDate(creationDate);
        newFile.setLastUpdate(creationDate);
        newFile.setName(fileName);
        newFile.setParent(parent);
        return newFile;
    }

    protected LocaleFile() {
        super();
        // children = new ArrayList<>(25);
    }

    public File getFile() {
        return new File(getFilePath());
    }



}
