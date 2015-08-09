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
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
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
    public List<LTContent> update(EntityManager em, LocaleContentJPAHelper lcntHelper)
                throws ParseException {
        List<LTContent> newAndModifiedList = new ArrayList<>(10);
        // We're parsing the original if this file has no default twin
        boolean isParsingOriginal = (this.getDefLocaleTwin() == null);

        LineNumberReader fileReader = this.getAsLineNumberReader();
        if (fileReader == null) {
            return null;
        }

        List<LTContent> parsedContentList = beforeParsingHook(fileReader);
        boolean changed;

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

            for (LTContent lcObject : parsedContentList) {
                lcObject.setParent(this);
                lcObject.setL10nId(this.getL10nId());
                if (lcObject instanceof LTLicense) {
                    LTLicense lt = (LTLicense) lcObject;
                    LTLicense thisLicense = this.getFileLicense();
                    if (thisLicense != null) {
                        thisLicense.setMarkedForDeletion(false);
                        changed = !((lt.getOrderInFile() == thisLicense.getOrderInFile()) &&
                                    (lt.getTextValue().equals(thisLicense.getTextValue())));
                        if (changed) {
                            thisLicense.setOrderInFile(lt.getOrderInFile());
                            thisLicense.setTextValue(lt.getTextValue());
                            thisLicense.setLastUpdate(lt.getLastUpdate());
                            thisLicense.setMarkedForDeletion(false);
                            newAndModifiedList.add(thisLicense);
                        }
                    } else {
                        this.setFileLicense(lt);
                        this.addChild(lt);
                        em.persist(lt);
                        newAndModifiedList.add(lt);
                    }
                } else if (lcObject instanceof LTComment) {
                    // Comments do not have a name nor anything that we really can
                    // use to identify them, besides their order in file (but, as it is
                    // really the line in the file, it can change at any moment without
                    // the comment itself having changed), so it is easy that we add and
                    // delete comments just because they have changed their position in the
                    // file
                    LTComment lc = (LTComment) lcObject;
                    LTComment existingComment = (LTComment)
                            this.getChildByOrderInFile(lc.getOrderInFile());

                    if (existingComment != null) {
                        existingComment.setMarkedForDeletion(false);
                        changed = !((lc.getOrderInFile() == existingComment.getOrderInFile()) &&
                                    (lc.getTextValue().equals(existingComment.getTextValue())));
                        if (changed) {
                            existingComment.setOrderInFile(lc.getOrderInFile());
                            existingComment.setTextValue(lc.getTextValue());
                            existingComment.setLastUpdate(lc.getLastUpdate());
                            existingComment.setMarkedForDeletion(false);
                            newAndModifiedList.add(existingComment);
                            if (lc.getEntityName() != null) {
                                LocaleContent l = this.getChildByName(lc.getEntityName());
                                if (l != null && l instanceof LTKeyValuePair) {
                                    ((LTKeyValuePair) l).setComment(existingComment);
                                }
                            }
                        }
                    } else {
                        this.addChild(lc);
                        em.persist(lc);
                        newAndModifiedList.add(lc);
                        if (lc.getEntityName() != null) {
                            LocaleContent l = this.getChildByName(lc.getEntityName());
                            if (l != null && l instanceof LTKeyValuePair) {
                                ((LTKeyValuePair) l).setComment(lc);
                            }
                        }
                    }
                } else if (lcObject instanceof LTIniSection) {

                } else if (lcObject instanceof LTKeyValuePair) {
                    LTKeyValuePair lkvp = (LTKeyValuePair) lcObject;
                    LTKeyValuePair existingKey = (LTKeyValuePair) this.getChildByName(lkvp.getName());

                    if (existingKey != null) {
                        existingKey.setMarkedForDeletion(false);
                        changed = !((lkvp.getOrderInFile() == existingKey.getOrderInFile()) &&
                                    (lkvp.getTextValue().equals(existingKey.getTextValue())));
                        if (changed) {
                            existingKey.setOrderInFile(lkvp.getOrderInFile());
                            existingKey.setTextValue(lkvp.getTextValue());
                            existingKey.setLastUpdate(lkvp.getLastUpdate());
                            existingKey.setMarkedForDeletion(false);
                            newAndModifiedList.add(existingKey);
                        }
                    } else {
                        this.addChild(lkvp);
                        em.persist(lkvp);
                        newAndModifiedList.add(lkvp);
                    }
                } else if (lcObject instanceof LTWhitespace) {

                } else if (lcObject instanceof LTExternalEntity) {
                    LTExternalEntity lExtEnt = (LTExternalEntity) lcObject;
                    LTExternalEntity existingEntity = (LTExternalEntity) this.getChildByName(lExtEnt.getName());

                    if (existingEntity != null) {
                        existingEntity.setMarkedForDeletion(false);
                        changed = !((lExtEnt.getOrderInFile() == existingEntity.getOrderInFile()) &&
                                    (lExtEnt.getTextValue().equals(existingEntity.getTextValue())));
                        if (changed) {
                            existingEntity.setOrderInFile(lExtEnt.getOrderInFile());
                            existingEntity.setTextValue(lExtEnt.getTextValue());
                            existingEntity.setLastUpdate(lExtEnt.getLastUpdate());
                            existingEntity.setMarkedForDeletion(false);
                            newAndModifiedList.add(existingEntity);
                        }
                    } else {
                        this.addChild(lExtEnt);
                        em.persist(lExtEnt);
                        newAndModifiedList.add(lExtEnt);
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

    /**
     * Performs initial operations to parse. In practice, this method is what
     * actually parses the file, returning a list of LTContent objects
     * @param fileReader a LineNumberReader with the character stream from the
     *                   file that is to be parsed
     * @return a list of LTContent objects representing every significant item
     *         in the file
     * @throws java.text.ParseException if anything prevents from completing the
     *         parsing
     */
    protected abstract List<LTContent> beforeParsingHook(LineNumberReader fileReader)
            throws ParseException;

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
                    printLocaleContent(pw, lc);
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
        return fileLicense;
    }

    @Override
    public void setFileLicense(LTLicense fileLicense) {
        this.fileLicense = fileLicense;
    }

    @Override
    public List<LTContent> getLObjectCollection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
