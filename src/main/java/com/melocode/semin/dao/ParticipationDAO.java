package com.melocode.semin.dao;

import com.melocode.semin.models.Participation;
import com.melocode.semin.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO {

    // Fetch all participations from the database
    public List<Participation> getAllParticipations() {
        List<Participation> participations = new ArrayList<>();
        String query = "SELECT ID_Participation, ID_Employe, ID_Seminaire, Statut, Date_inscription, Evaluation, Certificat FROM participationseminaire";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Participation participation = new Participation(
                        rs.getInt("ID_Participation"),
                        rs.getInt("ID_Employe"),
                        rs.getInt("ID_Seminaire"),
                        rs.getString("Statut"),
                        rs.getDate("Date_inscription").toLocalDate(),
                        rs.getString("Evaluation"),
                        rs.getString("Certificat")
                );
                participations.add(participation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    // Add a new participation to the database
    public void ajouterParticipation(Participation participation) {
        String query = "INSERT INTO participationseminaire (ID_Employe, ID_Seminaire, Statut, Date_inscription, Evaluation, Certificat) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, participation.getID_Employe());
            ps.setInt(2, participation.getID_Seminaire());
            ps.setString(3, participation.getStatut());
            ps.setDate(4, Date.valueOf(participation.getDate_inscription()));
            ps.setString(5, participation.getEvaluation());
            ps.setString(6, participation.getCertificat());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void modifierParticipation(Participation participation) {
        String query = "UPDATE participationseminaire SET statut=?, evaluation=?, certificat=? WHERE ID_Participation=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, participation.getStatut());
            stmt.setString(2, participation.getEvaluation());
            stmt.setString(3, participation.getCertificat());
            stmt.setInt(4, participation.getID_Participation());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a participation from the database
    public void supprimerParticipation(int idParticipation) {
        String query = "DELETE FROM participationseminaire WHERE ID_Participation = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idParticipation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
