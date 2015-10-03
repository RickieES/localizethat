/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.tasks;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.localizethat.Main;
import net.localizethat.gui.dialogs.OnExistingTermAction;
import net.localizethat.model.Glossary;
import net.localizethat.model.GlsEntry;
import net.localizethat.model.GlsTranslation;
import net.localizethat.model.L10n;
import net.localizethat.model.PartOfSpeech;
import net.localizethat.util.DateToStringConverter;
import net.localizethat.util.gui.JStatusBar;

/**
 * SwingWorker task that imports a CSV file as a glossary
 * @author rpalomares
 */
public class CSVImporterWorker extends SwingWorker<List<String>, Void> {
    private final File csvFile;
    private final CSV csvEnvironment;
    private final EntityManager em;
    private final Glossary glossary;
    private final boolean testMode;
    private final CSVImportSettings cis;
    private final CSVImportResults cir;
    private final JStatusBar statusBar;

    public CSVImporterWorker(CSVImportSettings cis, JStatusBar statusBar) {
        super();
        this.csvFile = cis.getFileToImport();
        this.csvEnvironment = CSV.separator(cis.getFieldDelimiter())
                .quote(cis.getTextDelimiter())
                .charset(cis.getCharset())
                .skipLines(cis.getSkippedLines()) // UI control counts from 1, file rows counts from 0
                .create();
        this.testMode = cis.isTestMode();
        this.em = Main.emf.createEntityManager();
        this.glossary = em.find(Glossary.class, cis.getGlossary().getId());
        this.cis = cis;
        this.statusBar = statusBar;

        this.cir = new CSVImportResults(csvFile, testMode, cis.getSkippedLines(), glossary.getName());
        this.cir.setHeadersIncluded(cis.isFirstRowHasHeaders());
    }

