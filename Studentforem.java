import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.text.Text;

public class Studentforem extends Application {

    private ObservableList<Student> registrations = FXCollections.observableArrayList();

    // Custom exception for validation errors
    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class Student {
        private final String id;
        private final String fullName;
        private final int age;
        private final String phone;
        private final String email;
        private final String password;
        private final String course;

        public Student(String id, String fullName, int age, String phone, String email, String password,
                String course) {
            this.id = id;
            this.fullName = fullName;
            this.age = age;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.course = course;
        }

        public String getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public int getAge() {
            return age;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getCourse() {
            return course;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Main layout: VBox
        VBox mainLayout = new VBox(80);
        mainLayout.setPadding(new Insets(50));
        mainLayout.setAlignment(Pos.CENTER);


        VBox formSection = new VBox(10);
        formSection.setAlignment(Pos.CENTER);
        formSection.setPadding(new Insets(10));
        formSection.setStyle("-fx-border-color:blue; -fx-border-width: 1; -fx-background-color: wheate;");

        Text title = new Text("Student Registration Form");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // GridPane for form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Form fields with HBox for each row
        TextField idField = new TextField();
        idField.setPromptText("ID (e.g., DBU123)");
        HBox idRow = new HBox(5, new Label("ID:"), idField);
        idRow.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = new TextField();
        nameField.setPromptText("First Name");
        HBox nameRow = new HBox(5, new Label("Name:"), nameField);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        TextField fnameField = new TextField();
        fnameField.setPromptText("Father Name");
        HBox fnameRow = new HBox(5, new Label("Father Name:"), fnameField);
        fnameRow.setAlignment(Pos.CENTER_LEFT);

        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        HBox ageRow = new HBox(5, new Label("Age:"), ageField);
        ageRow.setAlignment(Pos.CENTER_LEFT);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (e.g., 09xxxxxxx)");
        HBox phoneRow = new HBox(5, new Label("Phone:"), phoneField);
        phoneRow.setAlignment(Pos.CENTER_LEFT);

        TextField emailField = new TextField();
        emailField.setPromptText("Email (@gmail.com)");
        HBox emailRow = new HBox(5, new Label("Email:"), emailField);
        emailRow.setAlignment(Pos.CENTER_LEFT);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("Password");
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setOnAction(e -> {
            if (showPassword.isSelected()) {
                passwordTextField.setText(passwordField.getText());
                passwordTextField.setManaged(true);
                passwordTextField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(passwordTextField.getText());
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                passwordTextField.setManaged(false);
                passwordTextField.setVisible(false);
            }
        });

        HBox passwordRow = new HBox(5, new Label("Password:"), passwordField, passwordTextField);
        passwordRow.setAlignment(Pos.CENTER_LEFT);
        VBox passwordSection = new VBox(5, passwordRow, showPassword);
        passwordSection.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(
                "Java Data Science", "Data Visualization", "Machine Learning",
                "AI", "DBMS", "Python", "Algorithm", "Statistics");
        courseCombo.setPromptText("Select a Course");
        HBox courseRow = new HBox(5, new Label("Course:"), courseCombo);
        courseRow.setAlignment(Pos.CENTER_LEFT);

        // Add rows to GridPane
        grid.add(idRow, 0, 0);
        grid.add(nameRow, 0, 1);
        grid.add(fnameRow, 0, 2);
        grid.add(ageRow, 0, 3);
        grid.add(phoneRow, 0, 4);
        grid.add(emailRow, 0, 5);
        grid.add(passwordSection, 0, 6);
        grid.add(courseRow, 0, 7);

        formSection.getChildren().addAll(title, grid);

        // Button and message section: VBox
        VBox actionSection = new VBox(10);
        actionSection.setAlignment(Pos.CENTER);
        actionSection.setPadding(new Insets(10));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Submit");
        Button clearBtn = new Button("Clear");
        Button showAllBtn = new Button("Show All");
        buttonBox.getChildren().addAll(submitBtn, clearBtn, showAllBtn);

        TextArea messages = new TextArea();
        messages.setEditable(false);
        messages.setPrefHeight(100);
        messages.setPromptText("Messages will appear here");

        actionSection.getChildren().addAll(buttonBox, messages);

        // Add sections to main layout
        mainLayout.getChildren().addAll(formSection, actionSection);

        // Submit button event with try-catch validation
        submitBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String fname = fnameField.getText().trim();
            String age = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = showPassword.isSelected() ? passwordTextField.getText().trim()
                    : passwordField.getText().trim();
            String course = courseCombo.getValue();

            StringBuilder errors = new StringBuilder();
            int ageInt = 0;

            try {
                // Empty input validations
                if (id.isEmpty())
                    throw new ValidationException("ID is required.");
                if (name.isEmpty())
                    throw new ValidationException("Name is required.");
                if (fname.isEmpty())
                    throw new ValidationException("Father Name is required.");
                if (age.isEmpty())
                    throw new ValidationException("Age is required.");
                if (phone.isEmpty())
                    throw new ValidationException("Phone is required.");
                if (email.isEmpty())
                    throw new ValidationException("Email is required.");
                if (password.isEmpty())
                    throw new ValidationException("Password is required.");
                if (course == null)
                    throw new ValidationException("Course is required.");

                // Additional validations
                if (!id.startsWith("dbu"))
                    throw new ValidationException("ID must start with 'dbu'.");
                if (!name.matches("[A-Za-z]+"))
                    throw new ValidationException("Name must be alphabetic.");
                if (!fname.matches("[A-Za-z]+"))
                    throw new ValidationException("Father Name must be alphabetic.");

                try {
                    ageInt = Integer.parseInt(age);
                    if (ageInt < 16 || ageInt > 40)
                        throw new ValidationException("Age must be between 16 and 40.");
                } catch (NumberFormatException ex) {
                    throw new ValidationException("Age must be numeric.");
                }

                if (!phone.matches("09\\d{8}"))
                    throw new ValidationException("Phone must start with '09' and be 10 digits.");
                if (!email.endsWith("@gmail.com"))
                    throw new ValidationException("Email must end with '@gmail.com'.");
                if (registrations.stream().anyMatch(student -> student.getId().equals(id))) {
                    throw new ValidationException("ID already exists.");
                }

                // If all validations pass, register the student
                String fullName = name + " " + fname;
                registrations.add(new Student(id, fullName, ageInt, phone, email, password, course));

                // Success message
                messages.setText("Registration Successful!\nID: " + id + "\nFull Name: " + fullName);

            } catch (ValidationException ex) {
                errors.append(ex.getMessage()).append("\n");
            } finally {
                // Display errors in TextArea if any
                if (errors.length() > 0) {
                    messages.setText("Errors:\n" + errors.toString());
                }
            }
        });

        // Clear button
        clearBtn.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            fnameField.clear();
            ageField.clear();
            phoneField.clear();
            emailField.clear();
            passwordField.clear();
            passwordTextField.clear();
            courseCombo.setValue(null);
            showPassword.setSelected(false);
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordTextField.setManaged(false);
            passwordTextField.setVisible(false);
            messages.clear();
        });

        // Show all button
        showAllBtn.setOnAction(e -> {
            Stage tableStage = new Stage();
            tableStage.initModality(Modality.APPLICATION_MODAL);
            tableStage.setTitle("Registered Students");

            TableView<Student> table = new TableView<>();
            table.setItems(registrations);

            // Row number column
            TableColumn<Student, Number> noCol = new TableColumn<>("No");
            noCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(
                    table.getItems().indexOf(cellData.getValue()) + 1));
            noCol.setMinWidth(50);

            TableColumn<Student, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setMinWidth(100);

            TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            nameCol.setMinWidth(150);

            TableColumn<Student, Number> ageCol = new TableColumn<>("Age");
            ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
            ageCol.setMinWidth(80);

            TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            phoneCol.setMinWidth(120);

            TableColumn<Student, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailCol.setMinWidth(150);

            TableColumn<Student, String> passwordCol = new TableColumn<>("Password");
            passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
            passwordCol.setMinWidth(100);

            TableColumn<Student, String> courseCol = new TableColumn<>("Course");
            courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
            courseCol.setMinWidth(150);

            table.getColumns().addAll(noCol, idCol, nameCol, ageCol, phoneCol, emailCol, passwordCol, courseCol);

            // Wrap TableView in VBox with padding
            VBox tableLayout = new VBox(10, table);
            tableLayout.setPadding(new Insets(10));
            tableLayout.setAlignment(Pos.CENTER);

            // Add HBox for potential future buttons
            HBox tableButtonBox = new HBox(10);
            tableButtonBox.setAlignment(Pos.CENTER);
            tableLayout.getChildren().add(tableButtonBox); // Empty for now, can add buttons later

            Scene scene = new Scene(tableLayout, 900, 400);
            tableStage.setScene(scene);
            tableStage.show();
        });

        Scene scene = new Scene(mainLayout, 700, 650);
        primaryStage.setTitle("Student Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}