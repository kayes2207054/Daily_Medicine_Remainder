package com.example.view;

import com.example.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthController authController;

    public LoginFrame(AuthController authController) {
        super("DailyDose - Login");
        this.authController = authController;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Create Account");

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (authController.login(username, password)) {
                // Open MainFrame
                SwingUtilities.invokeLater(() -> {
                    MainFrame mf = new MainFrame();
                    mf.setVisible(true);
                });
                dispose();
            }
        });

        signupBtn.addActionListener(e -> {
            SignupFrame sf = new SignupFrame(authController, this);
            sf.setVisible(true);
            setVisible(false);
        });

        panel.add(userLabel); panel.add(userField);
        panel.add(passLabel); panel.add(passField);
        panel.add(loginBtn); panel.add(signupBtn);

        setContentPane(panel);
    }
}
