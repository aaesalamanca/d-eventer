package es.achraf.deventer.viewmodel;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import es.achraf.deventer.view.IView;

public interface IViewModel {

    // Fields

    // Clave para la obtención del email en las SharedPreferences
    String K_SP_EMAIL = "email";
    // Clave para la obtención de la contraseña en las SharedPreferences
    String K_SP_PASSWORD = "password";
    // Clave de usuarios en el JSON de Firebase Realtime Database
    String USERS = "users";
    // Clave del usuario en el JSON de Firebase Realtime Database
    String USER_ID = "user_id";
    // Clave del nombre de cada usuario en el JSON de Firebase Realtime Database
    String USER_NAME = "name";
    // Clave del array de String que contiene los ID de los eventos a los que se ha apuntado un
    // usuario en el JSON de Firebase Realtime Database
    String USER_EVENTS = "alEvent";
    // Clave de eventos en el JSON de Firebase Realtime Database
    String EVENTS = "events";
    // Clave de chats en el JSON de Firebase Realtime Database
    String CHATS = "chats";
    // Clave de las imágenes de perfil en la estructura de Cloud Storage
    String PROFILE_IMAGES = "profile_images";
    // Clave de las imágenes de los eventos en la estructura de Cloud Storage
    String EVENT_IMAGES = "event_images";
    // Clave de las imágenes de los chats en la estructura de Cloud Storage
    String CHAT_IMAGES = "chat_images";
    // Separador para las rutas en las bases de datos
    String SEPARATOR = "/";
    // Extensión de las imágenes
    String IMAGE_EXT = ".jpg";
    // Nombre de la función de Cloud Functions para enviar notificaciones
    String SEND_NOTIFICATION_FUNCTION = "sendNotification";

    // Interfaces
    interface GetUserId {
        /**
         * Obtiene el id del usuario en Firebase Authentication.
         *
         * @return el id del usuario en Firebase Authentication.
         */
        String getUserId();
    }

    interface GetEmail {
        /**
         * Devuelve el email del usuario.
         * <p>
         *
         * @return el email del usuario.
         */
        String getEmail();
    }

    interface GetPassword {
        /**
         * Devuelve la contaseña del usuario.
         *
         * @return la contraseña del usuario.
         */
        String getPassword();
    }

    interface GetDisplayName {
        /**
         * Devuelve el nombre del usuario.
         *
         * @return el nombre del usuario.
         */
        String getName();
    }

    interface GetAge {
        /**
         * Devuelve la edad del usuario.
         *
         * @return la edad del usuario.
         */
        String getAge();
    }

    interface GetSex {
        /**
         * Devuelve el sexo del usuario.
         *
         * @return el sexo del usuario.
         */
        String getSex();
    }

    interface GetPostalCode {
        /**
         * Devuelve el código postal del usuario.
         *
         * @return el código postal del usuario.
         */
        String getPostalCode();
    }

    interface GetEvents {
        /**
         * Establece el listener que escuchará la petición de leer los eventos de la base de
         * datos.
         *
         * @param getEventsListener es el listener de la petición de lectura de los eventos de la
         *                          base de datos.
         */
        void setGetEventsListener(IView.GetEventsListener getEventsListener);

        /**
         * Solicita leer los eventos a la base de datos.
         */
        void getEvents();
    }

    interface SetGetPreferencesListener {
        /**
         * Establece el Listener que escuchará la petición de las SharedPreferences
         *
         * @param getPreferencesListener es el Listener de la petición de las SharedPreferences.
         */
        void setGetPreferencesListener(IView.GetPreferencesListener getPreferencesListener);
    }

    interface SetGetProfileListener {
        /**
         * Establece el Listener que escuchará la petición del perfil a la base de datos.
         *
         * @param getProfileListener es el Listener de la petición del perfil a la base de datos.
         */
        void setGetProfileListener(IView.GetProfileListener getProfileListener);
    }

    interface SetGetImageListener {
        /**
         * Establece el Listener que escuchará la petición de una imagen a la base de datos.
         *
         * @param getImageListener es el Listener de la petición de la imagen a la base de datos.
         */
        void setGetImageListener(IView.GetImageListener getImageListener);

        /**
         * Solicita la obtención del Uri de la imagen en la base de datos.
         */
        void getImage();
    }

    interface SetGetNameListener {
        /**
         * Establece el listener que escuhará la petición del nombre del usuario a la base de datos.
         *
         * @param getNameListener es el listener de la petición del nombre de usuario a la base
         *                        de datos.
         */
        void setGetNameListener(IView.GetNameListener getNameListener);
    }

