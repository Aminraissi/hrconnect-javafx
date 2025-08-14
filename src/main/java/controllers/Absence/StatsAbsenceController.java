package controllers.Absence;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import models.Absence;
import models.Employe;
import services.AbsenceService;
import services.EmployeService;
import utils.ShowMenu;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatsAbsenceController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private PieChart pieChart;

    @FXML
    private BarChart<String, Number> barChart;

    AbsenceService absenceService = new AbsenceService();
    EmployeService employeService = new EmployeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        try {
            List<Absence> absences = absenceService.getAllAbsences();
            Map<String, Integer> motifStats = new HashMap<>();
            Map<String, Integer> employeStats = new HashMap<>();

            for (Absence absence : absences) {
                String motif = absence.getMotif().name();
                motifStats.put(motif, motifStats.getOrDefault(motif, 0) + 1);

                Employe employe = employeService.getEmployeById(absence.getEmployeId());
                String employeName = employe.getNom() + " " + employe.getPrenom();
                employeStats.put(employeName, employeStats.getOrDefault(employeName, 0) + 1);
            }

            for (Map.Entry<String, Integer> entry : motifStats.entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : employeStats.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onBack() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Absence/listeAbsence.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pieChart.getScene().setRoot(root);
    }
}