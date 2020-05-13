package es.achraf.deventer.viewmodel;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import es.achraf.deventer.model.Event;

public class ViewModelEvents implements IViewModel.UploadEvent {

    // Fields

    // Constructors

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

        FirebaseStorage.getInstance().getReference().child(IViewModel.EVENT_IMAGES)
                .child(databaseReference.getKey()).putFile(localUri);
        FirebaseStorage.getInstance().getReference().child(IViewModel.EVENT_IMAGES)
                .child(databaseReference.getKey()).getDownloadUrl()
                .addOnSuccessListener(uri -> event.setImageUri(uri.toString()));

        event.setName(name.trim());
        event.setDate(date.trim());
        event.setTime(time.trim());
        event.setLocation(location.trim());
        event.setPrice(price.trim());
        event.setDescription(description.trim());
        event.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        event.setUsersNum(0);
    }
}
