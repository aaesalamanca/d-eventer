package es.achraf.deventer.viewmodel;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import es.achraf.deventer.model.User;
import es.achraf.deventer.view.IView;

public class ViewModelProfile implements IViewModel.GetEmail, IViewModel.GetDisplayName,
        IViewModel.GetAge, IViewModel.GetSex, IViewModel.GetPostalCode,
        IViewModel.SetGetProfileListener, IViewModel.SetGetImageListener, IViewModel.UploadImage {

    // Fields
    private IView.GetProfileListener getProfileListener;
    private IView.GetImageListener getImageListener;

    private String email;
    private String name;

    private User user;

    // Constructors

    /**
     * Constructor vacío —por defecto—.
     */
    public ViewModelProfile() {
        init();
    }

    // Getters

    /**
     * Devuelve el email del usuario.
     *
     * @return el email del usuario.
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @return el nombre del usuario.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Devuelve la edad del usuario.
     *
     * @return la edad del usuario.
     */
    @Override
    public String getAge() {
        return user.getAge();
    }

    /**
     * Devuelve el sexo del usuario.
     *
     * @return el sexo del usuario.
     */
    @Override
    public String getSex() {
        return user.getSex();
    }

    /**
     * Devuelve el código postal del usuario.
     *
     * @return el código postal del usuario.
     */
    @Override
    public String getPostalCode() {
        return user.getPostalCode();
    }

    /**
     * Solicita la obtención del Uri de la imagen en la base de datos.
     * <p>
     * https://firebase.google.com/docs/storage/android/start#set_up
     * https://firebase.google.com/docs/storage/android/create-reference#create_a_reference
     * https://firebase.google.com/docs/storage/android/download-files#create_a_reference
     * https://firebase.google.com/docs/storage/android/download-files#download_data_via_url
     */
    @Override
    public void getImage() {
        FirebaseStorage.getInstance().getReference().child(IViewModel.PROFILE_IMAGES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + IViewModel.IMAGE_EXT)
                .getDownloadUrl().addOnSuccessListener(uri ->
                getImageListener.onImageUploaded(uri, false));
    }

    // Setters

    /**
     * Establece el Listener que escuchará la petición del perfil a la base de datos.
     *
     * @param getProfileListener es el Listener de la petición del perfil a la base de datos.
     */
    @Override
    public void setGetProfileListener(IView.GetProfileListener getProfileListener) {
        this.getProfileListener = getProfileListener;
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

    // Methods

    /**
     * Inicializa los elementos.
     */
    private void init() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // https://firebase.google.com/docs/auth/android/manage-users?authuser=0#get_a_users_profile
        // https://firebase.google.com/docs/auth/android/start?authuser=0#access_user_information
        email = firebaseUser.getEmail();
        name = firebaseUser.getDisplayName();

        // https://firebase.google.com/docs/database/android/start?authuser=0#read_from_your_database
        // https://firebase.google.com/docs/auth/android/manage-users?authuser=0#get_a_users_profile
        // https://firebase.google.com/docs/auth/android/start?authuser=0#access_user_information
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(IViewModel.USERS)
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            /**
             * Handler que lleva a cabo la acción requerida cuando cambia cualquier dato del
             * usuario.
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                getProfileListener.onGetProfileComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sube la imagen a la base de datos.
     *
     * @param localUri es la Uri de la imagen en local.
     */
    @Override
    public void uploadImage(Uri localUri) {
        FirebaseStorage.getInstance().getReference().child(IViewModel.PROFILE_IMAGES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + IViewModel.IMAGE_EXT)
                .putFile(localUri).addOnSuccessListener(taskSnapshot ->
                getImageListener.onImageUploaded(localUri, true));
    }
}
