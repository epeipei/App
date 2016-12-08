/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.toolbar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Bugtracking",
        id = "org.word.editor.toolbar.SetFont"
)
@ActionRegistration(
        iconBase = "org/word/editor/toolbar/Font_Book_16px_1186235_easyicon.net.png",
        displayName = "#CTL_SetFont"
)
@ActionReference(path = "Toolbars/File", position = 0)
@Messages("CTL_SetFont=字体")
public final class SetFont extends AbstractAction implements Presenter.Toolbar{

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }

    @Override
    public Component getToolbarPresenter() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       return new FontPanel();
    }
}
