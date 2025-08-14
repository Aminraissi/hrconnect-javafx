package models;


import java.security.cert.CertPathBuilder;

public class Utilisateur {
    private int id;
    private int cin;
    private String  tel;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String roles;

    public Utilisateur() {
    }

    public Utilisateur(int id, int cin, String tel, String nom, String prenom, String email, String password,String roles) {
        this.id = id;
        this.cin=cin;
        this.tel=tel;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles=roles;
    }
    public Utilisateur(int cin, String tel, String nom, String prenom, String email, String password, String roles) {
        this.cin=cin;
        this.tel=tel;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.roles=roles;
    }
    public Utilisateur(int id, int cin, String tel, String nom, String prenom, String email, String password) {
        this.id=id;
        this.cin=cin;
        this.tel=tel;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public String gettel() {
        return tel;
    }

    public void settel(String tel) {
        this.tel = tel;
    }

    public String getroles() {
        return roles;
    }

    public void setroles(String roles) {
        this.roles = roles;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Vérifier si l'email est au format email kifeh?
        this.email = email;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        {
            this.password = password;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        if (id != that.id) return false;
        return cin == that.cin;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + cin;
        return result;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", cin=" + cin +
                ", tel=" + tel +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
