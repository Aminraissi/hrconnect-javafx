package services;

import models.Candidature;
import utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CandidatureService {

    private Connection conn;

    public CandidatureService() {
        this.conn = MyDb.getInstance().getConn();
    }

    // Méthode pour créer une candidature
    public void create(Candidature candidature) throws Exception {
        // Générer une référence unique
        String reference = generateUniqueReference();
        candidature.setReference(reference);
        candidature.setStatus("En cours");

        // Insérer dans la table candidature
        String sql = "INSERT INTO candidature (candidat_id, offre_emploi_id, cv, reference, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, candidature.getCandidatId());
        stmt.setInt(2, candidature.getOffreEmploiId());
        stmt.setString(3, candidature.getCv());
        stmt.setString(4, candidature.getReference());
        stmt.setString(5, candidature.getStatus());
        stmt.executeUpdate();

        // Insérer dans l'historique
        updateCandidatureStatus(reference, "En cours");
    }

    private String generateUniqueReference() throws Exception {
        String reference;
        boolean isUnique = false;
        do {
            // Générer une référence de 8 caractères (lettres majuscules et chiffres)
            reference = "CAN" + String.format("%05d", (int)(Math.random() * 100000));

            // Vérifier si la référence existe déjà
            String sql = "SELECT COUNT(*) FROM candidature WHERE reference = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reference);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                isUnique = rs.getInt(1) == 0;
            }
        } while (!isUnique);

        return reference;
    }

    // Méthode pour mettre à jour une candidature
    public boolean update(Candidature candidature) {
        try {
            if (candidature == null) {
                throw new IllegalArgumentException("La candidature ne peut pas être nulle");
            }

            // Mise à jour directe sans passer par updateCandidatureStatus
            String sql = "UPDATE candidature SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, candidature.getStatus());
            stmt.setInt(2, candidature.getId());

            System.out.println("Mise à jour candidature - Status: " + candidature.getStatus() +
                    ", ID: " + candidature.getId());

            int rowsAffected = stmt.executeUpdate();

            // Mettre à jour l'historique si nécessaire
            if (rowsAffected > 0 && candidature.getReference() != null) {
                updateCandidatureStatus(candidature.getReference(), candidature.getStatus());
            }

            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("ERREUR lors de la mise à jour de la candidature: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour de la candidature", e);
        }
    }

    // Méthode pour récupérer toutes les candidatures
    public List<Candidature> getAllCandidatures() throws Exception {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT * FROM candidature";

        try {
            // Debug de la connexion
            if (conn == null || conn.isClosed()) {
                System.out.println("Error: Database connection is null or closed!");
                return candidatures;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Debug de la requête
            System.out.println("Executing SQL: " + sql);

            while (rs.next()) {
                Candidature candidature = new Candidature();
                candidature.setId(rs.getInt("id"));
                candidature.setCandidatId(rs.getInt("candidat_id"));
                candidature.setOffreEmploiId(rs.getInt("offre_emploi_id"));
                candidature.setCv(rs.getString("cv"));
                candidature.setReference(rs.getString("reference"));
                candidature.setStatus(rs.getString("status"));

                // Debug des données
                System.out.println("Found candidature: " +
                        "ID=" + candidature.getId() +
                        ", CandidatID=" + candidature.getCandidatId() +
                        ", OffreID=" + candidature.getOffreEmploiId());

                candidatures.add(candidature);
            }

            System.out.println("Total candidatures found: " + candidatures.size());

        } catch (Exception e) {
            System.out.println("Error in getAllCandidatures: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return candidatures;
    }

    // Méthode pour récupérer les candidatures par offre_emploi_id
    public List<Candidature> getCandidaturesByOffreEmploiId(int offreEmploiId) throws Exception {
        String sql = "SELECT * FROM candidature WHERE offre_emploi_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, offreEmploiId);
        ResultSet rs = stmt.executeQuery();
        List<Candidature> candidatures = new ArrayList<>();
        while (rs.next()) {
            Candidature candidature = new Candidature(
                    rs.getInt("id"),
                    rs.getInt("candidat_id"),
                    rs.getInt("offre_emploi_id"),
                    rs.getString("cv")
            );
            candidatures.add(candidature);
        }
        return candidatures;
    }

    // Méthode pour récupérer les candidatures par candidat_id
    public List<Candidature> getCandidaturesByCandidatId(int candidatId) throws Exception {
        String sql = "SELECT * FROM candidature WHERE candidat_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, candidatId);
        ResultSet rs = stmt.executeQuery();
        List<Candidature> candidatures = new ArrayList<>();
        while (rs.next()) {
            Candidature candidature = new Candidature(
                    rs.getInt("id"),
                    rs.getInt("candidat_id"),
                    rs.getInt("offre_emploi_id"),
                    rs.getString("cv")
            );
            candidatures.add(candidature);
        }
        return candidatures;
    }

    // Méthode pour supprimer une candidature
    public void delete(Candidature candidature) throws Exception {
        String sql = "DELETE FROM candidature WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, candidature.getId());
        stmt.executeUpdate();
    }

    // Méthode pour supprimer les candidatures par offre_emploi_id
    public void deleteByOffreEmploiId(int offreEmploiId) throws Exception {
        // D'abord, récupérer toutes les références des candidatures à supprimer
        String sqlSelect = "SELECT reference, status FROM candidature WHERE offre_emploi_id = ?";
        PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
        stmtSelect.setInt(1, offreEmploiId);
        ResultSet rs = stmtSelect.executeQuery();

        while (rs.next()) {
            String reference = rs.getString("reference");
            String status = rs.getString("status");
            // Mettre à jour l'historique avant la suppression
            updateCandidatureStatus(reference, status);
        }

        // Ensuite, supprimer les candidatures
        String sqlDelete = "DELETE FROM candidature WHERE offre_emploi_id = ?";
        PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
        stmtDelete.setInt(1, offreEmploiId);
        stmtDelete.executeUpdate();
    }

    public List<Candidature> getCandidaturesByCandidat(int candidatId) throws Exception {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT * FROM candidature WHERE candidat_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, candidatId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Candidature candidature = new Candidature();
            candidature.setId(rs.getInt("id"));
            candidature.setCandidatId(rs.getInt("candidat_id"));
            candidature.setOffreEmploiId(rs.getInt("offre_emploi_id"));
            candidature.setCv(rs.getString("cv"));
            candidature.setReference(rs.getString("reference"));
            candidature.setStatus(rs.getString("status"));
            candidatures.add(candidature);
        }

        return candidatures;
    }

    public Candidature getCandidatureByReference(String reference) throws Exception {
        String sql = "SELECT * FROM candidature WHERE reference = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, reference);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Candidature candidature = new Candidature();
            candidature.setId(rs.getInt("id"));
            candidature.setCandidatId(rs.getInt("candidat_id"));
            candidature.setOffreEmploiId(rs.getInt("offre_emploi_id"));
            candidature.setCv(rs.getString("cv"));
            candidature.setReference(rs.getString("reference"));
            candidature.setStatus(rs.getString("status"));
            return candidature;
        }

        return null;
    }

    public Candidature getLatestCandidatureByCandidat(int candidatId) throws Exception {
        String sql = "SELECT * FROM candidature WHERE candidat_id = ? ORDER BY id DESC LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, candidatId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Candidature candidature = new Candidature();
            candidature.setId(rs.getInt("id"));
            candidature.setCandidatId(rs.getInt("candidat_id"));
            candidature.setOffreEmploiId(rs.getInt("offre_emploi_id"));
            candidature.setCv(rs.getString("cv"));
            candidature.setReference(rs.getString("reference"));
            candidature.setStatus(rs.getString("status"));
            return candidature;
        }
        return null;
    }

    public void updateCandidatureStatus(String reference, String status) throws Exception {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence ne peut pas être nulle");
        }

        // Vérifier si la candidature existe dans l'historique
        String checkSql = "SELECT reference FROM historique_candidatures WHERE reference = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, reference);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // Mise à jour si existe
            String updateSql = "UPDATE historique_candidatures SET status = ? WHERE reference = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, status);
            updateStmt.setString(2, reference);
            updateStmt.executeUpdate();
        } else {
            // Insertion si n'existe pas
            String insertSql = "INSERT INTO historique_candidatures (reference, status) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, reference);
            insertStmt.setString(2, status);
            insertStmt.executeUpdate();
        }
    }

    public String getCandidatureStatus(String reference) throws Exception {
        // D'abord, essayer de trouver dans la table candidature
        String sqlCandidature = "SELECT status FROM candidature WHERE reference = ?";
        PreparedStatement stmtCandidature = conn.prepareStatement(sqlCandidature);
        stmtCandidature.setString(1, reference);
        ResultSet rsCandidature = stmtCandidature.executeQuery();

        if (rsCandidature.next()) {
            return rsCandidature.getString("status");
        }

        // Si non trouvée, chercher dans l'historique
        String sqlHistorique = "SELECT status FROM historique_candidatures WHERE reference = ?";
        PreparedStatement stmtHistorique = conn.prepareStatement(sqlHistorique);
        stmtHistorique.setString(1, reference);
        ResultSet rsHistorique = stmtHistorique.executeQuery();

        if (rsHistorique.next()) {
            return rsHistorique.getString("status");
        }

        return null;
    }

    public void updateCandidatureStatus(int candidatureId, String newStatus) throws Exception {
        // Récupérer d'abord la candidature pour obtenir sa référence
        String getRefSql = "SELECT reference FROM candidature WHERE id = ?";
        PreparedStatement getRefStmt = conn.prepareStatement(getRefSql);
        getRefStmt.setInt(1, candidatureId);
        ResultSet rs = getRefStmt.executeQuery();

        if (rs.next()) {
            String reference = rs.getString("reference");
            // Utiliser la méthode existante qui prend une référence
            updateCandidatureStatus(reference, newStatus);
        } else {
            throw new Exception("Aucune candidature trouvée avec l'ID: " + candidatureId);
        }
    }
}