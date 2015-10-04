/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVRuntimeException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;
import net.localizethat.gui.dialogs.ImportCSVGlossaryDialog;

/**
 * SwingWorker task that reads the CSV file to grab the first line to import as headers
 * @author rpalomares
 */
public class CSVHeaderReaderWorker extends SwingWorker<List<String>, Void> {
    private final File csvFile;
    private final CSV csvEnvironment;
    private List<String> headers;
    private final List<DefaultComboBoxModel<String>> headerModelList;
    boolean firstRowHasHeaders;
    ImportCSVGlossaryDialog icg;

    public CSVHeaderReaderWorker(File csvFile, CSV csvSettings, List<DefaultComboBoxModel<String>> headerModelList,
            boolean firstRowHasHeaders, ImportCSVGlossaryDialog icg) {

        this.csvFile = csvFile;
        this.csvEnvironment = csvSettings;
        this.headerModelList = headerModelList;
        this.firstRowHasHeaders = firstRowHasHeaders;
        this.icg = icg;
    }

    @Override
    protected List<String> doInBackground() throws Exception {
        CSVReader csvr;
        headers = new ArrayList<>(5);

        try {
            csvr = csvEnvironment.reader(csvFile);
            headers.add("(none)");
            headers.addAll(Arrays.asList(csvr.readNext()));
        } catch (CSVRuntimeException e) {
            headers = null;
        }
        return headers;
    }

    @Override
    protected void done() {
        if (!firstRowHasHeaders) {
            // TODO enhance default naming scheme to use letters instead of numbers (A, B,...Z, AA, AB...)
            for(int i = 1; i < headers.size(); i++) {
                headers.set(i, "Column " + i);
            }
        }

        for(DefaultComboBoxModel<String> cmbModel : headerModelList) {
            cmbModel.removeAllElements();
            for(String h : headers) {
                cmbModel.addElement(h);
            }
        }
        icg.retrieveSavedCSVImportFieldBindings();
    }
}
