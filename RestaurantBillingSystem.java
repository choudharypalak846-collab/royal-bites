import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RestaurantBillingSystem extends JFrame {

    private final Color bg = new Color(18, 24, 38);
    private final Color panel = new Color(29, 38, 56);
    private final Color orange = new Color(255, 145, 55);
    private final Color white = new Color(245, 247, 250);
    private final Color muted = new Color(170, 180, 195);
    private final Color fieldBg = new Color(43, 54, 75);

    private final String[] names = {
        "Pizza", "Burger", "Sandwich", "Pasta",
        "Coffee", "Cold Drink", "French Fries", "Paneer Roll"
    };

    private final int[] prices = {200, 100, 80, 150, 60, 40, 90, 120};

    private String selectedFood = "";
    private int selectedPrice = 0;

    private JTextField customerField;
    private JTextField tableField;
    private JSpinner quantity;
    private JComboBox<String> paymentBox;

    private JLabel selectedLabel;
    private JLabel subtotalLabel;
    private JLabel gstLabel;
    private JLabel totalLabel;

    private JLabel ordersValue;
    private JLabel salesValue;
    private JLabel todayValue;

    private DefaultTableModel model;
    private JTable cartTable;

    private int totalOrders = 0;
    private double totalSales = 0.0;
    private double todayRevenue = 0.0;

    private final ArrayList<String[]> orderHistory = new ArrayList<>();

    public RestaurantBillingSystem() {
        setTitle("Royal Bites | Restaurant Billing System");
        setSize(1200, 820);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bg);
        setLayout(new BorderLayout(12, 12));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panel);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 3));
        brand.setBackground(panel);

        JLabel logo = new JLabel("ROYAL BITES");
        logo.setFont(new Font("Arial", Font.BOLD, 27));
        logo.setForeground(orange);

        JLabel subtitle = new JLabel("RESTAURANT  |  BILLING SYSTEM");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(muted);

        brand.add(logo);
        brand.add(subtitle);

        JLabel date = new JLabel(currentDateTime());
        date.setForeground(white);
        date.setFont(new Font("Arial", Font.BOLD, 13));

        header.add(brand, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // CENTER CONTENT: DASHBOARD + MAIN AREA
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(bg);
        center.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JPanel dashboard = new JPanel(new GridLayout(1, 3, 12, 0));
        dashboard.setBackground(bg);
        dashboard.add(createDashboardCard("TOTAL ORDERS", "0", 0));
        dashboard.add(createDashboardCard("TOTAL SALES", "Rs. 0.00", 1));
        dashboard.add(createDashboardCard("TODAY'S REVENUE", "Rs. 0.00", 2));

        center.add(dashboard, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(1, 2, 16, 0));
        main.setBackground(bg);

        // LEFT MENU PANEL
        JPanel left = new JPanel(new BorderLayout(10, 12));
        left.setBackground(panel);
        left.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel menuHeading = new JPanel(new GridLayout(2, 1, 0, 4));
        menuHeading.setBackground(panel);

        JLabel menuTitle = new JLabel("EXPLORE OUR MENU");
        menuTitle.setForeground(white);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel menuSub = new JLabel("Select your favourite food");
        menuSub.setForeground(muted);

        menuHeading.add(menuTitle);
        menuHeading.add(menuSub);

        JPanel foodGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        foodGrid.setBackground(panel);

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            JButton foodButton = new JButton(
                "<html><center><b>" + names[i] + "</b><br><br>Rs. "
                + prices[i] + "<br><br><font color='#FF9137'>SELECT ITEM</font></center></html>"
            );
            styleButton(foodButton, fieldBg, white);
            try {
                BufferedImage raw = ImageIO.read(new URL(foodPhotoUrl(index)));
                Image thumb = raw.getScaledInstance(180, 105, Image.SCALE_SMOOTH);
                foodButton.setIcon(new ImageIcon(thumb));
                foodButton.setHorizontalTextPosition(SwingConstants.CENTER);
                foodButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                foodButton.setIconTextGap(10);
                foodButton.setPreferredSize(new Dimension(240, 220));
            } catch (Exception photoError) {
                foodButton.setText("<html><center><b>" + names[i] + "</b><br>Rs. " + prices[i] + "<br>SELECT ITEM</center></html>");
            }
            foodButton.setFont(new Font("Arial", Font.PLAIN, 16));
            foodButton.setBorder(BorderFactory.createLineBorder(new Color(66, 78, 100), 1));
            foodButton.setPreferredSize(new Dimension(240, 220));

            foodButton.addActionListener(e -> {
                selectedFood = names[index];
                selectedPrice = prices[index];
                selectedLabel.setText(selectedFood + "  |  Rs. " + selectedPrice);
                selectedLabel.setForeground(orange);
            });

            foodGrid.add(foodButton);
        }

        JScrollPane foodScroll = new JScrollPane(foodGrid);
        foodScroll.setBorder(null);
        foodScroll.getViewport().setBackground(panel);

        selectedLabel = new JLabel("No item selected");
        selectedLabel.setForeground(muted);
        selectedLabel.setFont(new Font("Arial", Font.BOLD, 14));

        quantity = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));

        JButton addButton = new JButton("ADD TO ORDER");
        styleButton(addButton, orange, bg);

        JPanel selection = new JPanel(new GridLayout(3, 1, 5, 6));
        selection.setBackground(panel);
        selection.add(selectedLabel);
        selection.add(quantity);
        selection.add(addButton);

        // Optional pizza picture. Keep images/pizza.jpg inside the project folder.
        JPanel menuContent = new JPanel(new BorderLayout(0, 8));
        menuContent.setBackground(panel);
        JLabel pizzaLabel = new JLabel();
        pizzaLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon pizzaIcon = loadPizzaImage();
        if (pizzaIcon != null) {
            Image scaled = pizzaIcon.getImage().getScaledInstance(150, 95, Image.SCALE_SMOOTH);
            pizzaLabel.setIcon(new ImageIcon(scaled));
        }
        menuContent.add(pizzaLabel, BorderLayout.NORTH);
        menuContent.add(foodScroll, BorderLayout.CENTER);

        left.add(menuHeading, BorderLayout.NORTH);
        left.add(menuContent, BorderLayout.CENTER);
        left.add(selection, BorderLayout.SOUTH);

        // RIGHT ORDER PANEL
        JPanel right = new JPanel(new BorderLayout(10, 12));
        right.setBackground(panel);
        right.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel orderTitle = new JLabel("YOUR ORDER");
        orderTitle.setForeground(white);
        orderTitle.setFont(new Font("Arial", Font.BOLD, 20));

        customerField = new JTextField();
        tableField = new JTextField();
        styleField(customerField);
        styleField(tableField);

        JPanel customerPanel = new JPanel(new GridLayout(2, 2, 8, 7));
        customerPanel.setBackground(panel);
        customerPanel.add(makeLabel("Customer Name"));
        customerPanel.add(makeLabel("Table Number"));
        customerPanel.add(customerField);
        customerPanel.add(tableField);

        paymentBox = new JComboBox<>(new String[]{"Cash", "UPI", "Card"});
        paymentBox.setFont(new Font("Arial", Font.PLAIN, 14));
        paymentBox.setBackground(fieldBg);
        paymentBox.setForeground(white);

        JPanel paymentPanel = new JPanel(new BorderLayout(8, 0));
        paymentPanel.setBackground(panel);
        paymentPanel.add(makeLabel("Payment Method"), BorderLayout.WEST);
        paymentPanel.add(paymentBox, BorderLayout.CENTER);

        JPanel topOrder = new JPanel(new GridLayout(3, 1, 0, 8));
        topOrder.setBackground(panel);
        topOrder.add(orderTitle);
        topOrder.add(customerPanel);
        topOrder.add(paymentPanel);

        model = new DefaultTableModel(new String[]{"Item", "Price", "Qty", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(model);
        cartTable.setRowHeight(34);
        cartTable.setFont(new Font("Arial", Font.PLAIN, 13));
        cartTable.setForeground(white);
        cartTable.setBackground(new Color(37, 47, 66));
        cartTable.setSelectionBackground(orange);
        cartTable.setSelectionForeground(bg);
        cartTable.setGridColor(new Color(65, 76, 95));
        cartTable.setFillsViewportHeight(true);

        JTableHeader tableHeader = cartTable.getTableHeader();
        tableHeader.setBackground(fieldBg);
        tableHeader.setForeground(orange);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13));
        tableHeader.setPreferredSize(new Dimension(100, 36));

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(null);
        cartScroll.getViewport().setBackground(new Color(37, 47, 66));

        JPanel summary = new JPanel(new GridLayout(3, 1, 5, 6));
        summary.setBackground(panel);
        subtotalLabel = makeLabel("Subtotal: Rs. 0.00");
        gstLabel = makeLabel("GST (5%): Rs. 0.00");
        totalLabel = makeLabel("GRAND TOTAL: Rs. 0.00");
        totalLabel.setForeground(orange);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        summary.add(subtotalLabel);
        summary.add(gstLabel);
        summary.add(totalLabel);

        JButton removeButton = new JButton("REMOVE ITEM");
        JButton historyButton = new JButton("ORDER HISTORY");
        JButton resetButton = new JButton("RESET");
        JButton billButton = new JButton("GENERATE BILL");

        styleButton(removeButton, new Color(190, 65, 65), white);
        styleButton(historyButton, new Color(65, 78, 100), white);
        styleButton(resetButton, new Color(65, 78, 100), white);
        styleButton(billButton, orange, bg);

        JPanel utilityButtons = new JPanel(new GridLayout(1, 3, 7, 0));
        utilityButtons.setBackground(panel);
        utilityButtons.add(removeButton);
        utilityButtons.add(historyButton);
        utilityButtons.add(resetButton);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setBackground(panel);
        bottom.add(summary, BorderLayout.CENTER);

        JPanel buttonStack = new JPanel(new GridLayout(2, 1, 0, 7));
        buttonStack.setBackground(panel);
        buttonStack.add(billButton);
        buttonStack.add(utilityButtons);
        bottom.add(buttonStack, BorderLayout.SOUTH);

        right.add(topOrder, BorderLayout.NORTH);
        right.add(cartScroll, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        main.add(left);
        main.add(right);
        center.add(main, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JLabel footer = new JLabel("ROYAL BITES  |  THANK YOU FOR VISITING!", JLabel.CENTER);
        footer.setForeground(muted);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addSelectedItem());

        removeButton.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) {
                model.removeRow(cartTable.convertRowIndexToModel(row));
                updateBill();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.");
            }
        });

        resetButton.addActionListener(e -> resetOrder());

        billButton.addActionListener(e -> generateBill());

        historyButton.addActionListener(e -> showOrderHistory());

        setVisible(true);
    }

    private JPanel createDashboardCard(String heading, String value, int cardNumber) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(panel);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(66, 78, 100)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel title = new JLabel(heading, JLabel.CENTER);
        title.setForeground(muted);
        title.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel number = new JLabel(value, JLabel.CENTER);
        number.setForeground(orange);
        number.setFont(new Font("Arial", Font.BOLD, 20));

        if (cardNumber == 0) ordersValue = number;
        if (cardNumber == 1) salesValue = number;
        if (cardNumber == 2) todayValue = number;

        card.add(title);
        card.add(number);
        return card;
    }

    private String currentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  |  hh:mm a"));
    }


    private String foodPhotoUrl(int index) {
        String[] urls = {
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1513558161293-cdaf765edfd4?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?w=400&h=250&fit=crop",
            "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=250&fit=crop"
        };
        return urls[index];
    }

    private ImageIcon loadPizzaImage() {
        File imageFile = new File("images/pizza.jpg");
        if (imageFile.exists()) {
            return new ImageIcon(imageFile.getAbsolutePath());
        }
        return null;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(white);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private void styleField(JTextField field) {
        field.setBackground(fieldBg);
        field.setForeground(white);
        field.setCaretColor(white);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(75, 88, 110)),
            BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
    }

    private void styleButton(JButton button, Color color, Color textColor) {
        button.setBackground(color);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addSelectedItem() {
        if (selectedFood.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a food item!");
            return;
        }

        int qty = (Integer) quantity.getValue();
        int itemTotal = selectedPrice * qty;
        model.addRow(new Object[]{selectedFood, selectedPrice, qty, itemTotal});
        updateBill();
    }

    private double getSubtotal() {
        double sum = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            sum += ((Number) model.getValueAt(i, 3)).doubleValue();
        }
        return sum;
    }

    private void updateBill() {
        double subtotal = getSubtotal();
        double gst = subtotal * 0.05;
        double total = subtotal + gst;

        subtotalLabel.setText(String.format("Subtotal: Rs. %.2f", subtotal));
        gstLabel.setText(String.format("GST (5%%): Rs. %.2f", gst));
        totalLabel.setText(String.format("GRAND TOTAL: Rs. %.2f", total));
    }

    private void updateDashboard() {
        ordersValue.setText(String.valueOf(totalOrders));
        salesValue.setText(String.format("Rs. %.2f", totalSales));
        todayValue.setText(String.format("Rs. %.2f", todayRevenue));
    }

    private void resetOrder() {
        model.setRowCount(0);
        customerField.setText("");
        tableField.setText("");
        quantity.setValue(1);
        paymentBox.setSelectedIndex(0);
        selectedFood = "";
        selectedPrice = 0;
        selectedLabel.setText("No item selected");
        selectedLabel.setForeground(muted);
        updateBill();
    }

    private void showOrderHistory() {
        String[] columns = {"Order No.", "Customer", "Table", "Payment", "Date & Time", "Total"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String[] order : orderHistory) {
            historyModel.addRow(order);
        }

        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(28);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        if (orderHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No completed orders yet.", "Order History",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, new JScrollPane(historyTable),
            "ROYAL BITES - ORDER HISTORY", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateBill() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your order is empty!");
            return;
        }

        String customer = customerField.getText().trim();
        String table = tableField.getText().trim();

        if (customer.isEmpty() || table.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name and table number!");
            return;
        }

        double subtotal = getSubtotal();
        double gst = subtotal * 0.05;
        double total = subtotal + gst;
        String paymentMethod = (String) paymentBox.getSelectedItem();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        StringBuilder receipt = new StringBuilder();
        receipt.append("========================================\n");
        receipt.append("              ROYAL BITES\n");
        receipt.append("          RESTAURANT RECEIPT\n");
        receipt.append("========================================\n");
        receipt.append("Order No: ").append(totalOrders + 1).append("\n");
        receipt.append("Customer: ").append(customer).append("\n");
        receipt.append("Table No: ").append(table).append("\n");
        receipt.append("Date: ").append(dateTime).append("\n");
        receipt.append("Payment: ").append(paymentMethod).append("\n");
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-18s %5s %10s%n", "ITEM", "QTY", "TOTAL"));
        receipt.append("----------------------------------------\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            String item = String.valueOf(model.getValueAt(i, 0));
            String qty = String.valueOf(model.getValueAt(i, 2));
            String itemTotal = String.valueOf(model.getValueAt(i, 3));
            receipt.append(String.format("%-18s %5s %10s%n", item, qty, "Rs." + itemTotal));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("Subtotal:                 Rs. %.2f%n", subtotal));
        receipt.append(String.format("GST (5%%):                  Rs. %.2f%n", gst));
        receipt.append(String.format("GRAND TOTAL:              Rs. %.2f%n", total));
        receipt.append("========================================\n");
        receipt.append("         THANK YOU! VISIT AGAIN\n");
        receipt.append("========================================\n");

        JTextArea receiptArea = new JTextArea(receipt.toString(), 22, 42);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setMargin(new Insets(12, 12, 12, 12));

        int choice = JOptionPane.showOptionDialog(
            this,
            new JScrollPane(receiptArea),
            "YOUR FINAL BILL",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"Confirm Order & Print", "Confirm Order", "Cancel"},
            "Confirm Order"
        );

        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        // Save the order once it is confirmed.
        totalOrders++;
        totalSales += total;
        todayRevenue += total;

        orderHistory.add(new String[]{
            String.valueOf(totalOrders),
            customer,
            table,
            paymentMethod,
            dateTime,
            String.format("Rs. %.2f", total)
        });

        updateDashboard();

        if (choice == 0) {
            try {
                receiptArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this, "Order saved successfully!");
        resetOrder();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantBillingSystem::new);
    }
}