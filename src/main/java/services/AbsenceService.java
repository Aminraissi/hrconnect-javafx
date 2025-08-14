package services;

import models.Absence;
import utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbsenceService {
    private Connection conn = MyDb.getInstance().getConn();

    public void createAbsence(Absence absence) throws SQLException {
        String sql = "INSERT INTO absence (employe_id, motif, justificatif, remarque, date_enregistrement) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, absence.getEmployeId());
            stmt.setString(2, absence.getMotif().name());
            stmt.setString(3, absence.getJustificatif());
            stmt.setString(4, absence.getRemarque());
            stmt.setTimestamp(5, absence.getDateEnregistrement());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    absence.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateAbsence(Absence absence) throws SQLException {
        String sql = "UPDATE absence SET employe_id = ?, motif = ?, justificatif = ?, remarque = ?, date_enregistrement = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, absence.getEmployeId());
            stmt.setString(2, absence.getMotif().name());
            stmt.setString(3, absence.getJustificatif());
            stmt.setString(4, absence.getRemarque());
            stmt.setTimestamp(5, absence.getDateEnregistrement());
            stmt.setInt(6, absence.getId());
            stmt.executeUpdate();
        }
    }

    public Absence getAbsenceById(int id) throws SQLException {
        String sql = "SELECT * FROM absence WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Absence absence = new Absence();
                    absence.setId(rs.getInt("id"));
                    absence.setEmployeId(rs.getInt("employe_id"));
                    absence.setMotif(Absence.Motif.valueOf(rs.getString("motif")));
                    absence.setJustificatif(rs.getString("justificatif"));
                    absence.setRemarque(rs.getString("remarque"));
                    absence.setDateEnregistrement(rs.getTimestamp("date_enregistrement"));
                    return absence;
                }
            }
        }
        return null;
    }

    public void deleteAbsence(int id) throws SQLException {
        String sql = "DELETE FROM absence WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Absence> getAllAbsences() throws SQLException {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT * FROM absence";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Absence absence = new Absence();
                absence.setId(rs.getInt("id"));
                absence.setEmployeId(rs.getInt("employe_id"));
                absence.setMotif(Absence.Motif.valueOf(rs.getString("motif")));
                absence.setJustificatif(rs.getString("justificatif"));
                absence.setRemarque(rs.getString("remarque"));
                absence.setDateEnregistrement(rs.getTimestamp("date_enregistrement"));
                absences.add(absence);
            }
        }
        return absences;
    }
}