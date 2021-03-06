/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.word.editor.core//HtmlView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "HtmlViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "org.word.editor.core.HtmlViewTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_HtmlViewAction",
        preferredID = "HtmlViewTopComponent"
)
@Messages({
    "CTL_HtmlViewAction=HtmlView",
    "CTL_HtmlViewTopComponent=HtmlView Window",
    "HINT_HtmlViewTopComponent=This is a HtmlView window"
})
public final class HtmlViewTopComponent extends TopComponent {
    private static Logger logger=Logger.getLogger(HtmlViewTopComponent.class);
    
    private JEditorPane htmlEditorPane;
    private JScrollPane scrollPane;
    public HtmlViewTopComponent() {
        initComponents();
        this.setLayout(new BorderLayout());
        htmlEditorPane=new JEditorPane();
        htmlEditorPane.setContentType("text/html");//设置显示html
                
        scrollPane=new JScrollPane();
        scrollPane.setViewportView(htmlEditorPane);
        
        this.add(scrollPane);
        setName(Bundle.CTL_HtmlViewTopComponent());
        setToolTipText(Bundle.HINT_HtmlViewTopComponent());
        
        //htmlEditorPane 加入鼠标监听
        htmlEditorPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseMoved(MouseEvent e) {
               // Point point=e.getPoint();
                //editorPane.g
                if(Contents.Cov_Flag.equals(Contents.ECMCDC)){//在ECMCDC时添加鼠标移动事件
                    JPopupMenu popup=new JPopupMenu();
                    popup.setLayout(new BorderLayout());
                    JPanel infoPanel=createInfoPanel(e.getX(),e.getY());
                    popup.add(infoPanel,BorderLayout.CENTER);
                    popup.show(htmlEditorPane,e.getX(),e.getY());
                }
            }
        });

    }
   private  JPanel createInfoPanel(int x,int y){
        JPanel infoPanel=new JPanel();
        infoPanel.setLayout(new FlowLayout());
        infoPanel.add(new JLabel(x+":"+y));
        return  infoPanel;
    }
    public JEditorPane getEditorPane(){
        return this.htmlEditorPane;
    }
    public void setText(String text){
        this.htmlEditorPane.setText(text);
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
}
