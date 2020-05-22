package es.achraf.deventer.mensaje;

public class Message extends Mensaje {

    private Long hora;
    private String token;
    private boolean visto;

    public Message() {
    }

    public Message(Long hora) {
        this.hora = hora;
    }

    public Message(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Long hora) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
        this.visto = isVisto();
    }

    public Message(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Long hora, String token, boolean visto) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
        this.token = token;
        this.visto = visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public boolean isVisto() {
        return visto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
