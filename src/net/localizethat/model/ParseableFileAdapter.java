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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.RollbackException;
import net.localizethat.model.jpa.LocaleContentJPAHelper;

/**
 * Adapter class that extends LocaleFile (thus, implementing LocaleNode) and
 * implements ParseableFile to provide the general process of updating a
 * parseable file. Two abstract methods deal with the previous parsing of
 * @author rpalomares
 */
@MappedSuperclass
public abstract class ParseableFileAdapter extends LocaleFile implements ParseableFile {
    private static final long serialVersionUID = 1L;
    @OneToOne(optional = true)
    @JoinColumn(name = "LFILELICENSE")
    LTLicense fileLicense;

    @Override
    public List<LocaleContent> update(EntityManager em, LocaleContentJPAHelper lcntHelper)
                throws ParseException {
        List<LocaleContent> newAndModifiedList = new ArrayList<>(10);
        // We're parsing the original if this file has no default twin

        LineNumberReader fileReader = this.getAsLineNumberReader();
        if (fileReader == null) {
            return null;
        }

        // beforeParsingHook is where the actual parsing happens
        List<LocaleContent> parsedContentList = beforeParsingHook(fileReader);

        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            
            // We mark all contents for deletion; every object modified or added will
            // have their markedForDeletion reset, so in the end we will have that mark
            // only in the objects no longer existing in the file
            for (LocaleContent lc : this.children) {
                lc.setMarkedForDeletion(true);
            }

            for (LocaleContent lcObject : parsedContentList) {
                lcObject.setParent(this);
                lcObject.setL10nId(this.getL10nId());

                switch (lcObject.getClass().getSimpleName()) {
                    case "LTLicense":
                        processLocaleContent((LTLicense) lcObject, newAndModifiedList, em);
                        break;
                    case "LTComment":
                        processLocaleContent((LTComment) lcObject, newAndModifiedList, em);
                        break;
                    case "LTIniSection":
                        processLocaleContent((LTIniSection) lcObject, newAndModifiedList, em);
                        break;
                    case "LTKeyValuePair":
                        processLocaleContent((LTKeyValuePair) lcObject, newAndModifiedList, em);
                        break;
                    case "LTWhitespace":
                        processLocaleContent((LTWhitespace) lcObject, newAndModifiedList, em);
                        break;
                    case "LTExternalEntity":
                        processLocaleContent((LTExternalEntity) lcObject, newAndModifiedList, em);
                        break;
                    default:
                        Logger.getLogger(ParseableFileAdapter.class.getName()).log(Level.SEVERE,
                                "Unhandled LocaleContent subclass found, ID {0} , class {1}",
                                new Object[]{lcObject.getId(), lcObject.getClass().getSimpleName()});
                }
            }

            for (LocaleContent lc : this.children) {
                if (lc instanceof LTComment) {
                    LTComment ltc = (LTComment) lc;
                    if (!(ltc.getEntityName() == null || ltc.getEntityName().isEmpty())) {
                        LocaleContent ent = this.getChildByName(ltc.getEntityName());
                        // If the entity exists, is a key-value pair and has different
                        // associated comment than ltc (including having a null one)
                        if (ent != null && ent instanceof LTKeyValuePair
                                && ((LTKeyValuePair) ent).getComment() != ltc) {
                            ((LTKeyValuePair) ent).setComment(ltc);
                        }
                    }
                }
            }

            em.getTransaction().commit();

            // Permanently delete obsolete contents
            for(Iterator<? extends LocaleContent> iterator = this.children.iterator();
                    iterator.hasNext();) {
                LocaleContent lc = iterator.next();
                if (lc.isMarkedForDeletion()) {
                    iterator.remove();
                    lcntHelper.removeRecursively(lc);
                }
            }
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }
        return newAndModifiedList;
    }

    @Override
    public List<LocaleContent> importFromFile(File f, EntityManager em,
            boolean replaceExistingValues) throws ParseException {
        List<LocaleContent> newAndModifiedList = new ArrayList<>(10);
        ParseableFile defaultTwin = (ParseableFile) getDefLocaleTwin();

        LineNumberReader fileReader = getAsLineNumberReader(f);
        if (fileReader == null) {
            return null;
        }

        // beforeParsingHook is where the actual parsing happens
        List<LocaleContent> parsedContentList = beforeParsingHook(fileReader);

        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            for(LocaleContent lcObject : parsedContentList) {
                if (lcObject.getTextValue() == null || lcObject.getTextValue().isEmpty()) {
                    continue;
                }
                lcObject.setParent(this);
                lcObject.setL10nId(this.getL10nId());
                switch (lcObject.getClass().getSimpleName()) {
                    case "LTLicense":
                        LTLicense origLicense = defaultTwin.getFileLicense();
                        if (origLicense != null) {
                            LTLicense thisLicense = getFileLicense();
                            if (thisLicense == null || thisLicense.getTextValue() == null
                                    || thisLicense.getTextValue().isEmpty() || replaceExistingValues) {
                                if (thisLicense == null) {
                                    setFileLicense((LTLicense) lcObject);
                                    thisLicense = getFileLicense();
                                    em.persist(thisLicense);
                                    addChild(thisLicense);
                                    origLicense.addTwin(thisLicense);
                                }

                                // We will (over)write the value only if it is different
                                if (thisLicense.getTextValue() == null
                                        || thisLicense.getTextValue().compareTo(lcObject.getTextValue()) != 0) {
                                    thisLicense.setTextValue(lcObject.getTextValue());
                                    thisLicense.setKeepOriginal(false);
                                    thisLicense.setTrnsStatus(TranslationStatus.Proposed);
                                    newAndModifiedList.add(origLicense);
                                }
                            }
                        }
                        break;
                    case "LTKeyValuePair":
                        LTKeyValuePair origLkvp = (LTKeyValuePair) defaultTwin.getChildByName(lcObject.getName());
                        if (origLkvp != null) {
                            LTKeyValuePair thisValue = (LTKeyValuePair) origLkvp.getTwinByLocale(getL10nId());
                            if (thisValue == null || thisValue.getTextValue().isEmpty()
                                    || replaceExistingValues) {
                                if (thisValue == null) {
                                    thisValue = (LTKeyValuePair) lcObject;
                                    origLkvp.addTwin(thisValue);
                                    addChild(thisValue);
                                    em.persist(thisValue);
                                }

                                // We will (over)write the value only if it is different
                                if (thisValue.getTextValue() == null
                                        || thisValue.getTextValue().compareTo(lcObject.getTextValue()) != 0) {
                                    thisValue.setTextValue(lcObject.getTextValue());
                                    thisValue.setKeepOriginal(false);
                                    thisValue.setTrnsStatus(TranslationStatus.Proposed);
                                    newAndModifiedList.add(origLkvp);
                                }
                            }
                        }
                        break;
                    default: // Nothing to do for the remaining LocaleContent subclasses
                }
            }
            em.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }
        return newAndModifiedList;
    }

    private void processLocaleContent(LTLicense lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;

        LTLicense thisLicense = this.getFileLicense();
        if (thisLicense != null) {
            thisLicense.setMarkedForDeletion(false);
            changed = !((lcObject.getOrderInFile() == thisLicense.getOrderInFile()) &&
                        (lcObject.getTextValue().equals(thisLicense.getTextValue())));
            if (changed) {
                thisLicense.setOrderInFile(lcObject.getOrderInFile());
                thisLicense.setTextValue(lcObject.getTextValue());
                thisLicense.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(thisLicense);
            }
        } else {
            this.setFileLicense(lcObject);
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    private void processLocaleContent(LTComment lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;
        LTComment existingComment = null;

        // Comments do not have a name nor anything that we really can
        // use to identify them, besides their order in file (but, as it is
        // really the line in the file, it can change at any moment without
        // the comment itself having changed), so it is easy that we add and
        // delete comments just because they have changed their position in the
        // file
        if (this.getChildByOrderInFile(lcObject.getOrderInFile()) instanceof LTComment) {
            existingComment = (LTComment) this.getChildByOrderInFile(lcObject.getOrderInFile());
        }

        if (existingComment != null) {
            existingComment.setMarkedForDeletion(false);
            // EntityName is transient; we will use it later to find connections
            existingComment.setEntityName(lcObject.getEntityName());

            changed = !((lcObject.getOrderInFile() == existingComment.getOrderInFile())
                    && (lcObject.getCommentType().equals(existingComment.getCommentType()))
                    && (lcObject.getTextValue().equals(existingComment.getTextValue())));
            if (changed) {
                existingComment.setOrderInFile(lcObject.getOrderInFile());
                existingComment.setCommentType(lcObject.getCommentType());
                existingComment.setTextValue(lcObject.getTextValue());
                existingComment.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(existingComment);
            }
        } else {
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    private void processLocaleContent(LTIniSection lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;

        LTIniSection existingIniSection = (LTIniSection) this.getChildByName(lcObject.getName());

        if (existingIniSection != null) {
            existingIniSection.setMarkedForDeletion(false);
            changed = (lcObject.getOrderInFile() != existingIniSection.getOrderInFile());

            if (changed) {
                existingIniSection.setOrderInFile(lcObject.getOrderInFile());
                existingIniSection.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(existingIniSection);
            }
        } else {
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    private void processLocaleContent(LTKeyValuePair lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;

        LTKeyValuePair existingKey = (LTKeyValuePair) this.getChildByName(lcObject.getName());

        if (existingKey != null) {
            existingKey.setMarkedForDeletion(false);
            changed = !((lcObject.getOrderInFile() == existingKey.getOrderInFile()) &&
                        (lcObject.getTextValue().equals(existingKey.getTextValue())));
            if (changed) {
                existingKey.setOrderInFile(lcObject.getOrderInFile());
                existingKey.setTextValue(lcObject.getTextValue());
                existingKey.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(existingKey);
            }
        } else {
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    private void processLocaleContent(LTWhitespace lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;

        LTWhitespace existingWhitespace = (LTWhitespace) this.getChildByName(lcObject.getName());

        if (existingWhitespace != null) {
            existingWhitespace.setMarkedForDeletion(false);
            changed = (lcObject.getOrderInFile() != existingWhitespace.getOrderInFile());

            if (changed) {
                existingWhitespace.setOrderInFile(lcObject.getOrderInFile());
                existingWhitespace.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(existingWhitespace);
            }
        } else {
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    private void processLocaleContent(LTExternalEntity lcObject,
            List<LocaleContent> newAndModifiedList, EntityManager em) {
        boolean changed;

        LTExternalEntity existingEntity = (LTExternalEntity) this.getChildByName(lcObject.getName());

        if (existingEntity != null) {
            existingEntity.setMarkedForDeletion(false);
            changed = !((lcObject.getOrderInFile() == existingEntity.getOrderInFile()) &&
                        (lcObject.getTextValue().equals(existingEntity.getTextValue())));
            if (changed) {
                existingEntity.setOrderInFile(lcObject.getOrderInFile());
                existingEntity.setTextValue(lcObject.getTextValue());
                existingEntity.setLastUpdate(lcObject.getLastUpdate());
                newAndModifiedList.add(existingEntity);
            }
        } else {
            this.addChild(lcObject);
            em.persist(lcObject);
            newAndModifiedList.add(lcObject);
        }
    }

    /**
     * Performs initial operations to parse. In practice, this method is what
     * actually parses the file, returning a list of LTContent objects
     * @param fileReader a LineNumberReader with the character stream from the
     *                   file that is to be parsed
     * @return a list of LocaleContent objects representing every significant item
     *         in the file
     * @throws java.text.ParseException if anything prevents from completing the
     *         parsing
     */
    protected abstract List<LocaleContent> beforeParsingHook(LineNumberReader fileReader)
            throws ParseException;

    /**
     * Performs post-read operations to parse. This is to allow subclasses to
     * add post-read operations while parsing if needed; most subclasses will
     * likely implement this as an empty method.
     * @param fileReader a LineNumberReader with the character stream from the
     *                   file that is to be parsed
     */
    protected abstract void afterParsingHook(LineNumberReader fileReader);

    @Override
    public boolean exportToFile(File f) throws IOException {
        boolean result = false;
        PrintWriter pw;
        List<LocaleContent> sortedChildren = new ArrayList<>(children.size());
        
        pw = getAsPrintWriter(f);
        
        if (pw != null) {
            result = true;
            sortedChildren.addAll(children);
            Collections.sort(sortedChildren, LTContent.orderInFileComparator);
            
            for(LocaleContent lc : sortedChildren) {
                if (!lc.isDontExport()) {
                    if (lc.isKeepOriginal()) {
                        printLocaleContent(pw, lc.getDefLocaleTwin());
                    } else {
                        printLocaleContent(pw, lc);
                    }
                }
            }
            pw.flush();
            pw.close();
        }
        return result;
    }
    
    private void printLocaleContent(PrintWriter pw, LocaleContent lc) {
        if (lc instanceof LTComment) {
            // TODO once we have a preference about exporting comments, check it
            printLocaleContent(pw, (LTComment) lc);
        } else if (lc instanceof LTExternalEntity) {
            printLocaleContent(pw, (LTExternalEntity) lc);
        } else if (lc instanceof LTIniSection) {
            printLocaleContent(pw, (LTIniSection) lc);
        } else if (lc instanceof LTKeyValuePair) {
            printLocaleContent(pw, (LTKeyValuePair) lc);
        } else if (lc instanceof LTLicense) {
            printLocaleContent(pw, (LTLicense) lc);
        } else if (lc instanceof LTWhitespace) {
            printLocaleContent(pw, (LTWhitespace) lc);
        }
    }
    
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTComment lc);
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTExternalEntity lc);
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTIniSection lc);
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTKeyValuePair lc);
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTLicense lc);
    @Override
    public abstract void printLocaleContent(PrintWriter pw, LTWhitespace lc);
    
    
    @Override
    public LTLicense getFileLicense() {
        if (fileLicense == null) {
            LocaleContent licenseInChildren = getChildByName("LTLicenseHeader");
            if (licenseInChildren != null && licenseInChildren instanceof LTLicense) {
                // This may or may not get persisted in DB, depending on what is done with
                // the ParseableFile entity and if it is managed or not, but at least we
                // give it a change to autofix itself AND return the correct value for the
                // getter
                fileLicense = (LTLicense) licenseInChildren;
            }
        }
        return fileLicense;
    }

    @Override
    public void setFileLicense(LTLicense fileLicense) {
        this.fileLicense = fileLicense;
    }
}
