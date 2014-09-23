/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.system;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.localizethat.util.VersionObject;

/**
 * Uses raw JDBC connection to check for the existence of the expected Derby database
 * and creates it if needed, so JPA can find the environment ready
 *
 * @author rpalomares
 */
public final class DBChecker {

    /**
     * Default directory name for the database in this application
     */
    public static final String DB_NAME = "lt-data";
    private static final String[] scriptList = {"script-0.0.a1.sql", "script-0.0.a2.sql"};
    private final String pathToDB; // Base dir for Derby databases; the actual database dir lives inside this one
    private final String login;
    private final String passwd;
    private File fPathToDB;

    /**
     * Constructs a DBChecker instance
     * @param pathToDB path to the Derby database dir; the actual database lives inside it
     * @param login the username to connect to the database
     * @param passwd the password in cleartext to the database
     */
    public DBChecker(String pathToDB, String login, String passwd) {
        this.pathToDB = pathToDB;
        this.login = login;
        this.passwd = passwd;
    }

    /**
     * Checks if the Derby database dir exists and tries to create if it doesn't
     * @return true if the database dir exists or it has been succesfully created, and
     * it is possible to read from and write to it
     */
    public boolean checkAndCreateDBDir() {
        fPathToDB = new File(pathToDB);
        boolean check;

        check = (fPathToDB.exists() || fPathToDB.mkdirs())
                && fPathToDB.canRead() && fPathToDB.canWrite();
        return check;
    }

    /**
     * Checks if the actual database exists and tries to create if it doesn't
     *
     * Currently, the method assumes that, if the database dir exists, the actual database files
     * living inside it exist and the right schema has been created. Future versions will check
     * the schema version and will incrementally update it if needed
     * @return true if the database exists or it has been successfully created, including their
     * table schema and counters; false otherwise
     */
    public boolean checkAndCreateDB() {
        System.setProperty("derby.system.home", pathToDB);
        String ltDBDir = pathToDB + System.getProperty("file.separator") + DB_NAME;
        File fltDBDir = new File(ltDBDir);
        boolean createDB = !fltDBDir.exists();
        Connection dbConnection = null;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            dbConnection = DriverManager.getConnection("jdbc:derby:" + DB_NAME + ";create="
                    + (Boolean.toString(createDB)), login, passwd);

            return configureDatabase(dbConnection, scriptList);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBChecker.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("XJ040")) {
                ex = ex.getNextException();
                if (ex.getSQLState().equals("XSDB6")) {
                    Logger.getLogger(DBChecker.class.getName()).log(Level.SEVERE,
                            "There is probably another instance/program using the database");
                }
            }
            Logger.getLogger(DBChecker.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                    dbConnection = DriverManager.getConnection("jdbc:derby:;shutdown=true");
                    dbConnection.close();
                } catch (SQLException ex) {
                    if (ex.getSQLState().equals("XJ015")) {
                        Logger.getLogger(DBChecker.class.getName()).info("Database cleanly closed");
                    } else {
                        Logger.getLogger(DBChecker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private boolean configureDatabase(Connection dbConnection, String... sqlScripts) {
        VersionObject currentDBVersion;
        boolean result = true;
        VersionObject[] versionObjects;
        InputStream is;

        currentDBVersion = getDBVersion(dbConnection);
        int i = 0;
        versionObjects = new VersionObject[sqlScripts.length];

        while (result && i < sqlScripts.length) {
            VersionObject vo = new VersionObject(sqlScripts[i].replace(".sql", "").substring(7));
            vo.setPayLoad(sqlScripts[i]);
            versionObjects[i] = vo;
            i++;
        }

        Arrays.sort(versionObjects);

        for(VersionObject vo : versionObjects) {
            if (vo.compareTo(currentDBVersion) > 0) {
                is = getClass().getResourceAsStream("../model/jpa/" + (String) vo.getPayLoad());
                SQLScriptRunner sqlr = new SQLScriptRunner(dbConnection, is);
                result = sqlr.runScript();
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    private VersionObject getDBVersion(Connection dbConnection) {
        Statement st;
        String dbVersionString = "";

        try {
            st = dbConnection.createStatement();
            ResultSet res = st.executeQuery("SELECT VALUE FROM APP.CONFIG WHERE ID = 'DB_VERSION'");
            while (res.next()) {
                dbVersionString = res.getString("VALUE");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBChecker.class.getName()).log(Level.WARNING, null, ex);
            dbVersionString = "0.0.0";
        }
        VersionObject dbVersion = new VersionObject(dbVersionString);
        return dbVersion;
    }
}
