/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import net.localizethat.Main;
import net.localizethat.model.L10n;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocalePath;
import net.localizethat.model.ParseableFile;
import net.localizethat.model.jpa.JPAHelperBundle;
import net.localizethat.model.jpa.LocaleContainerJPAHelper;
import net.localizethat.model.jpa.LocaleFileJPAHelper;
import net.localizethat.util.gui.JStatusBar;

/**
 * SwingWorker task that performs an export process in the locale paths passed
 * in the constructor
 * @author rpalomares
 */
public class ExportProductWorker extends SwingWorker<Void, String> {
    private final JTextArea feedbackArea;
    private final JStatusBar statusBar;
    private final Iterator<LocalePath> localePathIterator;
    private final EntityManager em;
    private final JPAHelperBundle jhb;
    private final boolean removeObsoleteFiles;
    private final L10n targetLocale;
    private int filesExportedNew;
    private int filesExportedExisting;
    private int filesDeleted;
    private int foldersExportedNew;
    private int foldersExportedExisting;
    private int foldersDeleted;

    public ExportProductWorker(JTextArea feedbackArea, Iterator<LocalePath> localePathIterator,
            boolean removeObsoleteFiles, L10n targetLocale) {
        this.feedbackArea = feedbackArea;
        this.localePathIterator = localePathIterator;
        this.statusBar = Main.mainWindow.getStatusBar();
        this.em = Main.emf.createEntityManager();
        this.jhb = JPAHelperBundle.getInstance(em);
        this.removeObsoleteFiles = removeObsoleteFiles;
        this.targetLocale = targetLocale;
    }

    @Override
    protected Void doInBackground() {
        int totalFilesExportedNew = 0;
        int totalFilesExportedExisting = 0;
        int totalFilesDeleted = 0;
        int totalFoldersExportedNew = 0;
        int totalFoldersExportedExisting = 0;
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
                publish("Export process cancelled, work done until now can't be undone");
                if (em.isJoinedToTransaction()) {
                    em.getTransaction().rollback();
                }
                break;
            } else {
                totalFilesExportedNew += filesExportedNew;
                totalFilesExportedExisting += filesExportedExisting;
                totalFilesDeleted += filesDeleted;
                totalFoldersExportedNew += foldersExportedNew;
                totalFoldersExportedExisting += foldersExportedExisting;
                totalFoldersDeleted += foldersDeleted;

                publish("  Files... Added: " + filesExportedNew + "; Modified: " + filesExportedExisting
                        + "; Deleted: " + filesDeleted);
                publish("  Folders... Added: " + foldersExportedNew + "; Modified: " + foldersExportedExisting
                        + "; Deleted: " + foldersDeleted);
            }
        }

        if (em.isJoinedToTransaction()) {
            em.getTransaction().commit();
        }
        em.close();
        publish("Total Files... Added: " + totalFilesExportedNew + "; Modified: " + totalFilesExportedExisting
                + "; Deleted: " + totalFilesDeleted);
        publish("Total Folders... Added: " + totalFoldersExportedNew + "; Modified: " + totalFoldersExportedExisting
                + "; Deleted: " + totalFoldersDeleted);
        return null;
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
    }

    private void processPath(LocalePath lp) {
        // Initialize the counters for each path
        filesExportedNew = 0;
        filesExportedExisting = 0;
        filesDeleted = 0;
        foldersExportedNew = 0;
        foldersExportedExisting = 0;
        foldersDeleted = 0;

        LocaleContainer lc = lp.getLocaleContainer().getTwinByLocale(targetLocale);
        if (lc != null) {
            TypedQuery<LocalePath> lpQuery = em.createNamedQuery("LocalePath.findByLocaleContainer",
                LocalePath.class);
            lpQuery.setParameter("localecontainer", lc);
            LocalePath targetLp = lpQuery.getSingleResult();
            processContainer(targetLp.getFilePath(), lc);
        } else {
            publish("There is no content for " + targetLocale.getCode());
        }
    }

    private void processContainer(String currentPath, LocaleContainer lc) {
        File curDir = new File(currentPath);
        File[] childFiles = curDir.listFiles();
        LocaleContainerJPAHelper lcHelper = jhb.getLocaleContainerJPAHelper();
        LocaleFileJPAHelper lfHelper = jhb.getLocaleFileJPAHelper();

        if (isCancelled()) {
            return;
        }
        
        /*
         * Traverse LocaleContainer list, comparing the list with existing
         * folders in disk, to end with a list of obsolete folders, in
         * case the user has checked to remove them
         */
        for(LocaleContainer lcChild : lc.getChildren()) {
            File f = new File(currentPath + "/" + lcChild.getName());
            
            if (!f.exists()) {
                f.mkdirs();
            } else {
                if (!f.isDirectory()) {
                    publish("Error: " + currentPath + "/" + lcChild.getName()
                            + " exists but it is not a directory");
                    return;
                }
                f = fileExistsInArray(childFiles, f.getName());
                removeFileFromArray(childFiles, f);
            }
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
                            + " exists but it is a directory");
                    return;
                }
                f = fileExistsInArray(childFiles, f.getName());
                removeFileFromArray(childFiles, f);
            }
            
            if (!lfChild.isDontExport()) {
                processFile(currentPath + "/" + lfChild.getName(), lfChild);
            }
        }
        
        if (removeObsoleteFiles) {
            for(int i = 0; i < childFiles.length; i++) {
                File f = childFiles[i];
                if (f != null) {
                    if (!f.delete()) {
                        publish("Error attempting to delete " +
                                f.getAbsolutePath());
                    }
                    childFiles[i] = null;
                }
            }
        }
    }

    private boolean processFile(String filePath, LocaleFile lf) {
        boolean result = false;
        
        if (lf instanceof ParseableFile) {
            ParseableFile plf = (ParseableFile) lf;
            try {
                result = plf.exportToFile(new File(filePath));
            } catch (IOException ex) {
                Logger.getLogger(ExportProductWorker.class.getName()).log(Level.SEVERE, null, ex);
                result = false;
            }
        }
        return result;
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
