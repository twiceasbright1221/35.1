import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Exercise35_01 extends JFrame {
    private JTextArea taOutput = new JTextArea();
    private JButton btBatchUpdate = new JButton("Batch Update");
    private JButton btNonBatchUpdate = new JButton("Non-Batch Update");
    private JButton btConnectToDB = new JButton("Connect to Database");

    private Connection connection = null;

    public Exercise35_01() {
        setTitle("Exercise35_01");

        taOutput.setEditable(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btBatchUpdate);
        buttonPanel.add(btNonBatchUpdate);

        add(new JScrollPane(taOutput), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(btConnectToDB, BorderLayout.NORTH);

        btConnectToDB.addActionListener(e -> connectToDatabase());
        btBatchUpdate.addActionListener(e -> batchUpdate());
        btNonBatchUpdate.addActionListener(e -> nonBatchUpdate());

        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        Dbconnectionpanel panel = new Dbconnectionpanel();
        int result = JOptionPane.showConfirmDialog(this, panel,
                "Connect to DB", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String driver = panel.getDriver();
                String url = panel.getURL();
                String user = panel.getUsername();
                String password = panel.getPassword();

                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);
                taOutput.append("Connected to " + url + "\n");

                // Create the Temp table if it doesn't exist
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS Temp " +
                        "(num1 DOUBLE, num2 DOUBLE, num3 DOUBLE)"
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(), "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void batchUpdate() {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Please connect to database first.");
            return;
        }

        try {
            // Clear table first
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM Temp");
            }

            long startTime = System.currentTimeMillis();

            connection.setAutoCommit(false);
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO Temp VALUES (?, ?, ?)")) {

                for (int i = 0; i < 1000; i++) {
                    pstmt.setDouble(1, Math.random());
                    pstmt.setDouble(2, Math.random());
                    pstmt.setDouble(3, Math.random());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                connection.commit();
            }
            connection.setAutoCommit(true);

            long endTime = System.currentTimeMillis();
            taOutput.append("Batch update completed\n");
            taOutput.append("The elapsed time is " + (endTime - startTime) + "\n");

        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException e2) {}
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void nonBatchUpdate() {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Please connect to database first.");
            return;
        }

        try {
            // Clear table first
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM Temp");
            }

            long startTime = System.currentTimeMillis();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO Temp VALUES (?, ?, ?)")) {

                for (int i = 0; i < 1000; i++) {
                    pstmt.setDouble(1, Math.random());
                    pstmt.setDouble(2, Math.random());
                    pstmt.setDouble(3, Math.random());
                    pstmt.executeUpdate();
                }
            }

            long endTime = System.currentTimeMillis();
            taOutput.append("Non-Batch update completed\n");
            taOutput.append("The elapsed time is " + (endTime - startTime) + "\n");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Exercise35_01::new);
    }
}
