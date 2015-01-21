/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.system;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing application settings
 *
 * @author rpalomares
 */
public class AppSettings {

    /**
     * Default filename for the properties file saving application preferences
     */
    public static final String PREFERENCES_FILENAME = "localizethat.properties";
    /**
     * Key for path to Derby database
     */
    public static final String PREF_DB_PATH = "db.path";
    /**
     * Key for DB user name
     */
    public static final String PREF_DB_LOGIN = "db.login";
    /**
     * Key for DB user password - WARNING: saved in cleartext
     */
    public static final String PREF_DB_PASSWD = "db.passwd";

    /**
     * Key for Persistence Unit Name
     */
    public static final String PREF_PU_NAME = "jpa.persistenceunit.name";

    /**
     * Key for GUI Look And Feel
     */
    public static final String PREF_GUI_LOOK_AND_FEEL = "gui.look.and.feel";

    /**
     * Keys for font name, style and size used in Edit translation panel
     */
    public static final String PREF_FONT_EDITPHRASE_NAME = "font.editphrase.name";
    public static final String PREF_FONT_EDITPHRASE_STYLE = "font.editphrase.style";
    public static final String PREF_FONT_EDITPHRASE_SIZE = "font.editphrase.size";

    /**
     * Keys for font name, style and size used in Table and overall row results
     */
    public static final String PREF_FONT_TABLEVIEW_NAME = "font.tableview.name";
    public static final String PREF_FONT_TABLEVIEW_STYLE = "font.tableview.style";
    public static final String PREF_FONT_TABLEVIEW_SIZE = "font.tableview.size";

    /**
     * Keys for logging preferences
     */
    public static final String PREF_LOGGING_FILENAME = "logging.filename";
    public static final String PREF_LOGGING_LOGLEVEL = "logging.loglevel";

    /**
     * Keys for colors used for string translation status
     */
    public static final String PREF_TRNS_STATUS = "gui.trnscolor.";
    public static final String PREF_TRNS_STATUS_APPROXIMATED = PREF_TRNS_STATUS + "approximated";
    public static final String PREF_TRNS_STATUS_COPIED = PREF_TRNS_STATUS + "copied";
    public static final String PREF_TRNS_STATUS_MODIFIED = PREF_TRNS_STATUS + "modified";
    public static final String PREF_TRNS_STATUS_PROPOSED = PREF_TRNS_STATUS + "proposed";
    public static final String PREF_TRNS_STATUS_TRANSLATED = PREF_TRNS_STATUS + "translated";
    public static final String PREF_TRNS_STATUS_UNTRANSLATED = PREF_TRNS_STATUS + "untranslated";


    private Properties prefValues;
    private String pathToPrefsFile;

    /**
     * Default constructor that looks for the default preferences filename
     */
    public AppSettings() {
        this(System.getProperty("user.dir", ".") + System.getProperty("file.separator")
                + PREFERENCES_FILENAME);
    }

    /**
     * General constructor that loads the preferences filename supplied as a parameter
     *
     * @param pathToPrefsFile path to the preferences file
     */
    public AppSettings(String pathToPrefsFile) {
        Properties defaultPrefs = getDefaultPrefs();

        prefValues = new Properties(defaultPrefs);
        this.pathToPrefsFile = pathToPrefsFile;
        if (!load()) {
            save();
        }
    }
    
