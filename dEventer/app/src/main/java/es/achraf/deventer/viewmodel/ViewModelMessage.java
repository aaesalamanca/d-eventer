package es.achraf.deventer.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import es.achraf.deventer.model.Message;
import es.achraf.deventer.view.IView;

public class ViewModelMessage implements IViewModel.Chat {

    // Fields
    private static final String K_TEXT = "text";

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

    // Methods

    /**
     * Solicita al ViewModel que comience a escuchar nuevos mensajes desde la base de datos.
     *
     * @param key es la clave cel evento.
     */
    @Override
    public void startListening(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.CHATS).child(key)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        chatListener.onNewMessage(message);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
        message.setText(text.trim());
        message.setDate(System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(IViewModel.USER_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message.setName((String) dataSnapshot.getValue());
                FirebaseStorage.getInstance().getReference()
                        .child(IViewModel.PROFILE_IMAGES)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                                + IViewModel.IMAGE_EXT).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            message.setProfileImageUri(uri.toString());
                            if (imageUri != null) {
                                FirebaseStorage.getInstance().getReference()
                                        .child(IViewModel.CHAT_IMAGES).child(key)
                                        .child(imageUri.getLastPathSegment())
                                        .putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                                    FirebaseStorage.getInstance().getReference()
                                            .child(IViewModel.CHAT_IMAGES).child(key)
                                            .child(imageUri.getLastPathSegment()).getDownloadUrl()
                                            .addOnSuccessListener(uri1 -> {
                                                message.setImageUri(uri1.toString());
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(IViewModel.CHATS).child(key)
                                                        .push().setValue(message);
                                            });
                                });
                            } else {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(IViewModel.CHATS).child(key)
                                        .push().setValue(message);
                            }
                        });

                Map<String, String> mData = new HashMap<>();
                mData.put(IView.K_EVENT_ID, key);
                mData.put(IViewModel.USER_NAME, message.getName());
                mData.put(K_TEXT, message.getText());
                FirebaseFunctions.getInstance()
                        .getHttpsCallable(IViewModel.SEND_NOTIFICATION_FUNCTION).call(mData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
