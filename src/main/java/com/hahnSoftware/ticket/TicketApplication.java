
package com.hahnSoftware.ticket;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.hahnSoftware.ticket.config.DatabaseConfig;
import com.hahnSoftware.ticket.ui.TicketDesktopLauncher;

@SpringBootApplication
@EntityScan(basePackages = "com.hahnSoftware.ticket.entity")
public class TicketApplication {
    
    public static void main(String[] args) {
        try {
            System.setProperty("java.awt.headless", "false");
			

			 SpringApplication.run(TicketApplication.class, args);


            // Single context approach
             AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(DatabaseConfig.class); // This now includes the initializer
            context.scan("com.hahnSoftware.ticket");
            context.refresh();

		
            // Launch UI with the same context
            TicketDesktopLauncher.launchUI(context);
            
        } catch (Exception e) {
            System.err.println("Error in main: ");
            e.printStackTrace();
        }
    }
}