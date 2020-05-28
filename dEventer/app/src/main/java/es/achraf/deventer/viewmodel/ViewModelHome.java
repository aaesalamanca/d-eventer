package es.achraf.deventer.viewmodel;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.IView;

public class ViewModelHome implements IViewModel.SignOut, IViewModel.Notification {

    // Fields
    private IView.SignOutListener signOutListener;
    private IView.NotificationListener notificationListener;

    // Constructors

    /**
     * Constructor vacío —por defecto—.
     * <p>
     * Suscribe inicialmente a todos los eventos a los que el usuario está apuntado.
     */
    public ViewModelHome() {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child((IViewModel.USER_EVENTS))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot fDataSnapshot : dataSnapshot.getChildren()) {
                            FirebaseMessaging.getInstance()
                                    .subscribeToTopic(fDataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // Getters

    /**
     * Obtiene un evento según la clave proporcionadao.
     *
     * @param key es la clave del evento que se desea obtener.
     */
    @Override
    public void getEvent(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.EVENTS)
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationListener.onEventRead(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Setters

    /**
     * Establece el Listener que escuchará el intento de cerrar la sesión actual.
     *
     * @param signOutListener
     */
    @Override
    public void setSignOutListener(IView.SignOutListener signOutListener) {
        this.signOutListener = signOutListener;
    }

    /**
     * Establece el listener que escuchará la petición de lectura de un evento de la base de
     * datos.
     *
     * @param notificationListener es el listener que escucha la petición de lectura de un
     *                             evento de la base de datos.
     */
    @Override
    public void setGetEventListener(IView.NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    // Methods

    /**
     * Cierra la sesión actual y desuscribe a lus usuarios de los eventos para no seguir
     * recibiendo notificaciones.
     */
    @Override
    public void signOut() {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child((IViewModel.USER_EVENTS))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot fDataSnapshot : dataSnapshot.getChildren()) {
                            FirebaseMessaging.getInstance()
                                    .unsubscribeFromTopic(fDataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseAuth.getInstance().signOut();
        signOutListener.onSignOutComplete();
    }
}
