package es.achraf.deventer.viewmodel;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.IView;

public class ViewModelChat implements IViewModel.GetEvents {

    // Fields
    private IView.GetEventsListener getEventsListener;

    // Getters

    /**
     * Solicita leer los eventos a la base de datos.
     */
    @Override
    public void getEvents() {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child((FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .child(IViewModel.USER_EVENTS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> alKeys = new ArrayList<>();
                        ArrayList<Event> alEvent = new ArrayList<>();
                        for (DataSnapshot fDataSnapshot : dataSnapshot.getChildren()) {
                            alKeys.add(fDataSnapshot.getValue().toString());
                        }

                        FirebaseDatabase.getInstance().getReference()
                                .child(IViewModel.EVENTS)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot fDataSnapshot : dataSnapshot.getChildren()) {
                                            for (String fKey : alKeys) {
                                                if (fKey.equals(fDataSnapshot.getKey())) {
                                                    alEvent.add(fDataSnapshot.getValue(Event.class));
                                                }
                                            }
                                        }

                                        getEventsListener.onEventsRead(alKeys, alEvent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
}
