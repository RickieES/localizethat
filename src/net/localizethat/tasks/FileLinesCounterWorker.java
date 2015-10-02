/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * SwingWorker task that tries to return the number of lines of a text file
 * @author rpalomares
 */
public class FileLinesCounterWorker extends SwingWorker<Integer, Void> {
    private final File textFile;
    private final JLabel ofLinesLabel;

    public FileLinesCounterWorker(File textFile, JLabel ofLinesLabel) {
        this.textFile = textFile;
        this.ofLinesLabel = ofLinesLabel;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        int lines;
        String l;

        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(textFile))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            // LineNumberReader counts from 0, but we want a "human value", so we add 1
            lines = lineNumberReader.getLineNumber() + 1;
        } catch (Exception e) {
            lines = -1;
        }
        return lines;
    }

    @Override
    protected void done() {
        int lines;

        try {
            lines = get();
        } catch (InterruptedException | ExecutionException e) {
            lines = -1;
        }
        ofLinesLabel.setText("(of " + lines + " lines)");
    }
}
