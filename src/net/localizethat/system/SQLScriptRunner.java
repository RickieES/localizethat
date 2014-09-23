/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class takes a connection to a database and an InputStream to a SQL script file and allows to execute the
 * sentences in the script file against the database connection, either in a synchronous way (by using the runScript()
 * method) or an asynchronous way (as the class implements the Runnable interface)
 *
 * This class is inspired in iBatis ScriptRunner class and Francisco Morero Peyrona's SQLExecutor class from Tapas
 * application
 *
 * @author rpalomares
 */
public class SQLScriptRunner implements Runnable {

    private static final String SQL_COMMENT_PREFIX = "--";
    private static final String SQL_STATEMENT_TERMINATOR = ";";
    private final Connection dbc;
    private final InputStream is;
    private boolean result;

    /**
     * Creates an instance of SQLScriptRunner
     *
     * @param dbConnection a valid database connection
     * @param is an input stream where the SQL senteces will be read from
     */
    public SQLScriptRunner(Connection dbConnection, InputStream is) {
        this.dbc = dbConnection;
        this.is = is;
        result = true;
    }

    /**
     * Runs the script synchronously
     *
     * @return true if all sentences were successfully executed, false otherwise
     */
    public boolean runScript() {
        run();
        return result;
    }

    /**
     * Runs the script as an asynchronous task; the result can be retrieved using isSuccessful()
     */
    @Override
    public void run() {
        int lineNumber = 0; // Used to provide feedback in case of an error
        String line;
        StringBuilder sb = new StringBuilder(512);

        try (BufferedReader r = new BufferedReader(new InputStreamReader(is));
                Statement stmt = dbc.createStatement()) {

            while ((line = r.readLine()) != null) {
                lineNumber++;
                if (line.startsWith(SQL_COMMENT_PREFIX)) {
                    continue;
                }

                sb.append(" ");
                sb.append(line.trim());
                if (line.endsWith(SQL_STATEMENT_TERMINATOR)) {
                    sb.deleteCharAt(sb.length() - 1);
                    stmt.execute(sb.toString());
                    sb.setLength(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SQLScriptRunner.class.getName()).log(Level.SEVERE,
                    "I/O Error (see trace below)");
            Logger.getLogger(SQLScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } catch (SQLException ex) {
            Logger.getLogger(SQLScriptRunner.class.getName()).log(Level.SEVERE,
                    "The line {0} caused an error: {1}", new Object[]{lineNumber, sb.toString()});
            Logger.getLogger(SQLScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
    }


/**
 * Returns the result of the execution
 *
 * @return true if there were no exceptions, all statements were executed and returned themselves true; false in any
 * other case
 */
public boolean isSuccessful() {
        return result;
    }
}
