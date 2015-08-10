/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import net.localizethat.gui.MainWindow;
import net.localizethat.system.AppSettings;
import net.localizethat.system.DBChecker;
import net.localizethat.util.gui.GuiUtils;

/**
 * LocalizeThat! Entry class
 * @author rpalomares
 */
public class Main {
    /**
     * String containing the version number in the format Major.Minor.devversion
     * Major and minor values are parsed and compared as integers, whereas the
     * devversion is parsed and compared as an String like "a1" for alpha
     * versions, "b2" for beta versions, or "r5" for release maintenance version
     *
     * For instance, when comparing "2.0.b4" to "12.0.r3", the comparison would be:
     * - Major: 2 &lt; 12
     * - Minor: 0 == 0
     * - Bugrelease: "b4" &lt; "r3"
     */
    public static final String version = "0.6";

    /**
     * Reference to the application settings
     */
    public static AppSettings appSettings;

    /**
     * Reference to the application main window
     */
    /**
     * Reference to the application main window
     */
    public static MainWindow mainWindow;
    /**
     * Reference to global Entity Manager Factory
     */
    public static EntityManagerFactory emf;

    /**
     * @param args the command line arguments (not used for now)
     */
    public static void main(String[] args) {
       // Process parameters
        processParameters(args);

        // Process preferences
        appSettings = new AppSettings();

        // Check database existence and create if needed
        DBChecker dbChecker = new DBChecker(appSettings.getString(AppSettings.PREF_DB_PATH),
                                            appSettings.getString(AppSettings.PREF_DB_LOGIN),
                                            appSettings.getString(AppSettings.PREF_DB_PASSWD));
        if (!(dbChecker.checkAndCreateDBDir() && dbChecker.checkAndCreateDB())) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                "Can't properly access the database, check error dump log");
            System.exit(1);
        }

        // Set up persistence unit
        Map<String, String> connProps = new HashMap<>(4);
        connProps.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        connProps.put("javax.persistence.jdbc.url", "jdbc:derby:" + DBChecker.DB_NAME);
        connProps.put("javax.persistence.jdbc.user", appSettings.getString(AppSettings.PREF_DB_LOGIN));
        connProps.put("javax.persistence.jdbc.password", appSettings.getString(AppSettings.PREF_DB_PASSWD));
        emf = Persistence.createEntityManagerFactory("localizethatPU", connProps);

        String preferredLafName = appSettings.getString(AppSettings.PREF_GUI_LOOK_AND_FEEL);
        if (!GuiUtils.setBestAvailableLookAndFeel(preferredLafName)) {
            System.exit(1);
        }

        mainWindow = new MainWindow();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            mainWindow.setVisible(true);
        });
    }

    private static void processParameters(String[] args) {
        // Nothing to do at the moment
    }

    /**
     * Cleans up the resources used by the application before closing the system
     */
    public static void cleanUpResources() {
        emf.close();
    }
}
