package models;

import utils.enums.QuizType;

public class Quiz {
    int id,formation_id,numRepCorrect;
    String question,reponse1,reponse2,reponse3;

    public  Quiz(){

    }

    public Quiz(int formation_id,String question, String reponse1, String reponse2, String reponse3,int numRepCorrect) {
        this.formation_id = formation_id;
        this.question = question;
        this.reponse1 = reponse1;
        this.reponse2 = reponse2;
        this.reponse3 = reponse3;
        this.numRepCorrect = numRepCorrect;
    }

    public Quiz(int id, int formation_id, QuizType type, String question, String reponse1, String reponse2, String reponse3,int numRepCorrect) {
        this.id = id;
        this.formation_id = formation_id;
        this.question = question;
        this.reponse1 = reponse1;
        this.reponse2 = reponse2;
        this.reponse3 = reponse3;
        this.numRepCorrect = numRepCorrect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormation_id() {
        return formation_id;
    }

    public void setFormation_id(int formation_id) {
        this.formation_id = formation_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse1() {
        return reponse1;
    }

    public void setReponse1(String reponse1) {
        this.reponse1 = reponse1;
    }

    public String getReponse2() {
        return reponse2;
    }

    public void setReponse2(String reponse2) {
        this.reponse2 = reponse2;
    }

    public String getReponse3() {
        return reponse3;
    }

    public void setReponse3(String reponse3) {
        this.reponse3 = reponse3;
    }

    public int getNumRepCorrect() {
        return numRepCorrect;
    }

    public void setNumRepCorrect(int numRepCorrect) {
        this.numRepCorrect = numRepCorrect;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", formation_id=" + formation_id +
                ", question='" + question + '\'' +
                ", reponse1='" + reponse1 + '\'' +
                ", reponse2='" + reponse2 + '\'' +
                ", reponse3='" + reponse3 + '\'' +
                '}';
    }
}
