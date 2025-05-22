import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Elibrary extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private List<User> users = new ArrayList<>();
    private List<User> admins = new ArrayList<>();
    private List<Media> mediaFiles = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private User loggedInUser;
    private boolean isAdmin = false;

    // Main panel controls
    private JTextArea titleArea;
    private JTextArea mediaNameArea;
    private JTextArea yearArea;
    private JLabel titleLabel;
    private JLabel nameLabel;
    private JLabel yearLabel;
    private JLabel typeLabel;
    private JComboBox<String> mediaTypeCombo;
    private JButton uploadFileButton;
    private JButton viewUsersButton;
    private JButton viewAdminsButton;
    private JButton viewMediaButton;
    private JButton viewRequestsButton;
    private JButton adminSignUpFromMainButton;
    private JButton logoutButtonMain;
    private JButton clearDataButton;
    private JLabel adminFunctionsLabel;

    // User class for storing user/admin data
    static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        String name, fname, phone, email, password;
        boolean active;

        User(String name, String fname, String phone, String email, String password) {
            this.name = name;
            this.fname = fname;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.active = true;
        }
    }

    // Media class for storing book/media data
    static class Media implements Serializable {
        private static final long serialVersionUID = 1L;
        String title, name, year, filePath;
        boolean isPaid;
        boolean active;

        Media(String title, String name, String year, String filePath, boolean isPaid) {
            this.title = title;
            this.name = name;
            this.year = year;
            this.filePath = filePath;
            this.isPaid = isPaid;
            this.active = true;
        }
    }

    // Request class for handling paid media access requests
    static class Request implements Serializable {
        private static final long serialVersionUID = 1L;
        String userEmail, mediaFilePath;
        boolean approved;
        boolean active;

        Request(String userEmail, String mediaFilePath) {
            this.userEmail = userEmail;
            this.mediaFilePath = mediaFilePath;
            this.approved = false;
            this.active = true;
        }
    }

    public Elibrary() {
        setTitle("E-Library Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Load data from files
        loadData("users.dat", users);
        loadData("admins.dat", admins);
        loadData("media.dat", mediaFiles);
        loadData("requests.dat", requests);
        initializeOwnerAdmin();

        // Initialize card layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);

        // Add panels to card layout
        cardPanel.add(createLoginPanel(), "Login");
        cardPanel.add(createSignUpPanel(false), "UserSignUp");
        cardPanel.add(createSignUpPanel(true), "AdminSignUp");
        cardPanel.add(createMainPanel(), "Main");

        cardLayout.show(cardPanel, "Login");
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        JButton toUserSignUpButton = new JButton("Go to User Sign Up");
        toUserSignUpButton.setBackground(new Color(0, 191, 255));
        toUserSignUpButton.setForeground(Color.WHITE);
        JButton backButtonLogin = new JButton("Back");
        backButtonLogin.setBackground(new Color(255, 0, 0));

        addShowPasswordListener(showPasswordCheckBox, passwordField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(showPasswordCheckBox, gbc);
        gbc.gridy = 3;
        loginPanel.add(loginButton, gbc);
        gbc.gridy = 4;
        loginPanel.add(toUserSignUpButton, gbc);
        gbc.gridy = 5;
        loginPanel.add(backButtonLogin, gbc);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password");
                return;
            }
            for (User user : users) {
                if (user.email.equals(email) && user.password.equals(password) && user.active) {
                    loggedInUser = user;
                    isAdmin = false;
                    setAdminControlsVisibility(false);
                    cardLayout.show(cardPanel, "Main");
                    emailField.setText("");
                    passwordField.setText("");
                    return;
                }
            }
            for (User admin : admins) {
                if (admin.email.equals(email) && admin.password.equals(password) && admin.active) {
                    loggedInUser = admin;
                    isAdmin = true;
                    setAdminControlsVisibility(true);
                    cardLayout.show(cardPanel, "Main");
                    emailField.setText("");
                    passwordField.setText("");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials or inactive account");
        });

        toUserSignUpButton.addActionListener(e -> cardLayout.show(cardPanel, "UserSignUp"));
        backButtonLogin.addActionListener(e -> System.exit(0));

        return loginPanel;
    }

    private JPanel createSignUpPanel(boolean isAdminPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 80, 25);
        JTextArea nameArea = createInputTextArea();
        nameArea.setBounds(120, 20, 200, 25);

        JLabel fnameLabel = new JLabel("Father's Name:");
        fnameLabel.setBounds(20, 60, 100, 25);
        JTextArea fnameArea = createInputTextArea();
        fnameArea.setBounds(120, 60, 200, 25);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(20, 100, 80, 25);
        JTextArea phoneArea = createInputTextArea();
        phoneArea.setBounds(120, 100, 200, 25);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 140, 80, 25);
        JTextArea emailArea = createInputTextArea();
        emailArea.setBounds(120, 140, 200, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 180, 80, 25);
        JTextArea passwordArea = createInputTextArea();
        passwordArea.setBounds(120, 180, 200, 25);

        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(120, 210, 120, 25);

        JButton signUpButton = new JButton("Register");
        signUpButton.setBounds(120, 240, 100, 30);
        signUpButton.setBackground(new Color(0, 123, 255));
        signUpButton.setForeground(Color.WHITE);
        JButton toLoginButton = new JButton("Go to Login");
        toLoginButton.setBounds(230, 240, 100, 30);
        toLoginButton.setBackground(new Color(40, 167, 69));
        toLoginButton.setForeground(Color.WHITE);

        addShowPasswordListener(showPasswordCheckBox, passwordArea);

        signUpButton.addActionListener(e -> {
            String name = nameArea.getText().trim();
            String fname = fnameArea.getText().trim();
            String phone = phoneArea.getText().trim();
            String email = emailArea.getText().trim();
            String password = passwordArea.getText().trim();
            if (validateInput(name, fname, phone, email, password)) {
                (isAdminPanel ? admins : users).add(new User(name, fname, phone, email, password));
                if (isAdminPanel)
                    saveData("admins.dat", admins);
                else
                    saveData("users.dat", users);
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                nameArea.setText("");
                fnameArea.setText("");
                phoneArea.setText("");
                emailArea.setText("");
                passwordArea.setText("");
                cardLayout.show(cardPanel, "Login");
            }
        });

        toLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        panel.add(nameLabel);
        panel.add(nameArea);
        panel.add(fnameLabel);
        panel.add(fnameArea);
        panel.add(phoneLabel);
        panel.add(phoneArea);
        panel.add(emailLabel);
        panel.add(emailArea);
        panel.add(passLabel);
        panel.add(passwordArea);
        panel.add(showPasswordCheckBox);
        panel.add(signUpButton);
        panel.add(toLoginButton);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        titleLabel = new JLabel("Title of book:");
        titleLabel.setBounds(20, 20, 80, 25);
        titleArea = createInputTextArea();
        titleArea.setBounds(120, 20, 200, 25);

        nameLabel = new JLabel("Author Name:");
        nameLabel.setBounds(20, 60, 80, 25);
        mediaNameArea = createInputTextArea();
        mediaNameArea.setBounds(120, 60, 200, 25);

        yearLabel = new JLabel("Year:");
        yearLabel.setBounds(20, 100, 80, 25);
        yearArea = createInputTextArea();
        yearArea.setBounds(120, 100, 200, 25);

        typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 140, 80, 25);
        mediaTypeCombo = new JComboBox<>(new String[] { "Free", "Paid" });
        mediaTypeCombo.setBounds(120, 140, 200, 25);

        adminFunctionsLabel = new JLabel("Admin Possible Functions");
        adminFunctionsLabel.setBounds(120, 150, 200, 25);

        uploadFileButton = new JButton("Upload books");
        uploadFileButton.setBounds(120, 180, 100, 30);
        uploadFileButton.setBackground(new Color(0, 123, 255));
        viewUsersButton = new JButton("View Users");
        viewUsersButton.setBounds(120, 220, 100, 30);
        viewUsersButton.setBackground(new Color(40, 167, 69));
        viewAdminsButton = new JButton("View Admins");
        viewAdminsButton.setBounds(120, 260, 100, 30);
        viewAdminsButton.setBackground(new Color(40, 167, 69));
        viewMediaButton = new JButton("Book lists");
        viewMediaButton.setBounds(230, 180, 100, 30);
        viewMediaButton.setBackground(new Color(40, 167, 69));
        viewRequestsButton = new JButton("View Requests");
        viewRequestsButton.setBounds(230, 220, 100, 30);
        viewRequestsButton.setBackground(new Color(40, 167, 69));
        adminSignUpFromMainButton = new JButton("Admin Sign Up");
        adminSignUpFromMainButton.setBounds(230, 260, 100, 30);
        adminSignUpFromMainButton.setBackground(new Color(40, 167, 69));
        clearDataButton = new JButton("Clear Data");
        clearDataButton.setBounds(120, 300, 100, 30);
        clearDataButton.setBackground(new Color(40, 167, 69));
        logoutButtonMain = new JButton("Logout");
        logoutButtonMain.setBounds(230, 300, 100, 30);
        logoutButtonMain.setBackground(new Color(255, 0, 0));
        logoutButtonMain.setForeground(Color.WHITE);

        mainPanel.add(titleLabel);
        mainPanel.add(titleArea);
        mainPanel.add(nameLabel);
        mainPanel.add(mediaNameArea);
        mainPanel.add(yearLabel);
        mainPanel.add(yearArea);
        mainPanel.add(typeLabel);
        mainPanel.add(mediaTypeCombo);
        mainPanel.add(adminFunctionsLabel);
        mainPanel.add(uploadFileButton);
        mainPanel.add(viewUsersButton);
        mainPanel.add(viewAdminsButton);
        mainPanel.add(viewMediaButton);
        mainPanel.add(viewRequestsButton);
        mainPanel.add(adminSignUpFromMainButton);
        mainPanel.add(clearDataButton);
        mainPanel.add(logoutButtonMain);

        uploadFileButton.addActionListener(e -> showUploadDialog());
        viewUsersButton.addActionListener(e -> showTable(users, "Registered Users", () -> saveData("users.dat", users)));
        viewAdminsButton.addActionListener(e -> showTable(admins, "Registered Admins", () -> saveData("admins.dat", admins)));
        viewMediaButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(1, 2));
            JButton freeButton = new JButton("Free");
            JButton paidButton = new JButton("Paid");
            panel.add(freeButton);
            panel.add(paidButton);

            freeButton.addActionListener(e1 -> showMediaTable(mediaFiles.stream()
                    .filter(m -> !m.isPaid && m.active).collect(Collectors.toList()), isAdmin));
            paidButton.addActionListener(e1 -> showMediaTable(mediaFiles.stream()
                    .filter(m -> m.isPaid && m.active).collect(Collectors.toList()), isAdmin));

            JOptionPane.showMessageDialog(this, panel, "Select Media Type", JOptionPane.PLAIN_MESSAGE);
        });
        viewRequestsButton.addActionListener(e -> showRequestsTable());
        adminSignUpFromMainButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminSignUp"));
        logoutButtonMain.addActionListener(e -> {
            cardLayout.show(cardPanel, "Login");
            loggedInUser = null;
            isAdmin = false;
            setAdminControlsVisibility(false);
        });
        clearDataButton.addActionListener(e -> showClearDataDialog());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                setAdminControlsVisibility(isAdmin);
            }
        });

        return mainPanel;
    }

    private JTextArea createInputTextArea() {
        JTextArea textArea = new JTextArea(1, 20);
        textArea.setLineWrap(false);
        textArea.setEditable(true);
        textArea.setPreferredSize(new Dimension(200, 25));
        return textArea;
    }

    private void addShowPasswordListener(JCheckBox checkBox, Component field) {
        checkBox.addActionListener(e -> {
            if (field instanceof JPasswordField) {
                ((JPasswordField) field).setEchoChar(checkBox.isSelected() ? (char) 0 : '*');
            } else if (field instanceof JTextArea) {
                ((JTextArea) field).setForeground(checkBox.isSelected() ? Color.BLACK : Color.GRAY);
            }
        });
    }

    private JDialog createDialog(String title, boolean modal) {
        JDialog dialog = new JDialog(this, title, modal);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void showUploadDialog() {
        JDialog uploadDialog = createDialog("Upload books", true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField titleField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Free", "Paid" });
        JLabel fileLabel = new JLabel("No file selected");
        JButton chooseFileButton = new JButton("Choose book");
        JButton sendButton = new JButton("Send");
        final File[] selectedFile = new File[1];

        gbc.gridx = 0;
        gbc.gridy = 0;
        uploadDialog.add(new JLabel("Book title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        uploadDialog.add(titleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        uploadDialog.add(new JLabel("Author Name:"), gbc);
        gbc.gridx = 1;
        uploadDialog.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        uploadDialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        uploadDialog.add(yearField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        uploadDialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        uploadDialog.add(typeCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        uploadDialog.add(chooseFileButton, gbc);
        gbc.gridx = 1;
        uploadDialog.add(fileLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        uploadDialog.add(sendButton, gbc);

        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Supported Files", "ppt", "pptx", "pdf", "doc", "docx", "mp3", "wav", "mp4", "avi", "jpg", "jpeg", "png");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(uploadDialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile[0].getName());
            }
        });

        sendButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String name = nameField.getText().trim();
            String year = yearField.getText().trim();
            boolean isPaid = typeCombo.getSelectedItem().equals("Paid");

            if (title.isEmpty() || name.isEmpty() || year.isEmpty() || selectedFile[0] == null) {
                JOptionPane.showMessageDialog(uploadDialog, "Please fill all fields and select a file");
                return;
            }

            try {
                String destDir = isPaid ? "Uploads/Paid" : "Uploads";
                String destPath = destDir + "/" + selectedFile[0].getName();
                Files.createDirectories(Paths.get(destDir));
                Files.copy(selectedFile[0].toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
                mediaFiles.add(new Media(title, name, year, destPath, isPaid));
                saveData("media.dat", mediaFiles);
                JOptionPane.showMessageDialog(uploadDialog, "Book uploaded successfully");
                uploadDialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(uploadDialog, "Error uploading file");
            }
        });

        uploadDialog.pack();
        uploadDialog.setVisible(true);
    }

    private void showTable(List<User> data, String title, Runnable saveAction) {
        String[] columnNames = { "Name", "Father's Name", "Phone", "Email", "Password", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (User user : data) {
            tableModel.addRow(new Object[] { user.name, user.fname, user.phone, user.email, user.password, "Delete" });
        }

        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setBackground(new Color(0, 191, 255));
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && table.columnAtPoint(e.getPoint()) == 5) {
                    showAdminAuthDialog("Delete " + title.split(" ")[1], () -> {
                        data.remove(row);
                        tableModel.removeRow(row);
                        saveAction.run();
                        JOptionPane.showMessageDialog(Elibrary.this, title.split(" ")[1] + " deleted successfully");
                    });
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMediaTable(List<Media> mediaList, boolean allowDelete) {
        String[] columnNames = { "Book Title", "Author Name", "Year", "Book List", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Media media : mediaList) {
            tableModel.addRow(new Object[] { media.title, media.name, media.year, media.filePath, "Delete" });
        }

        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setBackground(new Color(0, 191, 255));
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search (Title/Name/Year):"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            tableModel.setRowCount(0);
            List<Media> filteredList = mediaList.stream()
                    .filter(media -> media.title.toLowerCase().contains(query) ||
                            media.name.toLowerCase().contains(query) ||
                            media.year.toLowerCase().contains(query))
                    .collect(Collectors.toList());
            for (Media media : filteredList) {
                tableModel.addRow(new Object[] { media.title, media.name, media.year, media.filePath, "Delete" });
            }
        });

        if (allowDelete && isAdmin) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && table.columnAtPoint(e.getPoint()) == 4) {
                        showAdminAuthDialog("Delete Media", () -> {
                            String filePath = (String) tableModel.getValueAt(row, 3);
                            mediaFiles.removeIf(media -> media.filePath.equals(filePath));
                            tableModel.removeRow(row);
                            saveData("media.dat", mediaFiles);
                            JOptionPane.showMessageDialog(Elibrary.this, "Book deleted successfully");
                        });
                    }
                }
            });
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 3) {
                    String filePath = (String) tableModel.getValueAt(row, 3);
                    Media selectedMedia = mediaFiles.stream()
                            .filter(media -> media.filePath.equals(filePath)).findFirst().orElse(null);
                    if (selectedMedia != null) {
                        if (!selectedMedia.isPaid || isAdmin) {
                            openOrDownloadFile(filePath);
                        } else {
                            boolean isApproved = requests.stream()
                                    .anyMatch(r -> r.userEmail.equals(loggedInUser.email) &&
                                            r.mediaFilePath.equals(filePath) && r.approved && r.active);
                            if (isApproved) {
                                openOrDownloadFile(filePath);
                            } else {
                                showPaidMediaRequestDialog(filePath);
                            }
                        }
                    }
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 350));
        JOptionPane.showMessageDialog(this, panel, "Book List", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openOrDownloadFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(file.getName()));
                if (fileChooser.showSaveDialog(Elibrary.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        Files.copy(file.toPath(), fileChooser.getSelectedFile().toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(Elibrary.this, "Downloaded successfully");
                    } catch (IOException ex2) {
                        JOptionPane.showMessageDialog(Elibrary.this, "Error downloading file");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(Elibrary.this, "File not found");
        }
    }

    private void showRequestsTable() {
        String[] columnNames = { "Name", "User Email", "Book List", "Status", "Action", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Request request : requests) {
            String userName = "Unknown";
            for (User user : users) {
                if (user.email.equals(request.userEmail) && user.active) {
                    userName = user.name;
                    break;
                }
            }
            for (User admin : admins) {
                if (admin.email.equals(request.userEmail) && admin.active) {
                    userName = admin.name;
                    break;
                }
            }
            String status = request.approved ? "Approved" : "Pending";
            tableModel.addRow(new Object[] { userName, request.userEmail, request.mediaFilePath, status, "", "Delete" });
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setBackground(new Color(0, 191, 255));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), tableModel));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0) {
                    if (col == 5) {
                        showAdminAuthDialog("Delete Request", () -> {
                            requests.remove(row);
                            tableModel.removeRow(row);
                            saveData("requests.dat", requests);
                            JOptionPane.showMessageDialog(Elibrary.this, "Request deleted successfully");
                        });
                    } else if (col == 2 && isAdmin) {
                        String mediaFilePath = (String) tableModel.getValueAt(row, 2);
                        String fileName = new File(mediaFilePath).getName();
                        File requestFile = new File("Requests/" + fileName);
                        if (requestFile.exists()) {
                            try {
                                Desktop.getDesktop().open(requestFile);
                            } catch (IOException ex) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setSelectedFile(new File(requestFile.getName()));
                                if (fileChooser.showSaveDialog(Elibrary.this) == JFileChooser.APPROVE_OPTION) {
                                    try {
                                        Files.copy(requestFile.toPath(), fileChooser.getSelectedFile().toPath(),
                                                StandardCopyOption.REPLACE_EXISTING);
                                        JOptionPane.showMessageDialog(Elibrary.this, "Request file downloaded successfully");
                                    } catch (IOException ex2) {
                                        JOptionPane.showMessageDialog(Elibrary.this, "Error downloading request file");
                                    }
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(Elibrary.this, "Request file not found");
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Paid Book Requests", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPaidMediaRequestDialog(String filePath) {
        JDialog dialog = createDialog("Request Paid Book Access", true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField codeField = new JTextField("1000457005068", 20);
        codeField.setEditable(false);
        JLabel fileLabel = new JLabel("Selected File: " + filePath);
        JButton chooseFileButton = new JButton("Choose File");
        JLabel chosenFileLabel = new JLabel("No file selected");
        JButton sendButton = new JButton("Send");
        final File[] selectedFile = new File[1];

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Pay with account:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(codeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Upload screenshot:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(chooseFileButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(chosenFileLabel, gbc);
        gbc.gridy = 3;
        dialog.add(fileLabel, gbc);
        gbc.gridy = 4;
        dialog.add(sendButton, gbc);

        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                chosenFileLabel.setText(selectedFile[0].getName());
            }
        });

        sendButton.addActionListener(e -> {
            if (selectedFile[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a file");
                return;
            }
            try {
                String destPath = "Requests/" + selectedFile[0].getName();
                Files.createDirectories(Paths.get("Requests"));
                Files.copy(selectedFile[0].toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
                requests.add(new Request(loggedInUser.email, filePath));
                saveData("requests.dat", requests);
                JOptionPane.showMessageDialog(dialog, "Request submitted. Awaiting admin approval.");
                dialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Error uploading file");
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    private void showClearDataDialog() {
        JDialog dialog = createDialog("Clear Data", true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JComboBox<String> tableCombo = new JComboBox<>(new String[] { "Users", "Admins", "Requests" });
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        JButton clearButton = new JButton("Clear Selected Table");

        addShowPasswordListener(showPasswordCheckBox, passwordField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Select Table:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(tableCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Admin Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Admin Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(showPasswordCheckBox, gbc);
        gbc.gridy = 4;
        dialog.add(clearButton, gbc);

        clearButton.addActionListener(_ -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean validAdmin = admins.stream()
                    .anyMatch(a -> a.email.equals(email) && a.password.equals(password) && a.active);
            if (!validAdmin) {
                JOptionPane.showMessageDialog(dialog, "Invalid admin credentials");
                return;
            }

            String selectedTable = (String) tableCombo.getSelectedItem();
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to clear all " + selectedTable + " data?", "Confirm Clear",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                switch (selectedTable) {
                    case "Users":
                        users.clear();
                        saveData("users.dat", users);
                        break;
                    case "Admins":
                        admins.clear();
                        initializeOwnerAdmin();
                        saveData("admins.dat", admins);
                        break;
                    case "Requests":
                        requests.clear();
                        saveData("requests.dat", requests);
                        break;
                }
                JOptionPane.showMessageDialog(dialog, selectedTable + " data cleared successfully");
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    private void showAdminAuthDialog(String action, Runnable onSuccess) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");

        addShowPasswordListener(showPasswordCheckBox, passwordField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Admin Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Admin Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(showPasswordCheckBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Authorize " + action, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (admins.stream().anyMatch(a -> a.email.equals(email) && a.password.equals(password) && a.active)) {
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials");
            }
        }
    }

    private void initializeOwnerAdmin() {
        if (!admins.stream().anyMatch(a -> a.email.equals("mele@gmail.com") && a.active)) {
            admins.add(new User("Owner", "Admin", "0912345678", "mele@gmail.com", "MELES"));
            saveData("admins.dat", admins);
        }
    }

    private boolean validateInput(String name, String fname, String phone, String email, String password) {
        if (name.isEmpty() || fname.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return false;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email must contain '@'");
            return false;
        }
        if (password.length() <= 5) {
            JOptionPane.showMessageDialog(this, "Password must be longer than 5 characters");
            return false;
        }
        if (!phone.matches("^09\\d{8}$")) {
            JOptionPane.showMessageDialog(this, "Phone must be 10 digits, start with '09', and contain only numbers");
            return false;
        }
        if (users.stream().anyMatch(u -> u.email.equals(email) && u.active) ||
                admins.stream().anyMatch(a -> a.email.equals(email) && a.active)) {
            JOptionPane.showMessageDialog(this, "Email already registered");
            return false;
        }
        return true;
    }

    private void saveData(String fileName, List<?> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void loadData(String fileName, List<T> data) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            data.clear();
            data.addAll((List<T>) ois.readObject());
        } catch (FileNotFoundException e) {
            data.clear();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setAdminControlsVisibility(boolean showAdminFeatures) {
        titleLabel.setVisible(false);
        titleArea.setVisible(false);
        nameLabel.setVisible(false);
        mediaNameArea.setVisible(false);
        yearLabel.setVisible(false);
        yearArea.setVisible(false);
        typeLabel.setVisible(false);
        mediaTypeCombo.setVisible(false);
        adminFunctionsLabel.setVisible(showAdminFeatures);
        uploadFileButton.setVisible(showAdminFeatures);
        viewUsersButton.setVisible(showAdminFeatures);
        viewAdminsButton.setVisible(showAdminFeatures);
        viewRequestsButton.setVisible(showAdminFeatures);
        adminSignUpFromMainButton.setVisible(showAdminFeatures);
        clearDataButton.setVisible(showAdminFeatures);
        logoutButtonMain.setVisible(true);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText(requests.get(row).active ? (requests.get(row).approved ? "Reject" : "Approve") : "");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DefaultTableModel tableModel;
        private int row;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel tableModel) {
            super(checkBox);
            this.tableModel = tableModel;
            button = new JButton();
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = requests.get(row).active ? (requests.get(row).approved ? "Reject" : "Approve") : "";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && requests.get(row).active) {
                showAdminAuthDialog(label + " Request", () -> {
                    Request request = requests.get(row);
                    request.approved = !request.approved;
                    tableModel.setValueAt(request.approved ? "Approved" : "Pending", row, 3);
                    tableModel.setValueAt("", row, 4);
                    saveData("requests.dat", requests);
                    JOptionPane.showMessageDialog(Elibrary.this,
                            "Request " + (request.approved ? "approved" : "rejected") + " successfully");
                });
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Elibrary().setVisible(true));
    }
}
