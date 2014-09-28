/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import net.localizethat.util.gui.JStatusBar;

/**
 * Main window of LocalizeThat!
 * @author rpalomares
 */
public class MainWindow extends javax.swing.JFrame {
    private static final Icon CLOSE_TAB_ICON = new ImageIcon(
            MainWindow.class.getResource("/net/localizethat/resources/close-tab-button.png"));
    private static final Icon CLOSE_TAB_ICON_HOVER = new ImageIcon(
            MainWindow.class.getResource("/net/localizethat/resources/close-tab-button-hover.png"));

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
    }

    public JStatusBar getStatusBar() {
        return statusBar;
    }

    public void addTab(JPanel content, String title, int order) {
        // TODO: change addClosableTab to allow order positioning
        addClosableTab(tabPanel, content, title, null);
    }

    public void addTab(JPanel content, String title) {
        addClosableTab(tabPanel, content, title, null);
    }

    /**
     * Adds a component to a JTabbedPane with a little "Close tab" button on the right side of the tab.
     *
     * @param tabbedPane the JTabbedPane
     * @param c any JComponent, usually a JPanel
     * @param title the title for the tab
     * @param icon the icon for the tab, if desired
     */
    public void addClosableTab(final JTabbedPane tabbedPane, final JComponent c,
            final String title, final Icon icon) {

        // Add the tab to the pane without any label
        tabbedPane.addTab(null, c);
        int pos = tabbedPane.indexOfComponent(c);

        // Create a FlowLayout that will space things 5px apart
        FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

        // Make a small JPanel with the layout and make it non-opaque
        JPanel pnlTab = new JPanel(f);
        pnlTab.setOpaque(false);

        // Add a JLabel with title and the left-side tab icon
        JLabel lblTitle = new JLabel(title);
        lblTitle.setIcon(icon);

        // Create a JButton for the close tab button
        JButton btnClose = new JButton();
        btnClose.setOpaque(false);

        // Configure icon and rollover icon for button
        btnClose.setRolloverIcon(CLOSE_TAB_ICON_HOVER);
        btnClose.setRolloverEnabled(true);
        btnClose.setIcon(CLOSE_TAB_ICON);

        // Set border null so the button doesn't make the tab too big
        btnClose.setBorder(null);

        // Make sure the button can't get focus, otherwise it looks funny
        btnClose.setFocusable(false);

        // Put the panel together
        pnlTab.add(lblTitle);
        pnlTab.add(btnClose);

        // Add a thin border to keep the image below the top edge of the tab
        // when the tab is selected
        pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        // Now assign the component for the tab
        tabbedPane.setTabComponentAt(pos, pnlTab);

        // Add the listener that removes the tab
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // The component parameter must be declared "final" so that it can be
                // referenced in the anonymous listener class like this.
                tabbedPane.remove(c);
                c.setVisible(false);
            }
        };
        btnClose.addActionListener(listener);

        // Optionally bring the new tab to the front
        tabbedPane.setSelectedComponent(c);

    //-------------------------------------------------------------
        // Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
        //-------------------------------------------------------------
        AbstractAction closeTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.remove(c);
            }
        };

        // Create a keystroke
        KeyStroke controlW = KeyStroke.getKeyStroke("control W");

    // Get the appropriate input map using the JComponent constants.
        // This one works well when the component is a container.
        InputMap inputMap = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Add the key binding for the keystroke to the action name
        inputMap.put(controlW, "closeTab");

        // Now add a single binding for the action name to the anonymous action
        c.getActionMap().put("closeTab", closeTabAction);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutAction = new net.localizethat.actions.AboutAction();
        preferencesAction = new net.localizethat.actions.PreferencesAction();
        localeManagerAction = new net.localizethat.actions.LocaleManagerAction();
        statusBar = new net.localizethat.util.gui.JStatusBar();
        mainToolBar = new javax.swing.JToolBar();
        preferencesButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        tabPanel = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        updateProductsMenuItem = new javax.swing.JMenuItem();
        manageProductsMenuItem = new javax.swing.JMenuItem();
        manageLocalesMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        preferencesMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LocalizeThat!");

        mainToolBar.setRollover(true);

        preferencesButton.setAction(preferencesAction);
        preferencesButton.setHideActionText(true);
        preferencesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        preferencesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(preferencesButton);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPanel)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
        );

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        updateProductsMenuItem.setMnemonic('U');
        updateProductsMenuItem.setText("Update Products");
        fileMenu.add(updateProductsMenuItem);

        manageProductsMenuItem.setMnemonic('M');
        manageProductsMenuItem.setText("Manage Products");
        fileMenu.add(manageProductsMenuItem);

        manageLocalesMenuItem.setAction(localeManagerAction);
        fileMenu.add(manageLocalesMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(formListener);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        preferencesMenuItem.setAction(preferencesAction);
        preferencesMenuItem.setMnemonic('P');
        editMenu.add(preferencesMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setAction(aboutAction);
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
            .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == exitMenuItem) {
                MainWindow.this.exitMenuItemActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.localizethat.actions.AboutAction aboutAction;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private net.localizethat.actions.LocaleManagerAction localeManagerAction;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JMenuItem manageLocalesMenuItem;
    private javax.swing.JMenuItem manageProductsMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem pasteMenuItem;
    private net.localizethat.actions.PreferencesAction preferencesAction;
    private javax.swing.JButton preferencesButton;
    private javax.swing.JMenuItem preferencesMenuItem;
    private net.localizethat.util.gui.JStatusBar statusBar;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JMenuItem updateProductsMenuItem;
    // End of variables declaration//GEN-END:variables

}
