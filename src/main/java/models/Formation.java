package models;

import java.util.Date;

public class Formation {

    private int id,formateur_id,nb_participant;

    private String image,title,description,place;

    private boolean is_online , available_for_employee , available_for_intern,has_quiz;

    private Date start_date,end_date;

    private Formateur formateur;

    private Double price;

    private  Double lat,lng;


    public Formation() {
    }

    public Formation(int id, int formateur_id, String image, String title, String description,String place, boolean is_online, boolean available_for_employee, boolean available_for_intern, Date start_date, Date end_date,double price) {
        this.id = id;
        this.formateur_id = formateur_id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.place = place;
        this.is_online = is_online;
        this.available_for_employee = available_for_employee;
        this.available_for_intern = available_for_intern;
        this.start_date = start_date;
        this.end_date = end_date;
        this.price = price;
    }

    public Formation(int formateur_id,String image, String title, String description,String place, boolean is_online, boolean available_for_employee, boolean available_for_intern, Date start_date, Date end_date,Double price) {
        this.formateur_id = formateur_id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.place = place;
        this.is_online = is_online;
        this.available_for_employee = available_for_employee;
        this.available_for_intern = available_for_intern;
        this.start_date = start_date;
        this.end_date = end_date;
        this.price = price;
    }

    public Formation(int formateur_id,String image, String title, String description,String place, boolean is_online, boolean available_for_employee, boolean available_for_intern, Date start_date,Double price) {
        this.formateur_id = formateur_id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.place = place;
        this.is_online = is_online;
        this.available_for_employee = available_for_employee;
        this.available_for_intern = available_for_intern;
        this.start_date = start_date;
        this.price = price;
    }

    public Formation(int formateur_id,String image, String title, String description,String place, boolean is_online, boolean available_for_employee, boolean available_for_intern, Date start_date,Double price,Double lat,Double lng) {
        this.formateur_id = formateur_id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.place = place;
        this.is_online = is_online;
        this.available_for_employee = available_for_employee;
        this.available_for_intern = available_for_intern;
        this.start_date = start_date;
        this.price = price;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormateur_id() {
        return formateur_id;
    }

    public void setFormateur_id(int formateur_id) {
        this.formateur_id = formateur_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isIs_online() {
        return is_online;
    }

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }

    public boolean isAvailable_for_employee() {
        return available_for_employee;
    }

    public void setAvailable_for_employee(boolean available_for_employee) {
        this.available_for_employee = available_for_employee;
    }

    public boolean isAvailable_for_intern() {
        return available_for_intern;
    }

    public void setAvailable_for_intern(boolean available_for_intern) {
        this.available_for_intern = available_for_intern;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Formateur getFormateur() {
        return formateur;
    }

    public void setFormateur(Formateur formateur) {
        this.formateur = formateur;
    }

    public int getNb_participant() {
        return nb_participant;
    }

    public void setNb_participant(int nb_participant) {
        this.nb_participant = nb_participant;
    }

    public boolean isHas_quiz() {
        return has_quiz;
    }

    public void setHas_quiz(boolean has_quiz) {
        this.has_quiz = has_quiz;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isFree(){
        return this.price == 0;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", is_online=" + is_online +
                ", available_for_employee=" + available_for_employee +
                ", available_for_intern=" + available_for_intern +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
}
