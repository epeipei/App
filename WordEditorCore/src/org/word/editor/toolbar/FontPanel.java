/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.toolbar;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openide.util.Lookup;
import org.word.editor.core.Contents;
import org.word.editor.core.WordTopComponent;


/**
 *
 * @author BUAA--XIAO
 */
public class FontPanel extends javax.swing.JPanel implements Lookup.Provider{
    static Logger logger=Logger.getLogger(FontPanel.class);
    //为了给新建的topcomponent确定font fontsize等，确保同之前打开的组件的一样
    public static String fontName="Abyssinica SIL";
    public static int fontSize=12;
    /**
     * Creates new form FontPanel
     */
    public FontPanel() {
        initComponents();
        jComboBox1.removeAllItems();
        GraphicsEnvironment e=GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName=e.getAvailableFontFamilyNames();
        //初始化字体名称
        for(int i=0;i<fontName.length;i++){
            jComboBox1.addItem(fontName[i]);
        }
        //初始化字号的大小
        jComboBox2.removeAllItems();
        for(int i=12;i<=20;i++){
            jComboBox2.addItem(String.valueOf(i));
        }
        //初始化覆盖标准的选择框
        jComboBox3.removeAllItems();
        String[] criterias={Contents.STATEMENT,Contents.BRANCH,Contents.CONDITION,
            Contents.BRANCH_CONDITION,Contents.MCDC,Contents.PATH,
            Contents.ECMCDC,Contents.PATH_LOOP};
        for(int i=0;i<criterias.length;i++){
            jComboBox3.addItem(criterias[i]);
        }
        //给jcombox添加监听器
        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s=(String)jComboBox1.getSelectedItem();
                logger.info("select the font: "+s);
                updateFontName(s);
            }
        });
        jComboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int size=Integer.valueOf((String)jComboBox2.getSelectedItem());
                logger.info("select the font size: "+size);
                updateFontSize(size);
            }
        });
        jComboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("select the coverage criteria:"+jComboBox3.getSelectedItem());
                Contents.Cov_Flag=(String)jComboBox3.getSelectedItem();//设置全局选中的覆盖标准
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FontPanel.class, "FontPanel.jLabel1.text")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FontPanel.class, "FontPanel.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(jComboBox3))
        );
    }// </editor-fold>//GEN-END:initComponents


    //jcomboBox2是字号的box
    //JLabel1 是字体的panel
    //Jlabel 是coverage criteria的panel
    //JComBox3 是覆盖标准选择框
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

    static List<WordTopComponent> topcomponents=new ArrayList<>();
    
    private void updateFontName(String font){
        fontName=font;
        for(WordTopComponent component:topcomponents){
            component.setFont(new Font(fontName,Font.PLAIN,fontSize));
        }
    }
    private void updateFontSize(int size){
        fontSize=size;
        for(WordTopComponent component:topcomponents){
            component.setFont(new Font(fontName,Font.PLAIN,fontSize));
        }
    }
    public static void addObservers(WordTopComponent component){
        topcomponents.add(component);
    }
    @Override
    public Lookup getLookup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}