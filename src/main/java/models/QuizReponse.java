package models;

public class QuizReponse {
    int employe_id,quiz_id,num_reponse;

    public QuizReponse(){

    }

    public QuizReponse(int employe_id, int quiz_id, int num_reponse) {
        this.employe_id = employe_id;
        this.quiz_id = quiz_id;
        this.num_reponse = num_reponse;
    }

    public int getEmploye_id() {
        return employe_id;
    }

    public void setEmploye_id(int employe_id) {
        this.employe_id = employe_id;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public int getNum_reponse() {
        return num_reponse;
    }

    public void setNum_reponse(int num_reponse) {
        this.num_reponse = num_reponse;
    }
}
