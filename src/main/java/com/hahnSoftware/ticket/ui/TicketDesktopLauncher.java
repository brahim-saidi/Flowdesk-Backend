package com.hahnSoftware.ticket.ui;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import javax.swing.*;

import com.hahnSoftware.ticket.controller.AuditLogController;
import com.hahnSoftware.ticket.controller.CommentController;
import com.hahnSoftware.ticket.controller.TicketController;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;
import com.hahnSoftware.ticket.service.TicketService;


public class TicketDesktopLauncher {
    public static void launchUI(AnnotationConfigApplicationContext springContext) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Get all required beans from context
                TicketService ticketService = springContext.getBean(TicketService.class);
                TicketController ticketController = new TicketController(ticketService);
                CommentController commentController = springContext.getBean(CommentController.class);
                UserRepository userRepository = springContext.getBean(UserRepository.class);
                TicketRepository ticketRepository = springContext.getBean(TicketRepository.class);
                AuditLogController auditLogController = springContext.getBean(AuditLogController.class);

                TicketManagementUI ui = new TicketManagementUI(
                    ticketController,
                    springContext,
                    commentController,
                    userRepository,
                    ticketRepository,
                    auditLogController
                );
                ui.setVisible(true);
                
                System.out.println("UI initialized successfully with all required beans");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing UI: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}