package services;

import models.Employe;
import utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeService {
    private Connection conn = MyDb.getInstance().getConn();

    public void create(Employe employe) throws SQLException {
        String sql = "INSERT INTO Employe (nom, prenom, soldeConges) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employe.getNom());
            stmt.setString(2, employe.getPrenom());
            stmt.setInt(3, employe.getSoldeConges());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employe.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Employe employe) throws SQLException {
        String sql = "UPDATE Employe SET nom = ?, prenom = ?, soldeConges = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employe.getNom());
            stmt.setString(2, employe.getPrenom());
            stmt.setInt(3, employe.getSoldeConges());
            stmt.setInt(4, employe.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Employe employe) throws SQLException {
        String sql = "DELETE FROM Employe WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employe.getId());
            stmt.executeUpdate();
        }
    }

    public List<Employe> getAll() throws SQLException {
        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM Employe";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setSoldeConges(rs.getInt("soldeConges"));
                employes.add(e);
            }
        }
        return employes;
    }

    public Employe getEmployeById(int id) throws SQLException {
        String sql = "SELECT * FROM Employe WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Employe e = new Employe();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setSoldeConges(rs.getInt("soldeConges"));
                return e;
            }
            throw new SQLException("Employé non trouvé");
        }
    }
}