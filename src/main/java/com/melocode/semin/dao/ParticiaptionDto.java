package com.melocode.semin.dao;

import com.melocode.semin.models.Participation;
import com.melocode.semin.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ParticiaptionDto {

    // Fetch All Participations
    public static List<Participation> getAllParticipations() {
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

    // Search Participations based on filters
    public static List<Participation> searchParticipations(String statut, LocalDate dateDebut, LocalDate dateFin, String certificat) {
        List<Participation> participations = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT ID_Participation, ID_Employe, ID_Seminaire, Statut, Date_inscription, Evaluation, Certificat FROM participationseminaire WHERE 1=1");

        if (statut != null && !statut.isEmpty()) {
            query.append(" AND Statut LIKE ?");
        }
        if (dateDebut != null) {
            query.append(" AND Date_inscription >= ?");
        }
        if (dateFin != null) {
            query.append(" AND Date_inscription <= ?");
        }
        if (certificat != null && !certificat.isEmpty()) {
            query.append(" AND Certificat LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (statut != null && !statut.isEmpty()) {
                ps.setString(index++, "%" + statut + "%");
            }
            if (dateDebut != null) {
                ps.setDate(index++, Date.valueOf(dateDebut));
            }
            if (dateFin != null) {
                ps.setDate(index++, Date.valueOf(dateFin));
            }
            if (certificat != null && !certificat.isEmpty()) {
                ps.setString(index++, "%" + certificat + "%");
            }

            ResultSet rs = ps.executeQuery();
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

    // Add a new Participation
    public static void ajouterParticipation(Participation participation) {
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

    // Delete a Participation
    public static void deleteParticipation(int idParticipation) {
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
