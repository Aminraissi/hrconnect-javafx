package services;

import models.OffreEmploi;
import utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OffreEmploiService implements Crud<OffreEmploi> {
    Connection conn;

    public OffreEmploiService() {
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public void create(OffreEmploi obj) throws Exception {
        String sql = "INSERT INTO offre_emploi (title, description, location) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getTitle());
        stmt.setString(2, obj.getDescription());
        stmt.setString(3, obj.getLocation());
        stmt.executeUpdate();
    }

    @Override
    public void update(OffreEmploi obj) throws Exception {
        String sql = "UPDATE offre_emploi SET title = ?, description = ?, location = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getTitle());
        stmt.setString(2, obj.getDescription());
        stmt.setString(3, obj.getLocation());
        stmt.setInt(4, obj.getId());
        stmt.executeUpdate();
    }

    @Override
    public void delete(OffreEmploi obj) throws Exception {
        String sql = "DELETE FROM offre_emploi WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, obj.getId());
        stmt.executeUpdate();
    }

    @Override
    public List<OffreEmploi> getAll() throws Exception {
        String sql = "SELECT * FROM offre_emploi";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        List<OffreEmploi> offres = new ArrayList<>();
        while (rs.next()) {
            OffreEmploi offre = new OffreEmploi();
            offre.setId(rs.getInt("id"));
            offre.setTitle(rs.getString("title"));
            offre.setDescription(rs.getString("description"));
            offre.setLocation(rs.getString("location"));
            offres.add(offre);
        }
        return offres;
    }

    public OffreEmploi getById(int id) throws Exception {
        String sql = "SELECT * FROM offre_emploi WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            OffreEmploi offre = new OffreEmploi();
            offre.setId(rs.getInt("id"));
            offre.setTitle(rs.getString("title"));
            offre.setDescription(rs.getString("description"));
            offre.setLocation(rs.getString("location"));
            return offre;
        }
        return null;
    }

    // Méthode pour récupérer une offre d'emploi par titre
    public OffreEmploi getByTitle(String title) throws Exception {
        String sql = "SELECT * FROM offre_emploi WHERE title = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, title);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            OffreEmploi offre = new OffreEmploi();
            offre.setId(rs.getInt("id"));
            offre.setTitle(rs.getString("title"));
            offre.setDescription(rs.getString("description"));
            offre.setLocation(rs.getString("location"));
            return offre;
        }
        return null;
    }
}