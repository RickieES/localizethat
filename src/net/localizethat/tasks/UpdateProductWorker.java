/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.tasks;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import net.localizethat.Main;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleContent;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocalePath;
import net.localizethat.model.ParseableFile;
import net.localizethat.model.TextFile;
import net.localizethat.model.jpa.JPAHelperBundle;
import net.localizethat.model.jpa.LocaleContainerJPAHelper;
import net.localizethat.model.jpa.LocaleContentJPAHelper;
import net.localizethat.model.jpa.LocaleFileJPAHelper;
import net.localizethat.util.gui.JStatusBar;

/**
 * SwingWorker task that performs an update process in the locale paths passed in the constructor
 * @author rpalomares
 */
public class UpdateProductWorker extends SwingWorker<List<LocaleContent>, String> {
    private final JTextArea feedbackArea;
    private final JButton editChangesButton;
    private final JStatusBar statusBar;
    private final L10n targetLocale;
    private final Iterator<LocalePath> localePathIterator;
    private final List<LocaleContent> newAndModifiedList;
    private final EntityManager em;
    private final JPAHelperBundle jhb;
    private int filesAdded;
    private int filesModified;
    private int filesDeleted;
    private int foldersAdded;
    private int foldersModified;
    private int foldersDeleted;

    public UpdateProductWorker(JTextArea feedbackArea, JButton editChangesButton,
            L10n targetLocale, Iterator<LocalePath> localePathIterator) {
        this.feedbackArea = feedbackArea;
        this.editChangesButton = editChangesButton;
        this.targetLocale = targetLocale;
        this.localePathIterator = localePathIterator;
        this.statusBar = Main.mainWindow.getStatusBar();
        this.em = Main.emf.createEntityManager();
        this.newAndModifiedList = new ArrayList<>(10);
        this.jhb = JPAHelperBundle.getInstance(em);
    }

    @Override
    protected List<LocaleContent> doInBackground() {
        int totalFilesAdded = 0;
        int totalFilesModified = 0;
        int totalFilesDeleted = 0;
        int totalFoldersAdded = 0;
        int totalFoldersModified = 0;
        int totalFoldersDeleted = 0;

        em.getTransaction().begin();
        while (localePathIterator.hasNext()) {
            if (isCancelled()) {
                break;
            }
            LocalePath lp = localePathIterator.next();

            publish("Processing " + lp.getFilePath());

            processPath(lp);
            if (isCancelled()) {
                publish("Update process cancelled, work done until now can't be undone");
                if (em.isJoinedToTransaction()) {
                    em.getTransaction().rollback();
                }
                break;
            } else {
                totalFilesAdded += filesAdded;
                totalFilesModified += filesModified;
                totalFilesDeleted += filesDeleted;
                totalFoldersAdded += foldersAdded;
                totalFoldersModified += foldersModified;
                totalFoldersDeleted += foldersDeleted;

                publish("  Files... Added: " + filesAdded + "; Modified: " + filesModified
                        + "; Deleted: " + filesDeleted);
                publish("  Folders... Added: " + foldersAdded + "; Modified: " + foldersModified
                        + "; Deleted: " + foldersDeleted);
            }
        }

        if (em.isJoinedToTransaction()) {
            em.getTransaction().commit();
        }
        em.close();
        publish("Total Files... Added: " + totalFilesAdded + "; Modified: " + totalFilesModified
                + "; Deleted: " + totalFilesDeleted);
        publish("Total Folders... Added: " + totalFoldersAdded + "; Modified: " + totalFoldersModified
                + "; Deleted: " + totalFoldersDeleted);
        return newAndModifiedList;
    }

    @Override
    protected void process(List<String> messages) {
        for(String message : messages) {
            feedbackArea.append(message);
            feedbackArea.append(System.getProperty("line.separator"));
        }
    }

    @Override
    protected void done() {
        statusBar.endProgress();
        editChangesButton.setEnabled(true);
    }

    private void processPath(LocalePath lp) {
        // Initialize the counters for each path
        filesAdded = 0;
        filesModified = 0;
        filesDeleted = 0;
        foldersAdded = 0;
        foldersModified = 0;
        foldersDeleted = 0;

        LocaleContainer lc = lp.getLocaleContainer();
        processContainer(lp.getFilePath(), lc);
    }

