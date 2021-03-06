/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Main entity for LocaleFile instances
 * @author rpalomares
 */

@Entity
@Table(name = "APP.LOCALEFILE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="LF_TYPE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleFile.countAll", query = "SELECT COUNT(lf) FROM LocaleFile lf"),
    @NamedQuery(name = "LocaleFile.countByL10n", query = "SELECT COUNT(lf) FROM LocaleFile lf WHERE lf.l10nId = :l10nid"),
})
public class LocaleFile implements LocaleNode, Serializable {
    private static final int LOCALENODENAME_LENGTH = 128;
    private static final long serialVersionUID = 1L;

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
            case "ini":
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
        newFile.setName(fileName);
        newFile.setParent(parent);
        newFile.setL10nId(parent.getL10nId());
        Date creationDate = new Date();
        newFile.setCreationDate(creationDate);
        newFile.setLastUpdate(creationDate);
        return newFile;
    }
    @TableGenerator(name="LOCALENODE", schema="APP", table="COUNTERS", pkColumnName="ENTITY",
            valueColumnName="COUNTERVALUE", allocationSize = 5)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="LOCALENODE")
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Version
    @Column(name = "ENTITYVERSION")
    private int entityVersion;
    @Basic(optional = false)
    @Column(name = "LNODENAME", nullable = false, length = LOCALENODENAME_LENGTH)
    private String name;
    @JoinColumn(name = "LNODEPARENT", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = false)
    private LocaleContainer parent;
    @JoinColumn(name = "LNODETWIN", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true)
    private LocaleFile defLocaleTwin;
    @OneToMany(mappedBy="defLocaleTwin")
    private final Collection<LocaleFile> twins;
    @OneToMany(targetEntity=LTContent.class, cascade = CascadeType.ALL, mappedBy = "parent")
    protected Collection<LocaleContent> children;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODECREATIONDATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODELASTUPDATE", nullable = false)
    private Date lastUpdate;

    @Basic(optional = false)
    @Column(name = "LFILEDONTEXPORT", nullable = false)
    private boolean dontExport;

    protected LocaleFile() {
        super();
        children = new ArrayList<>(25);
        twins = new ArrayList<>(1); // Most of the time, there will be just one twin
    }

    @Override
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

    @Override
    public void setName(String name) {
        this.name = name.substring(0, Math.min(name.length(), LOCALENODENAME_LENGTH));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final void setParent(LocaleNode parent) {
        if (parent instanceof LocaleContainer) {
            this.parent = (LocaleContainer) parent;
        }
    }

    @Override
    public LocaleContainer getParent() {
        return parent;
    }

    @Override
    public String getFilePath() {
        StringBuilder sb = new StringBuilder(64);
        LocaleContainer p = getParent();

        if (p != null) {
            sb.append(p.getFilePath());
        }
        // We use the "/" literal instead of file.separator to avoid mixing of separators
        sb.append("/").append(getName());
        return sb.toString();
    }

    @Override
    public boolean addChild(LocaleNode node) {
        if ((node instanceof LTContent) && (!hasChild(node))) {
            LTContent e = (LTContent) node;
            children.add(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasChild(LocaleNode node) {
        if (node instanceof LTContent) {
            return children.contains((LTContent) node);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasChild(String name) {
        return hasChild(name, false);
    }

    @Override
    public boolean hasChild(String name, boolean matchCase) {
        boolean found = false;

        for(LocaleContent l : children) {
            found = found || ((matchCase) ? (l.getName().equals(name)) : (l.getName().equalsIgnoreCase(name)));
        }
        return found;
    }

    @Override
    public LocaleContent getChildByName(String name) {
        return getChildByName(name, false);
    }

    @Override
    public LocaleContent getChildByName(String name, boolean matchCase) {
        if (name == null) {
            return null;
        }

        for(LocaleContent l : children) {
            boolean found = (matchCase) ? (name.equals(l.getName()))
                                        : (name.equalsIgnoreCase(l.getName()));
            if (found) {
                return l;
            }
        }
        return null;
    }

    public LocaleContent getChildByOrderInFile(int orderInFile) {
        for(LocaleContent l : children) {
            boolean found = (l.getOrderInFile() == orderInFile);
            if (found) {
                return l;
            }
        }
        return null;
    }

    @Override
    public Collection<LocaleContent> getChildren() {
        return children;
    }

    @Override
    public LocaleContent removeChild(String name) {
        return removeChild(name, false);
    }

    @Override
    public LocaleContent removeChild(String name, boolean matchCase) {
        LocaleContent l = getChildByName(name, matchCase);

        if ((l != null) && (removeChild(l))) {
            return l;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        if (node instanceof LTContent) {
            return children.remove((LTContent) node);
        } else {
            return false;
        }
    }

    @Override
    public boolean clearChildren() {
        try {
            children.clear();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    @Override
    public void setDefLocaleTwin(LocaleNode twin) {
        // The twin must not be null, must be of the same class and must be a default locale itself
        if ((twin != null) && (twin instanceof LocaleFile)
                && (twin.getDefLocaleTwin() == null)) {
            this.defLocaleTwin = (LocaleFile) twin;
            if (twin.getTwinByLocale(l10nId) == null) {
                twin.addTwin(this);
            }
        } else {
            LocaleNode savedTwin = this.defLocaleTwin;
            this.defLocaleTwin = null;
            if ((savedTwin != null) && (savedTwin.getTwinByLocale(l10nId) == this)) {
                savedTwin.removeTwin(this);
            }
        }
    }

    @Override
    public LocaleFile getDefLocaleTwin() {
        return defLocaleTwin;
    }
    
    @Override
    public boolean addTwin(LocaleNode twin) {
        boolean result;

        // We only add non-null twins, of the same class and only on the default locale twin,
        // so there is only one list of twins kept in the default locale twin. getTwinByLocale
        // will look up in the default locale twin if called on a non-default locale twin
        result = ((twin != null) && (twin instanceof LocaleFile)
                && (twin.getDefLocaleTwin() == this) && (!isATwin(twin)));

        if (result) {
            this.twins.add((LocaleFile) twin);
        }
        return result;
    }

    @Override
    public boolean removeTwin(LocaleNode twin) {
        boolean result;

        result = (twin != null)
                && (twin instanceof LocaleFile)
                && (twins.contains((LocaleFile) twin))
                && twins.remove((LocaleFile) twin);
        return result;
    }

    @Override
    public boolean isATwin(LocaleNode possibleTwin) {
        // If this node is not a default locale twin, its twins collection should be empty, so
        // we call the method for the default locale twin
        if (this.getDefLocaleTwin() != null) {
            return this.getDefLocaleTwin().isATwin(possibleTwin);
        }

        for(LocaleNode s : twins) {
            if (s.equals(possibleTwin)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LocaleFile getTwinByLocale(L10n locale) {
        // If this node is not a default locale twin, its twins collection should be empty, so
        // we call the method for the default locale twin
        if (this.getDefLocaleTwin() != null) {
            return this.getDefLocaleTwin().getTwinByLocale(locale);
        }

        for(LocaleFile s : twins) {
            if (s.getL10nId().equals(locale)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Collection<LocaleFile> getTwins() {
        // If this node is not a default locale twin, its twins collection should be empty, so
        // we call the method for the default locale twin
        if (this.getDefLocaleTwin() != null) {
            return this.getDefLocaleTwin().getTwins();
        }

        return twins;
    }

    @Override
    public L10n getL10nId() {
        return l10nId;
    }

    @Override
    public void setL10nId(L10n l10nId) {
        this.l10nId = l10nId;
    }

    public File getFile() {
            return new File(getFilePath());
    }
    
    public InputStream getAsInputStream() {
        return getAsInputStream(getFile());
    }

    public InputStream getAsInputStream(File f) {
        FileInputStream is;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            Logger.getLogger(LocaleFile.class.getName()).log(Level.SEVERE, null, e);
            is = null;
        }
        return is;
    }

    public LineNumberReader getAsLineNumberReader() {
        return getAsLineNumberReader(getFile());
    }
    
    public LineNumberReader getAsLineNumberReader(File f) {
        LineNumberReader is;
        try {
            is = new LineNumberReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            Logger.getLogger(LocaleFile.class.getName()).log(Level.SEVERE, null, e);
            is = null;
        }
        return is;
    }
    
    public OutputStream getAsOutputStream() {
        return getAsOutputStream(getFile());
    }
    
    public OutputStream getAsOutputStream(File f) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(f);
        } catch (IOException e) {
            // TODO log the exception
            os = null;
        }
        return os;
    }
    
    public PrintWriter getAsPrintWriter() {
        return getAsPrintWriter(getFile());
    }
    
    public PrintWriter getAsPrintWriter(File f) {
        PrintWriter os;
        try {
            os = new PrintWriter(new FileWriter(f));
        } catch (IOException ex) {
            Logger.getLogger(LocaleFile.class.getName()).log(Level.SEVERE, null, ex);
            os = null;
        }
        return os;
    }

    public boolean isDontExport() {
        return dontExport;
    }

    public void setDontExport(boolean dontExport) {
        this.dontExport = dontExport;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int compareTo(LocaleNode o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
