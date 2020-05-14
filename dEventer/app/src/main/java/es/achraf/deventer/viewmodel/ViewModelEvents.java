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
import es.achraf.deventer.view.IView;

public class ViewModelEvents implements IViewModel.UploadEvent, IViewModel.GetEvents {

    // Fields
    private IView.GetEventsListener getEventsListener;

    // Getters

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
}
