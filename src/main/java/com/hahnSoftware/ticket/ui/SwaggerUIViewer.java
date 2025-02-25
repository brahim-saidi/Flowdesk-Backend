package com.hahnSoftware.ticket.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SwaggerUIViewer extends Application {
    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        webView.getEngine().load("http://localhost:8080/swagger-ui.html");
        
        Scene scene = new Scene(webView, 1024, 768);
        stage.setScene(scene);
        stage.setTitle("API Documentation");
        stage.show();
    }
    
    public static void launchSwaggerUI() {
        new Thread(() -> Application.launch(SwaggerUIViewer.class)).start();
    }
}