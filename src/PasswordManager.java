import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordManager extends JFrame implements ActionListener {
    private List<PasswordEntry> passwordEntries;
    private DefaultTableModel tableModel;
    private JTextField websiteField, usernameField, notesField, searchField;
    private JButton addButton, editButton, deleteButton, saveButton, loadButton, searchButton;
    private JTable passwordTable;
    private JPasswordField passwordField;

    public PasswordManager() {
        super("Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        passwordEntries = new ArrayList<>();
        tableModel = new DefaultTableModel(new Object[]{"Website", "Username", "Password", "Notes"}, 0);
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

        JScrollPane scrollPane = new JScrollPane(passwordTable);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(inputPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String website = websiteField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String notes = notesField.getText();
            PasswordEntry entry = new PasswordEntry(website, username, password, notes);
            passwordEntries.add(entry);
            tableModel.addRow(new Object[]{website, username, password, notes});
            clearFields();
        } else if (e.getSource() == editButton) {
            int selectedRow = passwordTable.getSelectedRow();
            if (selectedRow != -1) {
                String website = websiteField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String notes = notesField.getText();
                PasswordEntry entry = new PasswordEntry(website, username, password, notes);
                passwordEntries.set(selectedRow, entry);
                tableModel.setValueAt(website, selectedRow, 0);
                tableModel.setValueAt(username, selectedRow, 1);
                tableModel.setValueAt(password, selectedRow, 2);
                tableModel.setValueAt(notes, selectedRow, 3);
                clearFields();
            }
        } else if (e.getSource() == deleteButton) {
            int selectedRow = passwordTable.getSelectedRow();
            if (selectedRow != -1) {
                passwordEntries.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        } else if (e.getSource() == saveButton) {
            saveEntries();
        } else if (e.getSource() == loadButton) {
            loadEntries();
        } else if (e.getSource() == searchButton) {
            String keyword = searchField.getText().toLowerCase();
            tableModel.setRowCount(0);
            for (PasswordEntry entry : passwordEntries) {
                if (entry.getWebsite().toLowerCase().contains(keyword) ||
                    entry.getUsername().toLowerCase().contains(keyword)) {
                    tableModel.addRow(new Object[]{entry.getWebsite(), entry.getUsername(), entry.getPassword(), entry.getNotes()});
                }
            }
        }
    }

    private void clearFields() {
        websiteField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        notesField.setText("");
    }

    private void saveEntries() {
        try (FileOutputStream fos = new FileOutputStream("passwords.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(passwordEntries);
            JOptionPane.showMessageDialog(this, "Passwords saved successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving passwords: " + ex.getMessage());
        }
    }

    private void loadEntries() {
        try (FileInputStream fis = new FileInputStream("passwords.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            passwordEntries = (List<PasswordEntry>) ois.readObject();
            tableModel.setRowCount(0);
            for (PasswordEntry entry : passwordEntries) {
                tableModel.addRow(new Object[]{entry.getWebsite(), entry.getUsername(), entry.getPassword(), entry.getNotes()});
            }
            JOptionPane.showMessageDialog(this, "Passwords loaded successfully.");
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading passwords: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PasswordManager passwordManager = new PasswordManager();
                passwordManager.setVisible(true);
            }
        });
    }
}

class PasswordEntry implements Serializable {
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