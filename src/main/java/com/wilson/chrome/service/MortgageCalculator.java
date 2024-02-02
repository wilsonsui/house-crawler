package com.wilson.chrome.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MortgageCalculator {
    private JPanel panelMain;
    private JTextField textFieldAmount;
    private JTextField textFieldRate;
    private JTextField textFieldYears;
    private JButton calculateButton;
    private JTextArea textAreaResult;

    public MortgageCalculator() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Add your mortgage calculation logic here.
                // You can get the input from the text fields like this:
                // String amountStr = textFieldAmount.getText();
                // String rateStr = textFieldRate.getText();
                // String yearsStr = textFieldYears.getText();
                // Don't forget to handle potential errors in the input.
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mortgage Calculator");
        frame.setContentPane(new MortgageCalculator().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

