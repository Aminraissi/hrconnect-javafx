package services;

import models.Demande_Conge;
import models.StatutDemande;
import models.TypeConge;
import utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeCongeService {
    private Connection conn = MyDb.getInstance().getConn();

    public void create(Demande_Conge demande) throws SQLException {
        String sql = "INSERT INTO Demande_Conge (employe_id, typeConge, dateDebut, dateFin, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, demande.getEmploye().getId());
            stmt.setString(2, demande.getTypeConge().name());
            stmt.setDate(3, Date.valueOf(demande.getDateDebut()));
            stmt.setDate(4, Date.valueOf(demande.getDateFin()));
            stmt.setString(5, demande.getStatut().name());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    demande.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateStatut(int demandeId, StatutDemande statut) throws SQLException {
        String sql = "UPDATE Demande_Conge SET statut = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setInt(2, demandeId);
            stmt.executeUpdate();
        }
    }

    public List<Demande_Conge> getAll() throws SQLException {
        List<Demande_Conge> demandes = new ArrayList<>();
        String sql = "SELECT d.*, v.commentaire AS validation_commentaire " +
                "FROM Demande_Conge d " +
                "LEFT JOIN Valider_Conge v ON d.id = v.demande_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Demande_Conge demande = new Demande_Conge();
                demande.setId(rs.getInt("id"));
                demande.setTypeConge(TypeConge.valueOf(rs.getString("typeConge")));
                demande.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                demande.setDateFin(rs.getDate("dateFin").toLocalDate());
                demande.setStatut(StatutDemande.valueOf(rs.getString("statut")));

                // Récupérer le commentaire de validation
                String validationCommentaire = rs.getString("validation_commentaire");
                demande.setValidationCommentaire(validationCommentaire);

                EmployeService employeService = new EmployeService();
                demande.setEmploye(employeService.getEmployeById(rs.getInt("employe_id")));

                demandes.add(demande);
            }
        }
        return demandes;
    }

    public void delete(Demande_Conge demande) throws SQLException {
        String sql = "DELETE FROM Demande_Conge WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, demande.getId());
            stmt.executeUpdate();
        }
    }
}