    /**
     * Loads preferences from the properties file pointed by the pathToPrefsFile property
     * 
     * @return true if the file was successfully loaded
     */
    public final boolean load() {
        try (FileInputStream appPrefsFH = new FileInputStream(pathToPrefsFile)) {
            prefValues.load(appPrefsFH);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppSettings.class.getName()).log(Level.WARNING,
                    "Preferences file {0} not found, it will be created", pathToPrefsFile);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(AppSettings.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Saves preferences to the properties file pointed by the pathToPrefsFile property
     *
     * @return true if the file was successfully saved
     */
    public final boolean save() {
        try (FileOutputStream appPrefsFH = new FileOutputStream(this.pathToPrefsFile)) {
            prefValues.store(appPrefsFH, "LocalizeThat! preferences file");
            return true;
        } catch (IOException ex) {
            Logger.getLogger(AppSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Returns the boolean value of a preference
     *
     * @param key the preference name to get
     * @return the preference value, or false if it is not set/defined
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Returns the boolean value of a preference
     *
     * @param key the preference name to get
     * @param defaultValue the default value in case it is not set/defined
     * @return the preference value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.valueOf(prefValues.getProperty(key, "" + defaultValue));
    }

    /**
     * Sets the value for a boolean preference
     *
     * @param key the preference name to set
     * @param value the value to set
     */
    public void setBoolean(String key, boolean value) {
        prefValues.setProperty(key, Boolean.toString(value));
    }

    /**
     * Returns a setting as a color, using white as the default
     * @param key the key to get
     * @return the result
     */
    public Color getColor(String key) {
        // TODO use hexadecimal format, like in Product database,
        // and refactor to a common function
        Color c;
        String s = prefValues.getProperty(key, "255,255,255");
        StringTokenizer st = new StringTokenizer(s, ",");

        c = new Color(Integer.parseInt(st.nextToken()),
                      Integer.parseInt(st.nextToken()),
                      Integer.parseInt(st.nextToken()));
        return c;
    }

    /**
     * Set a setting to a string representation of a color value
     * @param key the key to set
     * @param value the new value
     */
    public void setColor(String key, Color value) {
        // TODO use hexadecimal format, like in Product database,
        // and refactor to a common function
        String s = Integer.toString(value.getRed()) + ","
                 + Integer.toString(value.getGreen()) + ","
                 + Integer.toString(value.getBlue());
        prefValues.setProperty(key, s);
    }

    /**
     * Returns the integer value of a preference
     *
     * @param key the preference name to get
     * @return the preference value, or false if it is not set/defined
     */
    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    /**
     * Returns the integer value of a preference
     *
     * @param key the preference name to get
     * @param defaultValue the default value in case it is not set/defined
     * @return the preference value
     */
    public int getInteger(String key, int defaultValue) {
        return Integer.valueOf(prefValues.getProperty(key, "" + defaultValue));
    }

    /**
     * Sets the value for a integer preference
     *
     * @param key the preference name to set
     * @param value the value to set
     */
    public void setInteger(String key, int value) {
        prefValues.setProperty(key, Integer.toString(value));
    }

    /**
     * Returns the String value of a preference
     *
     * @param key the preference name to get
     * @return the preference value, or false if it is not set/defined
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * Returns the String value of a preference
     *
     * @param key the preference name to get
     * @param defaultValue the default value in case it is not set/defined
     * @return the preference value
     */
    public String getString(String key, String defaultValue) {
        return prefValues.getProperty(key, defaultValue);
    }

    /**
     * Sets the value for a String preference
     *
     * @param key the preference name to set
     * @param value the value to set
     */
    public void setString(String key, String value) {
        prefValues.setProperty(key, value);
    }

    private Properties getDefaultPrefs() {
        Properties defaultPrefs = new Properties();
        String dbPath;

        // Set the default path to database
        dbPath = System.getProperty("user.dir", ".") + System.getProperty("file.separator")
                + "db";
        dbPath = "/home/rpalomares/.netbeans-derby";
        defaultPrefs.setProperty(PREF_DB_PATH, dbPath);

        // Set the default username and password
        defaultPrefs.setProperty(PREF_DB_LOGIN, "sa");
        defaultPrefs.setProperty(PREF_DB_PASSWD, "sa");

        // Set the default persistence unit name and parameters
        defaultPrefs.setProperty(PREF_PU_NAME, "localizethatPU");

        // Set the default look and feel
        defaultPrefs.setProperty(PREF_GUI_LOOK_AND_FEEL, "Metal");

        // Set the default font for Edit translation font
        defaultPrefs.setProperty(PREF_FONT_EDITPHRASE_NAME, "Sans Serif");
        defaultPrefs.setProperty(PREF_FONT_EDITPHRASE_STYLE, Integer.toString(Font.PLAIN));
        defaultPrefs.setProperty(PREF_FONT_EDITPHRASE_SIZE, "12");

        // Set the default font for overall row results
        defaultPrefs.setProperty(PREF_FONT_TABLEVIEW_NAME, "Sans Serif");
        defaultPrefs.setProperty(PREF_FONT_TABLEVIEW_STYLE, Integer.toString(Font.PLAIN));
        defaultPrefs.setProperty(PREF_FONT_TABLEVIEW_SIZE, "12");

        return defaultPrefs;
    }

}
