package services;

import models.Candidat;
import utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CandidatService {

    private Connection conn;

    public CandidatService() {
        this.conn = MyDb.getInstance().getConn();
    }

    public int createAndGetId(Candidat candidat) throws Exception {
        String sql = "INSERT INTO candidat (last_name, first_name, email, phone) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, candidat.getLastName());
        stmt.setString(2, candidat.getFirstName());
        stmt.setString(3, candidat.getEmail());
        stmt.setString(4, candidat.getPhone());
        stmt.executeUpdate();

        // Récupérer l'ID généré
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); // Retourne l'ID du candidat
        }
        throw new Exception("Échec de la récupération de l'ID du candidat.");
    }

    public boolean isEmailUsed(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM candidat WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public boolean isPhoneUsed(String phone) throws Exception {
        String sql = "SELECT COUNT(*) FROM candidat WHERE phone = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, phone);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public Candidat getById(int id) throws Exception {
        String sql = "SELECT * FROM candidat WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Candidat(
                    rs.getInt("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email"),
                    rs.getString("phone")
            );
        }
        return null;
    }

    public Candidat getByEmailAndPhone(String email, String phone) throws Exception {
        String sql = "SELECT * FROM candidat WHERE email = ? AND phone = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        stmt.setString(2, phone);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Candidat(
                    rs.getInt("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email"),
                    rs.getString("phone")
            );
        }
        return null;
    }

    public List<Candidat> getAll() throws Exception {
        String sql = "SELECT * FROM candidat";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        List<Candidat> candidats = new ArrayList<>();
        while (rs.next()) {
            Candidat candidat = new Candidat(
                    rs.getInt("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email"),
                    rs.getString("phone")
            );
            candidats.add(candidat);
        }
        return candidats;
    }
}