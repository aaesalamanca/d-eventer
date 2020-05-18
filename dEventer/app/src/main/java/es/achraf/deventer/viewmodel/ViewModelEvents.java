package es.achraf.deventer.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import es.achraf.deventer.model.Event;
import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

public class ViewModelEvents implements IViewModel.UploadEvent, IViewModel.GetEvents,
        IViewModel.SetGetImageListener, IViewModel.GetDisplayName, IViewModel.SetGetNameListener, IViewModel.Join {

    // Fields
    private IView.GetEventsListener getEventsListener;
    private IView.GetImageListener getImageListener;
    private IView.GetNameListener getNameListener;
    private IView.JoinListener joinListener;

    // Getters

    /**
     * Solicita leer los eventos a la base de datos.
     */
    @Override
    public void getEvents() {
        FirebaseDatabase.getInstance().getReference().child(IViewModel.EVENTS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> alKeys = new ArrayList<>();
                        ArrayList<Event> alEvent = new ArrayList<>();
                        for (DataSnapshot fDataSnapshot : dataSnapshot.getChildren()) {
                            alKeys.add(fDataSnapshot.getKey());
                            alEvent.add(fDataSnapshot.getValue(Event.class));
                        }

                        getEventsListener.onEventsRead(alKeys, alEvent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Solicita la obtención del Uri de la imagen en la base de datos.
     * <p>
     * Implementación nula para sobrecargar.
     */
    @Override
    public void getImage() {

    }

    /**
     * Solicita la obtención del Uri de la imagen en la base de datos en función de la clave
     * del usuario.
     *
     * @param key es la clave del usuario del que se quiere la imagen.
     */
    public void getImage(String key) {
        FirebaseStorage.getInstance().getReference().child(IViewModel.PROFILE_IMAGES)
                .child(key + IViewModel.IMAGE_EXT)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    getImageListener.onImageUploaded(uri, false);
                });
    }

    /**
     * Devuelve el nombre del usuario.
     * <p>
     * Implementación nula para sobrecargar.
     *
     * @return null.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @param key clave asociada al usuario.
     * @return null.
     */
    public String getName(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(key).child(IViewModel.USERS_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getNameListener.onNameReaded((String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return null;
    }

    // Setters

    /**
     * Establece el listener que escuchará la petición de leer los eventos de la base de
     * datos.
     *
     * @param getEventsListener es el listener de la petición de lectura de los eventos de la
     *                          base de datos.
     */
    @Override
    public void setGetEventsListener(IView.GetEventsListener getEventsListener) {
        this.getEventsListener = getEventsListener;
    }

    /**
     * Establece el Listener que escuchará la petición de una imagen a la base de datos.
     *
     * @param getImageListener es el Listener de la petición de la imagen a la base de datos.
     */
    @Override
    public void setGetImageListener(IView.GetImageListener getImageListener) {
        this.getImageListener = getImageListener;
    }

    /**
     * Establece el listener que escuhará la petición del nombre del usuario a la base de datos.
     *
     * @param getNameListener es el listener de la petición del nombre de usuario a la base
     *                        de datos.
     */
    @Override
    public void setGetNameListener(IView.GetNameListener getNameListener) {
        this.getNameListener = getNameListener;
    }

    /**
     * Establece el listener que escuchará los eventos accionados para comprobar si el usuario
     * se ha apuntado al evento y para apuntarse.
     *
     * @param joinListener es el listener.
     */
    @Override
    public void setJoinListener(IView.JoinListener joinListener) {
        this.joinListener = joinListener;
    }

    // Methods

    /**
     * Sube el evento a la base de datos.
     *
     * @param name        es el nombre del evento.
     * @param date        es la fecha del evento.
     * @param time        es la hora del evento.
     * @param location    es la ubicación del evento.
     * @param price       es el precio del evento.
     * @param description es la descripción del evento.
     * @param localUri    es la uri de la imagen del evento.
     */
    @Override
    public void uploadEvent(String name, String date, String time, String location,
                            String price, String description, Uri localUri) {
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance().getReference().child(IViewModel.EVENTS).push();

        Event event = new Event();

        event.setName(name.trim());
        event.setDate(date.trim());
        event.setTime(time.trim());
        event.setLocation(location.trim());
        event.setPrice(price.trim());
        event.setDescription(description.trim());
        event.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        event.setUsersNum(0);

        FirebaseStorage.getInstance().getReference().child(IViewModel.EVENT_IMAGES)
                .child(databaseReference.getKey()).putFile(localUri).addOnSuccessListener(taskSnapshot -> {
            FirebaseStorage.getInstance().getReference().child(IViewModel.EVENT_IMAGES)
                    .child(databaseReference.getKey()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        event.setImageUri(uri.toString());

                        databaseReference.setValue(event);
                    });
        });
    }

    /**
     * Comprueba si el usuario ya se ha apuntado al evento.
     *
     * @param key es la clave del evento.
     */
    @Override
    public void checkJoined(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasJoined = false;
                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> alEvent = user.getAlEvent();
                for (String fKey : alEvent) {
                    if (fKey.equals(key)) {
                        hasJoined = true;
                        break;
                    }
                }
                joinListener.checkJoinedCompleted(hasJoined);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Apunta al usuario al evento.
     *
     * @param key es la clave del evento.
     */
    @Override
    public void join(String key) {
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.getAlEvent().add(key);
                dbUser.setValue(user);
                joinListener.joinCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference dbEvent = FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.EVENTS)
                .child(key);
        dbEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setUsersNum(event.getUsersNum() + 1);
                dbEvent.setValue(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Da de baja al usuario del evento.
     *
     * @param key es la clave del evento.
     */
    @Override
    public void leave(String key) {
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.getAlEvent().remove(key);
                dbUser.setValue(user);
                joinListener.leaveCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference dbEvent = FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.EVENTS)
                .child(key);
        dbEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                event.setUsersNum(event.getUsersNum() -1);
                dbEvent.setValue(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
