/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util.gui;

import java.util.logging.Level;

/**
 * Implements a status bar with space for informational messages, an auxiliar message
 * (like a login name) and a progress bar
 * @author rpalomares
 */
public class JStatusBar extends javax.swing.JPanel {

    /**
     * Auxiliar text shown in the middle area of the status bar
     */
    private String auxText;

    /**
     * Status and information text shown in the left-to-middle area of the status bar
     */
    private String statusText;

    /**
     * Creates new form JStatusBar
     */
    public JStatusBar() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        longMsgDlg = new javax.swing.JDialog();
        shortMsgLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lngMsgText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cpClipButton = new javax.swing.JButton();
        statusMsg = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        auxMsg = new javax.swing.JLabel();
        statusPgBar = new javax.swing.JProgressBar();

        longMsgDlg.setTitle("LocalizeThat information dialog");

        shortMsgLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        shortMsgLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/localizethat/resources/dialog-error.png"))); // NOI18N
        shortMsgLabel.setText("Message goes here");
        shortMsgLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        shortMsgLabel.setIconTextGap(5);
        shortMsgLabel.setMaximumSize(new java.awt.Dimension(111, 75));
        shortMsgLabel.setMinimumSize(new java.awt.Dimension(111, 75));
        shortMsgLabel.setPreferredSize(new java.awt.Dimension(111, 75));
        longMsgDlg.getContentPane().add(shortMsgLabel, java.awt.BorderLayout.NORTH);

        lngMsgText.setEditable(false);
        lngMsgText.setColumns(20);
        lngMsgText.setFont(new java.awt.Font("DialogInput", 0, 12)); // NOI18N
        lngMsgText.setLineWrap(true);
        lngMsgText.setRows(5);
        lngMsgText.setWrapStyleWord(true);
        jScrollPane1.setViewportView(lngMsgText);

