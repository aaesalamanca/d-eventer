package es.achraf.deventer.mensaje;

import java.util.Map;

public class MensajeEnviar extends Mensaje {
    private Map hora;
    private String token;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora) {
        super(mensaje, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora, String token) {
        super(mensaje, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MensajeEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Map hora) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
