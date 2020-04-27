package es.achraf.deventer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String ID;
    private String nombreUsuario;
    private String edad;
    private String email;
    private String sexo;
    private String cp;
    private ArrayList<String> planesApuntados;

    public User(String ID, String nombreUSuario, String email, String edad, String sexo, String cp, ArrayList<String> planesApuntados) {
        this.ID = ID;
        this.nombreUsuario = nombreUSuario;
        this.email = email;
        this.edad = edad;
        this.sexo = sexo;
        this.cp = cp;
        this.planesApuntados = planesApuntados;
    }

    public User() {
        this.planesApuntados = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }



    public ArrayList<String> getPlanesApuntados() {
        return planesApuntados;
    }

    public void setPlanesApuntados(ArrayList<String> planesApuntados) {
        this.planesApuntados = planesApuntados;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }
}
