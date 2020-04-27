package es.achraf.deventer.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Plan implements Serializable {

    private String id;
    private String nombre;
    private String ubicacion;
    private String fecha;
    private String hora;
    private String precio;
    private String urlImagen;
    private String descripcion;
    private String duenoPlan;
    private String uriImageDuenoPlan;
    private ArrayList<String> usuariosApuntadosUID;

    public Plan() {
        this.usuariosApuntadosUID = new ArrayList<>();
    }

    public Plan(String id,String nombre, String ubicacion, String fecha, String hora, String precio, String urlImagen, String descripcion, String duenoPlan, String uriImageDuenoPlan, ArrayList<String> usuariosApuntadosUID) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.descripcion = descripcion;
        this.duenoPlan = duenoPlan;
        this.uriImageDuenoPlan = uriImageDuenoPlan;
        this.usuariosApuntadosUID = usuariosApuntadosUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getUsuariosApuntadosUID() {
        return usuariosApuntadosUID;
    }

    public void setUsuariosApuntadosUID(ArrayList<String> usuariosApuntadosUID) {
        this.usuariosApuntadosUID = usuariosApuntadosUID;
    }

    public String getUriImageDuenoPlan() {
        return uriImageDuenoPlan;
    }

    public void setUriImageDuenoPlan(String uriImageDuenoPlan) {
        this.uriImageDuenoPlan = uriImageDuenoPlan;
    }

    public String getDuenoPlan() {
        return duenoPlan;
    }

    public void setDuenoPlan(String duenoPlan) {
        this.duenoPlan = duenoPlan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
