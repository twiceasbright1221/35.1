import java.awt.*;
import javax.swing.*;

public class Dbconnectionpanel extends JPanel {
    private JTextField tfDriver = new JTextField("com.mysql.jdbc.Driver", 20);
    private JTextField tfURL = new JTextField("jdbc:mysql://localhost/javabook", 20);
    private JTextField tfUsername = new JTextField("scott", 20);
    private JPasswordField tfPassword = new JPasswordField("tiger", 20);

    public Dbconnectionpanel() {
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("JDBC Driver:"));
        add(tfDriver);
        add(new JLabel("Database URL:"));
        add(tfURL);
        add(new JLabel("Username:"));
        add(tfUsername);
        add(new JLabel("Password:"));
        add(tfPassword);
    }

    public String getDriver()   { return tfDriver.getText().trim(); }
    public String getURL()      { return tfURL.getText().trim(); }
    public String getUsername() { return tfUsername.getText().trim(); }
    public String getPassword() { return new String(tfPassword.getPassword()); }
}