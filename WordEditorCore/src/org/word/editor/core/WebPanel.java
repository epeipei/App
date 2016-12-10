/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.word.editor.core;

import java.net.URL;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author xiao
 */
public class WebPanel{
    public  JFXPanel jfxpanel=new JFXPanel();
    //private WebView view;
    private WebEngine engine;
    
    //进度条相关
    private final JLabel lblStatus = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();

    public WebPanel() {
        createScene();
    }
    private void createScene() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override
                    public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });

                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setValue(newValue.intValue());
                            }
                        });
                    }
                });
                jfxpanel.setScene(new Scene(view));
            }
        });
        
    }
    private String toURL(String str){
        try {
            return new URL(str).toExternalForm();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void loadURL(final String url){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String tmp=toURL(url);
                if(tmp==null){
                    tmp=toURL("http://"+url);
                }
                System.out.println(tmp);
                engine.load(tmp);
            }
        });
    }
}
    