    @Override
    protected List<String> doInBackground() throws Exception {
        List<GlsEntry> entriesPool;
        String[] valuesFromCSVLine;
        boolean foundInDB;
        boolean foundInPool;
        int indexPool;
        int transactCount = 0;
        int processedLines;

        entriesPool = new ArrayList<>(cis.getFileLines());
        try (CSVReader csvr = csvEnvironment.reader(csvFile)) {
            processedLines = skipLines(cis, csvr);
            this.setProgress(processedLines*100/cis.getFileLines());

            valuesFromCSVLine = csvr.readNext();
            while (valuesFromCSVLine != null) {
                processedLines++;
                if (!this.testMode && transactCount == 0 && !em.isJoinedToTransaction()) {
                    em.getTransaction().begin();
                }
                // foundInDB = false;
                foundInPool = false;

                // At the very least, we need an original term and a translation
                if ((cis.getOrigTermColumn() >= 0)
                        && (cis.getOrigTermColumn() < valuesFromCSVLine.length)
                        && (cis.getTrnsValueColumn() >= 0)
                        && (cis.getTrnsValueColumn() < valuesFromCSVLine.length)) {
                    cir.incrementProcessedLines();

                    // builtGe holds the GlsEntry created from the imported CSV line,
                    // ge holds a referenece either to the same object, or to the
                    // one retrieved from the DB or entriesPool
                    GlsEntry builtGe = fillGlsEntry(valuesFromCSVLine);
                    GlsEntry ge = builtGe;

                    foundInDB = (glossary.glsEntryExists(builtGe));
                    if (foundInDB) {
                        cir.incrementExistingEntries();
                        ge = glossary.findGlsEntry(builtGe);
                    } else {
                        cir.incrementAddedEntries();
                        indexPool = findGlsEntryInPool(entriesPool, ge);
                        foundInPool = (indexPool != -1);
                        if (foundInPool) {
                            ge = entriesPool.get(indexPool);
                        }
                    }

                    if (!foundInDB) {
                        if (!this.testMode) {
                            glossary.getGlsEntryCollection().add(ge);
                            em.persist(ge);
                            ge = glossary.findGlsEntry(ge);
                            transactCount++;
                        }

                        if (!foundInPool) {
                            entriesPool.add(ge);
                        }
                    }

                    // At this point, we have in ge a valid reference for the imported GlsEntry;
                    // either it existed in DB or entriesPool, or we have created it in the DB
                    // and/or entriesPool

                    List<GlsTranslation> lstGT = fillGlsTranslations(valuesFromCSVLine, ge);
                    switch (cis.getOnExistingTerms()) {
                        case ADD_NEW_TRANSLATIONS_ONLY:
                            for(GlsTranslation gt : lstGT) {
                                if (!ge.glsTranslationExists(gt)) {
                                    cir.incrementAddedTranslations();
                                    ge.getGlsTranslationCollection().add(gt);
                                    if (!this.testMode) {
                                        em.persist(gt);
                                        transactCount++;
                                    }
                                } else {
                                    cir.incrementExistingTranslations();
                                }
                            }
                            break;
                        case MERGE:
                            // Update comment and last update in GlsEntry if it already existed
                            ge.setLastUpdate(builtGe.getLastUpdate());
                            if (builtGe.getComment() != null
                                    && builtGe.getComment().length() > 0) {
                                ge.setComment(builtGe.getComment());
                            }

                            // Only if we have found the GlsEntry in DB and we're not in test mode,
                            // we'd be doing one more change to the DB
                            if (foundInDB && !this.testMode) {
                                transactCount++;
                            }

                            for(GlsTranslation builtGt : lstGT) {
                                if (!ge.glsTranslationExists(builtGt)) {
                                    cir.incrementAddedTranslations();
                                    builtGt.setGlseId(ge);
                                    ge.getGlsTranslationCollection().add(builtGt);
                                    if (!this.testMode) {
                                        em.persist(builtGt);
                                    }
                                } else {
                                    GlsTranslation gt = ge.findGlsTranslation(builtGt);

                                    if (builtGt.getComment() != null
                                            && builtGt.getComment().length() > 0) {
                                        gt.setComment(builtGt.getComment());
                                    }
                                    gt.setLastUpdate(builtGt.getLastUpdate());
                                    cir.incrementExistingTranslations();
                                }
                                if (!this.testMode) {
                                    transactCount++;
                                }
                            }
                            break;
                        case REPLACE:
                            // Update comment and last update in GlsEntry
                            if (builtGe.getComment() != null
                                    && builtGe.getComment().length() > 0) {
                                ge.setComment(builtGe.getComment());
                            }
                            ge.setLastUpdate(builtGe.getLastUpdate());

                            // Only if we have found the GlsEntry in DB and we're not in test mode,
                            // we'd be doing one more change to the DB
                            if (foundInDB && !this.testMode) {
                                transactCount++;
                            }

                            // Remove existing translations
                            Collection<GlsTranslation> geGlsTCollection = ge.getGlsTranslationCollection();
                            Iterator<GlsTranslation> gtColIter = geGlsTCollection.iterator();
                            while (gtColIter.hasNext()) {
                                GlsTranslation gtToRemove = gtColIter.next();
                                gtColIter.remove();
                                if (foundInDB && !this.testMode) {
                                    em.remove(gtToRemove);
                                    transactCount++;
                                }
                            }

                            for(GlsTranslation gt : lstGT) {
                                // The following condition will likely be true always, since we
                                // have just wiped out the existing list
                                if (!ge.glsTranslationExists(gt)) {
                                    gt.setGlseId(ge);
                                    geGlsTCollection.add(gt);
                                    cir.incrementOverridenTranslations();
                                    if (!this.testMode) {
                                        em.persist(gt);
                                        transactCount++;
                                    }
                                }
                            }
                            break;
                        case DO_NOT_ADD_TRANSLATIONS:
                            // Do nothing if the GlsEntry already exists
                        default:
                            break;
                    }
                }

                if (transactCount > 50) {
                    em.getTransaction().commit();
                    transactCount = 0;
                }

                valuesFromCSVLine = csvr.readNext();
                this.setProgress(processedLines*100/cis.getFileLines());
            }

            if (em.isJoinedToTransaction() && transactCount > 0) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            Logger.getLogger(CSVImporterWorker.class.getName()).log(Level.SEVERE, null, e);
            if (em.isJoinedToTransaction()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
        this.setProgress(100);
        return null;
    }

    @Override
    protected void done() {
        statusBar.logMessage(JStatusBar.LogMsgType.INFO, cir.getResultsShortMessage(),
                cir.getResultsLongMessage());
    }

    /**
     * Skips the calculated number of lines not relevant at the beginning of the CSV file
     * @param cis the settings object, that include the number of lines to skip and if there are headers
     * @param csvr the CSVReader
     * @return the number of skipped lines
     * @throws IOException in case of an error while reading (EoF is dealt without raising exceptions)
     */
    private int skipLines(CSVImportSettings cis, CSVReader csvr) throws IOException {
        String[] valuesFromCSVLine;
        int lineCounter = cis.getSkippedLines();
        int result;

        // If there is a headers line, it must be skipped also
        if (cis.isFirstRowHasHeaders()) {
            lineCounter++;
        }

        result = lineCounter;

        // Go beyond skipped lines
        if (lineCounter > 0) {
            do {
                valuesFromCSVLine = csvr.readNext();
                lineCounter--;
            } while ((valuesFromCSVLine != null) && lineCounter > 0);
        }
        return result;
    }

    /**
     * Creates and fills a GlsEntry with the data received from the CSV file
     * @param valuesFromCSVLine an array of String objects with the field data for the CSV imported line
     * @return a GlsEntry with sensible defaults for properties not supplied through the CSV line
     */
    private GlsEntry fillGlsEntry(String[] valuesFromCSVLine) {
        int csvLen = valuesFromCSVLine.length;
        DateToStringConverter d2sConverter = new DateToStringConverter();
        GlsEntry ge = new GlsEntry();

        ge.setTerm(valuesFromCSVLine[cis.getOrigTermColumn()]);
        ge.setGlosId(glossary);

        if ((cis.getOrigTermCommentColumn() >= 0)
                && (cis.getOrigTermCommentColumn() < csvLen)) {
            ge.setComment(valuesFromCSVLine[cis.getOrigTermCommentColumn()]);
        }

        if ((cis.getOrigTermCreationDateColumn() >= 0)
                && (cis.getOrigTermCreationDateColumn() < csvLen)) {
            ge.setCreationDate((Date) d2sConverter.convertReverse(
                    valuesFromCSVLine[cis.getOrigTermCreationDateColumn()]));
        } else {
            ge.setCreationDate(new Date());
        }

        if ((cis.getOrigTermLastUpdateColumn() >= 0)
                && (cis.getOrigTermLastUpdateColumn() < csvLen)) {
            ge.setLastUpdate((Date) d2sConverter.convertReverse(
                    valuesFromCSVLine[cis.getOrigTermLastUpdateColumn()]));
        } else {
            ge.setLastUpdate(new Date());
        }

        if ((cis.getOrigTermPoSColumn() >= 0)
                && (cis.getOrigTermPoSColumn() < csvLen)) {
            try {
                ge.setPartOfSpeech(PartOfSpeech.valueOf(
                        valuesFromCSVLine[cis.getOrigTermPoSColumn()]));
            } catch (IllegalArgumentException e) {
                ge.setPartOfSpeech(PartOfSpeech.OTHER);
            }
        } else {
            ge.setPartOfSpeech(PartOfSpeech.OTHER);
        }
        return ge;
    }

    /**
     * Creates GlsTranslation objects for one CSV imported line. It may create more than one
     * GlsTranslation object if multiple values per line is checked
     * @param valuesFromCSVLine a String array of fields imported from the CSV file
     * @param ge the parent GlsEntry according to the CSV file
     * @return a list of GlsTranslation objects
     */
    private List<GlsTranslation> fillGlsTranslations(String[] valuesFromCSVLine, GlsEntry ge) {
        int csvLen = valuesFromCSVLine.length;
        List<GlsTranslation> lstGT = new ArrayList<>(1);
        DateToStringConverter d2sConverter = new DateToStringConverter();
        GlsTranslation gt;
        String trnsValue = valuesFromCSVLine[cis.getTrnsValueColumn()];
        String[] values;

        if (trnsValue.contains(String.valueOf(cis.getMultipeValuesSeparator()))) {
            values = trnsValue.split(",");
        } else {
            values = new String[] {trnsValue};
        }

        for(String value : values) {
            if ((cis.getTrnsValueColumn() >= 0)
                    && (cis.getTrnsValueColumn() < csvLen)) {
                gt = new GlsTranslation();
                gt.setValue(value.trim());
                gt.setL10nId(cis.getImportLocale());
                gt.setGlseId(ge);

                if ((cis.getTrnsCommentColumn() >= 0)
                        && (cis.getTrnsCommentColumn() < csvLen)) {
                    gt.setComment(valuesFromCSVLine[cis.getTrnsCommentColumn()]);
                }

                if ((cis.getTrnsCreationDateColumn() >= 0)
                        && (cis.getTrnsCreationDateColumn() < csvLen)) {
                    gt.setCreationDate((Date) d2sConverter.convertReverse(
                            valuesFromCSVLine[cis.getTrnsCreationDateColumn()]));
                } else {
                    gt.setCreationDate(new Date());
                }

                if ((cis.getTrnsLastUpdateColumn() >= 0)
                        && (cis.getTrnsLastUpdateColumn() < csvLen)) {
                    gt.setLastUpdate((Date) d2sConverter.convertReverse(
                            valuesFromCSVLine[cis.getTrnsLastUpdateColumn()]));
                } else {
                    gt.setLastUpdate(new Date());
                }
                lstGT.add(gt);
            }
        }
        return lstGT;
    }

    private int findGlsEntryInPool(List<GlsEntry> entriesPool, GlsEntry ge) {
        for(GlsEntry e : entriesPool) {
            if ((e != null) && (e.getTerm().equals(ge.getTerm()))
                    && (e.getPartOfSpeech() == ge.getPartOfSpeech())) {
                return entriesPool.indexOf(e);
            }
        }
        return -1;
    }

    @XmlRootElement
    public static class CSVImportSettings {
        public static CSVImportSettings recreateFromXML(String fileName) {
            try {
                JAXBContext jc = JAXBContext.newInstance (CSVImportSettings.class);
                Unmarshaller u = jc.createUnmarshaller ();
                File f = new File(fileName);
                CSVImportSettings cis = (CSVImportSettings) u.unmarshal(f);
                return cis;
            } catch (JAXBException e) {
                if (e.getLinkedException() instanceof FileNotFoundException) {
                    Main.mainWindow.getStatusBar().setWarnText("Saved preferences file not found");
                } else {
                    Logger.getLogger(CSVImportSettings.class.getName()).log(Level.SEVERE, null, e);
                }
                return null;
            }
        }
        private Charset charset;
        private File fileToImport;
        private boolean testMode;
        private Glossary glossary;
        private char fieldDelimiter;
        private int fileLines;
        private char textDelimiter;
        private int skippedLines;
        private boolean allFieldsQuoted;
        private boolean firstRowHasHeaders;
        private OnExistingTermAction onExistingTerms;
        private boolean multipleValuesOnTranslation;
        private char multipeValuesSeparator;
        private L10n importLocale;
        private int origTermColumn;
        private int origTermCommentColumn;
        private int origTermCreationDateColumn;
        private int origTermLastUpdateColumn;
        private int origTermPoSColumn;
        private int trnsValueColumn;
        private int trnsCommentColumn;
        private int trnsCreationDateColumn;
        private int trnsLastUpdateColumn;

        public void serializeToXML(String fileName) {
            try {
                JAXBContext context = JAXBContext.newInstance(CSVImportSettings.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(this, new File(fileName));
            } catch (JAXBException e) {
                Logger.getLogger(CSVImportSettings.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        @XmlJavaTypeAdapter(CharsetJAXBAdapter.class)
        public Charset getCharset() {
            return charset;
        }

                public File getFileToImport() {
                    return fileToImport;
                }

        public boolean isTestMode() {
            return testMode;
        }

        public Glossary getGlossary() {
            return glossary;
        }

        public char getFieldDelimiter() {
            return fieldDelimiter;
        }

        public char getTextDelimiter() {
            return textDelimiter;
        }

        public int getSkippedLines() {
            return skippedLines;
        }

        public boolean isAllFieldsQuoted() {
            return allFieldsQuoted;
        }

        public boolean isFirstRowHasHeaders() {
            return firstRowHasHeaders;
        }

        public int getFileLines() {
            return fileLines;
        }

        public OnExistingTermAction getOnExistingTerms() {
            return onExistingTerms;
        }

        public boolean isMultipleValuesOnTranslation() {
            return multipleValuesOnTranslation;
        }

        public char getMultipeValuesSeparator() {
            return multipeValuesSeparator;
        }

        public L10n getImportLocale() {
            return importLocale;
        }

        public int getOrigTermColumn() {
            return origTermColumn;
        }

        public int getOrigTermCommentColumn() {
            return origTermCommentColumn;
        }

        public int getOrigTermCreationDateColumn() {
            return origTermCreationDateColumn;
        }

        public int getOrigTermLastUpdateColumn() {
            return origTermLastUpdateColumn;
        }

        public int getOrigTermPoSColumn() {
            return origTermPoSColumn;
        }

        public int getTrnsValueColumn() {
            return trnsValueColumn;
        }

        public int getTrnsCommentColumn() {
            return trnsCommentColumn;
        }

        public int getTrnsCreationDateColumn() {
            return trnsCreationDateColumn;
        }

        public int getTrnsLastUpdateColumn() {
            return trnsLastUpdateColumn;
        }

        public CSVImportSettings setFileToImport(File fileToImport) {
            this.fileToImport = fileToImport;
            return this;
        }

        public CSVImportSettings setTestMode(boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        public CSVImportSettings setGlossary(Glossary glossary) {
            this.glossary = glossary;
            return this;
        }

        public CSVImportSettings setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public CSVImportSettings setFieldDelimiter(char fieldDelimiter) {
            this.fieldDelimiter = fieldDelimiter;
            return this;
        }

        public CSVImportSettings setTextDelimiter(char textDelimiter) {
            this.textDelimiter = textDelimiter;
            return this;
        }

        public CSVImportSettings setSkippedLines(int skippedLines) {
            this.skippedLines = skippedLines;
            return this;
        }

        public CSVImportSettings setAllFieldsQuoted(boolean allFieldsQuoted) {
            this.allFieldsQuoted = allFieldsQuoted;
            return this;
        }

        public CSVImportSettings setFirstRowHasHeaders(boolean firstRowHasHeaders) {
            this.firstRowHasHeaders = firstRowHasHeaders;
            return this;
        }

        public CSVImportSettings setFileLines(int fileLines) {
            this.fileLines = fileLines;
            return this;
        }

        public CSVImportSettings setOnExistingTerms(OnExistingTermAction onExistingTerms) {
            this.onExistingTerms = onExistingTerms;
            return this;
        }

        public CSVImportSettings setMultipleValuesOnTranslation(boolean multipleValuesOnTranslation) {
            this.multipleValuesOnTranslation = multipleValuesOnTranslation;
            return this;
        }

        public CSVImportSettings setMultipeValuesSeparator(char multipeValuesSeparator) {
            this.multipeValuesSeparator = multipeValuesSeparator;
            return this;
        }

        public CSVImportSettings setImportLocale(L10n importLocale) {
            this.importLocale = importLocale;
            return this;
        }

        public CSVImportSettings setOrigTermColumn(int origTermColumn) {
            this.origTermColumn = origTermColumn;
            return this;
        }

        public CSVImportSettings setOrigTermCommentColumn(int origTermCommentColumn) {
            this.origTermCommentColumn = origTermCommentColumn;
            return this;
        }

        public CSVImportSettings setOrigTermCreationDateColumn(int origTermCreationDateColumn) {
            this.origTermCreationDateColumn = origTermCreationDateColumn;
            return this;
        }

        public CSVImportSettings setOrigTermLastUpdateColumn(int origTermLastUpdateColumn) {
            this.origTermLastUpdateColumn = origTermLastUpdateColumn;
            return this;
        }

        public CSVImportSettings setOrigTermPoSColumn(int origTermPoSColumn) {
            this.origTermPoSColumn = origTermPoSColumn;
            return this;
        }

        public CSVImportSettings setTrnsValueColumn(int trnsValueColumn) {
            this.trnsValueColumn = trnsValueColumn;
            return this;
        }

        public CSVImportSettings setTrnsCommentColumn(int trnsCommentColumn) {
            this.trnsCommentColumn = trnsCommentColumn;
            return this;
        }

        public CSVImportSettings setTrnsCreationDateColumn(int trnsCreationDateColumn) {
            this.trnsCreationDateColumn = trnsCreationDateColumn;
            return this;
        }

        public CSVImportSettings setTrnsLastUpdateColumn(int trnsLastUpdateColumn) {
            this.trnsLastUpdateColumn = trnsLastUpdateColumn;
            return this;
        }

        private static class CharsetJAXBAdapter extends XmlAdapter<String, Charset> {
            @Override
            public Charset unmarshal(final String v) throws Exception {
                Charset cs;
                try {
                    cs = Charset.forName(v);
                } catch (Exception e) {
                    cs = Charset.forName("UTF-8");
                }
                return cs;
            }

            @Override
            public String marshal(Charset v) throws Exception {
                return v.name();
            }
        }

    }

    public class CSVImportResults {
        private final File filename;
        private final boolean testMode;
        private final int skippedLines;
        private final String glossaryName;
        private boolean headersIncluded;
        private int processedLines;
        private int addedEntries;
        private int existingEntries;
        private int failedEntries;
        private int addedTranslations;
        private int existingTranslations;
        private int failedTranslations;
        private int overridenTranslations;

        public CSVImportResults(File f, boolean testMode,
                int skippedLines, String glossaryName) {
            this.filename = f;
            this.testMode = testMode;
            this.skippedLines = skippedLines;
            this.glossaryName = glossaryName;
        }

        public boolean isHeadersIncluded() {
            return headersIncluded;
        }

        public void setHeadersIncluded(boolean headersIncluded) {
            this.headersIncluded = headersIncluded;
        }

        public int getProcessedLines() {
            return processedLines;
        }

        public void setProcessedLines(int processedLines) {
            this.processedLines = processedLines;
        }

        public int incrementProcessedLines() {
            return ++processedLines;
        }

        public int getAddedEntries() {
            return addedEntries;
        }

        public void setAddedEntries(int addedEntries) {
            this.addedEntries = addedEntries;
        }

        public int incrementAddedEntries() {
            return ++addedEntries;
        }

        public int getExistingEntries() {
            return existingEntries;
        }

        public void setExistingEntries(int existingEntries) {
            this.existingEntries = existingEntries;
        }

        public int incrementExistingEntries() {
            return ++existingEntries;
        }

        public int getFailedEntries() {
            return failedEntries;
        }

        public void setFailedEntries(int failedEntries) {
            this.failedEntries = failedEntries;
        }

        public int incrementFailedEntries() {
            return ++failedEntries;
        }

        public int getAddedTranslations() {
            return addedTranslations;
        }

        public void setAddedTranslations(int addedTranslations) {
            this.addedTranslations = addedTranslations;
        }

        public int incrementAddedTranslations() {
            return ++addedTranslations;
        }

        public int getExistingTranslations() {
            return existingTranslations;
        }

        public void setExistingTranslations(int existingTranslations) {
            this.existingTranslations = existingTranslations;
        }

        public int incrementExistingTranslations() {
            return ++existingTranslations;
        }

        public int getFailedTranslations() {
            return failedTranslations;
        }

        public void setFailedTranslations(int failedTranslations) {
            this.failedTranslations = failedTranslations;
        }

        public int incrementFailedTranslations() {
            return ++failedTranslations;
        }

        public int getOverridenTranslations() {
            return overridenTranslations;
        }

        public void setOverridenTranslations(int overridenTranslations) {
            this.overridenTranslations = overridenTranslations;
        }

        public int incrementOverridenTranslations() {
            return ++overridenTranslations;
        }

        public String getResultsShortMessage() {
            StringBuilder sb = new StringBuilder(128);

            sb.append("CSV import summary: ");
            sb.append(getAddedEntries());
            sb.append(" entries added, ");
            sb.append(getFailedEntries());
            sb.append(" failed; ");
            sb.append(getAddedTranslations());
            sb.append(" translations added, ");
            sb.append(getExistingTranslations());
            sb.append(" existed, ");
            sb.append(getFailedTranslations());
            sb.append(" failed.");
            return sb.toString();
        }

        public String getResultsLongMessage() {
            StringBuilder sb = new StringBuilder(512);

            /*

            CSV import results for file xxxx

            Path to file: sss
            Imported to glossary:

            Starting lines ignored...............:
            Headers..............................:
            Rows processed (not counting headers):
            Entries imported.....................:
            Entries existing already in glossary.:
            Entries failed to import.............:
            Translations added...................:
            Translations existing already........:
            Translations overriden...............:
            Translationes failed to import.......:
            */

            sb.append("CSV import results for file ");
            sb.append(filename.getName()).append(System.lineSeparator());
            sb.append(System.lineSeparator());

            sb.append("Path to file: ").append(filename.getAbsolutePath()).append(System.lineSeparator());
            sb.append("Imported to glossary: ").append(glossaryName).append(System.lineSeparator());
            sb.append("Test mode: ").append(testMode).append(System.lineSeparator());
            sb.append(System.lineSeparator());

            sb.append("Starting lines ignored...............: ");
            sb.append(skippedLines).append(System.lineSeparator());
            sb.append("Headers..............................: ");
            sb.append(headersIncluded ? "yes" : "no").append(System.lineSeparator());

            sb.append("Rows processed (not counting headers): ");
            sb.append(processedLines).append(System.lineSeparator());

            sb.append("Entries imported.....................: ");
            sb.append(getAddedEntries()).append(System.lineSeparator());

            sb.append("Entries existing already in glossary.: ");
            sb.append(getExistingEntries()).append(System.lineSeparator());

            sb.append("Entries failed to import.............: ");
            sb.append(getFailedEntries()).append(System.lineSeparator());

            sb.append("Translations added...................: ");
            sb.append(getAddedTranslations()).append(System.lineSeparator());

            sb.append("Translations existing already........: ");
            sb.append(getExistingTranslations()).append(System.lineSeparator());

            sb.append("Translations overriden...............: ");
            sb.append(getOverridenTranslations()).append(System.lineSeparator());

            sb.append("Translations failed to import........: ");
            sb.append(getFailedTranslations()).append(System.lineSeparator());

            sb.append(System.lineSeparator());
            return sb.toString();
        }


    }
}
