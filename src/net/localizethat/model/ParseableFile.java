/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import net.localizethat.model.jpa.LocaleContentJPAHelper;

/**
 * Interface defining features of files that can be parseable
 * @author rpalomares
 */
public interface ParseableFile extends LocaleNode {

    /**
     * Returns the list of children of this LocaleNode
     * @return A list of LocaleNode items that represent the children of this one
     */
    @Override
    Collection<? extends LocaleContent> getChildren();

    /**
     * Parses a text file (like a DTD file, a Properties file, etc.) corresponding to
     * this ParseableFile and "merges" both, removing obsolete entries, adding
     * new ones (only if ParseableFile is declared as default twin) and updating the
     * remaining if they have changed
     *
     * @param em an EntityManager used to persist in DB new, modified and deleted records
     * @param lcntHelper 
     * @return a list of LTContent objects added or modified
     * @throws ParseException in case the parsing fails
     */
    List<LocaleContent> update(EntityManager em, LocaleContentJPAHelper lcntHelper)
            throws ParseException;

    /**
     * Parses a text file (like a DTD file, a Properties file, etc.) and tries to apply
     * it to this ParseableFile. Unlike update(), import does not remove obsolete entries;
     * it just adds new ones, even if this is not the default twin, and updates the
     * remaining if they have changed
     *
     * @param f a pointer to the file that will be parsed
     * @param em an EntityManager used to persist in DB new, modified and deleted records
     * @param replaceExistingValues true if existing values must be replaced by imported values
     * @return a list of LTContent objects added or modified
     * @throws ParseException in case the parsing fails
     */
    List<LocaleContent> importFromFile(File f, EntityManager em, boolean replaceExistingValues)
            throws ParseException;

    /**
     * Exports a parseable file to disk
     * @param f an object representing the file in the disk where it will be
     *          exported (it does not have to exist previously)
     * @return true if the file has successfully saved to disk
     * @throws IOException in case something goes wrong while writing
     */
    boolean exportToFile(File f) throws IOException;

    /**
     * Prints to the file identified by pw the comment identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the comment to write
     */
    void printLocaleContent(PrintWriter pw, LTComment lc);

    /**
     * Prints to the file identified by pw the external entity identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the external entity to write
     */
    void printLocaleContent(PrintWriter pw, LTExternalEntity lc);

    /**
     * Prints to the file identified by pw the INI section identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the INI section to write
     */
    void printLocaleContent(PrintWriter pw, LTIniSection lc);

    /**
     * Prints to the file identified by pw the key-value pair identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the key-value pair to write
     */
    void printLocaleContent(PrintWriter pw, LTKeyValuePair lc);

    /**
     * Prints to the file identified by pw the license identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the license to write
     */
    void printLocaleContent(PrintWriter pw, LTLicense lc);

    /**
     * Prints to the file identified by pw the whitespace sequence identified by lc
     * @param pw the reference to the file that is being written
     * @param lc the whitespace sequence to write
     */
    void printLocaleContent(PrintWriter pw, LTWhitespace lc);

    /**
     * Returns the license header of the file
     * @return an object representing the license header of the file, or null if no
     * license has been detected
     */
    LTLicense getFileLicense();

    /**
     * Sets the license header of the file
     * @param fileLicense an object representing the license header of the file
     */
    void setFileLicense(LTLicense fileLicense);
}