    private void processContainer(String currentPath, LocaleContainer lc) {
        File curDir = new File(currentPath);
        File[] childFiles = curDir.listFiles();
        LocaleContainerJPAHelper lcHelper = jhb.getLocaleContainerJPAHelper();
        LocaleFileJPAHelper lfHelper = jhb.getLocaleFileJPAHelper();

        if (isCancelled()) {
            return;
        }

        // Take the real files & dirs in the disk and add those missing in the datamodel
        try {
            if (!em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }
            for (File curFile : childFiles) {
                if (isCancelled()) {
                    if (em.isJoinedToTransaction()) {
                        em.getTransaction().rollback();
                    }
                    return;
                }

                if (curFile.isDirectory()) {
                    boolean exists = (lc.hasChild(curFile.getName(), true));
                    if (!exists) {
                        LocaleContainer newLc = new LocaleContainer(curFile.getName(), lc);
                        newLc.setL10nId(lc.getL10nId());
                        lc.addChild(newLc);
                        foldersAdded++;
                        em.persist(newLc);
                        
                        // Create the twin for the target locale
                        lcHelper.createRecursively(newLc, targetLocale, false);
                    }
                } else {
                    boolean exists = (lc.hasFileChild(curFile.getName(), true));
                    if (!exists) {
                        LocaleFile lf = LocaleFile.createFile(curFile.getName(), lc);
                        lc.addFileChild(lf);
                        filesAdded++;
                        em.persist(lf);

                        // Create the twin for the target locale
                        lfHelper.createRecursively(lf, targetLocale, false);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        if (isCancelled()) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        // Remove directories (LocaleContainer items) no longer present in the disk
        try {
            em.getTransaction().begin();
            for (Iterator<LocaleContainer> iterator = lc.getChildren().iterator(); iterator.hasNext();) {
                if (isCancelled()) {
                    if (em.isJoinedToTransaction()) {
                        em.getTransaction().rollback();
                    }
                    return;
                }

                LocaleContainer lcChild = iterator.next();
                lcChild = em.merge(lcChild);
                File result = fileExistsInArray(childFiles, lcChild.getName());
                boolean exists = (result != null && result.isDirectory());
                if (!exists) {
                    lcChild.setParent(null);
                    iterator.remove();
                    lcHelper.removeRecursively(lcChild);
                    foldersDeleted++;
                } else {
                    // Remove the entry in the directory entries array to mark it as processed
                    removeFileFromArray(childFiles, result);
                }
            }
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            Logger.getLogger(UpdateProductWorker.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        if (isCancelled()) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        // Remove files (LocaleFiles items) no longer present in the disk
        try {
            em.getTransaction().begin();
            for(Iterator<? extends LocaleFile> iterator = lc.getFileChildren().iterator(); iterator.hasNext();) {
                if (isCancelled()) {
                    if (em.isJoinedToTransaction()) {
                        em.getTransaction().rollback();
                    }
                    return;
                }

                LocaleFile lfChild = iterator.next();
                em.merge(lfChild);
                File result = fileExistsInArray(childFiles, lfChild.getName());
                boolean exists = (result != null && !result.isDirectory());
                if (!exists) {
                    lfChild.setParent(null);
                    iterator.remove();
                    lfHelper.removeRecursively(lfChild);
                    filesDeleted++;
                } else {
                    removeFileFromArray(childFiles, result);
                }
            }
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            Logger.getLogger(UpdateProductWorker.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        if (isCancelled()) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
            return;
        }

        // At this point, the datamodel for currentPath matches the disk contents

        // Traverse the datamodel LocaleContainers (folders/dirs)
        for(LocaleContainer lcChild : lc.getChildren()) {
            processContainer(currentPath + "/" + lcChild.getName(), lcChild);
        }

        // Traverse the datamodel LocaleFiles (files)
        for(LocaleFile lfChild : lc.getFileChildren()) {
            if (isCancelled()) {
                if (em.isJoinedToTransaction()) {
                    em.getTransaction().rollback();
                }
                return;
            }
            processFile(currentPath + "/" + lfChild.getName(), lfChild);
        }
    }

    private boolean processFile(String filePath, LocaleFile lf) {
        LocaleContentJPAHelper lcntHelper = jhb.getLocaleContentJPAHelper();
        boolean result = true;

        try {
            if (lf instanceof ParseableFile) {
            // if (lf instanceof DtdFile) {
                ParseableFile pf = (ParseableFile) em.merge(lf);
                newAndModifiedList.addAll(pf.update(this.em, lcntHelper));

                for(LocaleContent lcnt : pf.getChildren()) {
                    // If the original locale content is set to not be exported
                    // we don't want to create sibling for it
                    if ((!lcnt.isDontExport() && lcnt.getTwinByLocale(targetLocale) == null)) {
                        // em.contains(entity) to know whether an entity is managed or not
                        LocaleContent mergedLcnt = em.merge(lcnt);
                        result = lcntHelper.createRecursively(mergedLcnt, targetLocale, false);
                    }
                }
                if (em.isJoinedToTransaction()) {
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
            } else if (lf instanceof TextFile) {
                if (!em.isJoinedToTransaction()) {
                    em.getTransaction().begin();
                }
                TextFile mergedLf = (TextFile) em.merge(lf);
                newAndModifiedList.addAll(mergedLf.update(this.em));
                if (em.isJoinedToTransaction()) {
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
            }
            filesModified++;
            return result;
        } catch (ParseException ex) {
            return false;
        } catch (Exception ex) {
            Logger.getLogger(UpdateProductWorker.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private File fileExistsInArray(File[] fileArray, String filename) {
        File result = null;

        for(File f : fileArray) {
            if ((f != null) && (f.getName().equals(filename))) {
                result = f;
                break;
            }
        }
        return result;
    }

    private boolean removeFileFromArray(File[] fileArray, File fileToRemove) {
        boolean result = false;

        for(int i = 0; i < fileArray.length; i++) {
            if ((fileArray[i] != null) && (fileArray[i].equals(fileToRemove))) {
                fileArray[i] = null;
                result = true;
                break;
            }
        }
        return result;
    }
}
