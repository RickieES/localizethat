/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
 * Superclass of all file contents, except images and non parseable files. This
 * class defines the entity and the main JPA mapping
 * @author rpalomares
 */
@Entity(name="LocaleContent")
@Table(name = "APP.LOCALECONTENT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="LC_TYPE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LocaleContent.countAll", query = "SELECT COUNT(lc) FROM LocaleContent lc"),
    @NamedQuery(name = "LocaleContent.count", query = "SELECT COUNT(lc) FROM LocaleContent lc")
})
public class LTContent implements LocaleContent {
    private static final long serialVersionUID = 1L;
    private static final int LOCALENODENAME_LENGTH = 128;
    public static final int TEXTVALUE_LENGTH = 32672;

    /**
     * Comparator based in the position of the LTContent objects in a file
     */
    public static Comparator<LocaleContent> orderInFileComparator = new Comparator<LocaleContent>() {
            @Override
            public int compare(LocaleContent o1, LocaleContent o2) {
                return o1.getOrderInFile() - o2.getOrderInFile();
            }
    };

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
    private LocaleFile parent;
    @JoinColumn(name = "LNODETWIN", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, targetEntity=LTContent.class)
    private LocaleContent defLocaleTwin;
    @OneToMany(mappedBy="defLocaleTwin", targetEntity=LTContent.class)
    private Collection<LocaleContent> twins;
    @Basic(optional = false)
    @Column(name = "LCONTENTDONTEXPORT", nullable = false)
    private boolean dontExport;
    @Basic(optional = false)
    @Column(name = "LCONTENTORDERINFILE", nullable = false)
    private int orderInFile;
    @JoinColumn(name = "L10N_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private L10n l10nId;
    @Basic(optional = false)
    @Column(name = "LCONTENTTEXTVALUE", nullable = false, length = TEXTVALUE_LENGTH)
    private String textValue;
    @Basic(optional = true)
    @Column(name = "LCONTENTKEEPORIG", nullable = true)
    private boolean keepOriginal;
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODECREATIONDATE", nullable = false)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LNODELASTUPDATE", nullable = false)
    private Date lastUpdate;
    
    // Used during parsing of files to put apart LTContent no longer present in the file
    private transient boolean markedForDeletion;

    public LTContent() {
        super();
        this.orderInFile = 0;
        this.dontExport = false; // So, by default, we want to export content
        twins = new ArrayList<>(1); // Most of the time, there will be just one twin
    }

    public LTContent(int orderInFile) {
        super();
        this.orderInFile = orderInFile;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name.substring(0, Math.min(name.length(), LOCALENODENAME_LENGTH));
    }

    @Override
    public LocaleFile getParent() {
        return parent;
    }

    @Override
    public void setParent(LocaleNode parent) {
        setParent((LocaleFile) parent);
    }
    
    @Override
    public void setParent(LocaleFile parent) {
        this.parent = parent;
    }

    @Override
    public String getFilePath() {
        StringBuilder sb = new StringBuilder(64);
        LocaleFile p = getParent();

        if (p != null) {
            sb.append(p.getFilePath());
        }
        // We use the "/" literal instead of file.separator to avoid mixing of separators
        sb.append("/").append(getName());
        return sb.toString();
    }

    @Override
    public boolean addChild(LocaleNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasChild(LocaleNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasChild(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasChild(String name, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocaleNode getChildByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocaleNode getChildByName(String name, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends LocaleNode> getChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocaleNode removeChild(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LocaleNode removeChild(String name, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeChild(LocaleNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean clearChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDefLocaleTwin(LocaleNode twin) {
        // The twin must not be null, must be of the same class and must be a default locale itself
        if ((twin != null) && (twin instanceof LocaleContent)
                && (twin.getDefLocaleTwin() == null)) {
            this.defLocaleTwin = (LocaleContent) twin;
            if (twin.getTwinByLocale(l10nId) == null) {
                twin.addTwin(this);
            }
        } else {
            twin = this.defLocaleTwin;
            this.defLocaleTwin = null;
            if ((twin != null) && (twin.getTwinByLocale(l10nId) == this)) {
                twin.removeTwin(this);
            }
        }
    }

    @Override
    public LocaleContent getDefLocaleTwin() {
        return defLocaleTwin;
    }

    @Override
    public boolean addTwin(LocaleNode twin) {
        boolean result;

        // We only add non-null twins, of the same class and only on the default locale twin,
        // so there is only one list of twins kept in the default locale twin. getTwinByLocale
        // will look up in the default locale twin if called on a non-default locale twin
        // And, of course, we only add twins not already in the list
        result = ((twin != null) && (twin instanceof LocaleContent)
                && (twin.getDefLocaleTwin() == this) && (!isATwin(twin)));

        if (result) {
            this.twins.add((LocaleContent) twin);
        }
        return result;
    }

    @Override
    public boolean removeTwin(LocaleNode twin) {
        boolean result;

        result = (twin != null)
                && (twin instanceof LTContent)
                && (twins.contains((LTContent) twin))
                && twins.remove((LTContent) twin);
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
    public LocaleContent getTwinByLocale(L10n locale) {
        // If this node is not a default locale twin, its twins collection should be empty, so
        // we call the method for the default locale twin
        if (this.getDefLocaleTwin() != null) {
            return this.getDefLocaleTwin().getTwinByLocale(locale);
        }

        for(LocaleContent s : twins) {
            if (s.getL10nId().equals(locale)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Collection<? extends LocaleContent> getTwins() {
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
    public int getOrderInFile() {
        return orderInFile;
    }

    @Override
    public void setOrderInFile(int orderInFile) {
        this.orderInFile = orderInFile;
    }

    @Override
    public boolean isDontExport() {
        return dontExport;
    }

    @Override
    public void setDontExport(boolean dontExport) {
        this.dontExport = dontExport;
    }

    @Override
    public boolean isKeepOriginal() {
        return keepOriginal;
    }

    @Override
    public void setKeepOriginal(boolean keepOriginal) {
        this.keepOriginal = keepOriginal;
    }
    
    @Override
    public String getTextValue() {
        return textValue;
    }

    @Override
    public void setTextValue(String textValue) {
        this.textValue = textValue.substring(0, Math.min(textValue.length(), LTKeyValuePair.TEXTVALUE_LENGTH));
    }

    @Override
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    @Override
    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    /**
     * This method returns true if the instance of LTContent can be edited/localized
     * (by default it is). Subclasses of LTContent should override this method
     * returning the appropiate value
     * @return true if the LTContent is intended to be editable/localizable
     */
    @Override
    public boolean isEditable() {
        return true;
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
