/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.tasks;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import net.localizethat.Main;
import net.localizethat.model.LocaleContainer;
import net.localizethat.model.LocaleFile;
import net.localizethat.model.LocaleNode;
import net.localizethat.model.LocalePath;
import net.localizethat.model.jpa.LocaleContainerJPAHelper;
import net.localizethat.model.jpa.LocaleFileJPAHelper;

/**
 *
 * @author rpalomares
 */
public class UpdateProductWorker extends SwingWorker<Collection<LocaleNode>, String> {
    private final JTextArea feedbackArea;
    private final JButton editChangesButton;
    private final Iterator<LocalePath> localePathIterator;
    private final EntityManager em;
    private int filesAdded;
    private int filesModified;
    private int filesDeleted;
    private int foldersAdded;
    private int foldersModified;
    private int foldersDeleted;

    public UpdateProductWorker(JTextArea feedbackArea, JButton editChangesButton,
            Iterator<LocalePath> localePathIterator) {
        this.feedbackArea = feedbackArea;
        this.editChangesButton = editChangesButton;
        this.localePathIterator = localePathIterator;
        this.em = Main.emf.createEntityManager();
    }

    @Override
    protected Collection<LocaleNode> doInBackground() {
        int totalFilesAdded = 0;
        int totalFilesModified = 0;
        int totalFilesDeleted = 0;
        int totalFoldersAdded = 0;
        int totalFoldersModified = 0;
        int totalFoldersDeleted = 0;

        em.getTransaction().begin();
        while (localePathIterator.hasNext()) {
            LocalePath lp = localePathIterator.next();
            
            publish("Processing " + lp.getFilePath());

            processPath(lp);
            totalFilesAdded += filesAdded;
            totalFilesModified += filesModified;
            totalFilesDeleted += filesDeleted;
            totalFoldersAdded += foldersAdded;
            totalFoldersModified += foldersModified;
            totalFoldersDeleted += foldersDeleted;

            publish("  Files... Added: " + filesAdded + "; Modified: " + filesModified + "; Deleted: " + filesDeleted);
            publish("  Folders... Added: " + foldersAdded + "; Modified: " + foldersModified + "; Deleted: " + foldersDeleted);
        }

        if (em.isJoinedToTransaction()) {
            em.getTransaction().commit();
        }
        em.close();
        return null; // TODO return a real collection
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
        LocaleContainerJPAHelper lcHelper = new LocaleContainerJPAHelper(em);
        LocaleFileJPAHelper lfHelper = new LocaleFileJPAHelper(em);

        // Take the real files & dirs in the disk and add those missing in the datamodel
        try {
            if (!em.isJoinedToTransaction()) {
                em.getTransaction().begin();
            }
            for (File curFile : childFiles) {
                if (curFile.isDirectory()) {
                    boolean exists = (lc.hasChild(curFile.getName(), true));
                    if (!exists) {
                        LocaleContainer newLc = new LocaleContainer(curFile.getName(), lc);
                        lc.addChild(newLc);
                        foldersAdded++;
                        em.persist(newLc);
                    }
                } else {
                    boolean exists = (lc.hasFileChild(curFile.getName(), true));
                    if (!exists) {
                        LocaleFile lf = LocaleFile.createFile(curFile.getName(), lc);
                        lc.addFileChild(lf);
                        filesAdded++;
                        em.persist(lf);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }

        // Remove directories (LocaleContainer items) no longer present in the disk
        try {
            em.getTransaction().begin();
            for (Iterator<LocaleContainer> iterator = lc.getChildren().iterator(); iterator.hasNext();) {
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
                    removeFileFromArray(childFiles, result);
                }
            }
            em.getTransaction().commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }

        // Remove files (LocaleFiles items) no longer present in the disk
        try {
            em.getTransaction().begin();
            for(Iterator<? extends LocaleFile> iterator = lc.getFileChildren().iterator(); iterator.hasNext();) {
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
            e.printStackTrace();
        } catch (Exception e) {
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        }

        // At this point, the datamodel for currentPath matches the disk contents

        // Traverse the datamodel LocaleContainers (folders/dirs)
        for(LocaleContainer lcChild : lc.getChildren()) {
            processContainer(currentPath + "/" + lcChild.getName(), lcChild);
        }

        // Traverse the datamodel LocaleFiles (files)
        for(LocaleFile lfChild : lc.getFileChildren()) {
            processFile(currentPath + "/" + lfChild.getName(), lfChild);
        }


        /*
        - Compare the lc children list with the real children list in the disk
        - Delete the no longer existing nodes and create the new ones
        - Compare the last updated timestamp of every File child with the real
          file in the disk (dummy test; later, it will be replaced by a full
          parse and comparison)
        */
    }

    private boolean processFile(String filePath, LocaleFile lf) {
        return true;
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
