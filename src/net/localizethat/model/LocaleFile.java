/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
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
public class LocaleFile implements LocaleNode, Serializable {
    private static final int LOCALENODENAME_LENGTH = 128;

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
    private Collection<LocaleFile> twins;
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    transient protected Collection<LocaleContent> children;
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
        // children = new ArrayList<>(25);
    }

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
        if ((node instanceof LocaleContent) && (!hasChild(node))) {
            children.add((LocaleContent) node);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasChild(LocaleNode node) {
        if (node instanceof LocaleContent) {
            return children.contains((LocaleContent) node);
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
    public LocaleNode getChildByName(String name) {
        return getChildByName(name, false);
    }

    @Override
    public LocaleNode getChildByName(String name, boolean matchCase) {
        for(LocaleNode l : children) {
            boolean found = (matchCase) ? (l.getName().equals(name))
                                        : (l.getName().equalsIgnoreCase(name));
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
    public LocaleNode removeChild(String name) {
        return removeChild(name, false);
    }


    @Override
    public LocaleNode removeChild(String name, boolean matchCase) {
        LocaleContent l = (LocaleContent) getChildByName(name, matchCase);

        if ((l != null) && (removeChild(l))) {
            return l;
        } else {
            return null;
        }
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        if (node instanceof LocaleContent) {
            return children.remove((LocaleContent) node);
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
        if ((twin != null) && (twin instanceof LocaleFile)) {
            this.defLocaleTwin = (LocaleFile) twin;
            twin.addTwin(this);
        } else {
            this.defLocaleTwin = null;
        }
    }

    @Override
    public LocaleNode getDefLocaleTwin() {
        return defLocaleTwin;
    }
    
    @Override
    public boolean addTwin(LocaleNode twin) {
        boolean result;

        result = (twin.getDefLocaleTwin() == this);

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
                && (twin.getDefLocaleTwin() == null)
                && (twins.contains((LocaleFile) twin))
                && twins.remove((LocaleFile) twin);
        return result;
    }

    @Override
    public boolean isATwin(LocaleNode possibleTwin) {
        for(LocaleNode s : twins) {
            if (s.equals(possibleTwin)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<LocaleFile> getTwins() {
        return twins;
    }

    public File getFile() {
        return new File(getFilePath());
    }

    public boolean isDontExport() {
        return dontExport;
    }

    public void setDontExport(boolean dontExport) {
        this.dontExport = dontExport;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
