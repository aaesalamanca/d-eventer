package es.achraf.deventer.viewmodel;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import es.achraf.deventer.view.IView;

public class ViewModelMessageAdapter implements IViewModel.SetGetImageListener,
        IViewModel.SetGetNameListener, IViewModel.GetDisplayName {

    // Fields
    private IView.GetImageListener getImageListener;
    private IView.GetNameListener getNameListener;

    // Getters

    /**
     * Solicita la obtención del Uri de la imagen en la base de datos.
     * <p>
     * Implementación vacía para sobrecargar.
     */
    @Override
    public void getImage() {

    }

    /**
     * Solicita la obtención del uri de la imagen en la base de datos.
     *
     * @param key es la clave del elemento en la base de datos.
     */
    public void getImage(String key) {
        FirebaseStorage.getInstance().getReference()
                .child(IViewModel.PROFILE_IMAGES).child(key + IViewModel.IMAGE_EXT)
                .getDownloadUrl().addOnSuccessListener(uri ->
                getImageListener.onImageUploaded(uri, false));
    }

    /**
     * Devuelve el nombre del usuario.
     * <p>
     * Implementación vacía para sobrecargar.
     *
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @param key es la clave del usuario en la base de datos.
     * @return null.
     */
    public String getName(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child(IViewModel.USERS)
                .child(key).child(IViewModel.USER_NAME)
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
}
