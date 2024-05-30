import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class MainInterface extends JFrame {
    private Connection connection;
    private DefaultTableModel experimentTableModel;
    private DefaultTableModel astronautTableModel;
    private DefaultTableModel discoveryTableModel;
    private DefaultTableModel resourceSetTableModel;
    private DefaultTableModel locationTableModel;
    private DefaultTableModel stationTableModel;
    private DefaultTableModel missionTableModel;

    private JPanel buttonPanel;
    private JTabbedPane tabbedPane;


    public MainInterface() {
        // Initialize the connection and table models
        initializeConnection();
        initializeTableModels();
        loadAllInitialData();

        // Setup the main interface
        setupMainInterface();
    }

    private void initializeConnection() {
        // Replace with your database connection details
        String url = "jdbc:mysql://localhost:3306/Xero";
        String user = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
        }
    }


    private void initializeTableModels() {
        experimentTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Date"}, 0);
        astronautTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Age", "Gender", "Nationality", "Medical Report", "Training History", "Mission_ID", "Space_Station_ID"}, 0);
        discoveryTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Date", "Significance"}, 0);
        resourceSetTableModel = new DefaultTableModel(new Object[]{"ID", "Quantity", "Consumption Rate", "Type", "Space_Station_ID"}, 0);
        locationTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Distance", "Coordinates", "Space_Station_ID"}, 0);
        stationTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Operational Status", "Capacity"}, 0);
        missionTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Date", "Status"}, 0);

    }

    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateButtonsForSelectedTab();
        }
    }

    private void updateButtonsForSelectedTab() {
        buttonPanel.removeAll();

        int selectedIndex = tabbedPane.getSelectedIndex();
        String selectedTabTitle = tabbedPane.getTitleAt(selectedIndex);

        if (selectedTabTitle.equals("Experiments")) {
            addStyledButton(buttonPanel, "Add Experiment", e -> addExperiment());
            addStyledButton(buttonPanel, "Delete Experiment", e -> deleteExperiment());
            addStyledButton(buttonPanel, "Update Experiment", e -> updateExperiment());
        } else if (selectedTabTitle.equals("Astronauts")) {
            addStyledButton(buttonPanel, "Add Astronaut", e -> addAstronaut());
            addStyledButton(buttonPanel, "Delete Astronaut", e -> deleteAstronaut());
            addStyledButton(buttonPanel, "Update Astronaut", e -> updateAstronaut());
        } else if (selectedTabTitle.equals("Discoveries")) {
            addStyledButton(buttonPanel, "Add Discovery", e -> addDiscovery());
            addStyledButton(buttonPanel, "Delete Discovery", e -> deleteDiscovery());
            addStyledButton(buttonPanel, "Update Discovery", e -> updateDiscovery());
        } else if (selectedTabTitle.equals("Resource Sets")) {
            addStyledButton(buttonPanel, "Add Resource Set", e -> addResourceSet());
            addStyledButton(buttonPanel, "Delete Resource Set", e -> deleteResourceSet());
            addStyledButton(buttonPanel, "Update Resource Set", e -> updateResourceSet());

        } else if (selectedTabTitle.equals("Locations")) {
            addStyledButton(buttonPanel, "Add Location", e -> addLocation());
            addStyledButton(buttonPanel, "Delete Location", e -> deleteLocation());
            addStyledButton(buttonPanel, "Update Location", e -> updateLocation());
        } else if (selectedTabTitle.equals("Space Stations")) {
            addStyledButton(buttonPanel, "Add Space Station", e -> addSpaceStation());
            addStyledButton(buttonPanel, "Delete Space Station", e -> deleteSpaceStation());
            addStyledButton(buttonPanel, "Update Space Station", e -> updateSpaceStation());
        } else if (selectedTabTitle.equals("Missions")) {
            addStyledButton(buttonPanel, "Add Mission", e -> addMission());
            addStyledButton(buttonPanel, "Delete Mission", e -> deleteMission());
            addStyledButton(buttonPanel, "Update Mission", e -> updateMission());
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private JMenuItem createMenuItem(String title, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private void setupMainInterface() {
        setTitle("Space Exploration Management (Xero)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenu queryMenu = new JMenu("Queries");


        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.addChangeListener(new TabChangeListener());

        tabbedPane.add("Space Stations", createTabPanel(stationTableModel));
        tabbedPane.add("Astronauts", createTabPanel(astronautTableModel));
        tabbedPane.add("Missions", createTabPanel(missionTableModel));
        tabbedPane.add("Experiments", createTabPanel(experimentTableModel));
        tabbedPane.add("Discoveries", createTabPanel(discoveryTableModel));
        tabbedPane.add("Resource Sets", createTabPanel(resourceSetTableModel));
        tabbedPane.add("Locations", createTabPanel(locationTableModel));



        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButtonsForSelectedTab();



        queryMenu.add(createMenuItem("Advanced Training Astronauts", e -> loadAdvancedTrainingAstronauts()));
        queryMenu.add(createMenuItem("Astronauts With Missions", e -> loadAstronautsWithMissions()));
        //queryMenu.add(createMenuItem("Astronauts With Experiments", e -> loadAstronautsWithExperiments()));
        queryMenu.add(createMenuItem("Create Astronaut Mission View", e -> createAstronautMissionView()));
        queryMenu.add(createMenuItem("Create High Significance Discoveries View", e -> createHighSignificanceDiscoveriesView()));
        queryMenu.add(createMenuItem("Astronauts Not in Advanced Training", e -> loadAstronautsNotInAdvancedTraining()));
        queryMenu.add(createMenuItem("Experiments Not in 2024", e -> loadExperimentsNotIn2024()));
        queryMenu.add(createMenuItem("Female or Advanced Training Astronauts", e -> loadFemaleOrAdvancedTrainingAstronauts()));
        queryMenu.add(createMenuItem("High or Medium Significance Discoveries", e -> loadHighOrMediumSignificanceDiscoveries()));
        queryMenu.add(createMenuItem("Filter Astronauts by Age and Gender", e -> {
            int age = Integer.parseInt(JOptionPane.showInputDialog("Enter Age:"));
            String gender = JOptionPane.showInputDialog("Enter Gender:");
            loadAstronautsByAgeAndGender(age, gender);
        }));
        queryMenu.add(createMenuItem("Astronauts by Space Station ID", e -> {
            int spaceStationID = Integer.parseInt(JOptionPane.showInputDialog("Enter Space Station ID:"));
            loadAstronautsBySpaceStation(spaceStationID);
        }));

        queryMenu.add(createMenuItem("Delete Completed Missions", e -> deleteCompletedMissions()));
        queryMenu.add(createMenuItem("Uncompleted Missions", e -> loadUncompletedMissions()));
        queryMenu.add(createMenuItem("Completed Missions", e -> loadCompletedMissions()));
        queryMenu.add(createMenuItem("Mission End Date", e -> {
            int missionID = Integer.parseInt(JOptionPane.showInputDialog("Enter Mission ID:"));
            loadMissionEndDate(missionID);
        }));

        // Add menus to menu bar

        menuBar.add(queryMenu);

        // Set menu bar
        setJMenuBar(menuBar);





        // Add menus to menu bar
        queryMenu.add(createMenuItem("Show Resource Histogram", e -> showResourceHistogram()));
        menuBar.add(queryMenu);

        // Set menu bar
        setJMenuBar(menuBar);



    }

    private void addStyledButton(JPanel panel, String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(new LineBorder(Color.BLACK, 2));
        panel.add(button);
    }

    private JPanel createTabPanel(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(20);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showResourceHistogram() {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "SELECT Resource_ID, QuantityAvailable FROM resourceset";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int resourceId = rs.getInt("Resource_ID");
                double quantityAvailable = rs.getDouble("QuantityAvailable");
                dataset.addValue(quantityAvailable, "Quantity", Integer.toString(resourceId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching resource data: " + e.getMessage());
            return;
        }

        // Create chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Resource Quantities",
                "Resource ID",
                "Quantity Available",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Create and display chart panel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        JFrame chartFrame = new JFrame("Resource Histogram");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.getContentPane().add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }



    //add functions

    private void addExperiment() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField dateField = new JTextField();

        formPanel.add(new JLabel("Experiment ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Experiment Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Experiment Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Experiment Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Experiment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            String date = dateField.getText();

            try {
                String query = "INSERT INTO experiment (ExperimentID, Experiment_Name, Experiment_Description, EXPERIMENT_DATE) VALUES (?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, description);
                statement.setString(4, date);
                statement.executeUpdate();

                experimentTableModel.addRow(new Object[]{id, name, description, date});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding experiment: " + e.getMessage());
            }
        }
    }

    private void addAstronaut() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField nationalityField = new JTextField();
        JTextField medicalReportField = new JTextField();
        JTextField trainingHistoryField = new JTextField();
        JPanel genderPanel = new JPanel();
        JRadioButton maleRadioButton = new JRadioButton("Male");
        JRadioButton femaleRadioButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();

        formPanel.add(new JLabel("Astronaut ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);
        genderPanel.add(maleRadioButton);
        genderPanel.add(femaleRadioButton);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderPanel);
        formPanel.add(new JLabel("Nationality:"));
        formPanel.add(nationalityField);
        formPanel.add(new JLabel("Medical Report:"));
        formPanel.add(medicalReportField);
        formPanel.add(new JLabel("Training History:"));
        formPanel.add(trainingHistoryField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Astronaut", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender;
            if (maleRadioButton.isSelected()) {
                gender = "Male";
            } else if (femaleRadioButton.isSelected()) {
                gender = "Female";
            } else {
                // Default gender if none selected
                gender = "";
            }
            String nationality = nationalityField.getText();
            String medicalReport = medicalReportField.getText();
            String trainingHistory = trainingHistoryField.getText();

            try {
                String query = "INSERT INTO astronaut (Astronaut_ID, Astronaut_Name, Astronaut_Age, Astronaut_Nationality,Astronaut_Gender, Astronaut_Medical_Report, Astronaut_Training_History) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setInt(3, age);
                statement.setString(4, gender);
                statement.setString(5, nationality);
                statement.setString(6, medicalReport);
                statement.setString(7, trainingHistory);
                statement.executeUpdate();

                astronautTableModel.addRow(new Object[]{id, name, age,gender, nationality, medicalReport, trainingHistory});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding astronaut: " + e.getMessage());
            }
        }
    }

    private void addDiscovery() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField significanceField = new JTextField();

        formPanel.add(new JLabel("Discovery ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Significance:"));
        formPanel.add(significanceField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Discovery", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            String date = dateField.getText();
            String significance = significanceField.getText();

            try {
                String query = "INSERT INTO discovery (Discovery_ID, Discovery_Name, DISCOVERY_DESCRIPTION, DISCOVERY_DATE, significance_level) VALUES (?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, description);
                statement.setString(4, date);
                statement.setString(5, significance);
                statement.executeUpdate();

                discoveryTableModel.addRow(new Object[]{id, name, description, date, significance});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding discovery: " + e.getMessage());
            }
        }
    }

    private void addResourceSet() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField consumptionRateField = new JTextField();
        JTextField typeField = new JTextField();

        formPanel.add(new JLabel("Resource Set ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Consumption Rate:"));
        formPanel.add(consumptionRateField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Resource Set", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            float consumptionRate = Float.parseFloat(consumptionRateField.getText());
            String type = typeField.getText();

            try {
                String query = "INSERT INTO resourceset (Resource_ID, QuantityAvailable, ConsumptionRate, TYPE) VALUES (?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setInt(2, quantity);
                statement.setFloat(3, consumptionRate);
                statement.setString(4, type);
                statement.executeUpdate();

                resourceSetTableModel.addRow(new Object[]{id, quantity, consumptionRate, type});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding resource set: " + e.getMessage());
            }
        }
    }

    private void addLocation() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField distanceField = new JTextField();
        JTextField coordinatesField = new JTextField();

        formPanel.add(new JLabel("Location ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Distance:"));
        formPanel.add(distanceField);
        formPanel.add(new JLabel("Coordinates:"));
        formPanel.add(coordinatesField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Location", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            float distance = Float.parseFloat(distanceField.getText());
            String coordinates = coordinatesField.getText();

            try {
                String query = "INSERT INTO location (Location_ID, Location_Name, Distance, Coordinates) VALUES (?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setFloat(3, distance);
                statement.setString(4, coordinates);
                statement.executeUpdate();

                locationTableModel.addRow(new Object[]{id, name, distance, coordinates});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding location: " + e.getMessage());
            }
        }
    }

    private void addSpaceStation() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField operationalStatusField = new JTextField();
        JTextField capacityField = new JTextField();

        formPanel.add(new JLabel("Space Station ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Space Station Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Operational Status:"));
        formPanel.add(operationalStatusField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Space Station", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String operationalStatus = operationalStatusField.getText();
            int capacity = Integer.parseInt(capacityField.getText());

            try {
                String query = "INSERT INTO space_station (Space_StationID, Space_StationName, Operational_Status, Capacity) VALUES (?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setString(3, operationalStatus);
                statement.setInt(4, capacity);
                statement.executeUpdate();

                stationTableModel.addRow(new Object[]{name, operationalStatus, capacity});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding space station: " + e.getMessage());
            }
        }
    }

    private void addMission() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField statusField = new JTextField();

        formPanel.add(new JLabel("Mission ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Mission Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Mission Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Mission Status:"));
        formPanel.add(statusField);

        int result = JOptionPane.showConfirmDialog(null, formPanel, "Add Mission", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            String date = dateField.getText();
            String status = statusField.getText();

            try {
                String query = "INSERT INTO mission (Mission_ID, Mission_Name, Mission_Date, Mission_Status) VALUES (?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, date);
                statement.setString(4, status);
                statement.executeUpdate();

                missionTableModel.addRow(new Object[]{id, name, date, status});
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding mission: " + e.getMessage());
            }
        }
    }


    //              DELETE FUNCTIONS ALL




    private void deleteExperiment() {
        String id = JOptionPane.showInputDialog("Enter Experiment ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM experiment WHERE ExperimentID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < experimentTableModel.getRowCount(); i++) {
                        if (experimentTableModel.getValueAt(i, 0).toString().equals(id)) {
                            experimentTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Experiment deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Experiment not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting experiment: " + e.getMessage());
            }
        }
    }

    private void deleteAstronaut() {
        String id = JOptionPane.showInputDialog("Enter Astronaut ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM astronaut WHERE Astronaut_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < astronautTableModel.getRowCount(); i++) {
                        if (astronautTableModel.getValueAt(i, 0).toString().equals(id)) {
                            astronautTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Astronaut deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Astronaut not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting astronaut: " + e.getMessage());
            }
        }
    }

    private void deleteDiscovery() {
        String id = JOptionPane.showInputDialog("Enter Discovery ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM discovery WHERE Discovery_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < discoveryTableModel.getRowCount(); i++) {
                        if (discoveryTableModel.getValueAt(i, 0).toString().equals(id)) {
                            discoveryTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Discovery deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Discovery not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting discovery: " + e.getMessage());
            }
        }
    }

    private void deleteResourceSet() {
        String id = JOptionPane.showInputDialog("Enter Resource Set ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM resourceset WHERE Resource_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < resourceSetTableModel.getRowCount(); i++) {
                        if (resourceSetTableModel.getValueAt(i, 0).toString().equals(id)) {
                            resourceSetTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Resource Set deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Resource Set not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting resource set: " + e.getMessage());
            }
        }
    }

    private void deleteLocation() {
        String id = JOptionPane.showInputDialog("Enter Location ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM location WHERE Location_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < locationTableModel.getRowCount(); i++) {
                        if (locationTableModel.getValueAt(i, 0).toString().equals(id)) {
                            locationTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Location deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Location not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting location: " + e.getMessage());
            }
        }
    }

    private void deleteMission() {
        String id = JOptionPane.showInputDialog("Enter Mission ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM mission WHERE Mission_ID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < missionTableModel.getRowCount(); i++) {
                        if (missionTableModel.getValueAt(i, 0).toString().equals(id)) {
                            missionTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Mission deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Mission not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting mission: " + e.getMessage());
            }
        }
    }

    private void deleteSpaceStation() {
        String id = JOptionPane.showInputDialog("Enter Space Station ID to delete:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "DELETE FROM space_station WHERE Space_StationID=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    // Remove the row from the table model
                    for (int i = 0; i < stationTableModel.getRowCount(); i++) {
                        if (stationTableModel.getValueAt(i, 0).toString().equals(id)) {
                            stationTableModel.removeRow(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Space Station deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Space Station not found with ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting space station: " + e.getMessage());
            }
        }
    }

    //Update Functions


    private void updateExperiment() {
        String id = JOptionPane.showInputDialog("Enter Experiment ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM experiment WHERE ExperimentID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Experiment_Name"));
                    JTextField descriptionField = new JTextField(resultSet.getString("Experiment_Description"));
                    JTextField dateField = new JTextField(resultSet.getString("EXPERIMENT_DATE"));

                    formPanel.add(new JLabel("Experiment Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Experiment Description:"));
                    formPanel.add(descriptionField);
                    formPanel.add(new JLabel("Experiment Date (YYYY-MM-DD):"));
                    formPanel.add(dateField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Experiment", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        String description = descriptionField.getText();
                        String date = dateField.getText();

                        String updateQuery = "UPDATE experiment SET Experiment_Name = ?, Experiment_Description = ?, EXPERIMENT_DATE = ? WHERE ExperimentID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setString(2, description);
                        updateStatement.setString(3, date);
                        updateStatement.setString(4, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < experimentTableModel.getRowCount(); i++) {
                            if (experimentTableModel.getValueAt(i, 0).equals(id)) {
                                experimentTableModel.setValueAt(name, i, 1);
                                experimentTableModel.setValueAt(description, i, 2);
                                experimentTableModel.setValueAt(date, i, 3);
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Experiment ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating experiment: " + e.getMessage());
            }
        }
    }


    private void updateAstronaut() {
        String id = JOptionPane.showInputDialog("Enter Astronaut ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM astronaut WHERE Astronaut_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Astronaut_Name"));
                    JTextField ageField = new JTextField(String.valueOf(resultSet.getInt("Astronaut_Age")));
                    String gender = resultSet.getString("Astronaut_Gender");
                    JPanel genderPanel = new JPanel();
                    JRadioButton maleRadioButton = new JRadioButton("Male");
                    JRadioButton femaleRadioButton = new JRadioButton("Female");
                    ButtonGroup genderGroup = new ButtonGroup();
                    genderGroup.add(maleRadioButton);
                    genderGroup.add(femaleRadioButton);
                    if (gender.equals("Male")) {
                        maleRadioButton.setSelected(true);
                    } else if (gender.equals("Female")) {
                        femaleRadioButton.setSelected(true);
                    }
                    genderPanel.add(maleRadioButton);
                    genderPanel.add(femaleRadioButton);
                    JTextField nationalityField = new JTextField(resultSet.getString("Astronaut_Nationality"));
                    JTextField medicalReportField = new JTextField(resultSet.getString("Astronaut_Medical_Report"));
                    JTextField trainingHistoryField = new JTextField(resultSet.getString("Astronaut_Training_History"));

                    formPanel.add(new JLabel("Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Age:"));
                    formPanel.add(ageField);
                    formPanel.add(new JLabel("Gender:"));
                    formPanel.add(genderPanel);
                    formPanel.add(new JLabel("Nationality:"));
                    formPanel.add(nationalityField);
                    formPanel.add(new JLabel("Medical Report:"));
                    formPanel.add(medicalReportField);
                    formPanel.add(new JLabel("Training History:"));
                    formPanel.add(trainingHistoryField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Astronaut", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        int age = Integer.parseInt(ageField.getText());
                        String newGender = maleRadioButton.isSelected() ? "Male" : "Female";
                        String nationality = nationalityField.getText();
                        String medicalReport = medicalReportField.getText();
                        String trainingHistory = trainingHistoryField.getText();

                        String updateQuery = "UPDATE astronaut SET Astronaut_Name = ?, Astronaut_Age = ?, Astronaut_Gender = ?, Astronaut_Nationality = ?, Astronaut_Medical_Report = ?, Astronaut_Training_History = ? WHERE Astronaut_ID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setInt(2, age);
                        updateStatement.setString(3, newGender);
                        updateStatement.setString(4, nationality);
                        updateStatement.setString(5, medicalReport);
                        updateStatement.setString(6, trainingHistory);
                        updateStatement.setString(7, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < astronautTableModel.getRowCount(); i++) {
                            if (astronautTableModel.getValueAt(i, 0).equals(id)) {
                                astronautTableModel.setValueAt(name, i, 1);
                                astronautTableModel.setValueAt(age, i, 2);
                                astronautTableModel.setValueAt(newGender, i, 3);
                                astronautTableModel.setValueAt(nationality, i, 4);
                                astronautTableModel.setValueAt(medicalReport, i, 5);
                                astronautTableModel.setValueAt(trainingHistory, i, 6);
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Astronaut ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating astronaut: " + e.getMessage());
            }
        }
    }

    private void updateDiscovery() {
        String id = JOptionPane.showInputDialog("Enter Discovery ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM discovery WHERE Discovery_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Discovery_Name"));
                    JTextField descriptionField = new JTextField(resultSet.getString("DISCOVERY_DESCRIPTION"));
                    JTextField dateField = new JTextField(resultSet.getString("DISCOVERY_DATE"));
                    JTextField significanceField = new JTextField(resultSet.getString("significance_level"));

                    formPanel.add(new JLabel("Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Description:"));
                    formPanel.add(descriptionField);
                    formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
                    formPanel.add(dateField);
                    formPanel.add(new JLabel("Significance:"));
                    formPanel.add(significanceField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Discovery", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        String description = descriptionField.getText();
                        String date = dateField.getText();
                        String significance = significanceField.getText();

                        String updateQuery = "UPDATE discovery SET Discovery_Name = ?, DISCOVERY_DESCRIPTION = ?, DISCOVERY_DATE = ?, significance_level = ? WHERE Discovery_ID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setString(2, description);
                        updateStatement.setString(3, date);
                        updateStatement.setString(4, significance);
                        updateStatement.setString(5, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < discoveryTableModel.getRowCount(); i++) {
                            if (discoveryTableModel.getValueAt(i, 0).equals(id)) {
                                discoveryTableModel.setValueAt(name, i, 1);
                                discoveryTableModel.setValueAt(description, i, 2);
                                discoveryTableModel.setValueAt(date, i, 3);
                                discoveryTableModel.setValueAt(significance, i, 4);
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Discovery ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating discovery: " + e.getMessage());
            }
        }
    }


    private void updateResourceSet() {
        String id = JOptionPane.showInputDialog("Enter Resource Set ID to update:");
        if (id != null && !id.isEmpty()) {
            try {
                String query = "SELECT * FROM resourceset WHERE Resource_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField quantityAvailableField = new JTextField(resultSet.getString("QuantityAvailable"));
                    JTextField consumptionRateField = new JTextField(resultSet.getString("ConsumptionRate"));
                    JTextField typeField = new JTextField(resultSet.getString("TYPE"));

                    formPanel.add(new JLabel("Quantity Available:"));
                    formPanel.add(quantityAvailableField);
                    formPanel.add(new JLabel("Consumption Rate:"));
                    formPanel.add(consumptionRateField);
                    formPanel.add(new JLabel("Type:"));
                    formPanel.add(typeField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Resource Set", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String quantityAvailable = quantityAvailableField.getText();
                        String consumptionRate = consumptionRateField.getText();
                        String type = typeField.getText();

                        String updateQuery = "UPDATE resourceset SET QuantityAvailable = ?, ConsumptionRate = ?, TYPE = ? WHERE Resource_ID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, quantityAvailable);
                        updateStatement.setString(2, consumptionRate);
                        updateStatement.setString(3, type);
                        updateStatement.setString(4, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < resourceSetTableModel.getRowCount(); i++) {
                            if (resourceSetTableModel.getValueAt(i, 0).equals(id)) {
                                resourceSetTableModel.setValueAt(quantityAvailable, i, 1);
                                resourceSetTableModel.setValueAt(consumptionRate, i, 2);
                                resourceSetTableModel.setValueAt(type, i, 3);
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Resource Set updated successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Resource Set ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating resource set: " + e.getMessage());
            }
        }
    }



    private void updateLocation() {
        String id = JOptionPane.showInputDialog("Enter Location ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM location WHERE Location_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Location_Name"));
                    JTextField distanceField = new JTextField(resultSet.getString("Distance"));
                    JTextField coordinatesField = new JTextField(resultSet.getString("Coordinates"));

                    formPanel.add(new JLabel("Location Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Distance:"));
                    formPanel.add(distanceField);
                    formPanel.add(new JLabel("Coordinates:"));
                    formPanel.add(coordinatesField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Location", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        String distance = distanceField.getText();
                        String coordinates = coordinatesField.getText();

                        String updateQuery = "UPDATE location SET Location_Name = ?, Distance = ?, Coordinates = ? WHERE Location_ID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setString(2, distance);
                        updateStatement.setString(3, coordinates);
                        updateStatement.setString(4, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < locationTableModel.getRowCount(); i++) {
                            if (locationTableModel.getValueAt(i, 0).equals(id)) {
                                locationTableModel.setValueAt(name, i, 1);
                                locationTableModel.setValueAt(distance, i, 2);
                                locationTableModel.setValueAt(coordinates,i,3);
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Location updated successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Location ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating location: " + e.getMessage());
            }
        }
    }



    private void updateMission() {
        String id = JOptionPane.showInputDialog("Enter Mission ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM mission WHERE Mission_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Mission_Name"));
                    JTextField dateField = new JTextField(resultSet.getString("Mission_Date"));
                    JTextField statusField = new JTextField(resultSet.getString("Mission_Status"));

                    formPanel.add(new JLabel("Mission Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Mission Date (YYYY-MM-DD):"));
                    formPanel.add(dateField);
                    formPanel.add(new JLabel("Mission Status:"));
                    formPanel.add(statusField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Mission", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        String date = dateField.getText();
                        String status = statusField.getText();

                        String updateQuery = "UPDATE mission SET Mission_Name = ?, Mission_Date = ?, Mission_Status = ? WHERE Mission_ID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setString(2, date);
                        updateStatement.setString(3, status);
                        updateStatement.setString(4, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < missionTableModel.getRowCount(); i++) {
                            if (missionTableModel.getValueAt(i, 0).equals(id)) {
                                missionTableModel.setValueAt(name, i, 1);
                                missionTableModel.setValueAt(date, i, 2);
                                missionTableModel.setValueAt(status, i, 3);
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Mission ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating mission: " + e.getMessage());
            }
        }
    }

    private void updateSpaceStation() {
        String id = JOptionPane.showInputDialog("Enter Space Station ID to update:");
        if (id != null) {
            try {
                String query = "SELECT * FROM space_station WHERE Space_StationID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JPanel formPanel = new JPanel(new GridLayout(0, 1));
                    JTextField nameField = new JTextField(resultSet.getString("Space_StationName"));
                    JTextField statusField = new JTextField(resultSet.getString("Operational_Status"));
                    JTextField capacityField = new JTextField(resultSet.getString("Capacity"));

                    formPanel.add(new JLabel("Space Station Name:"));
                    formPanel.add(nameField);
                    formPanel.add(new JLabel("Operational Status:"));
                    formPanel.add(statusField);
                    formPanel.add(new JLabel("Capacity:"));
                    formPanel.add(capacityField);

                    int result = JOptionPane.showConfirmDialog(null, formPanel, "Update Space Station", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String name = nameField.getText();
                        String status = statusField.getText();
                        String capacity = capacityField.getText();

                        String updateQuery = "UPDATE space_station SET Space_StationName = ?, Operational_Status = ?, Capacity = ? WHERE Space_StationID = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, name);
                        updateStatement.setString(2, status);
                        updateStatement.setString(3, capacity);
                        updateStatement.setString(4, id);
                        updateStatement.executeUpdate();

                        // Update the table model
                        for (int i = 0; i < stationTableModel.getRowCount(); i++) {
                            if (stationTableModel.getValueAt(i, 0).equals(id)) {
                                stationTableModel.setValueAt(name, i, 1);
                                stationTableModel.setValueAt(status, i, 2);
                                stationTableModel.setValueAt(capacity, i, 3);
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Space Station updated successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Space Station ID not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error updating space station: " + e.getMessage());
            }
        }
    }

    //                           QUERIES

    private void loadAstronautsByAgeAndGender(int age, String gender) {
        String query = "SELECT * FROM astronaut WHERE Astronaut_Age = ? AND Astronaut_Gender = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, age);
            pstmt.setString(2, gender);
            try (ResultSet rs = pstmt.executeQuery()) {
                astronautTableModel.setRowCount(0);
                while (rs.next()) {
                    astronautTableModel.addRow(new Object[]{
                            rs.getInt("Astronaut_ID"),
                            rs.getString("Astronaut_Name"),
                            rs.getInt("Astronaut_Age"),
                            rs.getString("Astronaut_Gender"),
                            rs.getString("Astronaut_Nationality"),
                            rs.getString("Astronaut_Medical_Report"),
                            rs.getString("Astronaut_Training_History"),
                            rs.getInt("MISSION_MISSION_ID"),
                            rs.getInt("SPACE_STATIONID")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAstronautsBySpaceStation(int spaceStationID) {
        String query = "SELECT * FROM astronaut WHERE SPACE_STATIONID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, spaceStationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                astronautTableModel.setRowCount(0);
                while (rs.next()) {
                    astronautTableModel.addRow(new Object[]{
                            rs.getInt("Astronaut_ID"),
                            rs.getString("Astronaut_Name"),
                            rs.getInt("Astronaut_Age"),
                            rs.getString("Astronaut_Gender"),
                            rs.getString("Astronaut_Nationality"),
                            rs.getString("Astronaut_Medical_Report"),
                            rs.getString("Astronaut_Training_History"),
                            rs.getInt("MISSION_MISSION_ID"),
                            rs.getInt("SPACE_STATIONID")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void checkExpiringResources(java.sql.Date date) {
        String query = "SELECT * FROM resourceset WHERE DATE_ADD(CURDATE(), INTERVAL ConsumptionRate DAY) <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Resource ID: " + rs.getInt("Resource_ID") + " is expiring.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void deleteCompletedMissions() {
        String query = "DELETE FROM mission WHERE Mission_Status = 'Completed'";
        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(query);
            System.out.println("Deleted " + rowsAffected + " completed missions.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUncompletedMissions() {
        String query = "SELECT * FROM mission WHERE Mission_Status != 'Completed'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            missionTableModel.setRowCount(0);
            while (rs.next()) {
                missionTableModel.addRow(new Object[]{
                        rs.getInt("Mission_ID"),
                        rs.getString("Mission_Name"),
                        rs.getDate("Mission_Date"),
                        rs.getString("Mission_Status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCompletedMissions() {
        String query = "SELECT * FROM mission WHERE Mission_Status = 'Completed'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            missionTableModel.setRowCount(0);
            while (rs.next()) {
                missionTableModel.addRow(new Object[]{
                        rs.getInt("Mission_ID"),
                        rs.getString("Mission_Name"),
                        rs.getDate("Mission_Date"),
                        rs.getString("Mission_Status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAstronautDiscoveriesAndExperiments(String astronautName) {
        String query = "SELECT d.Discovery_Name, e.Experiment_Name " +
                "FROM astronaut a " +
                "JOIN astronauts_performs_experiments ape ON a.Astronaut_ID = ape.Astronaut_Astronaut_ID " +
                "JOIN experiment e ON ape.Experiment_ExperimentID = e.ExperimentID " +
                "JOIN discovery d ON e.DISCOVERY_DISCOVERY_ID = d.Discovery_ID " +
                "WHERE a.Astronaut_Name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, astronautName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Discovery: " + rs.getString("Discovery_Name") +
                            ", Experiment: " + rs.getString("Experiment_Name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMissionEndDate(int missionID) {
        String query = "SELECT * FROM mission WHERE Mission_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, missionID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Mission ID: " + missionID +
                            ", Mission End Date: " + rs.getDate("Mission_Date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAdvancedTrainingAstronauts() {
        String query = "SELECT * FROM astronaut WHERE Astronaut_Training_History = 'Advanced Training'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            astronautTableModel.setRowCount(0);
            while (rs.next()) {
                astronautTableModel.addRow(new Object[]{
                        rs.getInt("Astronaut_ID"),
                        rs.getString("Astronaut_Name"),
                        rs.getInt("Astronaut_Age"),
                        rs.getString("Astronaut_Gender"),
                        rs.getString("Astronaut_Nationality"),
                        rs.getString("Astronaut_Medical_Report"),
                        rs.getString("Astronaut_Training_History"),
                        rs.getInt("MISSION_MISSION_ID"),
                        rs.getInt("SPACE_STATIONID")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAstronautsWithMissions() {
        String query = "SELECT a.Astronaut_ID, a.Astronaut_Name, m.Mission_Name " +
                "FROM astronaut a " +
                "JOIN mission m ON a.MISSION_MISSION_ID = m.Mission_ID";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            astronautTableModel.setRowCount(0);
            while (rs.next()) {
                astronautTableModel.addRow(new Object[]{
                        rs.getInt("Astronaut_ID"),
                        rs.getString("Astronaut_Name"),
                        rs.getString("Mission_Name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAstronautsWithExperiments() {
        String query = "SELECT a.Astronaut_Name, e.Experiment_Name, e.Experiment_Description " +
                "FROM astronauts_performs_experiments ape " +
                "JOIN astronaut a ON ape.Astronaut_Astronaut_ID = a.Astronaut_ID " +
                "JOIN experiment e ON ape.Experiment_ExperimentID = e.ExperimentID";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            experimentTableModel.setRowCount(0);
            while (rs.next()) {
                experimentTableModel.addRow(new Object[]{
                        rs.getString("Astronaut_Name"),
                        rs.getString("Experiment_Name"),
                        rs.getString("Experiment_Description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAstronautMissionView() {
        String createViewQuery = "CREATE VIEW astronaut_mission_view AS " +
                "SELECT a.Astronaut_ID, a.Astronaut_Name, m.Mission_Name " +
                "FROM astronaut a " +
                "JOIN mission m ON a.MISSION_MISSION_ID = m.Mission_ID";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createViewQuery);
            JOptionPane.showMessageDialog(null, "View 'astronaut_mission_view' created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createHighSignificanceDiscoveriesView() {
        String createViewQuery = "CREATE VIEW high_significance_discoveries AS " +
                "SELECT Discovery_Name, DISCOVERY_DATE, Discovery_Description " +
                "FROM discovery " +
                "WHERE significance_level = 'High'";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createViewQuery);
            JOptionPane.showMessageDialog(null, "View 'high_significance_discoveries' created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAstronautsNotInAdvancedTraining() {
        String query = "SELECT * FROM astronaut WHERE Astronaut_Training_History != 'Advanced Training'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            astronautTableModel.setRowCount(0);
            while (rs.next()) {
                astronautTableModel.addRow(new Object[]{
                        rs.getInt("Astronaut_ID"),
                        rs.getString("Astronaut_Name"),
                        rs.getInt("Astronaut_Age"),
                        rs.getString("Astronaut_Gender"),
                        rs.getString("Astronaut_Nationality"),
                        rs.getString("Astronaut_Medical_Report"),
                        rs.getString("Astronaut_Training_History"),
                        rs.getInt("MISSION_MISSION_ID"),
                        rs.getInt("SPACE_STATIONID")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExperimentsNotIn2024() {
        String query = "SELECT * FROM experiment WHERE YEAR(EXPERIMENT_DATE) != 2024";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            experimentTableModel.setRowCount(0);
            while (rs.next()) {
                experimentTableModel.addRow(new Object[]{
                        rs.getInt("ExperimentID"),
                        rs.getString("Experiment_Name"),
                        rs.getString("Experiment_Description"),
                        rs.getDate("EXPERIMENT_DATE")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFemaleOrAdvancedTrainingAstronauts() {
        String query = "SELECT * FROM astronaut WHERE Astronaut_Gender = 'Female' OR Astronaut_Training_History = 'Advanced Training'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            astronautTableModel.setRowCount(0);
            while (rs.next()) {
                astronautTableModel.addRow(new Object[]{
                        rs.getInt("Astronaut_ID"),
                        rs.getString("Astronaut_Name"),
                        rs.getInt("Astronaut_Age"),
                        rs.getString("Astronaut_Gender"),
                        rs.getString("Astronaut_Nationality"),
                        rs.getString("Astronaut_Medical_Report"),
                        rs.getString("Astronaut_Training_History"),
                        rs.getInt("MISSION_MISSION_ID"),
                        rs.getInt("SPACE_STATIONID")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHighOrMediumSignificanceDiscoveries() {
        String query = "SELECT * FROM discovery WHERE significance_level = 'High' OR significance_level = 'Medium'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            discoveryTableModel.setRowCount(0);
            while (rs.next()) {
                discoveryTableModel.addRow(new Object[]{
                        rs.getInt("Discovery_ID"),
                        rs.getString("Discovery_Name"),
                        rs.getString("Discovery_Description"),
                        rs.getDate("DISCOVERY_DATE"),
                        rs.getString("significance_level")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    //load functions

    private void loadAllInitialData() {
        loadExperiments();
        loadAstronauts();
        loadDiscoveries();
        loadResourceSets();
        loadLocations();
        loadSpaceStations();
        loadMissions();
    }

    private void loadExperiments() {
        try {
            String query = "SELECT * FROM experiment";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("ExperimentID");
                String name = resultSet.getString("Experiment_Name");
                String description = resultSet.getString("Experiment_Description");
                String date = resultSet.getString("EXPERIMENT_DATE");
                experimentTableModel.addRow(new Object[]{id, name, description, date});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading experiments: " + e.getMessage());
        }
    }

    private void loadAstronauts() {
        try {
            String query = "SELECT * FROM astronaut";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("Astronaut_ID");
                String name = resultSet.getString("Astronaut_Name");
                int age = resultSet.getInt("Astronaut_Age");
                String gender = resultSet.getString("Astronaut_Gender");
                String nationality = resultSet.getString("Astronaut_Nationality");
                String medicalReport = resultSet.getString("Astronaut_Medical_Report");
                String trainingHistory = resultSet.getString("Astronaut_Training_History");
                String Mid = resultSet.getString("MISSION_MISSION_ID");
                String STid = resultSet.getString("SPACE_STATIONID");
                astronautTableModel.addRow(new Object[]{id, name, age, gender, nationality, medicalReport, trainingHistory, Mid, STid});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading astronauts: " + e.getMessage());
        }
    }

    private void loadResourceSets() {
        try {
            String query = "SELECT * FROM resourceset";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("Resource_ID");
                int quantity = resultSet.getInt("QuantityAvailable");
                float consumptionRate = resultSet.getFloat("Consumptionrate");
                String type = resultSet.getString("TYPE");
                int STid = resultSet.getInt("space_station_space_stationID");
                resourceSetTableModel.addRow(new Object[]{id, quantity, consumptionRate, type, STid});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading resource sets: " + e.getMessage());
        }
    }

    private void loadLocations() {
        try {
            String query = "SELECT * FROM location";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("Location_ID");
                String name = resultSet.getString("Location_Name");
                float distance = resultSet.getFloat("Distance");
                String coordinates = resultSet.getString("Coordinates");
                String spaceStationId = resultSet.getString("Space_Stationid");
                locationTableModel.addRow(new Object[]{id, name, distance, coordinates, spaceStationId});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading locations: " + e.getMessage());
        }
    }

    private void loadMissions() {
        try {
            String query = "SELECT * FROM mission";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("Mission_ID");
                String name = resultSet.getString("Mission_Name");
                String date = resultSet.getString("Mission_Date");
                String status = resultSet.getString("Mission_Status");
                missionTableModel.addRow(new Object[]{id, name, date, status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading missions: " + e.getMessage());
        }
    }

    private void loadSpaceStations() {
        try {
            String query = "SELECT * FROM space_station";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int stationID = resultSet.getInt("Space_StationID");
                String stationName = resultSet.getString("Space_StationName");
                String operationalStatus = resultSet.getString("Operational_Status");
                int capacity = resultSet.getInt("Capacity");
                stationTableModel.addRow(new Object[]{stationID, stationName, operationalStatus, capacity});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading space stations: " + e.getMessage());
        }
    }

    private void loadDiscoveries() {
        try {
            String query = "SELECT * FROM discovery";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("Discovery_ID");
                String name = resultSet.getString("Discovery_Name");
                String description = resultSet.getString("DISCOVERY_DESCRIPTION");
                String date = resultSet.getString("DISCOVERY_DATE");
                String significance = resultSet.getString("significance_level");
                discoveryTableModel.addRow(new Object[]{id, name, description, date, significance});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading discoveries: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainInterface().setVisible(true));
    }
}
