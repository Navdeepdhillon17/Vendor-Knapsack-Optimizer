import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class KnapsackUI extends JFrame {
    private JTextField capacityField;
    private JTextArea namesArea;
    private JTextArea weightsArea;
    private JTextArea profitsArea;
    private JTable resultTable;
    private JLabel resultLabel;

    public KnapsackUI() {
        setTitle("Vendor Product Optimizer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));

        inputPanel.add(new JLabel("Van Capacity (kg):"));
        capacityField = new JTextField();
        inputPanel.add(capacityField);

        inputPanel.add(new JLabel("Product Names (comma separated):"));
        namesArea = new JTextArea(2, 20);
        inputPanel.add(new JScrollPane(namesArea));

        inputPanel.add(new JLabel("Weights (kg, comma separated):"));
        weightsArea = new JTextArea(2, 20);
        inputPanel.add(new JScrollPane(weightsArea));

        inputPanel.add(new JLabel("Expected Profits (₹, comma separated):"));
        profitsArea = new JTextArea(2, 20);
        inputPanel.add(new JScrollPane(profitsArea));

        JButton solveButton = new JButton("Optimize Selection");
        solveButton.addActionListener(this::solveKnapsack);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(solveButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Selected Products"));

        resultLabel = new JLabel("Max Profit: ₹0");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(tableScroll, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
    }

    private void solveKnapsack(ActionEvent e) {
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());

            String[] nameArr = namesArea.getText().split(",");
            String[] weightArr = weightsArea.getText().split(",");
            String[] profitArr = profitsArea.getText().split(",");

            if (nameArr.length != weightArr.length || weightArr.length != profitArr.length) {
                throw new IllegalArgumentException("Each product must have a name, weight, and profit.");
            }

            int n = nameArr.length;
            int[] weights = new int[n];
            int[] profits = new int[n];
            String[] names = new String[n];

            for (int i = 0; i < n; i++) {
                names[i] = nameArr[i].trim();
                weights[i] = Integer.parseInt(weightArr[i].trim());
                profits[i] = Integer.parseInt(profitArr[i].trim());
            }

            // Knapsack DP
            int[][] dp = new int[n + 1][capacity + 1];
            for (int i = 1; i <= n; i++) {
                for (int w = 0; w <= capacity; w++) {
                    if (weights[i - 1] <= w) {
                        dp[i][w] = Math.max(profits[i - 1] + dp[i - 1][w - weights[i - 1]], dp[i - 1][w]);
                    } else {
                        dp[i][w] = dp[i - 1][w];
                    }
                }
            }

            // Backtrack to find selected items
            ArrayList<String[]> selectedItems = new ArrayList<>();
            int w = capacity;
            for (int i = n; i > 0 && w > 0; i--) {
                if (dp[i][w] != dp[i - 1][w]) {
                    selectedItems.add(new String[]{
                            names[i - 1],
                            weights[i - 1] + " kg",
                            "₹" + profits[i - 1]
                    });
                    w -= weights[i - 1];
                }
            }

            // Update table
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Product", "Weight", "Profit"}, 0);
            for (String[] row : selectedItems) {
                model.addRow(row);
            }
            resultTable.setModel(model);

            // Update result
            resultLabel.setText("Max Profit: ₹" + dp[n][capacity]);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new KnapsackUI().setVisible(true));
    }
}
