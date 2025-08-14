package services;

import models.Formation;
import models.Quiz;
import models.QuizReponse;
import utils.MyDb;
import utils.enums.QuizType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuizService implements Crud<Quiz> {

    Connection conn;

    public QuizService(){
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public void create(Quiz obj) throws Exception {
        String sql = "insert into quiz (formation_id,question,reponse1,reponse2,reponse3,num_reponse_correct) VALUES (?,?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,obj.getFormation_id());
        stmt.setString(2, obj.getQuestion());
        stmt.setString(3, obj.getReponse1());
        stmt.setString(4, obj.getReponse2());
        stmt.setString(5, obj.getReponse3());
        stmt.setInt(6, obj.getNumRepCorrect());
        stmt.executeUpdate();
    }

    @Override
    public void update(Quiz obj) throws Exception {
        String sql = "update  quiz set question = ? , reponse1 = ? , reponse2 = ? , reponse3 = ? , num_reponse_correct = ? where id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getQuestion());
        stmt.setString(2, obj.getReponse1());
        stmt.setString(3, obj.getReponse2());
        stmt.setString(4, obj.getReponse3());
        stmt.setInt(5, obj.getNumRepCorrect());
        stmt.setInt(6,obj.getId());
        stmt.executeUpdate();
    }

    @Override
    public void delete(Quiz obj) throws Exception {

    }

    public void delete(int id) throws Exception {
        String sql = "delete from quiz where id = ?";
        PreparedStatement  stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    @Override
    public List<Quiz> getAll() throws Exception {
        String sql = "select * from quiz ORDER BY id DESC";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Quiz> quizs = new ArrayList<>();
        while (rs.next()) {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setQuestion(rs.getString("question"));
            quiz.setReponse1(rs.getString("reponse1"));
            quiz.setReponse2(rs.getString("reponse2"));
            quiz.setReponse3(rs.getString("reponse3"));
            quiz.setNumRepCorrect(rs.getInt("num_reponse_correct"));
            quizs.add(quiz);
        }
        return quizs;
    }

    public List<Quiz> getAll(int formationId) throws Exception {
        String sql = "select * from quiz where formation_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, formationId);
        ResultSet rs = stmt.executeQuery();
        List<Quiz> quizs = new ArrayList<>();
        while (rs.next()) {
            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("id"));
            quiz.setQuestion(rs.getString("question"));
            quiz.setReponse1(rs.getString("reponse1"));
            quiz.setReponse2(rs.getString("reponse2"));
            quiz.setReponse3(rs.getString("reponse3"));
            quiz.setNumRepCorrect(rs.getInt("num_reponse_correct"));
            quizs.add(quiz);
        }
        return quizs;
    }

    public void enregisterReponse(int employe_id,int quiz_id,int num_reponse) throws Exception {
        String sql = "insert into quiz_reponses (employe_id,quiz_id,num_reponse) VALUES (?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,employe_id);
        stmt.setInt(2,quiz_id);
        stmt.setInt(3,num_reponse);
        stmt.executeUpdate();
    }

    public List<QuizReponse> getUserReponses(int formation_id,int employe_id) throws Exception{
        List<QuizReponse> reponses = new ArrayList<>();
        String sql = "select * from quiz_reponses where quiz_id in (select id from quiz where formation_id = ?) and employe_id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,formation_id);
        stmt.setInt(2,employe_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            QuizReponse quizReponse = new QuizReponse();
            quizReponse.setEmploye_id(rs.getInt("employe_id"));
            quizReponse.setQuiz_id(rs.getInt("quiz_id"));
            quizReponse.setNum_reponse(rs.getInt("num_reponse"));
            reponses.add(quizReponse);
        }
        return reponses;
    }
}
