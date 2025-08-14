package com.melocode.semin.dao;

import com.melocode.semin.models.Seminaire;
import com.melocode.semin.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeminaireDAO {

    // Adding seminar
    public static void ajouterSeminaire(Seminaire seminaire) {
        String query = "INSERT INTO seminaire (Nom_Seminaire, Description, Date_debut, Date_fin, Lieu, Formateur, Cout, Type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, seminaire.getNom());
            stmt.setString(2, seminaire.getDescription());
            stmt.setDate(3, Date.valueOf(seminaire.getDateDebut()));
            stmt.setDate(4, Date.valueOf(seminaire.getDateFin()));
            stmt.setString(5, seminaire.getLieu());
            stmt.setString(6, seminaire.getFormateur());
            stmt.setDouble(7, seminaire.getCout());
            stmt.setString(8, seminaire.getType());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                seminaire.setId(generatedKeys.getInt(1)); // Get auto-generated ID
            }

            System.out.println("Séminaire ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du séminaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Deleting seminar
    public static void supprimerSeminaire(int id) {
        String query = "DELETE FROM seminaire WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Séminaire supprimé avec succès !");
            } else {
                System.out.println("Aucun séminaire trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du séminaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Retrieve all seminars
    public static ObservableList<Seminaire> recupererTousLesSeminaires() {
        ObservableList<Seminaire> seminaireList = FXCollections.observableArrayList();
        String query = "SELECT ID_Seminaire AS id, Nom_Seminaire, Description, Date_debut, Date_fin, Lieu, Formateur, Cout, Type FROM seminaire";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Seminaire seminaire = new Seminaire(
                        rs.getInt("id"),
                        rs.getString("Nom_Seminaire"),
                        rs.getString("Description"),
                        rs.getDate("Date_debut").toLocalDate(),
                        rs.getDate("Date_fin").toLocalDate(),
                        rs.getString("Lieu"),
                        rs.getString("Formateur"),
                        rs.getDouble("Cout"),
                        rs.getString("Type")
                );
                seminaireList.add(seminaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séminaires : " + e.getMessage());
        }
        return seminaireList;
    }

    // Searching seminars by name and date
    public static ObservableList<Seminaire> searchSeminaire(String nom, LocalDate dateDebut, LocalDate dateFin) {
        ObservableList<Seminaire> seminaireList = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder("SELECT ID_Seminaire AS id, Nom_Seminaire, Description, Date_debut, Date_fin, Lieu, Formateur, Cout, Type FROM seminaire WHERE 1=1");

        if (nom != null && !nom.isEmpty()) {
            query.append(" AND Nom_Seminaire LIKE ? ");
        }
        if (dateDebut != null) {
            query.append(" AND Date_debut >= ? ");
        }
        if (dateFin != null) {
            query.append(" AND Date_fin <= ? ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int index = 1;
            if (nom != null && !nom.isEmpty()) {
                stmt.setString(index++, "%" + nom + "%");
            }
            if (dateDebut != null) {
                stmt.setDate(index++, Date.valueOf(dateDebut));
            }
            if (dateFin != null) {
                stmt.setDate(index++, Date.valueOf(dateFin));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Seminaire seminaire = new Seminaire(
                        rs.getInt("id"),
                        rs.getString("Nom_Seminaire"),
                        rs.getString("Description"),
                        rs.getDate("Date_debut").toLocalDate(),
                        rs.getDate("Date_fin").toLocalDate(),
                        rs.getString("Lieu"),
                        rs.getString("Formateur"),
                        rs.getDouble("Cout"),
                        rs.getString("Type")
                );
                seminaireList.add(seminaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des séminaires : " + e.getMessage());
        }
        return seminaireList;
    }

    // Update seminar
    public static void modifierSeminaire(Seminaire seminaire) {
        String query = "UPDATE seminaire SET Nom_Seminaire = ?, Description = ?, Date_debut = ?, Date_fin = ?, Lieu = ?, Formateur = ?, Cout = ?, Type = ? WHERE ID_Seminaire = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, seminaire.getNom());
            stmt.setString(2, seminaire.getDescription());
            stmt.setDate(3, Date.valueOf(seminaire.getDateDebut()));
            stmt.setDate(4, Date.valueOf(seminaire.getDateFin()));
            stmt.setString(5, seminaire.getLieu());
            stmt.setString(6, seminaire.getFormateur());
            stmt.setDouble(7, seminaire.getCout());
            stmt.setString(8, seminaire.getType());
            stmt.setInt(9, seminaire.getId());

            stmt.executeUpdate();
            System.out.println("Séminaire mis à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du séminaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get statistics about seminars (for example: total count and average cost)
    public static String getSeminaireStatistics() {
        String statistics = "";
        String query = "SELECT COUNT(*) AS count, AVG(Cout) AS average_cost FROM seminaire";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int count = rs.getInt("count");
                double averageCost = rs.getDouble("average_cost");
                statistics = "Total Seminars: " + count + ", Average Cost: " + averageCost;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des statistiques : " + e.getMessage());
        }
        return statistics;
    }

    public List<Seminaire> getAllSeminaires() {
        List<Seminaire> seminaires = new ArrayList<>();
        try {Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM seminaire";  // Assuming you have a table `seminaire`
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Seminaire seminaire = new Seminaire(
                        rs.getInt("ID_Seminaire"),
                        rs.getString("Nom_Seminaire"),
                        rs.getString("Description"),
                        rs.getDate("Date_debut").toLocalDate(),
                        rs.getDate("Date_fin").toLocalDate(),
                        rs.getString("Lieu"),
                        rs.getString("Formateur"),
                        rs.getDouble("Cout"),
                        rs.getString("Type")
                );
                seminaires.add(seminaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seminaires;
    }
    public Seminaire getSeminarByName(String seminarName) {
        Seminaire seminar = null;
        String query = "SELECT * FROM seminaire WHERE Nom_Seminaire = ?";  // Adjust table and column names as needed

        try (Connection conn = DatabaseConnection. getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, seminarName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Assuming your Seminaire class has a constructor that accepts all these fields
                seminar = new Seminaire(
                        rs.getInt("ID_Seminaire"),
                        rs.getString("Nom_Seminaire"),
                        rs.getString("Description"),
                        rs.getDate("Date_debut").toLocalDate(),
                        rs.getDate("Date_fin").toLocalDate(),
                        rs.getString("Lieu"),
                        rs.getString("Formateur"),
                        rs.getDouble("Cout"),
                        rs.getString("Type")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seminar;
    }
    public static List<Seminaire> getSeminarsByDate(LocalDate date) {
        List<Seminaire> seminars = new ArrayList<>();
        String query = "SELECT ID_Seminaire, Nom_Seminaire, Description, Date_debut, Date_fin, Lieu, Formateur, Cout, Type " +
                "FROM seminaire WHERE ? BETWEEN Date_debut AND Date_fin";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date)); // Bind the date parameter
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Seminaire seminaire = new Seminaire(
                        rs.getInt("ID_Seminaire"),
                        rs.getString("Nom_Seminaire"),
                        rs.getString("Description"),
                        rs.getDate("Date_debut").toLocalDate(),
                        rs.getDate("Date_fin").toLocalDate(),
                        rs.getString("Lieu"),
                        rs.getString("Formateur"),
                        rs.getDouble("Cout"),
                        rs.getString("Type")
                );
                seminars.add(seminaire);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séminaires par date : " + e.getMessage());
            e.printStackTrace();
        }
        return seminars;
    }


}
