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
import net.localizethat.model.jpa.LocaleFileJPAHelper;
import net.localizethat.util.gui.JStatusBar;

/**
 * SwingWorker task that performs an import process in the locale paths passed
 * in the constructor
 * @author rpalomares
 */
public class ImportProductWorker extends SwingWorker<List<LocaleContent>, String> {
    private final JTextArea feedbackArea;
    private final JButton editChangesButton;
    private final JStatusBar statusBar;
    private final boolean replaceExistingValues;
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

    public ImportProductWorker(JTextArea feedbackArea, JButton editChangesButton,
            boolean replaceExistingValues, L10n targetLocale,
            Iterator<LocalePath> localePathIterator) {
        this.feedbackArea = feedbackArea;
        this.editChangesButton = editChangesButton;
        this.replaceExistingValues = replaceExistingValues;
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
            processPath(lp);

            if (isCancelled()) {
                publish("Import process cancelled, work done until now can't be undone");
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

        // We try to get the sibling LocalePath for the target locale
        LocalePath siblingLp = lp.getLocaleContainer().getTwinByLocale(targetLocale).getLocalePath(true);

        if (siblingLp != null) {
            publish("Processing " + siblingLp.getFilePath());
            LocaleContainer lc = lp.getLocaleContainer();
            processContainer(siblingLp.getFilePath(), lc);
        }

    }

    private void processContainer(String currentPath, LocaleContainer lc) {
        if (isCancelled()) {
            return;
        }

        publish("    Processing " + currentPath);

        if (!em.isJoinedToTransaction()) {
            em.getTransaction().begin();
        }
        /*
         * Traverse LocaleContainer list, comparing the list with existing
         * folders in disk, to end with a list of obsolete folders, in
         * case the user has checked to remove them
         */
        for(LocaleContainer lcChild : lc.getChildren()) {
            processContainer(currentPath + "/" + lcChild.getName(), lcChild);
        }

        /*
         * Traverse LocaleFile list, comparing the list with existing files
         * in disk, to end with a list of obsolete files, in
         * case the user has checked to remove them
         */
        for(LocaleFile lfChild : lc.getFileChildren()) {
            File f = new File(currentPath + "/" + lfChild.getName());

            if (f.exists()) {
                if (f.isDirectory()) {
                    publish("Error: " + currentPath + "/" + lfChild.getName()
                            + " is a directory in disk but a regular file in default locale");
                    return;
                } else {
                    processFile(f, lfChild);
                }
            }
        }
    }

    private boolean processFile(File f, LocaleFile lf) {
        LocaleFileJPAHelper lfHelper = jhb.getLocaleFileJPAHelper();
        boolean result = true;

        try {
            if (lf instanceof ParseableFile) {
                ParseableFile pf = (ParseableFile) em.merge(lf);
                if (pf.getTwinByLocale(targetLocale) != null
                        || lfHelper.createRecursively(lf, targetLocale, true)) {
                    newAndModifiedList.addAll(((ParseableFile) pf.getTwinByLocale(targetLocale))
                            .importFromFile(f, em, replaceExistingValues));
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
                if (mergedLf.getTwinByLocale(targetLocale) != null
                        || lfHelper.createRecursively(lf, targetLocale, true)) {
                    newAndModifiedList.addAll(((TextFile) mergedLf.getTwinByLocale(targetLocale))
                            .importFromFile(f, em, replaceExistingValues));
                }

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
            Logger.getLogger(ImportProductWorker.class.getName()).log(Level.SEVERE, null, ex);
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
