/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.xml.bind.annotation.XmlRootElement;
import net.localizethat.io.parsers.PropertiesReadHelper;
import net.localizethat.util.BlobChecker;

/**
 * Class for unparsed text files
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
    public static final int TEXTFILE_LENGTH = 512*1024; // We expect files no longer than 512 KBytes
    private static final String CHILDNAME = "text-content";
    private static final long serialVersionUID = 1L;
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(table = "APP.LFILELOBS", name="LFILECLOB", length = TEXTFILE_LENGTH)
    private String fileContent;

    @Basic(optional = false)
    @Column(name = "LFILEMD5HASH", nullable = false)
    private String md5Hash;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
        setMd5Hash(BlobChecker.getMD5Hash(fileContent));
    }

    public void clearFileContent() {
        this.fileContent = "";
    }

    public void setFileContent(String fileContent, String md5Hash) {
        this.fileContent = fileContent;
        this.md5Hash = md5Hash;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    @Override
    public boolean addChild(LocaleNode node) {
        return false;
    }

    @Override
    public boolean clearChildren() {
        return false;
    }

    @Override
    public LocaleContent getChildByName(String name, boolean matchCase) {
        if (super.getChildByName(CHILDNAME, false) == null) {
            LTTextContent child = new LTTextContent();
            child.setName(CHILDNAME);
            child.setParent(this);
            child.setOrderInFile(0);
            super.addChild(child);
        }
        return super.getChildByName(CHILDNAME, false);
    }

    @Override
    public LocaleContent removeChild(String name, boolean matchCase) {
        return null;
    }
    
    @Override
    public boolean removeChild(LocaleNode node) {
        return false;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        super.setCreationDate(creationDate);
        getChildByName(CHILDNAME).setCreationDate(creationDate);
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        super.setLastUpdate(lastUpdate);
        getChildByName(CHILDNAME).setLastUpdate(lastUpdate);
    }

    @Override
    public void setL10nId(L10n l10nId) {
        super.setL10nId(l10nId);
        getChildByName(CHILDNAME).setL10nId(l10nId);
    }

    @Override
    public void setDefLocaleTwin(LocaleNode twin) {
        // The twin must not be null, must be of the same class and
        // must be a default locale itself
        if ((twin != null) && (twin instanceof TextFile)
                && (twin.getDefLocaleTwin() == null)) {
            super.setDefLocaleTwin(twin);
            LocaleContent child = getChildByName(CHILDNAME);
            LocaleContent defLocaleTwinChild = ((TextFile) twin).getChildByName(CHILDNAME);
            if (child != null && defLocaleTwinChild != null) {
                child.setDefLocaleTwin(defLocaleTwinChild);
            }
        } else {
            super.setDefLocaleTwin(null);
        }
    }

    public List<LocaleContent> update(EntityManager em) {
        List<LocaleContent> newAndModifiedList = new ArrayList<>(1);
        String line;
        StringBuilder sb;
        // We're parsing the original if this file has no default twin
        boolean isParsingOriginal = (this.getDefLocaleTwin() == null);

        LineNumberReader fileReader = this.getAsLineNumberReader();
        if (fileReader == null) {
            return null;
        }

        sb = new StringBuilder((int) getFile().length());

        try {
            line = fileReader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = fileReader.readLine();
            }
            fileReader.close();

            // We ask for the MD5 because it is fetched eagerly and, if it is empty, then
            // also the full file content is empty
            if (getMd5Hash() == null || getMd5Hash().isEmpty()) {
                setFileContent(sb.toString());
                setLastUpdate(new Date());
                newAndModifiedList.add(getChildByName(CHILDNAME));
            } else {
                String newMd5 = BlobChecker.getMD5Hash(sb.toString());
                if (newMd5 != null && newMd5.compareTo(getMd5Hash()) != 0) {
                    setFileContent(sb.toString(), newMd5);
                    setLastUpdate(new Date());
                    newAndModifiedList.add(getChildByName(CHILDNAME));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PropertiesReadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newAndModifiedList;
    }

    public boolean exportToFile(File f) throws IOException {
        boolean result = false;
        PrintWriter pw;

        pw = getAsPrintWriter(f);

        if (pw != null) {
            result = true;
            if (getChildByName(CHILDNAME).isKeepOriginal()) {
                pw.print(((TextFile) getDefLocaleTwin()).getFileContent());
            } else {
                pw.print(getFileContent());
            }
            pw.flush();
            pw.close();
        }
        return result;
    }

}
