/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.model;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author rpalomares
 */
public interface ParseableFile extends LocaleNode {

    /**
     * Parses a text file (like a DTD file, a Properties file, etc.) corresponding to
     * this ParseableFile and "merges" both, removing obsolete entries, adding
     * new ones (only if ParseableFile is declared as default twin) and updating the
     * remaining if they have changed
     *
     * @param em an EntityManager used to persist in DB new, modified and deleted records
     * @return a list of LocaleContent objects added or modified
     * @throws ParseException in case the parsing fails
     */
    List<LocaleContent> update(EntityManager em) throws ParseException;

    /**
     * Parses a text file (like a DTD file, a Properties file, etc.) and tries to apply
     * it to this ParseableFile. Unlike update(), import does not remove obsolete entries;
     * it just adds new ones, even if this is not the default twin, and updates the
     * remaining if they have changed
     *
     * @param f a pointer to the file that will be parsed
     * @return a list of LocaleContent objects added or modified
     * @throws ParseException in case the parsing fails
     */
    List<LocaleContent> importFromFile(File f) throws ParseException;

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

    // List<LObject> getLObjectCollection();
    List<LocaleContent> getLObjectCollection();
}
