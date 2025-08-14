package org.example;

import models.Formation;
import models.Quiz;
import services.FormationService;
import services.QuizService;
import utils.MyDb;
import utils.enums.QuizType;

import java.util.Date;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        //test db connnection
        MyDb db = MyDb.getInstance();

        QuizService qs = new QuizService();
        //qs.create(new Quiz(1,  QuizType.SINGLE,"question", "reponse1", "reponse2", "reponse3"));

       /* FormationService fs = new FormationService();

        try{
            fs.create(new Formation( "image", "title", "description", true, true, true, new Date(), new Date()));
            System.out.println("Formation created");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        try{
            fs.delete(1);
            System.out.println("Formation deleted");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        try{
            fs.update(new Formation( 2,"image", "updated title", "description", true, true, true, new Date(), new Date()));
            System.out.println("Formation updated");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


      try{
          List<Formation> formations = fs.getAll();

          System.out.println("List of formations :");

          for (Formation formation : formations) {
              System.out.println(formation);
          }
      }catch (Exception e){
          System.out.println(e.getMessage());
      }*/

    }
}
