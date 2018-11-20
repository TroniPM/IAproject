/*
 * Copyright 2016 Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tronipm.gauufrpe.view.util.popupmenu;

import com.tronipm.gauufrpe.view.JFramePrincipal;
import com.tronipm.gauufrpe.util.UtilMethods;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class PopUpMenuAtRightClickNormalEditorListener extends MouseAdapter {

    private JTextComponent component;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            component.requestFocus();
            doPop(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            component.requestFocus();
            doPop(e);
        }
    }

    public PopUpMenuAtRightClickNormalEditorListener(JTextComponent component) {
        this.component = component;
    }

    private void doPop(MouseEvent e) {
        JMenuItem[] aux = new JMenuItem[]{
            new JMenuItemWithTag("Clear", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    component.setText("");
                }
            }),
            new JMenuItemWithTag("Copy", JFramePrincipal.iconCopy, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String regex = component.getSelectedText();
                    if (regex == null) {
                        regex = "";
                    }
                    UtilMethods.copyToClipboard(regex);
                }
            }),
            new JMenuItemWithTag("Select all", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    component.selectAll();
                }
            })};
        PopUpMenuAtRightClick menu = new PopUpMenuAtRightClick(component, aux);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public class JMenuItemWithTag extends JMenuItem {

        public JMenuItemWithTag(String label, ActionListener action) {
            super(label);
            this.addActionListener(action);
        }

        public JMenuItemWithTag(String label, Icon icon, ActionListener action) {
            super(label, icon);
            this.addActionListener(action);
        }
    }
}
