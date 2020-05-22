package es.achraf.deventer.viewmodel;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;

import es.achraf.deventer.model.Message;
import es.achraf.deventer.view.IView;

public class ViewModelMessage implements IViewModel.Chat {

    // Fields
    private IView.ChatListener chatListener;

    // Setters

    /**
     * Establece el listener que escuchará la recepción de nuevos mensajes desde la base de
     * datos.
     *
     * @param chatListener es el listener que escucha la recepción de nuevos mensajes.
     */
    @Override
    public void setChatListener(IView.ChatListener chatListener) {
        this.chatListener = chatListener;
    }

    /**
     * Envía un nuevo mensaje al chat y, por tanto, a la base de datos.
     *
     * @param key      es la clave del evento.
     * @param text     es el texto del mensaje.
     * @param imageUri es la uri de la imagen, si el usuario envía una. Puede ser null.
     */
    @Override
    public void sendMessage(String key, String text, Uri imageUri) {
        Message message = new Message();
        message.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.setText(text);
        message.setImageUri(imageUri.toString());
        message.setDate(System.currentTimeMillis());

        
    }
}
