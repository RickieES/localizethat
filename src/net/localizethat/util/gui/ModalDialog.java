/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Class to build JDialogs that can return true or false (like in OK / Cancel)
 * @author rpalomares
 * @param <C> a Component subclass instance, usually a JPanel, that will be displayed as the
 *              content of the Dialog
 * @param <D> a DialogDataObject subclass instance used to transfer data to and from C instance
 */
public class ModalDialog<C extends Component, D extends DialogDataObject> extends JDialog {
    private final C dlgContent;
    private boolean result;
    private D ddo;

    /**
     * Default constructor for ModalDialog. It asks for Component, usually a JPanel with
     * everything that must be displayed inside the JDialog contentPane, including the
     * OK / Cancel buttons (this allows to the caller provide customized buttons)
     * @param owner the JFrame that owns this dialog
     * @param c a Component that will be the displayed content inside the JDialog
     */
    public ModalDialog(JFrame owner, C c) {
        this(owner, c, false);
    }
    
    public ModalDialog(JFrame owner, C c, boolean createOkCancelPanel) {
        super(owner, true);
        dlgContent = c;
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(c, BorderLayout.NORTH);
        
        if (createOkCancelPanel) {
            createOkCancelPanel();
        }
        
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        this.pack();
        this.setLocationByPlatform(true);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                setModalDialogResult(false);
            }
        });

        if (c instanceof ModalDialogComponent) {
            ((ModalDialogComponent) c).setModalDialogReference(this);
        }
    }



    /**
     * Initialization method that allows to transfer data to dlgContent from a specially crafted
     * DialogDataObject for the specific dlgContent instance
     *
     * @param ddo a DialogDataObject that can hold data to be transferred to dlgContent
     */
    public void initDialog(D ddo) {
        this.ddo = ddo;
    }

    /**
     * Shows the dialog in modal form. The exact steps are:
     * <ul>
     *   <li>if the DataDialogObject ddo member is not null, its transferTo method is
     *     called to transfer data to dlgContent</li>
     *   <li>the dialog is modally displayed</li>
     *   <li>on return, if the dialog reports that result is OK and the ddo member is not
     *     null, its collectFrom is called to collect data from dlgContent</li>
     *   <li>the result is returned in case the caller wants to be notified</li>
     * </ul>
     * 
     * @return true if the Component reports success in the UI interaction (usually because the
     *         user clicked an OK button), false otherwise
     */
    public final boolean showDialog() {
        result = false;

        if (ddo != null) {
            ddo.transferTo(dlgContent);
        }

        setVisible(true);

        if (result) {
            if (ddo != null) {
                ddo.collectFrom(dlgContent);
            }
        }
        return result;
    }

    /**
     * Provides a quick reference to the Component inside the ModalDialog. This is useful
     * as the actual GUI to be shown is entirely contained in the Component, so most interactions
     * will happen with it
     * @return the Component provided in the constructor that will be displayed
     */

    public final C getDlgContent() {
        return dlgContent;
    }
    
    /**
     * Returns the DialogDataObject that should have the prefill data (if called before showing
     * the dialog) or the results (if called after closing it). This is entirely based in the
     * Component
     * @return
     */
    public final D getCollectedData() {
        return ddo;
    }

    /**
     * Allows the concrete subclasses to provide feedback about the dialog outcome (was it closed
     * after clicking OK? Or was it closed after clicking Cancel or the Close dialog button)?
     * @param result
     */
    public final void setModalDialogResult(boolean result) {
        this.result = result;
        dlgContent.setVisible(false);
        setVisible(false);
    }

    /**
     * Returns the execution result of the dialog
     * @return true if the user clicked OK, false if the user clicked Cancel or closed the dialog
     */
    public final boolean getResult() {
        return this.result;
    }

    private void createOkCancelPanel() {
        java.awt.Dimension buttonDimension = new java.awt.Dimension(81, 25);
        JPanel okButtonPanel = new javax.swing.JPanel();
        JButton okButton = new javax.swing.JButton();
        JButton cancelButton = new javax.swing.JButton();

        okButton.setText("OK");
        okButton.setMaximumSize(buttonDimension);
        okButton.setMinimumSize(buttonDimension);
        okButton.setPreferredSize(buttonDimension);
        okButtonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(buttonDimension);
        cancelButton.setMinimumSize(buttonDimension);
        cancelButton.setPreferredSize(buttonDimension);
        okButtonPanel.add(cancelButton);

        getContentPane().add(okButtonPanel, java.awt.BorderLayout.SOUTH);

        cancelButton.addActionListener((ActionEvent e) -> {
            setModalDialogResult(false);
        });

        // KeyStroke cancelStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);


    }
}
