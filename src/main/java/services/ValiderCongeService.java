package services;

import models.Demande_Conge;
import models.StatutDemande;
import models.Valider_Conge;
import utils.MyDb;

import java.sql.*;
import java.time.LocalDate;

public class ValiderCongeService {
    private Connection conn = MyDb.getInstance().getConn();

    public void createValidation(Valider_Conge validation) throws SQLException {
        String sql = "INSERT INTO Valider_Conge (demande_id, statut, commentaire, dateValidation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, validation.getDemande().getId());
            stmt.setString(2, validation.getStatut().name());
            stmt.setString(3, validation.getCommentaire());
            stmt.setDate(4, Date.valueOf(validation.getDateValidation()));
            stmt.executeUpdate();
        }

        // Mettre à jour le statut de la demande
        DemandeCongeService demandeService = new DemandeCongeService();
        demandeService.updateStatut(validation.getDemande().getId(), validation.getStatut());
    }
}