import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KnapsackUI {
    private static final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Item", "Weight", "Value"}, 0);
    private static final List<Item> items = new ArrayList<>();

    public static void main(String[] args) {
        FlatCyanLightIJTheme.setup();
        JFrame frame = new JFrame("Vendor Knapsack Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 650);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        JLabel header = new JLabel("Empowering Merchants with Smart Packing Decisions", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 16));
        header.setForeground(new Color(80, 80, 120));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setBorder(new EmptyBorder(5, 0, 10, 0));
        mainPanel.add(header);

        JLabel heading = new JLabel("\uD83C\uDF92 Vendor Knapsack Optimizer", SwingConstants.CENTER);
        heading.setFont(new Font("Verdana", Font.BOLD, 22));
        heading.setForeground(new Color(25, 89, 114));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(heading);

        try {
            URL imageUrl = new URL("https://cdn-icons-png.flaticon.com/512/4712/4712035.png");

            BufferedImage image = ImageIO.read(imageUrl);
            JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setGridColor(Color.GRAY);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(480, 90));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(scrollPane);

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));

        JTextField nameField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField valueField = new JTextField();
        JTextField capacityField = new JTextField();
        JButton addButton = new JButton("➕ Add");
        JButton solveButton = new JButton("🚀 Solve");

        JPanel addItemPanel = new JPanel();
        addItemPanel.setLayout(new GridLayout(3, 2, 5, 5));
        addItemPanel.setBorder(BorderFactory.createTitledBorder("➕ Add New Item"));
        addItemPanel.setBackground(new Color(240, 248, 255));
        addItemPanel.add(new JLabel("Item Name:"));
        addItemPanel.add(nameField);
        addItemPanel.add(new JLabel("Weight:"));
        addItemPanel.add(weightField);
        addItemPanel.add(new JLabel("Value:"));
        addItemPanel.add(valueField);

        JPanel solvePanel = new JPanel();
        solvePanel.setLayout(new GridLayout(2, 2, 5, 5));
        solvePanel.setBorder(BorderFactory.createTitledBorder("🧮 Solve Knapsack"));
        solvePanel.setBackground(new Color(240, 248, 255));
        solvePanel.add(new JLabel("Capacity:"));
        solvePanel.add(capacityField);
        solvePanel.add(solveButton);
        solvePanel.add(addButton);

        inputPanel.add(addItemPanel);
        inputPanel.add(solvePanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(inputPanel);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(250, 250, 255));
        resultArea.setForeground(new Color(30, 30, 30));
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(480, 100));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(resultScroll);

        JLabel footer = new JLabel("💡 Tip: Use this tool to decide what to carry to maximize profit while minimizing load.", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 12));
        footer.setForeground(new Color(70, 100, 110));
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(footer);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int weight = Integer.parseInt(weightField.getText());
                int value = Integer.parseInt(valueField.getText());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Item name cannot be empty.");
                    return;
                }

                items.add(new Item(name, weight, value));
                tableModel.addRow(new Object[]{name, weight, value});

                nameField.setText("");
                weightField.setText("");
                valueField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for weight and value.");
            }
        });

        solveButton.addActionListener((ActionEvent e) -> {
            try {
                int capacity = Integer.parseInt(capacityField.getText());
                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No items added yet.");
                    return;
                }
                resultArea.setText(solveKnapsack(items, capacity));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid capacity.");
            }
        });

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private static String solveKnapsack(List<Item> items, int maxWeight) {
        int n = items.size();
        int[][] dp = new int[n + 1][maxWeight + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);
            for (int w = 0; w <= maxWeight; w++) {
                if (item.weight <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], item.value + dp[i - 1][w - item.weight]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        StringBuilder result = new StringBuilder("\u2728 Selected Items:\n");
        int w = maxWeight;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Item item = items.get(i - 1);
                result.append("- ").append(item.name).append(" (W: ").append(item.weight).append(", V: ").append(item.value).append(")\n");
                w -= item.weight;
            }
        }
        result.append("\n\uD83D\uDCB0 Maximum Value: ").append(dp[n][maxWeight]);
        return result.toString();
    }

    static class Item {
        String name;
        int weight;
        int value;

        Item(String name, int weight, int value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }
    }
}
