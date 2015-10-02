/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ExecutionException;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * SwingWorker task that tries to read the first lines of a text file and displays them in a JTextArea
 * @author rpalomares
 */
public class FileHeadReaderWorker extends SwingWorker<String, Void> {
    private static final int LINES_TO_READ = 15;
    private static final String NO_CONTENT_AVAILABLE = "Couldn't find or open file";
    private final File textFile;
    private final JTextArea ta;

    public FileHeadReaderWorker(File textFile, JTextArea ta) {
        this.textFile = textFile;
        this.ta = ta;
    }

    @Override
    protected String doInBackground() throws Exception {
        int lines = 0;
        String l;
        String head;

        try (BufferedReader bfr = new BufferedReader(new FileReader(textFile))) {
            StringBuilder sb = new StringBuilder(1024);
            while (lines < FileHeadReaderWorker.LINES_TO_READ && (l = bfr.readLine()) != null) {
                sb.append(l);
                sb.append(System.getProperty("line.separator"));
            }
            head = sb.toString();
        } catch (Exception e) {
            head = NO_CONTENT_AVAILABLE;
        }
        return head;
    }

    @Override
    protected void done() {
        String head;
        
        try {
            head = get();
        } catch (InterruptedException | ExecutionException e) {
            head = NO_CONTENT_AVAILABLE;
        }
        ta.setText(head);
    }
}
