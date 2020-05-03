package es.achraf.deventer.restApi;

public class Respuesta {

    private String id;
    private String token;
    private String mensaje;


    public Respuesta(String id, String token, String mensaje) {
        this.id = id;
        this.token = token;
        this.mensaje = mensaje;
    }

    public Respuesta() {

    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
