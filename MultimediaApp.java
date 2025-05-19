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
import javax.swing.filechooser.FileNameExtensionFilter;

public class MultimediaApp extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private List<User> users = new ArrayList<>();
    private List<User> admins = new ArrayList<>();
    private List<Media> mediaFiles = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private User loggedInUser;
    private String previousPanel = "Login";
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

    public MultimediaApp() {
        setTitle("E-Library Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        loadUsers();
        loadAdmins();
        loadMedia();
        loadRequests();
        initializeOwnerAdmin();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        JButton loginButton = new JButton("Login");
        JButton toUserSignUpButton = new JButton("Go to User Sign Up");
        JButton backButtonLogin = new JButton("Back");
        showPasswordCheckBox.addActionListener(_ -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*');
        });

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
                    previousPanel = "Login";
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
                    previousPanel = "Login";
                    setAdminControlsVisibility(true);
                    cardLayout.show(cardPanel, "Main");
                    emailField.setText("");
                    passwordField.setText("");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials or inactive account");
        });

        toUserSignUpButton.addActionListener(e -> {
            previousPanel = "Login";
            cardLayout.show(cardPanel, "UserSignUp");
        });

        backButtonLogin.addActionListener(e -> System.exit(0));

        // User Sign Up Panel
        JPanel userSignUpPanel = new JPanel();
        userSignUpPanel.setLayout(null);

        JLabel userNameLabel = new JLabel("Name:");
        userNameLabel.setBounds(20, 20, 80, 25);
        JTextArea userNameArea = createInputTextArea();
        userNameArea.setBounds(120, 20, 200, 25);

        JLabel userFnameLabel = new JLabel("Father's Name:");
        userFnameLabel.setBounds(20, 60, 100, 25);
        JTextArea userFnameArea = createInputTextArea();
        userFnameArea.setBounds(120, 60, 200, 25);

        JLabel userPhoneLabel = new JLabel("Phone:");
        userPhoneLabel.setBounds(20, 100, 80, 25);
        JTextArea userPhoneArea = createInputTextArea();
        userPhoneArea.setBounds(120, 100, 200, 25);

        JLabel userEmailLabel = new JLabel("Email:");
        userEmailLabel.setBounds(20, 140, 80, 25);
        JTextArea userEmailArea = createInputTextArea();
        userEmailArea.setBounds(120, 140, 200, 25);

        JLabel userPassLabel = new JLabel("Password:");
        userPassLabel.setBounds(20, 180, 80, 25);
        JTextArea userPasswordArea = createInputTextArea();
        userPasswordArea.setBounds(120, 180, 200, 25);

        JCheckBox userShowPasswordCheckBox = new JCheckBox("Show Password");
        userShowPasswordCheckBox.setBounds(120, 210, 120, 25);

        JButton userSignUpButton = new JButton("Register");
        userSignUpButton.setBounds(120, 240, 100, 30);

        JButton userToLoginButton = new JButton("Go to Login");
        userToLoginButton.setBounds(230, 240, 100, 30);

        userShowPasswordCheckBox.addActionListener(e -> {
            userPasswordArea.setForeground(userShowPasswordCheckBox.isSelected() ? Color.BLACK : Color.GRAY);
            userPasswordArea.setText(userPasswordArea.getText());
        });

        userSignUpButton.addActionListener(e -> {
            String name = userNameArea.getText().trim();
            String fname = userFnameArea.getText().trim();
            String phone = userPhoneArea.getText().trim();
            String email = userEmailArea.getText().trim();
            String password = userPasswordArea.getText().trim();
            if (validateInput(name, fname, phone, email, password)) {
                users.add(new User(name, fname, phone, email, password));
                saveUsers();
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                emailField.setText(email);
                userNameArea.setText("");
                userFnameArea.setText("");
                userPhoneArea.setText("");
                userEmailArea.setText("");
                userPasswordArea.setText("");
                cardLayout.show(cardPanel, "Login");
            }
        });

        userToLoginButton.addActionListener(e -> {
            previousPanel = "UserSignUp";
            cardLayout.show(cardPanel, "Login");
        });

        userSignUpPanel.add(userNameLabel);
        userSignUpPanel.add(userNameArea);
        userSignUpPanel.add(userFnameLabel);
        userSignUpPanel.add(userFnameArea);
        userSignUpPanel.add(userPhoneLabel);
        userSignUpPanel.add(userPhoneArea);
        userSignUpPanel.add(userEmailLabel);
        userSignUpPanel.add(userEmailArea);
        userSignUpPanel.add(userPassLabel);
        userSignUpPanel.add(userPasswordArea);
        userSignUpPanel.add(userShowPasswordCheckBox);
        userSignUpPanel.add(userSignUpButton);
        userSignUpPanel.add(userToLoginButton);

        // Admin Sign Up Panel
        JPanel adminSignUpPanel = new JPanel();
        adminSignUpPanel.setLayout(null);

        JLabel adminNameLabel = new JLabel("Name:");
        adminNameLabel.setBounds(20, 20, 80, 25);
        JTextArea adminNameArea = createInputTextArea();
        adminNameArea.setBounds(120, 20, 200, 25);

        JLabel adminFnameLabel = new JLabel("Father's Name:");
        adminFnameLabel.setBounds(20, 60, 100, 25);
        JTextArea adminFnameArea = createInputTextArea();
        adminFnameArea.setBounds(120, 60, 200, 25);

        JLabel adminPhoneLabel = new JLabel("Phone:");
        adminPhoneLabel.setBounds(20, 100, 80, 25);
        JTextArea adminPhoneArea = createInputTextArea();
        adminPhoneArea.setBounds(120, 100, 200, 25);

        JLabel adminEmailLabel = new JLabel("Email:");
        adminEmailLabel.setBounds(20, 140, 80, 25);
        JTextArea adminEmailArea = createInputTextArea();
        adminEmailArea.setBounds(120, 140, 200, 25);

        JLabel adminPassLabel = new JLabel("Password:");
        adminPassLabel.setBounds(20, 180, 80, 25);
        JTextArea adminPasswordArea = createInputTextArea();
        adminPasswordArea.setBounds(120, 180, 200, 25);

        JCheckBox adminShowPasswordCheckBox = new JCheckBox("Show Password");
        adminShowPasswordCheckBox.setBounds(120, 210, 120, 25);

        JButton adminSignUpButton = new JButton("Register");
        adminSignUpButton.setBounds(120, 240, 100, 30);

        JButton adminToLoginButton = new JButton("Go to Login");
        adminToLoginButton.setBounds(230, 240, 100, 30);

        adminShowPasswordCheckBox.addActionListener(e -> {
            adminPasswordArea.setForeground(adminShowPasswordCheckBox.isSelected() ? Color.BLACK : Color.GRAY);
            adminPasswordArea.setText(adminPasswordArea.getText());
        });

        adminSignUpButton.addActionListener(e -> {
            String name = adminNameArea.getText().trim();
            String fname = adminFnameArea.getText().trim();
            String phone = adminPhoneArea.getText().trim();
            String email = adminEmailArea.getText().trim();
            String password = adminPasswordArea.getText().trim();
            if (validateInput(name, fname, phone, email, password)) {
                admins.add(new User(name, fname, phone, email, password));
                saveAdmins();
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                emailField.setText(email);
                adminNameArea.setText("");
                adminFnameArea.setText("");
                adminPhoneArea.setText("");
                adminEmailArea.setText("");
                adminPasswordArea.setText("");
                cardLayout.show(cardPanel, "Login");
            }
        });

        adminToLoginButton.addActionListener(e -> {
            previousPanel = "AdminSignUp";
            cardLayout.show(cardPanel, "Login");
        });

        adminSignUpPanel.add(adminNameLabel);
        adminSignUpPanel.add(adminNameArea);
        adminSignUpPanel.add(adminFnameLabel);
        adminSignUpPanel.add(adminFnameArea);
        adminSignUpPanel.add(adminPhoneLabel);
        adminSignUpPanel.add(adminPhoneArea);
        adminSignUpPanel.add(adminEmailLabel);
        adminSignUpPanel.add(adminEmailArea);
        adminSignUpPanel.add(adminPassLabel);
        adminSignUpPanel.add(adminPasswordArea);
        adminSignUpPanel.add(adminShowPasswordCheckBox);
        adminSignUpPanel.add(adminSignUpButton);
        adminSignUpPanel.add(adminToLoginButton);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        titleLabel = new JLabel("Title of book:");
        titleLabel.setBounds(20, 20, 80, 25);
        titleArea = createInputTextArea();
        titleArea.setBounds(120, 20, 200, 25);

        nameLabel = new JLabel("auther Name:");
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
        viewUsersButton = new JButton("View Users");
        viewUsersButton.setBounds(120, 220, 100, 30);
        viewAdminsButton = new JButton("View Admins");
        viewAdminsButton.setBounds(120, 260, 100, 30);
        viewMediaButton = new JButton("book lists");
        viewMediaButton.setBounds(230, 180, 100, 30);
        viewRequestsButton = new JButton("View Requests");
        viewRequestsButton.setBounds(230, 220, 100, 30);
        adminSignUpFromMainButton = new JButton("Admin Sign Up");
        adminSignUpFromMainButton.setBounds(230, 260, 100, 30);
        clearDataButton = new JButton("Clear Data");
        clearDataButton.setBounds(120, 300, 100, 30);
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

        // Initially hide Title, Name, Year fields and labels
        titleLabel.setVisible(false);
        titleArea.setVisible(false);
        nameLabel.setVisible(false);
        mediaNameArea.setVisible(false);
        yearLabel.setVisible(false);
        yearArea.setVisible(false);
        typeLabel.setVisible(false);
        mediaTypeCombo.setVisible(false);
        adminFunctionsLabel.setVisible(false);

        setAdminControlsVisibility(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                setAdminControlsVisibility(isAdmin);
            }
        });

        uploadFileButton.addActionListener(e -> showUploadDialog());

        viewUsersButton.addActionListener(e -> showUsersTable());

        viewAdminsButton.addActionListener(e -> showAdminsTable());

        viewMediaButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(1, 2));
            JButton freeButton = new JButton("Free");
            JButton paidButton = new JButton("Paid");
            panel.add(freeButton);
            panel.add(paidButton);

            freeButton.addActionListener(e1 -> {
                List<Media> freeMedia = new ArrayList<>();
                for (Media media : mediaFiles) {
                    if (!media.isPaid && media.active) {
                        freeMedia.add(media);
                    }
                }
                showMediaTable(freeMedia, isAdmin);
            });

            paidButton.addActionListener(e1 -> {
                List<Media> paidMedia = new ArrayList<>();
                for (Media media : mediaFiles) {
                    if (media.isPaid && media.active) {
                        paidMedia.add(media);
                    }
                }
                showMediaTable(paidMedia, isAdmin);
            });

            JOptionPane.showMessageDialog(this, panel, "Select Media Type", JOptionPane.PLAIN_MESSAGE);
        });

        viewRequestsButton.addActionListener(e -> showRequestsTable());

        adminSignUpFromMainButton.addActionListener(e -> {
            previousPanel = "Main";
            cardLayout.show(cardPanel, "AdminSignUp");
        });

        logoutButtonMain.addActionListener(e -> {
            cardLayout.show(cardPanel, "Login");
            loggedInUser = null;
            isAdmin = false;
            setAdminControlsVisibility(false);
        });

        clearDataButton.addActionListener(e -> showClearDataDialog());

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(userSignUpPanel, "UserSignUp");
        cardPanel.add(adminSignUpPanel, "AdminSignUp");
        cardPanel.add(mainPanel, "Main");

        cardLayout.show(cardPanel, "Login");
    }

    private JTextArea createInputTextArea() {
        JTextArea textArea = new JTextArea(1, 20);
        textArea.setLineWrap(false);
        textArea.setEditable(true);
        textArea.setPreferredSize(new Dimension(200, 25));
        return textArea;
    }

    private void showUploadDialog() {
        JDialog uploadDialog = new JDialog(this, "Upload books", true);
        uploadDialog.setLayout(new GridBagLayout());
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
        uploadDialog.add(new JLabel("book title :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        uploadDialog.add(titleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        uploadDialog.add(new JLabel("auther Name:"), gbc);
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
                    "Supported Files", "ppt", "pptx", "pdf", "doc", "docx", "mp3", "wav", "mp4", "avi", "jpg", "jpeg",
                    "png");
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
                saveMedia();
                JOptionPane.showMessageDialog(uploadDialog, "book is uploaded successfully");
                uploadDialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(uploadDialog, "Error uploading file");
            }
        });

        uploadDialog.pack();
        uploadDialog.setLocationRelativeTo(this);
        uploadDialog.setVisible(true);
    }

    private void showUsersTable() {
        String[] columnNames = { "Name", "Father's Name", "Phone", "Email", "Password", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (User user : users) {
            Object[] row = { user.name, user.fname, user.phone, user.email, user.password, "Delete" };
            tableModel.addRow(row);
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
                    showAdminAuthDialog("Delete User", () -> {
                        users.remove(row);
                        tableModel.removeRow(row);
                        saveUsers();
                        JOptionPane.showMessageDialog(MultimediaApp.this, "User deleted successfully");
                    });
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, panel, "Registered Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAdminsTable() {
        String[] columnNames = { "Name", "Father's Name", "Phone", "Email", "Password", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (User admin : admins) {
            Object[] row = { admin.name, admin.fname, admin.phone, admin.email, admin.password, "Delete" };
            tableModel.addRow(row);
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
                    showAdminAuthDialog("Delete Admin", () -> {
                        admins.remove(row);
                        tableModel.removeRow(row);
                        saveAdmins();
                        JOptionPane.showMessageDialog(MultimediaApp.this, "Admin deleted successfully");
                    });
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, panel, "Registered Admins", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMediaTable(List<Media> mediaList, boolean allowDelete) {
        String[] columnNames = { "book Title", "auther Name", "Year", "book list", "Delete" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Media media : mediaList) {
            Object[] row = { media.title, media.name, media.year, media.filePath, "Delete" };
            tableModel.addRow(row);
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
                Object[] row = { media.title, media.name, media.year, media.filePath, "Delete" };
                tableModel.addRow(row);
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
                            saveMedia();
                            JOptionPane.showMessageDialog(MultimediaApp.this, "book is deleted successfully");
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
                    Media selectedMedia = null;
                    for (Media media : mediaFiles) {
                        if (media.filePath.equals(filePath)) {
                            selectedMedia = media;
                            break;
                        }
                    }
                    if (selectedMedia != null) {
                        if (!selectedMedia.isPaid) {
                            File file = new File(filePath);
                            if (file.exists()) {
                                try {
                                    Desktop.getDesktop().open(file);
                                } catch (IOException ex) {
                                    JFileChooser fileChooser = new JFileChooser();
                                    fileChooser.setSelectedFile(new File(file.getName()));
                                    if (fileChooser.showSaveDialog(MultimediaApp.this) == JFileChooser.APPROVE_OPTION) {
                                        try {
                                            Files.copy(file.toPath(), fileChooser.getSelectedFile().toPath(),
                                                    StandardCopyOption.REPLACE_EXISTING);
                                            JOptionPane.showMessageDialog(MultimediaApp.this,
                                                    " downloaded successfully");
                                        } catch (IOException ex2) {
                                            JOptionPane.showMessageDialog(MultimediaApp.this, "Error downloading file");
                                        }
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(MultimediaApp.this, "File not found");
                            }
                        } else {
                            if (isAdmin) {
                                File file = new File(filePath);
                                if (file.exists()) {
                                    try {
                                        Desktop.getDesktop().open(file);
                                    } catch (IOException ex) {
                                        JFileChooser fileChooser = new JFileChooser();
                                        fileChooser.setSelectedFile(new File(file.getName()));
                                        if (fileChooser
                                                .showSaveDialog(MultimediaApp.this) == JFileChooser.APPROVE_OPTION) {
                                            try {
                                                Files.copy(file.toPath(), fileChooser.getSelectedFile().toPath(),
                                                        StandardCopyOption.REPLACE_EXISTING);
                                                JOptionPane.showMessageDialog(MultimediaApp.this,
                                                        "downloaded successfully");
                                            } catch (IOException ex2) {
                                                JOptionPane.showMessageDialog(MultimediaApp.this,
                                                        "Error downloading file");
                                            }
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(MultimediaApp.this, "File not found");
                                }
                            } else {
                                boolean isApproved = false;
                                for (Request request : requests) {
                                    if (request.userEmail.equals(loggedInUser.email) &&
                                            request.mediaFilePath.equals(filePath) &&
                                            request.approved && request.active) {
                                        isApproved = true;
                                        break;
                                    }
                                }
                                if (isApproved) {
                                    File file = new File(filePath);
                                    if (file.exists()) {
                                        try {
                                            Desktop.getDesktop().open(file);
                                        } catch (IOException ex) {
                                            JFileChooser fileChooser = new JFileChooser();
                                            fileChooser.setSelectedFile(new File(file.getName()));
                                            if (fileChooser.showSaveDialog(
                                                    MultimediaApp.this) == JFileChooser.APPROVE_OPTION) {
                                                try {
                                                    Files.copy(file.toPath(), fileChooser.getSelectedFile().toPath(),
                                                            StandardCopyOption.REPLACE_EXISTING);
                                                    JOptionPane.showMessageDialog(MultimediaApp.this,
                                                            "downloaded successfully");
                                                } catch (IOException ex2) {
                                                    JOptionPane.showMessageDialog(MultimediaApp.this,
                                                            "Error downloading file");
                                                }
                                            }
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(MultimediaApp.this, "File not found");
                                    }
                                } else {
                                    showPaidMediaRequestDialog(filePath);
                                }
                            }
                        }
                    }
                }
            }
        });

        panel.setPreferredSize(new Dimension(500, 350));
        JOptionPane.showMessageDialog(this, panel, "book list", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRequestsTable() {
        String[] columnNames = { "Name", "User Email", "book list", "Status", "Action", "Delete" };
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
            Object[] row = { userName, request.userEmail, request.mediaFilePath, status, "", "Delete" };
            tableModel.addRow(row);
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
                            saveRequests();
                            JOptionPane.showMessageDialog(MultimediaApp.this, "Request deleted successfully");
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
                                if (fileChooser.showSaveDialog(MultimediaApp.this) == JFileChooser.APPROVE_OPTION) {
                                    try {
                                        Files.copy(requestFile.toPath(), fileChooser.getSelectedFile().toPath(),
                                                StandardCopyOption.REPLACE_EXISTING);
                                        JOptionPane.showMessageDialog(MultimediaApp.this,
                                                "Request file downloaded successfully");
                                    } catch (IOException ex2) {
                                        JOptionPane.showMessageDialog(MultimediaApp.this,
                                                "Error downloading request file");
                                    }
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(MultimediaApp.this, "Request file not found");
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Paid for book Requests", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPaidMediaRequestDialog(String filePath) {
        JDialog dialog = new JDialog(this, "Request Paid book Access", true);
        dialog.setLayout(new GridBagLayout());
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
        dialog.add(new JLabel("pay with account:"), gbc);
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
                saveRequests();
                JOptionPane.showMessageDialog(dialog, "Request submitted. Awaiting admin approval.");
                dialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Error uploading file");
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showClearDataDialog() {
        JDialog dialog = new JDialog(this, "Clear Data", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JComboBox<String> tableCombo = new JComboBox<>(new String[] { "Users", "Admins", "Requests" });
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        JButton clearButton = new JButton("Clear Selected Table");

        showPasswordCheckBox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*');
        });

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
            boolean validAdmin = false;
            for (User admin : admins) {
                if (admin.email.equals(email) && admin.password.equals(password) && admin.active) {
                    validAdmin = true;
                    break;
                }
            }
            if (!validAdmin) {
                JOptionPane.showMessageDialog(dialog, "Invalid admin credentials");
                return;
            }

            String selectedTable = (String) tableCombo.getSelectedItem();
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to clear all " + selectedTable + " data?",
                    "Confirm Clear", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                switch (selectedTable) {
                    case "Users":
                        users.clear();
                        saveUsers();
                        break;
                    case "Admins":
                        admins.clear();
                        initializeOwnerAdmin();
                        saveAdmins();
                        break;
                    case "Requests":
                        requests.clear();
                        saveRequests();
                        break;
                }
                JOptionPane.showMessageDialog(dialog, selectedTable + " data cleared successfully");
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAdminAuthDialog(String action, Runnable onSuccess) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");

        showPasswordCheckBox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*');
        });

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
            for (User admin : admins) {
                if (admin.email.equals(email) && admin.password.equals(password) && admin.active) {
                    onSuccess.run();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid admin credentials");
        }
    }

    private void initializeOwnerAdmin() {
        boolean ownerExists = false;
        for (User admin : admins) {
            if (admin.email.equals("mele@gmail.com") && admin.active) {
                ownerExists = true;
                break;
            }
        }
        if (!ownerExists) {
            admins.add(new User("Owner", "Admin", "0912345678", "mele@gmail.com", "MELES"));
            saveAdmins();
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
        for (User user : users) {
            if (user.email.equals(email) && user.active) {
                JOptionPane.showMessageDialog(this, "Email already registered");
                return false;
            }
        }
        for (User admin : admins) {
            if (admin.email.equals(email) && admin.active) {
                JOptionPane.showMessageDialog(this, "Email already registered");
                return false;
            }
        }
        return true;
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            users = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveAdmins() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("admins.dat"))) {
            oos.writeObject(admins);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAdmins() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("admins.dat"))) {
            admins = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            admins = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveMedia() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("media.dat"))) {
            oos.writeObject(mediaFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMedia() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("media.dat"))) {
            mediaFiles = (List<Media>) ois.readObject();
        } catch (FileNotFoundException e) {
            mediaFiles = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveRequests() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("requests.dat"))) {
            oos.writeObject(requests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRequests() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("requests.dat"))) {
            requests = (List<Request>) ois.readObject();
        } catch (FileNotFoundException e) {
            requests = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
            button.addActionListener(_ -> fireEditingStopped());
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
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
                    saveRequests();
                    JOptionPane.showMessageDialog(MultimediaApp.this,
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultimediaApp().setVisible(true));
    }
}