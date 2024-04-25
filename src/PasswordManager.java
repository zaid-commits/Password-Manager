import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PasswordManager extends JFrame implements ActionListener {
    private static final String FILE_PATH = "passwords.dat";
    private static final String FEEDBACK_EMAIL = "zaidcommits.github@gmail.com";

    private List<PasswordEntry> passwordEntries;
    private DefaultTableModel tableModel;
    private JTextField websiteField, usernameField, notesField, searchField;
    private JPasswordField passwordField;
    private JTable passwordTable;
    private JButton addButton, editButton, deleteButton, saveButton, loadButton, searchButton, aboutButton,
            feedbackButton;

    public PasswordManager() {
        super("Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        passwordEntries = new ArrayList<>();
        tableModel = new DefaultTableModel(new Object[] { "Website", "Username", "Password", "Notes" }, 0);
        passwordTable = new JTable(tableModel);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Website:"));
        websiteField = new JTextField();
        inputPanel.add(websiteField);
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Notes:"));
        notesField = new JTextField();
        inputPanel.add(notesField);
        inputPanel.add(new JLabel("Search:"));
        searchField = new JTextField();
        inputPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);
        editButton = new JButton("Edit");
        editButton.addActionListener(this);
        buttonPanel.add(editButton);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);
        loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        buttonPanel.add(loadButton);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        buttonPanel.add(searchButton);
        aboutButton = new JButton("About Developer");
        aboutButton.addActionListener(this);
        buttonPanel.add(aboutButton);
        feedbackButton = new JButton("Feedback");
        feedbackButton.addActionListener(this);
        buttonPanel.add(feedbackButton);

        JScrollPane scrollPane = new JScrollPane(passwordTable);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(inputPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        loadEntries(); // Load entries from file on startup
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addEntry();
        } else if (e.getSource() == editButton) {
            editEntry();
        } else if (e.getSource() == deleteButton) {
            deleteEntry();
        } else if (e.getSource() == saveButton) {
            saveEntries();
        } else if (e.getSource() == loadButton) {
            loadEntries();
        } else if (e.getSource() == searchButton) {
            searchEntries();
        } else if (e.getSource() == aboutButton) {
            showAboutDeveloper();
        } else if (e.getSource() == feedbackButton) {
            sendFeedback();
        }
    }

    private void addEntry() {
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String notes = notesField.getText().trim();

        if (!website.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            PasswordEntry entry = new PasswordEntry(website, username, password, notes);
            passwordEntries.add(entry);
            tableModel.addRow(new Object[] { website, username, password, notes });
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Website, Username, Password).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editEntry() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow != -1) {
            String website = websiteField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String notes = notesField.getText().trim();

            if (!website.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                PasswordEntry entry = new PasswordEntry(website, username, password, notes);
                passwordEntries.set(selectedRow, entry);
                tableModel.setValueAt(website, selectedRow, 0);
                tableModel.setValueAt(username, selectedRow, 1);
                tableModel.setValueAt(password, selectedRow, 2);
                tableModel.setValueAt(notes, selectedRow, 3);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (Website, Username, Password).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEntry() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow != -1) {
            passwordEntries.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveEntries() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(passwordEntries);
            JOptionPane.showMessageDialog(this, "Passwords saved successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving passwords: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEntries() {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            passwordEntries = (List<PasswordEntry>) ois.readObject();
            tableModel.setRowCount(0);
            for (PasswordEntry entry : passwordEntries) {
                tableModel.addRow(new Object[] { entry.getWebsite(), entry.getUsername(), entry.getPassword(),
                        entry.getNotes() });
            }
            JOptionPane.showMessageDialog(this, "Passwords loaded successfully.");
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading passwords: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEntries() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (PasswordEntry entry : passwordEntries) {
            if (entry.getWebsite().toLowerCase().contains(keyword) ||
                    entry.getUsername().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[] { entry.getWebsite(), entry.getUsername(), entry.getPassword(),
                        entry.getNotes() });
            }
        }
    }

    private void clearFields() {
        websiteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        notesField.setText("");
    }

    private void showAboutDeveloper() {
        String aboutMessage = "<html>This Password Manager application was developed by:<br><br>" +
                "<a href=\"https://github.com/zaid-commits\">Zaid Rakhange (GitHub)</a><br><br>" +
                "Feel free to explore my other projects and connect with me!";
        JLabel message = new JLabel(aboutMessage);
        message.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        message.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/zaid-commits"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JOptionPane.showMessageDialog(this, message, "About Developer", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendFeedback() {
        try {
            URI mailtoURI = new URI("mailto:" + FEEDBACK_EMAIL);
            Desktop.getDesktop().mail(mailtoURI);
        } catch (IOException | URISyntaxException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open mail composer. Please send your feedback manually to " + FEEDBACK_EMAIL, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager passwordManager = new PasswordManager();
            passwordManager.setVisible(true);
        });
    }
}

class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private String website;
    private String username;
    private String password;
    private String notes;

    public PasswordEntry(String website, String username, String password, String notes) {
        this.website = website;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }
}