    interface SignUp {
        /**
         * Establece el Listener que escuchará el intento de crear un user.
         *
         * @param signUpCompleteListener es el Listener del intento de inicio de sesión.
         */
        void setSignUpCompleteListener(IView.SignUpCompleteListener signUpCompleteListener);

        /**
         * Solicita crear un user con email y contraseña.
         *
         * @param email      es el email del user.
         * @param password   es la contraseña del user.
         * @param name       es el nombre del user.
         * @param age        es la age del user.
         * @param postalCode es el código postal del user.
         */
        void emailSignUp(String email, String password, String name, String age,
                         String sex, String postalCode);
    }

    interface SignIn {
        /**
         * Establece el Listener que escuchará el resultado del intento de inicio de sesión.
         *
         * @param signInCompleteListener es el Listener del intento de inicio de sesión.
         */
        void setSignInCompleteListener(IView.SignInCompleteListener signInCompleteListener);

        /**
         * Devuelve si el user ha iniciado o no sesión.
         *
         * @return true si ya ha iniciado sesión, false si no.
         */
        boolean isSignedIn();

        /**
         * Solicita iniciar sesión con email y contraseña. También los guarda en SharedPreferences si
         * el user marca la opción correspondiente.
         *
         * @param email         es el email del user.
         * @param password      es la contraseña del user.
         * @param saveBiometric indica si el user quiere guardar el email y la contraseña como
         *                      SharedPreferences para poder utilizar la huella:
         *                      <p>
         *                      - Sí -> True
         *                      - No -> False
         */
        void emailSignIn(String email, String password, boolean saveBiometric);

        /**
         * Solicita iniciar sesión con cuenta de Google.
         *
         * @param account es la cuenta de Google.
         */
        void googleSignIn(GoogleSignInAccount account);
    }

    interface UploadImage {
        /**
         * Sube la imagen a la base de datos.
         *
         * @param localUri es la Uri de la imagen.
         */
        void uploadImage(Uri localUri);
    }

    interface UploadEvent {
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
        void uploadEvent(String name, String date, String time, String location, String price,
                         String description, Uri localUri);
    }

    interface Join {
        /**
         * Establece el listener que escuchará los eventos accionados para comprobar si el usuario
         * se ha apuntado al evento y para apuntarse.
         *
         * @param joinListener es el listener.
         */
        void setJoinListener(IView.JoinListener joinListener);

        /**
         * Comprueba si el usuario ya se ha apuntado al evento.
         *
         * @param key es la clave del evento.
         */
        void checkJoined(String key);

        /**
         * Apunta al usuario al evento.
         *
         * @param key es la clave del evento.
         */
        void join(String key);

        /**
         * Da de baja al usuario del evento.
         *
         * @param key es la clave del evento.
         */
        void leave(String key);
    }

    interface Chat {
        /**
         * Establece el listener que escuchará la recepción de nuevos mensajes desde la base de
         * datos.
         *
         * @param chatListener es el listener que escucha la recepción de nuevos mensajes.
         */
        void setChatListener(IView.ChatListener chatListener);

        /**
         * Solicita al ViewModel que comience a escuchar nuevos mensajes desde la base de datos.
         *
         * @param key es la clave cel evento.
         */
        void startListening(String key);

        /**
         * Envía un nuevo mensaje al chat y, por tanto, a la base de datos.
         *
         * @param key      es la clave del evento.
         * @param text     es el texto del mensaje.
         * @param imageUri es la uri de la imagen, si el usuario envía una. Puede ser null.
         */
        void sendMessage(String key, String text, Uri imageUri);
    }

    interface Notification {
        /**
         * Obtiene un evento según la clave proporcionadao.
         *
         * @param key es la clave del evento que se desea obtener.
         */
        void getEvent(String key);

        /**
         * Establece el listener que escuchará la petición de lectura de un evento de la base de
         * datos.
         *
         * @param notificationListener es el listener que escucha la petición de lectura de un
         *                             evento de la base de datos.
         */
        void setGetEventListener(IView.NotificationListener notificationListener);
    }

    interface SignOut {
        /**
         * Establece el Listener que escuchará el intento de cerrar la sesión actual.
         *
         * @param signOutListener
         */
        void setSignOutListener(IView.SignOutListener signOutListener);

        /**
         * Cierra la sesión actual.
         */
        void signOut();
    }
}
