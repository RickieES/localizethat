/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.LineNumberReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Adapter class that extends LocaleFile (thus, implementing LocaleNode) and
 * implements ParseableFile to provide the general process of updating a
 * parseable file. Two abstract methods deal with the previous parsing of
 * @author rpalomares
 */
public abstract class ParseableFileAdapter extends LocaleFile implements ParseableFile {
    @OneToOne
    @JoinColumn(name = "ID")
    LTLicense fileLicense;

    @Override
    public List<LocaleContent> update(EntityManager em) throws ParseException {
        List<LocaleContent> newAndModifiedList = new ArrayList<>(10);
        // We're parsing the original if this file has no default twin
        boolean isParsingOriginal = (this.getDefLocaleTwin() == null);

        LineNumberReader fileReader = this.getAsLineNumberReader();
        if (fileReader == null) {
            return null;
        }

        List<LocaleContent> parsedContentList = beforeParsingHook(fileReader);
        boolean changed;

        try {
            em.getTransaction().begin();
            
            // We mark all contents for deletion; every object modified or added will
            // have their markedForDeletion reset, so in the end we will have that mark
            // only in the objects no longer existing in the file
            for (LocaleContent lc : this.children) {
                lc.setMarkedForDeletion(true);
            }

            for (LocaleContent lcObject : parsedContentList) {
                lcObject.setParent(this);
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
                                    ((LTKeyValuePair) l).setLtComment(existingComment);
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
                                ((LTKeyValuePair) l).setLtComment(lc);
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

                } else if (lcObject instanceof ExternalEntity) {
                    ExternalEntity lExtEnt = (ExternalEntity) lcObject;
                    ExternalEntity existingEntity = (ExternalEntity) this.getChildByName(lExtEnt.getName());

                    if (existingEntity != null) {
                        existingEntity.setMarkedForDeletion(false);
                        changed = !((lExtEnt.getOrderInFile() == existingEntity.getOrderInFile()) &&
                                    (lExtEnt.getSystemId().equals(existingEntity.getSystemId())));
                        if (changed) {
                            existingEntity.setOrderInFile(lExtEnt.getOrderInFile());
                            existingEntity.setSystemId(lExtEnt.getSystemId());
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

            // Permanently delete obsolete contents
            for(Iterator<? extends LocaleContent> iterator = this.children.iterator(); iterator.hasNext();) {
                LocaleContent lc = iterator.next();
                em.merge(lc);
                if (lc.isMarkedForDeletion()) {
                    lc.setParent(null);
                    iterator.remove();
                    em.remove(lc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }
        return newAndModifiedList;
    }

    /**
     * Performs initial operations to parse. In practice, this method is what actually parses
     * the file, returning a list of LocaleContent objects
     * @param fileReader a LineNumberReader with the character stream from the file that is to be parsed
     * @return a list of LocaleContent objects representing every significant item in the file
     * @throws java.text.ParseException if anything prevents from completing the parsing
     */
    protected abstract List<LocaleContent> beforeParsingHook(LineNumberReader fileReader)
            throws ParseException;

    protected abstract void afterParsingHook(LineNumberReader fileReader);

    @Override
    public LTLicense getFileLicense() {
        return fileLicense;
    }

    @Override
    public void setFileLicense(LTLicense fileLicense) {
        this.fileLicense = fileLicense;
    }

    @Override
    public List<LocaleContent> getLObjectCollection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
