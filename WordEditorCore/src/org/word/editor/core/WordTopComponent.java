/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import org.word.editor.utilty.TextFile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.event.MouseInputListener;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.word.editor.toolbar.FontPanel;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.word.editor.core//Word//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "WordTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "org.word.editor.core.WordTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_WordAction",
        preferredID = "WordTopComponent"
)
@Messages({
    "CTL_WordAction=Word",
    "CTL_WordTopComponent=Word Window",
    "HINT_WordTopComponent=This is a Word window"
})
public final class WordTopComponent extends TopComponent implements Observer{
    static Logger logger=Logger.getLogger(WordTopComponent.class);
    public WordTopComponent() {
        initComponents();
        this.setLayout(new BorderLayout());
        
        textArea=new RSyntaxTextArea(20,60);
        textArea.setFont(new Font(FontPanel.fontName,Font.PLAIN,FontPanel.fontSize));
        
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp=new RTextScrollPane(textArea);
        
        Image icon=this.getToolkit().getImage(this.getClass().getResource("text_x_c_16px_542379_easyicon.net.ico"));
        setIcon(icon);
        this.setIcon(icon);
        this.add(sp);
    }
    public WordTopComponent(String path, String tabName) {
        this();
        this.setName(tabName);
        this.setToolTipText(path);
        textArea.setText(TextFile.read(path));
        
        //自动获取焦点
        textArea.requestFocus();;
        textArea.setSelectionStart(10);
        textArea.setSelectionEnd(10);
        textArea.setSelectionColor(Color.BLUE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    private RSyntaxTextArea textArea;
    public void setFont(Font font){
        textArea.setFont(font);
    }
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void update(Observable o, Object arg) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
   }
    
}
