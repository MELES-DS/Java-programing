import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import AppS.User;

public class Storage extends Application {
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getUsername() {
            return name + " " + fname;
        }

        public String getType() {
            return active ? "user" : "inactive";
        }
    }
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);
    private final StringProperty activeSection = new SimpleStringProperty("home");
    private final StringProperty currentUserEmail = new SimpleStringProperty(null);
    private final StringProperty currentUserName = new SimpleStringProperty(null);
    private final StringProperty currentUserType = new SimpleStringProperty(null);
    private final IntegerProperty pendingRequestsCount = new SimpleIntegerProperty(0);
    private BorderPane root;
    private VBox mainContent;
    private final Storage storage = this;
    private final FileStorage fileStorage = new FileStorage();
    private List<User> users = new ArrayList<>();
    private List<User> admins = new ArrayList<>();
    private List<Media> mediaFiles = new ArrayList<>();
    private List<Media> paidMedia = new ArrayList<>();
    private List<Media> freeMedia = new ArrayList<>();
    private List<MediaRequest> requests = new ArrayList<>();

    public List<Storage.User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Storage.User> getAdmins() {
        return new ArrayList<>(admins);
    }

    public List<Storage.Media> getMediaFiles() {
        return new ArrayList<>(mediaFiles);
    }

    public List<Storage.Media> getPaidMedia() {
        return new ArrayList<>(paidMedia);
    }

    public List<Storage.Media> getFreeMedia() {
        return new ArrayList<>(freeMedia);
    }

    public List<Storage.MediaRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    public void loadUsers() {
        try {
            List<String> lines = Files.readAllLines(new File("Users.txt").toPath());
            users.clear();
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    User user = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAdmins() {
        try {
            List<String> lines = Files.readAllLines(new File("Admins.txt").toPath());
            admins.clear();
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    User admin = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    admins.add(admin);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMedia() {
        try {
            List<String> lines = Files.readAllLines(new File("Media.txt").toPath());
            mediaFiles.clear();
            paidMedia.clear();
            freeMedia.clear();
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Media media = new Media(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], Boolean.parseBoolean(parts[4]));
                    mediaFiles.add(media);
                    if (Boolean.parseBoolean(parts[4])) {
                        paidMedia.add(media);
                    } else {
                        freeMedia.add(media);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRequests() {
        try {
            List<String> lines = Files.readAllLines(new File("Requests.txt").toPath());
            requests.clear();
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    MediaRequest request = new MediaRequest(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3]));
                    requests.add(request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        try {
            List<String> lines = users.stream()
                .map(u -> String.format("%s,%s,%s,%s,%s", u.getName(), u.getFname(), u.getPhone(), u.getEmail(), u.getPassword()))
                .collect(Collectors.toList());
            Files.write(new File("Users.txt").toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAdmins() {
        try {
            List<String> lines = admins.stream()
                .map(a -> String.format("%s,%s,%s,%s,%s", a.getName(), a.getFname(), a.getPhone(), a.getEmail(), a.getPassword()))
                .collect(Collectors.toList());
            Files.write(new File("Admins.txt").toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMedia() {
        try {
            List<String> lines = mediaFiles.stream()
                .map(m -> String.format("%s,%s,%d,%s,%b", m.getTitle(), m.getAuthor(), m.getYear(), m.getFilePath(), m.isPaid()))
                .collect(Collectors.toList());
            Files.write(new File("Media.txt").toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRequests() {
        try {
            List<String> lines = requests.stream()
                .map(r -> String.format("%s,%s,%s,%b", r.getUserEmail(), r.getMediaFilePath(), r.getReason(), r.isApproved()))
                .collect(Collectors.toList());
            Files.write(new File("Requests.txt").toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeOwnerAdmin() {
        if (admins.isEmpty()) {
            User owner = new User("Owner", "Owner", "1234567890", "owner@example.com", "admin123");
            admins.add(owner);
            saveAdmins();
        }
    }

    // Definition for Book class (moved from method to here)
    public static class Book {
        private String title;
        private String author;
        private String year;
        private String accessType;
        private String fileName;
        private String fileType;

        public Book(String title, String author, String year, String accessType, String fileName, String fileType) {
            this.title = title;
            this.author = author;
            this.year = year;
            this.accessType = accessType;
            this.fileName = fileName;
            this.fileType = fileType;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getYear() {
            return year;
        }

        public String getAccessType() {
            return accessType;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }
    }

    // Definition for Request class
    public static class Request {
        private String userName;
        private String userEmail;
        private String bookTitle;
        private String bookId;
        private String status;
        private String screenshotFileName;

        public Request(String userName, String userEmail, String bookTitle, String bookId, String status, String screenshotFileName) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.bookTitle = bookTitle;
            this.bookId = bookId;
            this.status = status;
            this.screenshotFileName = screenshotFileName;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getBookId() {
            return bookId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getScreenshotFileName() {
            return screenshotFileName;
        }

        public void setScreenshotFileName(String screenshotFileName) {
            this.screenshotFileName = screenshotFileName;
        }
    }

    // Add this method to fix the error
    public List<Request> getRequests() throws IOException {
        // You should implement loading requests from your storage (e.g., file, database)
        // Here is a placeholder implementation that returns an empty list
        // Replace this with your actual loading logic
        return new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) {
        // Load preferences
        Preferences prefs = Preferences.userNodeForPackage(Storage.class);
        isDarkMode.set(prefs.getBoolean("darkMode", false));
        currentUserEmail.set(prefs.get("currentUserEmail", null));
        currentUserName.set(prefs.get("currentUserName", null));
        currentUserType.set(prefs.get("currentUserType", null));

        // Root layout
        root = new BorderPane();
        root.setStyle(isDarkMode.get() ? "-fx-background-color: #1f2937;" : "-fx-background-color: #f3f4f6;");

        // Header
        HBox header = createHeader();
        root.setTop(header);

        // Main content
        mainContent = new VBox();
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));
        root.setCenter(mainContent);

        // Listeners
        activeSection.addListener((obs, old, newSection) -> {
            updateContent();
            try {
                pendingRequestsCount.set(storage.getRequests().stream()
                        .filter(r -> r.getStatus().equals("Pending"))
                        .count());
            } catch (IOException e) {
                showError("Error loading requests");
            }
        });
        isDarkMode.addListener((obs, old, newValue) -> {
            root.setStyle(newValue ? "-fx-background-color: #1f2937;" : "-fx-background-color: #f3f4f6;");
            prefs.putBoolean("darkMode", newValue);
            updateContent();
        });

        // Initialize content
        updateContent();

        // Scene and stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("E-Library System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(isDarkMode.get() ? "-fx-background-color: #111827; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #1f2937; -fx-text-fill: #1f2937;");

        // Menu button
        Button menuButton = new Button("☰");
        menuButton.setStyle("-fx-font-size: 18px;");
        menuButton.setOnAction(e -> showNavMenu());

        // Title
        Label title = new Label("E-Library");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        // Navigation
        MenuBar navMenu = new MenuBar();
        Menu homeMenu = new Menu("Home");
        MenuItem homeItem = new MenuItem("Home");
        homeItem.setOnAction(e -> activeSection.set("home"));
        homeMenu.getItems().add(homeItem);

        Menu booksMenu = new Menu("Books");
        MenuItem booksItem = new MenuItem("Books");
        booksItem.setOnAction(e -> activeSection.set("books"));
        booksMenu.getItems().add(booksItem);

        Menu authMenu = new Menu("Auth");
        MenuItem registerItem = new MenuItem("Register");
        registerItem.setOnAction(e -> activeSection.set("register"));
        MenuItem loginItem = new MenuItem("Login");
        loginItem.setOnAction(e -> activeSection.set("login"));
        authMenu.getItems().addAll(registerItem, loginItem);
        navMenu.getMenus().addAll(homeMenu, booksMenu, authMenu);

        if (currentUserType.get() != null && currentUserType.get().equals("Admin")) {
            Menu adminMenu = new Menu("Admin Actions");
            String[] adminActions = { "View Books", "Upload Book", "Delete Book", "View Users", "View Requests",
                    "Update Account", "Delete Account", "Register Admin" };
            String[] adminSections = { "viewBooks", "upload", "deleteBook", "viewUsers", "requests",
                    "updateAccount", "deleteAccount", "registerAdmin" };
            for (int i = 0; i < adminActions.length; i++) {
                MenuItem item = new MenuItem(adminActions[i]);
                String section = adminSections[i];
                item.setOnAction(e -> activeSection.set(section));
                adminMenu.getItems().add(item);
            }
            navMenu.getMenus().add(adminMenu);
        }

        // Right-side controls
        HBox rightControls = new HBox(10);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        // Requests notification (Admin only)
        if (currentUserType.get() != null && currentUserType.get().equals("Admin")) {
            Button requestsButton = new Button("🔔");
            requestsButton.setStyle("-fx-font-size: 18px;");
            requestsButton.setOnAction(e -> activeSection.set("requests"));
            Label badge = new Label();
            badge.setStyle(
                    "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb; -fx-font-size: 12px; -fx-padding: 2px 4px; -fx-border-radius: 10px;");
            pendingRequestsCount.addListener((obs, old, newValue) -> {
                badge.setText(newValue.intValue() > 0 ? newValue.toString() : "");
                badge.setVisible(newValue.intValue() > 0);
            });
            StackPane notification = new StackPane(requestsButton, badge);
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            rightControls.getChildren().add(notification);
        }

        // Dark mode toggle
        Button darkModeToggle = new Button(isDarkMode.get() ? "☀️" : "🌙");
        darkModeToggle.setStyle("-fx-font-size: 18px;");
        darkModeToggle.setOnAction(e -> isDarkMode.set(!isDarkMode.get()));

        // User info and logout
        if (currentUserEmail.get() != null) {
            Label userLabel = new Label("Welcome, " + currentUserName.get());
            userLabel.setStyle("-fx-font-size: 14px;");
            Button logoutButton = new Button("Logout");
            logoutButton.setStyle(isDarkMode.get()
                    ? "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb; -fx-padding: 5px 10px; -fx-border-radius: 5px;"
                    : "-fx-background-color: #ef4444; -fx-text-fill: #ffffff; -fx-padding: 5px 10px; -fx-border-radius: 5px;");
            logoutButton.setOnAction(e -> logout());
            rightControls.getChildren().addAll(userLabel, logoutButton);
        }

        rightControls.getChildren().add(darkModeToggle);
        header.getChildren().addAll(menuButton, title, navMenu, rightControls);
        HBox.setHgrow(rightControls, Priority.ALWAYS);
        return header;
    }

    private void showNavMenu() {
        Stage navStage = new Stage();
        navStage.initModality(Modality.APPLICATION_MODAL);
        VBox navContent = new VBox(10);
        navContent.setPadding(new Insets(20));
        navContent.setStyle(isDarkMode.get() ? "-fx-background-color: #374151;" : "-fx-background-color: #ffffff;");

        String[] navItems = { "Home", "Books", "Register", "Login" };
        String[] navSections = { "home", "books", "register", "login" };
        for (int i = 0; i < navItems.length; i++) {
            Button btn = new Button(navItems[i]);
            String section = navSections[i];
            btn.setOnAction(e -> {
                activeSection.set(section);
                navStage.close();
            });
            navContent.getChildren().add(btn);
        }

        if (currentUserType.get() != null && currentUserType.get().equals("Admin")) {
            String[] adminActions = { "View Books", "Upload Book", "Delete Book", "View Users", "View Requests",
                    "Update Account", "Delete Account", "Register Admin" };
            String[] adminSections = { "viewBooks", "upload", "deleteBook", "viewUsers", "requests",
                    "updateAccount", "deleteAccount", "registerAdmin" };
            for (int i = 0; i < adminActions.length; i++) {
                Button btn = new Button(adminActions[i]);
                String section = adminSections[i];
                btn.setOnAction(e -> {
                    activeSection.set(section);
                    navStage.close();
                });
                navContent.getChildren().add(btn);
            }
        }

        Scene navScene = new Scene(navContent, 200, 400);
        navStage.setScene(navScene);
        navStage.show();
    }

    private void updateContent() {
        mainContent.getChildren().clear();
        switch (activeSection.get()) {
            case "home":
                mainContent.getChildren().add(createHomePane());
                break;
            case "login":
                mainContent.getChildren().add(createLoginPane());
                break;
            case "register":
                mainContent.getChildren().add(createRegisterPane());
                break;
            case "books":
                mainContent.getChildren().add(createBookListPane());
                break;
            case "upload":
                mainContent.getChildren().add(createUploadBookPane());
                break;
            case "viewBooks":
                mainContent.getChildren().add(createBookListAdminPane());
                break;
            case "viewUsers":
                mainContent.getChildren().add(createUserListPane());
                break;
            case "requests":
                mainContent.getChildren().add(createRequestListPane());
                break;
            case "updateAccount":
                mainContent.getChildren().add(createUpdateAccountPane());
                break;
            case "deleteAccount":
                mainContent.getChildren().add(createDeleteAccountPane());
                break;
            case "deleteBook":
                mainContent.getChildren().add(createDeleteBookPane());
                break;
            case "registerAdmin":
                mainContent.getChildren().add(createRegisterAdminPane());
                break;
            default:
                mainContent.getChildren().add(new Label("Section not implemented"));
        }
    }

    private VBox createHomePane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        Label title = new Label("Welcome to the E-Library");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");
        Label subtitle = new Label("Explore our collection of books and resources!");
        pane.getChildren().addAll(title, subtitle);
        return pane;
    }

    private VBox createLoginPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Login");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        showPassword.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        loginButton.setOnAction(e -> login(
                emailField.getText(),
                showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText(),
                errorLabel));

        pane.getChildren().addAll(title, errorLabel, emailField, passwordField, visiblePasswordField, showPassword,
                loginButton);
        return pane;
    }

    private VBox createRegisterPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Register (User)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        showPassword.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
            }
        });

        Button registerButton = new Button("Sign Up");
        registerButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        registerButton.setOnAction(e -> register(
                usernameField.getText(),
                emailField.getText(),
                showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText(),
                "User",
                errorLabel));

        pane.getChildren().addAll(title, errorLabel, usernameField, emailField, passwordField, visiblePasswordField,
                showPassword, registerButton);
        return pane;
    }

    private VBox createRegisterAdminPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Register (Admin)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        showPassword.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
            }
        });

        Button registerButton = new Button("Sign Up Admin");
        registerButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        registerButton.setOnAction(e -> showAdminVerificationModal("Register Admin", (adminEmail, adminPassword) -> {
            register(
                    usernameField.getText(),
                    emailField.getText(),
                    showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText(),
                    "Admin",
                    errorLabel);
        }, errorLabel));

        pane.getChildren().addAll(title, errorLabel, usernameField, emailField, passwordField, visiblePasswordField,
                showPassword, registerButton);
        return pane;
    }

    private VBox createUploadBookPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Upload Book (Admin Only)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        // Only allow admins to upload books
        if (currentUserType.get() == null || !currentUserType.get().equals("Admin")) {
            Label noAccess = new Label("You do not have permission to upload books.");
            noAccess.setStyle("-fx-text-fill: #ef4444;");
            pane.getChildren().addAll(title, noAccess);
            return pane;
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        ComboBox<String> accessType = new ComboBox<>();
        accessType.getItems().addAll("Free", "Paid");
        accessType.setValue("Free");
        accessType.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Button fileButton = new Button("Choose File");
        Label fileLabel = new Label("No file chosen");
        File[] selectedFile = { null };
        fileButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Allowed Files", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx",
                            "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp3", "*.wav", "*.mp4", "*.mpeg"));
            File file = chooser.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText(file.getName());
            }
        });

        Button uploadButton = new Button("Upload");
        uploadButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        uploadButton.setOnAction(e -> uploadBook(
                titleField.getText(),
                authorField.getText(),
                yearField.getText(),
                accessType.getValue(),
                selectedFile[0],
                errorLabel));

        pane.getChildren().addAll(title, errorLabel, titleField, authorField, yearField, accessType, fileButton, fileLabel, uploadButton);
        return pane;
    }

    private VBox createBookListAdminPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Book List (Admin Only)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField searchQuery = new TextField();
        searchQuery.setPromptText("Search by title");

        HBox filters = new HBox(10, searchQuery);
        filters.setAlignment(Pos.CENTER);

        TableView<Book> table = new TableView<>();
        table.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        TableColumn<Book, Integer> noCol = new TableColumn<>("No.");
        noCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(table.getItems().indexOf(cellData.getValue()) + 1).asObject());
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Book, String> accessTypeCol = new TableColumn<>("Access Type");
        accessTypeCol.setCellValueFactory(new PropertyValueFactory<>("accessType"));
        TableColumn<Book, String> fileTypeCol = new TableColumn<>("File Type");
        fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        TableColumn<Book, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    Button read = new Button("Read");
                    read.setOnAction(e -> readBook(book.getFileName(), errorLabel));
                    Button download = new Button("Download");
                    download.setOnAction(e -> downloadBook(book.getFileName(), errorLabel));
                    buttons.getChildren().addAll(read, download);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(noCol, titleCol, authorCol, yearCol, accessTypeCol, fileTypeCol, actionCol);

        Runnable updateTable = () -> {
            try {
                List<Book> books = storage.getBooks().stream()
                        .filter(book -> searchQuery.getText().isEmpty() ||
                                book.getTitle().toLowerCase().contains(searchQuery.getText().toLowerCase()))
                        .collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(books));
            } catch (IOException e) {
                errorLabel.setText("Error loading books");
            }
        };

        searchQuery.textProperty().addListener((obs, old, newValue) -> updateTable.run());
        updateTable.run();

        pane.getChildren().addAll(title, errorLabel, filters, table);
        return pane;
    }

    private VBox createUserListPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("User List (Admin Only)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TableView<User> table = new TableView<>();
        table.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        TableColumn<User, Integer> noCol = new TableColumn<>("No.");
        noCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(table.getItems().indexOf(cellData.getValue()) + 1).asObject());
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    Button delete = new Button("Delete");
                    delete.setStyle(isDarkMode.get() ? "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb;"
                            : "-fx-background-color: #ef4444; -fx-text-fill: #ffffff;");
                    delete.setOnAction(e -> showAdminVerificationModal("Delete User", (adminEmail, adminPassword) -> {
                        try {
                            List<User> users = storage.getUsers();
                            List<User> admins = storage.getAdmins();
                            if (user.getType().equals("User")) {
                                users.remove(user);
                                storage.saveUsers(users);
                            } else {
                                admins.remove(user);
                                storage.saveAdmins(admins);
                            }
                            updateContent();
                        } catch (IOException ex) {
                            errorLabel.setText("Error deleting user");
                        }
                    }, errorLabel));
                    buttons.getChildren().add(delete);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(noCol, usernameCol, emailCol, typeCol, actionCol);

        try {
            List<User> allUsers = new ArrayList<>();
            allUsers.addAll(storage.getUsers());
            allUsers.addAll(storage.getAdmins());
            table.setItems(FXCollections.observableArrayList(allUsers));
        } catch (IOException e) {
            errorLabel.setText("Error loading users");
        }

        pane.getChildren().addAll(title, errorLabel, table);
        return pane;
    }

    private VBox createRequestListPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Payment Requests (Admin Only)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TableView<Request> table = new TableView<>();
        table.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        TableColumn<Request, Integer> noCol = new TableColumn<>("No.");
        noCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(table.getItems().indexOf(cellData.getValue()) + 1).asObject());
        TableColumn<Request, String> userNameCol = new TableColumn<>("User");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        TableColumn<Request, String> userEmailCol = new TableColumn<>("Email");
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        TableColumn<Request, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Request, Void> screenshotCol = new TableColumn<>("Screenshot");
        screenshotCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request request = getTableView().getItems().get(getIndex());
                    if (request.getScreenshotFileName() != null) {
                        Button view = new Button("View");
                        view.setOnAction(e -> viewScreenshot(request.getScreenshotFileName(), errorLabel));
                        setGraphic(view);
                    } else {
                        setGraphic(new Label("No screenshot"));
                    }
                }
            }
        });
        TableColumn<Request, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request request = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    if (request.getStatus().equals("Pending")) {
                        Button approve = new Button("Approve");
                        approve.setStyle(isDarkMode.get() ? "-fx-background-color: #15803d; -fx-text-fill: #f9fafb;"
                                : "-fx-background-color: #22c55e; -fx-text-fill: #ffffff;");
                        approve.setOnAction(
                                e -> showAdminVerificationModal("Approve Request", (adminEmail, adminPassword) -> {
                                    try {
                                        List<Request> requests = storage.getRequests();
                                        requests.get(getIndex()).setStatus("Approved");
                                        storage.saveRequests(requests);
                                        updateContent();
                                    } catch (IOException ex) {
                                        errorLabel.setText("Error approving request");
                                    }
                                }, errorLabel));
                        Button reject = new Button("Reject");
                        reject.setStyle(isDarkMode.get() ? "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb;"
                                : "-fx-background-color: #ef4444; -fx-text-fill: #ffffff;");
                        reject.setOnAction(
                                e -> showAdminVerificationModal("Reject Request", (adminEmail, adminPassword) -> {
                                    try {
                                        List<Request> requests = storage.getRequests();
                                        requests.get(getIndex()).setStatus("Rejected");
                                        storage.saveRequests(requests);
                                        updateContent();
                                    } catch (IOException ex) {
                                        errorLabel.setText("Error rejecting request");
                                    }
                                }, errorLabel));
                        buttons.getChildren().addAll(approve, reject);
                    }
                    Button delete = new Button("Delete");
                    delete.setStyle(isDarkMode.get() ? "-fx-background-color: #4b5563; -fx-text-fill: #f9fafb;"
                            : "-fx-background-color: #6b7280; -fx-text-fill: #ffffff;");
                    delete.setOnAction(
                            e -> showAdminVerificationModal("Delete Request", (adminEmail, adminPassword) -> {
                                try {
                                    List<Request> requests = storage.getRequests();
                                    Request req = requests.remove(getIndex());
                                    if (req.getScreenshotFileName() != null) {
                                        fileStorage.deleteFile(req.getScreenshotFileName());
                                    }
                                    storage.saveRequests(requests);
                                    updateContent();
                                } catch (IOException ex) {
                                    errorLabel.setText("Error deleting request");
                                }
                            }, errorLabel));
                    buttons.getChildren().add(delete);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(noCol, userNameCol, userEmailCol, bookTitleCol, statusCol, screenshotCol, actionCol);

        try {
            table.setItems(FXCollections.observableArrayList(storage.getRequests()));
        } catch (IOException e) {
            errorLabel.setText("Error loading requests");
        }

        pane.getChildren().addAll(title, errorLabel, table);
        return pane;
    }

    private VBox createUpdateAccountPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Update Account");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField emailField = new TextField();
        emailField.setPromptText("New Email (optional)");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (optional)");
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        showPassword.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
            }
        });

        Button updateButton = new Button("Update");
        updateButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        updateButton.setOnAction(e -> showAdminVerificationModal("Update Account", (adminEmail, adminPassword) -> {
            updateAccount(
                    emailField.getText(),
                    showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText(),
                    errorLabel);
        }, errorLabel));

        pane.getChildren().addAll(title, errorLabel, emailField, passwordField, visiblePasswordField, showPassword,
                updateButton);
        return pane;
    }

    private VBox createDeleteAccountPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(300);
        pane.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        Label title = new Label("Delete Account");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setStyle(isDarkMode.get() ? "-fx-text-fill: #f9fafb;" : "-fx-text-fill: #1f2937;");
        showPassword.selectedProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setManaged(true);
                visiblePasswordField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                visiblePasswordField.setManaged(false);
                visiblePasswordField.setVisible(false);
            }
        });

        Button deleteButton = new Button("Delete Account");
        deleteButton.setStyle(isDarkMode.get()
                ? "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb; -fx-padding: 8px 16px; -fx-border-radius: 5px;"
                : "-fx-background-color: #ef4444; -fx-text-fill: #ffffff; -fx-padding: 8px 16px; -fx-border-radius: 5px;");
        deleteButton.setOnAction(e -> showAdminVerificationModal("Delete Account", (adminEmail, adminPassword) -> {
            deleteAccount(
                    emailField.getText(),
                    showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText(),
                    errorLabel);
        }, errorLabel));

        pane.getChildren().addAll(title, errorLabel, emailField, passwordField, visiblePasswordField, showPassword,
                deleteButton);
        return pane;
    }

    private VBox createDeleteBookPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Delete Book (Admin Only)");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TableView<Book> table = new TableView<>();
        table.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        TableColumn<Book, Integer> noCol = new TableColumn<>("No.");
        noCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(table.getItems().indexOf(cellData.getValue()) + 1).asObject());
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Book, String> accessTypeCol = new TableColumn<>("Access Type");
        accessTypeCol.setCellValueFactory(new PropertyValueFactory<>("accessType"));
        TableColumn<Book, String> fileTypeCol = new TableColumn<>("File Type");
        fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        TableColumn<Book, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    Button delete = new Button("Delete");
                    delete.setStyle(isDarkMode.get() ? "-fx-background-color: #b91c1c; -fx-text-fill: #f9fafb;"
                            : "-fx-background-color: #ef4444; -fx-text-fill: #ffffff;");
                    delete.setOnAction(e -> showAdminVerificationModal("Delete Book", (adminEmail, adminPassword) -> {
                        try {
                            List<Book> books = storage.getBooks();
                            books.remove(book);
                            storage.saveBooks(books);
                            fileStorage.deleteFile(book.getFileName());
                            List<Request> requests = storage.getRequests();
                            requests.removeIf(r -> r.getBookId().equals(book.getFileName()));
                            storage.saveRequests(requests);
                            updateContent();
                        } catch (IOException ex) {
                            errorLabel.setText("Error deleting book");
                        }
                    }, errorLabel));
                    setGraphic(delete);
                }
            }
        });

        table.getColumns().addAll(noCol, titleCol, authorCol, yearCol, accessTypeCol, fileTypeCol, actionCol);

        try {
            List<Book> books = storage.getBooks();
            table.setItems(FXCollections.observableArrayList(books));
        } catch (IOException e) {
            errorLabel.setText("Error loading books");
        }

        pane.getChildren().addAll(title, errorLabel, table);
        return pane;
    }

    private void register(String username, String email, String password, String type, Label errorLabel) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }
        if (!isValidEmail(email)) {
            errorLabel.setText("Invalid email format");
            return;
        }
        try {
            List<User> users = storage.getUsers();
            List<User> admins = storage.getAdmins();
            if (users.stream().anyMatch(u -> u.getEmail().equals(email) || u.getUsername().equals(username)) ||
                    admins.stream().anyMatch(a -> a.getEmail().equals(email) || a.getUsername().equals(username))) {
                errorLabel.setText("Username or email already registered");
                return;
            }
            User user = new User(username, email, password, type);
            if (type.equals("User")) {
                users.add(user);
                storage.saveUsers(users);
            } else {
                admins.add(user);
                storage.saveAdmins(admins);
            }
            activeSection.set("login");
        } catch (IOException e) {
            errorLabel.setText("Error saving user");
        }
    }

    private void uploadBook(String title, String author, String year, String accessType, File file, Label errorLabel) {
        if (title.isEmpty() || author.isEmpty() || year.isEmpty() || file == null) {
            errorLabel.setText("All fields and file are required");
            return;
        }
        try {
            int yearNum = Integer.parseInt(year);
            if (yearNum < 1000 || yearNum > java.time.Year.now().getValue()) {
                errorLabel.setText("Invalid year");
                return;
            }
            String fileType = Files.probeContentType(file.toPath());
            if (!FileStorage.ALLOWED_FILE_TYPES.contains(fileType)) {
                errorLabel.setText("Invalid file type");
                return;
            }
            fileStorage.saveFile(file, "files");
            List<Book> books = storage.getBooks();
            books.add(new Book(title, author, year, accessType, file.getName(), fileType));
            storage.saveBooks(books);
            activeSection.set("home");
        } catch (Exception e) {
            errorLabel.setText("Error uploading book: " + e.getMessage());
        }
    }

    private void readBook(String fileName, Label errorLabel) {
        try {
            File file = fileStorage.getFile(fileName, "files");
            if (file == null) {
                errorLabel.setText("File not found");
                return;
            }
            java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) {
            errorLabel.setText("Error opening file");
        }
    }

    private void downloadBook(String fileName, Label errorLabel) {
        try {
            File file = fileStorage.getFile(fileName, "files");
            if (file == null) {
                errorLabel.setText("File not found");
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(fileName);
            File dest = chooser.showSaveDialog(root.getScene().getWindow());
            if (dest != null) {
                Files.copy(file.toPath(), dest.toPath());
            }
        } catch (IOException e) {
            errorLabel.setText("Error downloading file");
        }
    }

    private void updateAccount(String newEmail, String newPassword, Label errorLabel) {
        if (newEmail.isEmpty() && newPassword.isEmpty()) {
            errorLabel.setText("At least one field must be provided");
            return;
        }
        if (!newEmail.isEmpty() && !isValidEmail(newEmail)) {
            errorLabel.setText("Invalid email format");
            return;
        }
        try {
            List<User> users = storage.getUsers();
            List<User> admins = storage.getAdmins();
            User account = users.stream()
                    .filter(u -> u.getEmail().equals(currentUserEmail.get()))
                    .findFirst()
                    .orElse(admins.stream()
                            .filter(a -> a.getEmail().equals(currentUserEmail.get()))
                            .findFirst()
                            .orElse(null));
            if (account == null) {
                errorLabel.setText("Account not found");
                return;
            }
            if (!newEmail.isEmpty()) {
                account.setEmail(newEmail);
                currentUserEmail.set(newEmail);
            }
            if (!newPassword.isEmpty()) {
                account.setPassword(newPassword);
            }
            if (account.getType().equals("User")) {
                storage.saveUsers(users);
            } else {
                storage.saveAdmins(admins);
            }
            errorLabel.setText("Account updated successfully");
        } catch (IOException e) {
            errorLabel.setText("Error updating account");
        }
    }

    private void showPaymentModal(Book book, Label errorLabel) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Payment for " + book.getTitle());
        title.setFont(new Font("Arial", 16));
        Label code = new Label("Payment Code: 1000457005068");
        Button fileButton = new Button("Choose Screenshot");
        Label fileLabel = new Label("No file chosen");
        File[] selectedFile = { null };
        fileButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"));
            File file = chooser.showOpenDialog(modal);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText(file.getName());
            }
        });

        Button sendButton = new Button("Send");
        sendButton.setStyle(isDarkMode.get() ? "-fx-background-color: #a16207; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #f59e0b; -fx-text-fill: #ffffff;");
        sendButton.setOnAction(e -> {
            if (selectedFile[0] == null) {
                errorLabel.setText("Please upload a payment screenshot");
                return;
            }
            try {
                fileStorage.saveFile(selectedFile[0], "screenshots");
                List<Request> requests = storage.getRequests();
                requests.add(new Request(
                        currentUserName.get(),
                        currentUserEmail.get(),
                        book.getTitle(),
                        book.getFileName(),
                        "Pending",
                        selectedFile[0].getName()));
                storage.saveRequests(requests);
                modal.close();
                activeSection.set("home");
            } catch (IOException ex) {
                errorLabel.setText("Error sending request");
            }
        });

        content.getChildren().addAll(title, code, fileButton, fileLabel, sendButton, errorLabel);
        modal.setScene(new Scene(content, 350, 250));
        modal.showAndWait();
    }

    private void showAdminVerificationModal(String action, AdminAction callback, Label errorLabel) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Admin Verification: " + action);
        title.setFont(new Font("Arial", 16));
        TextField emailField = new TextField();
        emailField.setPromptText("Admin Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Admin Password");
        Label error = new Label();
        error.setStyle("-fx-text-fill: #ef4444;");

        Button verifyButton = new Button("Verify");
        verifyButton.setStyle(isDarkMode.get() ? "-fx-background-color: #1e40af; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #3b82f6; -fx-text-fill: #ffffff;");
        verifyButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            try {
                List<User> admins = storage.getAdmins();
                boolean valid = admins.stream()
                        .anyMatch(a -> a.getEmail().equals(email) && a.getPassword().equals(password));
                if (!valid) {
                    error.setText("Invalid admin credentials");
                    return;
                }
                callback.execute(email, password);
                modal.close();
            } catch (IOException ex) {
                error.setText("Error verifying admin");
            }
        });

        content.getChildren().addAll(title, emailField, passwordField, error, verifyButton);
        modal.setScene(new Scene(content, 300, 200));
        modal.showAndWait();
    }

    private boolean hasPaidForBook(Book book) {
        try {
            List<Request> requests = storage.getRequests();
            return requests.stream().anyMatch(r -> r.getUserEmail().equals(currentUserEmail.get()) &&
                    r.getBookId().equals(book.getFileName()) &&
                    r.getStatus().equals("Approved"));
        } catch (IOException e) {
            return false;
        }
    }

    private void logout() {
        Preferences prefs = Preferences.userNodeForPackage(Storage.class);
        currentUserEmail.set(null);
        currentUserName.set(null);
        currentUserType.set(null);
        prefs.remove("currentUserEmail");
        prefs.remove("currentUserName");
        prefs.remove("currentUserType");
        activeSection.set("login");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // Dummy FileStorage class
    public static class FileStorage {
        public static final List<String> ALLOWED_FILE_TYPES = List.of(
                "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "image/jpeg", "image/png", "image/gif", "audio/mpeg", "audio/wav", "video/mp4", "video/mpeg"
        );

        public void saveFile(File file, String folder) throws IOException {
            // Dummy: do nothing
        }

        public File getFile(String fileName, String folder) {
            // Dummy: return null
            return null;
        }

        public void deleteFile(String fileName) {
            // Dummy: do nothing
        }
    }

    // Dummy storage methods
    public List<Book> getBooks() throws IOException {
        return new ArrayList<>();
    }

    public void saveBooks(List<Book> books) throws IOException {
        // Dummy: do nothing
    }

    public List<User> getUsers() throws IOException {
        return new ArrayList<>();
    }

    public void saveUsers(List<User> users) throws IOException {
        // Dummy: do nothing
    }

    public List<User> getAdmins() throws IOException {
        return new ArrayList<>();
    }

    public void saveAdmins(List<User> admins) throws IOException {
        // Dummy: do nothing
    }

    public void saveRequests(List<Request> requests) throws IOException {
        // Dummy: do nothing
    }

    private VBox createBookListPane() {
        VBox pane = new VBox(10);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb;"
                : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937;");

        Label title = new Label("Book List");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        TextField searchQuery = new TextField();
        searchQuery.setPromptText("Search by title");

        HBox filters = new HBox(10, searchQuery);
        filters.setAlignment(Pos.CENTER);

        TableView<Book> table = new TableView<>();
        table.setStyle(
                isDarkMode.get() ? "-fx-background-color: #374151; -fx-text-fill: #f9fafb; -fx-border-color: #4b5563;"
                        : "-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;");

        TableColumn<Book, Integer> noCol = new TableColumn<>("No.");
        noCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(table.getItems().indexOf(cellData.getValue()) + 1).asObject());
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Book, String> accessTypeCol = new TableColumn<>("Access Type");
        accessTypeCol.setCellValueFactory(new PropertyValueFactory<>("accessType"));
        TableColumn<Book, String> fileTypeCol = new TableColumn<>("File Type");
        fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        TableColumn<Book, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    Button read = new Button("Read");
                    read.setOnAction(e -> readBook(book.getFileName(), errorLabel));
                    if ("Paid".equals(book.getAccessType())) {
                        Button pay = new Button("Pay");
                        pay.setOnAction(e -> showPaymentModal(book, errorLabel));
                        buttons.getChildren().addAll(read, pay);
                    } else {
                        buttons.getChildren().add(read);
                    }
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(noCol, titleCol, authorCol, yearCol, accessTypeCol, fileTypeCol, actionCol);

        Runnable updateTable = () -> {
            try {
                List<Book> books = storage.getBooks().stream()
                        .filter(book -> searchQuery.getText().isEmpty() ||
                                book.getTitle().toLowerCase().contains(searchQuery.getText().toLowerCase()))
                        .collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(books));
            } catch (IOException e) {
                errorLabel.setText("Error loading books");
            }
        };

        searchQuery.textProperty().addListener((obs, old, newValue) -> updateTable.run());
        updateTable.run();

        pane.getChildren().addAll(title, errorLabel, filters, table);
        return pane;
    }

    private void showError(String message) {
        final Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    interface AdminAction {
        void execute(String email, String password) throws IOException;
    }

    public static void main(String[] args) {
        launch(args);
    }
}