        longMsgDlg.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        okButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(139, 25));
        okButton.setMinimumSize(new java.awt.Dimension(139, 25));
        okButton.setPreferredSize(new java.awt.Dimension(139, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        cpClipButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cpClipButton.setText("Copy to Clipboard");
        cpClipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpClipButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cpClipButton);

        longMsgDlg.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        setMaximumSize(new java.awt.Dimension(16516, 24));
        setMinimumSize(new java.awt.Dimension(332, 24));
        setPreferredSize(new java.awt.Dimension(350, 24));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        statusMsg.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        statusMsg.setText("Ready");
        statusMsg.setMaximumSize(new java.awt.Dimension(16284, 18));
        statusMsg.setMinimumSize(new java.awt.Dimension(100, 18));
        statusMsg.setPreferredSize(new java.awt.Dimension(36, 18));
        statusMsg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusMsgMouseClicked(evt);
            }
        });
        add(statusMsg);

        jSeparator1.setForeground(java.awt.SystemColor.control);
        jSeparator1.setMaximumSize(new java.awt.Dimension(5, 10));
        jSeparator1.setMinimumSize(new java.awt.Dimension(5, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(5, 10));
        add(jSeparator1);

        auxMsg.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        auxMsg.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        add(auxMsg);

        statusPgBar.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        statusPgBar.setMaximumSize(new java.awt.Dimension(148, 14));
        statusPgBar.setMinimumSize(new java.awt.Dimension(148, 14));
        statusPgBar.setStringPainted(true);
        add(statusPgBar);
    }// </editor-fold>//GEN-END:initComponents

    private void statusMsgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusMsgMouseClicked
        if (evt.getClickCount() == 2) {
            longMsgDlg.setLocationRelativeTo(null);
            longMsgDlg.setSize(400, 300);
            longMsgDlg.setVisible(true);
        }
    }//GEN-LAST:event_statusMsgMouseClicked

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        longMsgDlg.setVisible(false);
        statusMsg.setIcon(null);
        statusMsg.setText("");
    }//GEN-LAST:event_okButtonActionPerformed

    private void cpClipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpClipButtonActionPerformed
        lngMsgText.selectAll();
        lngMsgText.copy();

        // Deselect text
        lngMsgText.setCaretPosition(0);
        lngMsgText.moveCaretPosition(0);
    }//GEN-LAST:event_cpClipButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel auxMsg;
    private javax.swing.JButton cpClipButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea lngMsgText;
    private javax.swing.JDialog longMsgDlg;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel shortMsgLabel;
    private javax.swing.JLabel statusMsg;
    private javax.swing.JProgressBar statusPgBar;
    // End of variables declaration//GEN-END:variables

    /**
     * Returns the auxiliar text (the one in the middle of the status bar)
     *
     * @return Value of property auxText.
     */
    public String getAuxText() {
        return auxText;
    }

    /**
     * Sets the auxiliar text (the one in the middle of the status bar)
     *
     * @param auxText New value of property auxText.
     */
    public void setAuxText(String auxText) {
        this.auxText = auxText;
    }

    /**
     * Returns the status text (the one in the left-to-middle of the status bar)
     *
     * @return Value of property statusText.
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * Resets the progress bar to 0 and stops it
     */
    public void resetProgress() {
        statusPgBar.setIndeterminate(false);
        statusPgBar.setStringPainted(false);
        statusPgBar.setValue(0);
    }

    /**
     * Sets the progress bar value
     * @param val the value to set in the progress bar
     */
    public void setProgress(int val) {
        statusPgBar.setStringPainted(true);
        statusPgBar.setValue(val);
    }

    /**
     * Sets the progress bar to undefined progress and animates it
     */
    public void startUndefProgress() {
        statusPgBar.setStringPainted(false);
        statusPgBar.setValue(0);
        statusPgBar.setIndeterminate(true);
    }

    /**
     * Sets the progress bar as completed
     */

    public void endProgress() {
        statusPgBar.setValue(100);
    }

    /**
     * Sets the status text (the one in the left-to-middle part of the status bar)
     * @param msgType The message type which determines the displayed icon; if null, no icon will be shown
     * @param msg the text to display; if null, the text will be emptied
     */
    public void setText(LogMsgType msgType, String msg) {
        statusText = (msg == null) ? "" : msg;
        statusMsg.setText(msg);
        if (msgType != null) {
            statusMsg.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource(msgType.getSmallIconPath())));
        } else {
            statusMsg.setIcon(null);
        }
    }

    /**
     * Sets the status text (the one in the left-to-middle part of the status bar) and associates it
     * to a longer message and a exception dump that can be displayed by double-clicking
     * the status bar
     * @param msgType the message type which determines the displayed icon; if null, no icon will be shown
     * @param shortMsg the text to display in the status bar
     * @param longMsg the long text to show in the popup dialog
     * @param e the exception which will be stack traced in the dialog
     */
    public void logMessage(LogMsgType msgType, String shortMsg, String longMsg,
            Exception e) {
        setText(msgType, shortMsg);
        shortMsgLabel.setText(shortMsg);
        shortMsgLabel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource(msgType.getIconPath())));

        if (e == null) {
            lngMsgText.setText(longMsg);
        } else {
            StringBuilder sb = new StringBuilder(longMsg.length() + 5);
            sb.append(longMsg).append("\n\n");
            StackTraceElement[] st = e.getStackTrace();
            for (StackTraceElement st1 : st) {
                sb.append("    ").append(st1.toString());
                sb.append("\n");
            }
            lngMsgText.setText(sb.toString());

        }
    }

    /**
     * Sets the status text (the one in the left-to-middle part of the status bar) and associates it
     * to a longer message and a exception dump that can be displayed by double-clicking
     * the status bar
     * @param msgType the message type which determines the displayed icon; if null, no icon will be shown
     * @param shortMsg the text to display in the status bar
     * @param longMsg the long text to show in the popup dialog
     */
    public void logMessage(LogMsgType msgType, String shortMsg, String longMsg) {
        logMessage(msgType, shortMsg, longMsg, null);
    }

    /**
     * Sets an info text with the info icon
     * @param msg the text to display
     */
    public void setInfoText(String msg) {
        logMessage(LogMsgType.INFO, msg, "(no additional info)");
    }

    /**
     * Sets a warning text with the warning icon
     * @param msg the text to display
     */
    public void setWarnText(String msg) {
        logMessage(LogMsgType.WARNING, msg, "(no additional info)");
    }

    /**
     * Sets an error text with the error icon
     * @param msg the text to display
     */
    public void setErrorText(String msg) {
        logMessage(LogMsgType.ERROR, msg, "(no additional info)");
    }

    /**
     * Clears the status text
     */
    public void clearText() {
        setText(null, null);
    }

    /**
     * Enumeration containing the different types of messages, along with an associated icon
     */
    public enum LogMsgType {

        /**
         * Debug informational message. Normally only used when debugging
         */
        DEBUG("Debug", "dialog-information.png"),

        /**
         * Informative message, like general feedback information to user
         */
        INFO("Informative", "dialog-information.png"),

        /**
         * Warning message. Messages depicting out of ordinary workflow which are not to be
         * considered errors would be of this type
         */
        WARNING("Warning", "dialog-warning.png"),

        /**
         * Error message. Denotes an error, either from incorrect user procedure or of
         * the application. In both cases, it is recoverable/not critical
         */
        ERROR("Error", "dialog-error.png"),

        /**
         * Critical message. A fatal error that surely involves quitting the application
         */
        STOP("Critical", "dialog-error.png");

        private final String textual;
        private final String iconPath;

        LogMsgType(String textual, String iconPath) {
            this.textual = textual;
            this.iconPath = iconPath;
        }

        @Override
        public String toString() {
            return textual;
        }

        /**
         * Returns the logging level associated with the LogMsgType
         * @return
         */
        public Level getLoggerLevel() {
            Level level;

            switch (this) {
                case STOP:
                case ERROR:
                    level = Level.SEVERE;
                    break;
                case WARNING:
                    level = Level.WARNING;
                    break;
                case INFO:
                    level = Level.INFO;
                    break;
                case DEBUG:
                    level = Level.FINE;
                    break;
                default:
                    level = Level.WARNING;
            }
            return level;
        }

        /**
         * Returns the path to the icon for the specified LogMstType instance
         * @return
         */
        public String getIconPath() {
            return "/net/localizethat/resources/" + iconPath;
        }

        /**
         * Returns the path to the small (16px) icon for the specified LogMstType instance
         * @return
         */
        public String getSmallIconPath() {
            return "/net/localizethat/resources/16-" + iconPath;
        }
    }
}
