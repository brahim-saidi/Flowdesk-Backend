package com.hahnSoftware.ticket.ui;

import com.hahnSoftware.ticket.controller.AuditLogController;
import com.hahnSoftware.ticket.controller.CommentController;
import com.hahnSoftware.ticket.controller.TicketController;
import com.hahnSoftware.ticket.entity.*;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;


public class TicketManagementUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel createTicketPanel;
    private JPanel viewTicketsPanel;
    private JTextField searchField;
    private JTable ticketsTable;
    private JComboBox<Ticket.Status> statusFilter;
    private DefaultTableModel tableModel;
    private final TicketController ticketController;
    private Users.Role userRole;
    private boolean initialized = false;

    private final AnnotationConfigApplicationContext context;  // Add this field
        private Long userId;
                private AuditLogController auditLogController;
            
            
                public TicketManagementUI(
                    TicketController ticketController, 
                    AnnotationConfigApplicationContext context,
                    CommentController commentController,
                    UserRepository userRepository,
                    TicketRepository ticketRepository,
                     AuditLogController auditLogController
                ) {
                    if (ticketController == null) {
                        throw new IllegalArgumentException("TicketController cannot be null");
                    }
                    if (context == null) {
                        throw new IllegalArgumentException("Spring context cannot be null");
                    }
                    
                    // Initialize all fields first
                    this.ticketController = ticketController;
                    this.context = context;
                    this.commentController = commentController;
                    this.userRepository = userRepository;
                    this.ticketRepository = ticketRepository;
                    this.auditLogController = auditLogController;
            
            // Then show login dialog and continue with initialization
            Login loginResult = showLoginDialog();
            if (loginResult != null) {
                System.out.println("Login successful. User role: " + loginResult.getRole());
                this.userRole = loginResult.getRole();
                commonInit();
            } else {
                System.out.println("Login failed. Exiting application...");
                JOptionPane.showMessageDialog(this, "Login failed. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    
        private Login showLoginDialog() {
            JTextField usernameField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);
        
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
        
            while (true) {
                int result = JOptionPane.showConfirmDialog(null, panel, 
                    "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
                if (result == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword());
        
                    try {
                        // Get Login bean from Spring context
                        Login login = context.getBean(Login.class);
                        login.setUsername(username);
                        login.setPassword(password);
        
                        if (login.authenticate()) {
                            this.userRole = login.getRole();
                            this.userId = login.getUserId(); // Capture the user ID
                        System.out.println("Authentication successful. User role: " + userRole + ", User ID: " + userId);
                        return login;
                    } else {
                        JOptionPane.showMessageDialog(null,
                            "Invalid username or password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error during login: " + e.getMessage(),
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private void commonInit() {
        System.out.println("Starting commonInit() for role: " + userRole);
        setupMainFrame();
        createAllPanels();  // Create all panels first
        layoutComponents(); // Then do the layout
        initialized = true;
        
        // Load tickets for IT Support
        if (userRole == Users.Role.IT_SUPPORT) {
            loadTickets();
        }
        System.out.println("Finished commonInit()");
    }


    private void setupMainFrame() {
        setTitle("IT Support Ticket System - " + userRole);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void createAllPanels() {
        System.out.println("Creating panels for role: " + userRole);
        tabbedPane = new JTabbedPane();
        
        // Create both panels
        createCreateTicketPanel();
        createViewTicketsPanel();
        System.out.println("Both panels created");
    }

 
  

    private void createCreateTicketPanel() {
        System.out.println("Creating create ticket panel");
        createTicketPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField titleField = new JTextField(20);
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JComboBox<Ticket.Priority> priorityCombo = new JComboBox<>(Ticket.Priority.values());
        JComboBox<Ticket.Category> categoryCombo = new JComboBox<>(Ticket.Category.values());
        
        addCreateTicketComponents(createTicketPanel, gbc, titleField, descArea, priorityCombo, categoryCombo);
        createTicketPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        System.out.println("Finished creating create ticket panel");
    }


    private void addLabelAndComponent(JPanel panel, GridBagConstraints gbc,
    String labelText, JComponent component, int row) {
// Add the label
gbc.gridx = 0;
gbc.gridy = row;
gbc.weightx = 0.0;
gbc.anchor = GridBagConstraints.EAST;
panel.add(new JLabel(labelText), gbc);

// Add the component
gbc.gridx = 1;
gbc.weightx = 1.0;
gbc.anchor = GridBagConstraints.WEST;
panel.add(component, gbc);

// Reset gridx for next use
gbc.gridx = 0;
}

private void addCreateTicketComponents(JPanel panel, GridBagConstraints gbc,
         JTextField titleField, JTextArea descArea,
         JComboBox<Ticket.Priority> priorityCombo,
         JComboBox<Ticket.Category> categoryCombo) {
// Title
addLabelAndComponent(panel, gbc, "Title:", titleField, 0);

// Description
gbc.gridy = 1;
gbc.gridx = 0;
gbc.weightx = 0.0;
panel.add(new JLabel("Description:"), gbc);

gbc.gridx = 1;
gbc.weightx = 1.0;
JScrollPane scrollPane = new JScrollPane(descArea);
panel.add(scrollPane, gbc);

// Priority
addLabelAndComponent(panel, gbc, "Priority:", priorityCombo, 2);

// Category
addLabelAndComponent(panel, gbc, "Category:", categoryCombo, 3);

// Submit Button
gbc.gridx = 1;
gbc.gridy = 4;
gbc.weightx = 1.0;
gbc.anchor = GridBagConstraints.EAST;
JButton submitButton = new JButton("Submit Ticket");
submitButton.addActionListener(e -> submitTicket(
titleField.getText(),
descArea.getText(),
(Ticket.Priority) priorityCombo.getSelectedItem(),
(Ticket.Category) categoryCombo.getSelectedItem()
));
panel.add(submitButton, gbc);
}

private void createViewTicketsPanel() {
    System.out.println("Creating view tickets panel");
    viewTicketsPanel = new JPanel(new BorderLayout());
    
    String[] columnNames = {"ID", "Title", "Priority", "Category", "Status", "Created Date"};
    tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    ticketsTable = new JTable(tableModel);
    ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Add double-click listener
    ticketsTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int row = ticketsTable.getSelectedRow();
                if (row != -1) {
                    Long ticketId = (Long) tableModel.getValueAt(row, 0);
                    showTicketDetails(ticketId);
                }
            }
        }
    });
    
    JScrollPane scrollPane = new JScrollPane(ticketsTable);
    JPanel filterPanel = createFilterPanel();
    
    viewTicketsPanel.add(filterPanel, BorderLayout.NORTH);
    viewTicketsPanel.add(scrollPane, BorderLayout.CENTER);
    System.out.println("Finished creating view tickets panel");
}

    private void submitTicket(String title, String description, Ticket.Priority priority, Ticket.Category category) {
        if (ticketController != null) {
            TicketCreationDTO ticket = new TicketCreationDTO();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setPriority(priority);
            ticket.setCategory(category);
            ticket.setStatus(Ticket.Status.NEW);
            
            // Set the user ID based on the logged-in user
            ticket.setCreatedByUserId(this.userId); // Replace with actual user ID
            
            try {
                ResponseEntity<?> response = ticketController.createTicket(ticket);
                if (response.getStatusCode().is2xxSuccessful()) {
                    JOptionPane.showMessageDialog(this, 
                        "Ticket created successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    if (userRole == Users.Role.IT_SUPPORT) {
                        loadTickets();
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error creating ticket: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTickets() {
        if (!initialized || ticketController == null) {
            return;
        }
        
        try {
            ResponseEntity<List<TicketDTO>> response = ticketController.getAllTickets();
            if (response != null && response.getBody() != null) {
                updateTableData(response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableData(List<TicketDTO> tickets) {
        tableModel.setRowCount(0);
        for (TicketDTO ticket : tickets) {
            tableModel.addRow(new Object[]{
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getStatus(),
                ticket.getCreatedAt()
            });
        }
    }

    
    private void showTicketDetails(Long ticketId) {
        try {
            // Debug log
            System.out.println("Loading ticket details for ID: " + ticketId);
            
            ResponseEntity<TicketDTO> response = ticketController.getTicketById(ticketId);
            if (response.getBody() != null) {
                TicketDTO ticket = response.getBody();
                System.out.println("Ticket loaded successfully: " + ticket.getTitle());
                
                JDialog detailsDialog = new JDialog(this, "Ticket Details", true);
                detailsDialog.setLayout(new BorderLayout());
                
                // Create tabbed pane
                JTabbedPane tabbedPane = new JTabbedPane();
                
                // Details tab
                JPanel detailsPanel = createDetailsPanel(ticket);
                tabbedPane.addTab("Details", new JScrollPane(detailsPanel));
                
                // Comments tab with fixed layout
                JPanel commentsPanel = new JPanel(new BorderLayout(5, 5));
                commentsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Comments display area
                JTextArea commentsArea = new JTextArea(15, 40);
                commentsArea.setEditable(false);
                commentsArea.setLineWrap(true);
                commentsArea.setWrapStyleWord(true);
                JScrollPane commentsScroll = new JScrollPane(commentsArea);
                commentsPanel.add(commentsScroll, BorderLayout.CENTER);
                
                // New comment input panel
                JPanel newCommentPanel = new JPanel(new BorderLayout(5, 5));
                JTextArea newCommentArea = new JTextArea(4, 40);
                newCommentArea.setLineWrap(true);
                newCommentArea.setWrapStyleWord(true);
                JScrollPane newCommentScroll = new JScrollPane(newCommentArea);
                
                JButton addCommentButton = new JButton("Add Comment");
                addCommentButton.addActionListener(e -> {
                    String content = newCommentArea.getText().trim();
                    if (!content.isEmpty()) {
                        addComment(ticketId, content, commentsArea);
                        newCommentArea.setText("");
                    }
                });
                
                // Add comment input components
                JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
                inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Comment"));
                inputPanel.add(newCommentScroll, BorderLayout.CENTER);
                inputPanel.add(addCommentButton, BorderLayout.SOUTH);
                
                newCommentPanel.add(inputPanel, BorderLayout.CENTER);
                commentsPanel.add(newCommentPanel, BorderLayout.SOUTH);
                
                // Load existing comments
                loadComments(ticketId, commentsArea);
                
                tabbedPane.addTab("Comments", commentsPanel);
                
                // Add audit logs tab
                JPanel auditPanel = createAuditLogsPanel(ticketId);
                tabbedPane.addTab("Audit Logs", auditPanel);
                
                // Add tabbed pane to dialog
                detailsDialog.add(tabbedPane, BorderLayout.CENTER);
                
                // Add close button
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> detailsDialog.dispose());
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(closeButton);
                detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                // Set dialog properties
                detailsDialog.setSize(800, 600);
                detailsDialog.setLocationRelativeTo(this);
                detailsDialog.setVisible(true);
                
            } else {
                throw new Exception("Failed to load ticket details");
            }
        } catch (Exception e) {
            System.err.println("Error showing ticket details: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading ticket details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createDetailsPanel(TicketDTO ticket) {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add ticket details
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(String.valueOf(ticket.getTicketId())), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(ticket.getTitle()), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        // Use JTextArea for description to handle multiple lines
        JTextArea descArea = new JTextArea(ticket.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(detailsPanel.getBackground());
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 100));
        detailsPanel.add(descScroll, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Priority:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(ticket.getPriority().toString()), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(ticket.getCategory().toString()), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        // For IT_SUPPORT, show status as a combo box
        if (userRole == Users.Role.IT_SUPPORT) {
            JComboBox<Ticket.Status> statusCombo = new JComboBox<>(Ticket.Status.values());
            statusCombo.setSelectedItem(ticket.getStatus());
            statusCombo.addActionListener(e -> {
                Ticket.Status newStatus = (Ticket.Status) statusCombo.getSelectedItem();
                updateTicketStatus(ticket.getTicketId(), newStatus);
            });
            detailsPanel.add(statusCombo, gbc);
        } else {
            detailsPanel.add(new JLabel(ticket.getStatus().toString()), gbc);
        }
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Created Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(ticket.getCreatedAt().toString()), gbc);


        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Created By:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(ticket.getCreatedBy().getUsername() != null ? 
            ticket.getCreatedBy().getUsername() : "Unknown"), gbc);

        
        
       
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        detailsPanel.add(new JPanel(), gbc);
        
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        return detailsPanel;
    }

    private void loadComments(Long ticketId, JTextArea commentsArea) {
        try {
            System.out.println("\n=== Starting Comment Loading Process ===");
            System.out.println("Ticket ID: " + ticketId);
            
            // Verify commentController is not null
            if (commentController == null) {
                throw new IllegalStateException("CommentController is not initialized");
            }
            
            // Get comments from controller
            System.out.println("Calling commentController.getCommentsByTicketId...");
            ResponseEntity<List<Comment>> response = commentController.getCommentsByTicketId(ticketId);
            System.out.println("Response status: " + response.getStatusCode());
            System.out.println("Response body null? " + (response.getBody() == null));
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Comment> comments = response.getBody();
                System.out.println("Number of comments retrieved: " + comments.size());
                
                StringBuilder commentsText = new StringBuilder();
                
                if (comments.isEmpty()) {
                    System.out.println("No comments found for this ticket");
                    commentsText.append("No comments yet. Be the first to comment!");
                } else {
                    for (Comment comment : comments) {
                        System.out.println("\nProcessing comment ID: " + comment.getCommentId());
                        
                        // Safe user retrieval
                        String username = "Unknown User";
                        if (comment.getUser() != null) {
                            username = comment.getUser().getUsername();
                            System.out.println("Comment by user: " + username);
                        } else {
                            System.out.println("Warning: Comment has no associated user");
                        }
                        
                        // Safe date retrieval
                        String dateStr = "Unknown Date";
                        if (comment.getCreatedAt() != null) {
                            dateStr = comment.getCreatedAt().toString();
                            System.out.println("Comment date: " + dateStr);
                        }
                        
                        // Safe content retrieval
                        String content = "";
                        if (comment.getContent() != null) {
                            content = comment.getContent();
                            System.out.println("Comment content length: " + content.length());
                        } else {
                            System.out.println("Warning: Comment has no content");
                        }
                        
                        commentsText.append("===================================\n")
                                  .append("From: ").append(username).append("\n")
                                  .append("Date: ").append(dateStr).append("\n")
                                  .append("-----------------------------------\n")
                                  .append(content).append("\n\n");
                    }
                }
                
                // Set the text in the comments area
                System.out.println("Setting comments text in UI");
                commentsArea.setText(commentsText.toString());
                commentsArea.setCaretPosition(0);
                System.out.println("Comments loaded successfully");
                
            } else {
                System.out.println("Error: Invalid response or null body");
                commentsArea.setText("Unable to load comments. Please try again later.");
            }
            
        } catch (Exception e) {
            System.err.println("\n=== Comment Loading Error ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
            
            // Set a more detailed error message in the UI
            String errorMessage = "Error loading comments. \nType: " + e.getClass().getSimpleName() + 
                                "\nDetails: " + e.getMessage();
            commentsArea.setText(errorMessage);
            
            // Show error dialog with details
            JOptionPane.showMessageDialog(null,
                "Error loading comments:\n" + e.getMessage() + 
                "\nCheck console for more details.",
                "Comment Loading Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addComment(Long ticketId, String content, JTextArea commentsArea) {
        try {
            System.out.println("\n=== Starting Comment Addition Process ===");
            System.out.println("Ticket ID: " + ticketId);
            System.out.println("Content length: " + content.length());
            
            // Verify repositories are not null
            if (ticketRepository == null || userRepository == null) {
                throw new IllegalStateException("Required repositories are not initialized");
            }
            
            // Find ticket and user
            System.out.println("Finding ticket and user...");
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
            Optional<Users> userOpt = userRepository.findById(userId);
            
            System.out.println("Ticket found: " + ticketOpt.isPresent());
            System.out.println("User found: " + userOpt.isPresent());
            
            if (ticketOpt.isPresent() && userOpt.isPresent()) {
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setTicket(ticketOpt.get());
                comment.setUser(userOpt.get());
                
                System.out.println("Created comment object, sending to controller...");
                ResponseEntity<Comment> response = commentController.addComment(comment);
                
                System.out.println("Response status: " + response.getStatusCode());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Comment added successfully");
                    loadComments(ticketId, commentsArea);
                } else {
                    throw new Exception("Failed to add comment. Status: " + response.getStatusCode());
                }
            } else {
                throw new Exception(
                    "Could not find " + 
                    (!ticketOpt.isPresent() ? "ticket" : "user") + 
                    " in database"
                );
            }
        } catch (Exception e) {
            System.err.println("\n=== Comment Addition Error ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null,
                "Error adding comment:\n" + e.getMessage() +
                "\nCheck console for more details.",
                "Comment Addition Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private UserRepository userRepository ;
    private TicketRepository ticketRepository ;
    private CommentController commentController ;
   
    

    private void updateTicketStatus(Long ticketId, Ticket.Status newStatus) {
        try {
            // First get the current ticket to record the old status
            ResponseEntity<TicketDTO> currentTicketResponse = ticketController.getTicketById(ticketId);
            if (currentTicketResponse != null && currentTicketResponse.getBody() != null) {
                TicketDTO ticketDTO = currentTicketResponse.getBody();
                Ticket.Status oldStatus = ticketDTO.getStatus(); // Get the old status
                
                // Update the ticket status
                ResponseEntity<Ticket> response = ticketController.updateTicketStatus(ticketId, newStatus);
                if (response.getStatusCode().is2xxSuccessful()) {
                    // Create audit log entry
                    Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
                    Optional<Users> userOpt = userRepository.findById(userId);
                    
                    if (ticketOpt.isPresent() && userOpt.isPresent()) {
                        AuditLog auditLog = new AuditLog();
                        auditLog.setTicket(ticketOpt.get());
                        auditLog.setUser(userOpt.get());
                        auditLog.setAction("STATUS_CHANGE");
                        auditLog.setOldValue(oldStatus != null ? oldStatus.toString() : "UNKNOWN");
                        auditLog.setNewValue(newStatus.toString());
                        
                        try {
                            ResponseEntity<AuditLog> auditResponse = auditLogController.addAuditLog(auditLog);
                            
                            if (auditResponse.getStatusCode().is2xxSuccessful()) {
                                JOptionPane.showMessageDialog(this,
                                    "Ticket status updated successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                                loadTickets();
                                
                                // Reload ticket details to show the update
                                showTicketDetails(ticketId);
                            }
                        } catch (Exception e) {
                            System.err.println("Error creating audit log: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                throw new Exception("Could not retrieve current ticket status");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating ticket status: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAuditLogsPanel(Long ticketId) {
        JPanel auditPanel = new JPanel(new BorderLayout(5, 5));
        auditPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea auditLogsArea = new JTextArea(15, 40);
        auditLogsArea.setEditable(false);
        auditLogsArea.setLineWrap(true);
        auditLogsArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(auditLogsArea);
        auditPanel.add(scrollPane, BorderLayout.CENTER);
        
        try {
            System.out.println("Loading audit logs for ticket: " + ticketId);
            ResponseEntity<List<AuditLogDTO>> response = auditLogController.getAuditLogsByTicketId(ticketId);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<AuditLogDTO> auditLogs = response.getBody();
                System.out.println("Number of audit logs loaded: " + auditLogs.size());
                
                StringBuilder logsText = new StringBuilder();
                for (AuditLogDTO log : auditLogs) {
                    logsText.append("=== ").append(log.getUsername()).append(" ===\n")
                            .append("Date: ").append(log.getCreatedAt()).append("\n")
                            .append("Action: ").append(log.getAction()).append("\n")
                            .append("Changed from '").append(log.getOldValue())
                            .append("' to '").append(log.getNewValue()).append("'\n\n");
                }
                
                auditLogsArea.setText(logsText.toString());
                auditLogsArea.setCaretPosition(0);
                System.out.println("Audit logs loaded successfully");
            } else {
                System.out.println("No audit logs found or invalid response");
                auditLogsArea.setText("No audit logs available for this ticket.");
            }
        } catch (Exception e) {
            System.err.println("Error loading audit logs: " + e.getMessage());
            e.printStackTrace();
            auditLogsArea.setText("Error loading audit logs. Please try again.");
        }
        
        return auditPanel;
    }



    private void layoutComponents() {
        System.out.println("Laying out components for role: " + userRole);
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        if (userRole == Users.Role.IT_SUPPORT) {
            System.out.println("Setting up IT Support view");
            // IT Support sees View Tickets first, then Create Ticket
            tabbedPane.addTab("View Tickets", viewTicketsPanel);
            tabbedPane.addTab("Create Ticket", createTicketPanel);
        } else {
            System.out.println("Setting up Employee view");
            // Employee only sees Create Ticket
            tabbedPane.addTab("Create Ticket", createTicketPanel);
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        System.out.println("Layout completed for role: " + userRole);
    }


    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        statusFilter = new JComboBox<>(Ticket.Status.values());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);
        
        // Add action listeners
        searchButton.addActionListener(e -> searchTickets());
        refreshButton.addActionListener(e -> loadTickets());
        statusFilter.addActionListener(e -> filterByStatus());
        
        return filterPanel;
    }

    private void searchTickets() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        Ticket.Status selectedStatus = (Ticket.Status) statusFilter.getSelectedItem();
        
        try {
            ResponseEntity<List<TicketDTO>> response = ticketController.getAllTickets();
            if (response != null && response.getBody() != null) {
                List<TicketDTO> allTickets = response.getBody();
                List<TicketDTO> filteredTickets = allTickets.stream()
                    .filter(ticket -> {
                        // Safely handle potential null values
                        String title = ticket.getTitle() != null ? ticket.getTitle().toLowerCase() : "";
                        String description = ticket.getDescription() != null ? ticket.getDescription().toLowerCase() : "";
                        
                        // Check if searching by ID
                        boolean matchesId = false;
                        try {
                            if (searchTerm.matches("\\d+")) {  // If search term is numeric
                                Long searchId = Long.parseLong(searchTerm);
                                matchesId = ticket.getTicketId().equals(searchId);
                            }
                        } catch (NumberFormatException e) {
                            // Not a valid number, ignore ID search
                        }
                        
                        boolean matchesSearch = searchTerm.isEmpty() ||
                            title.contains(searchTerm) ||
                            description.contains(searchTerm) ||
                            matchesId;
                        
                        boolean matchesStatus = selectedStatus == null ||
                            ticket.getStatus() == selectedStatus;
                            
                        return matchesSearch && matchesStatus;
                    })
                    .collect(java.util.stream.Collectors.toList());
                
                updateTableData(filteredTickets);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Add this for debugging
            JOptionPane.showMessageDialog(this,
                "Error searching tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByStatus() {
        Ticket.Status selectedStatus = (Ticket.Status) statusFilter.getSelectedItem();
        try {
            ResponseEntity<List<Ticket>> response = ticketController.getTicketsByStatus(selectedStatus);
            if (response != null && response.getBody() != null) {
                List<Ticket> filteredTickets = response.getBody();
                // Convert Ticket to TicketDTO for table display
                List<TicketDTO> ticketDTOs = filteredTickets.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
                updateTableData(ticketDTOs);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt());
        return dto;
    }





    
    





   
}