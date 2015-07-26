/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.localizethat.gui.components;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.text.JTextComponent;

/**
 * KeyListener that replaces the [Alt]+[.] keystroke by the Unicode char U+2026
 * (&hellip; HTML entity)
 * @author rpalomares
 */
public class EllipsisUnicodeCharKeyAdapter extends KeyAdapter {
    @Override public void keyTyped(KeyEvent e) {
	if ((e.getKeyChar()=='.') && (e.isAltDown()) &&
                (!(e.isAltGraphDown() || e.isControlDown() || e.isShiftDown()))) {
            Component src = (Component) e.getSource();
            
            if (src instanceof JTextComponent) {
                JTextComponent txtComp = (JTextComponent) src;
                txtComp.replaceSelection("…");
            }
        }
    }
}
