package services;

import models.Formateur;
import models.Formation;
import utils.MyDb;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class FormateurService implements Crud<Formateur> {

    Connection conn;

    public FormateurService(){
        this.conn = MyDb.getInstance().getConn();
    }


    @Override
    public void create(Formateur obj) throws Exception {

    }

    @Override
    public void update(Formateur obj) throws Exception {

    }

    @Override
    public void delete(Formateur obj) throws Exception {

    }

    @Override
    public List<Formateur> getAll() throws Exception {
        String sql = "select * from formateurs";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Formateur> formateurs = new ArrayList<>();
        while (rs.next()) {
            Formateur f = new Formateur();
            f.setId(rs.getInt("id"));
            f.setFirstName(rs.getString("first_name"));
            f.setLastName(rs.getString("last_name"));
            formateurs.add(f);
        }
        return formateurs;
    }

    public  Formateur getById(int id) throws SQLException {
        String sql = "select * from formateurs where id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Formateur f = new Formateur();
        if (rs.next()) {
            f.setId(rs.getInt("id"));
            f.setFirstName(rs.getString("first_name"));
            f.setLastName(rs.getString("last_name"));
        }
        return f;
    }
}
