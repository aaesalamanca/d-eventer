package es.achraf.deventer.view;

import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;

import es.achraf.deventer.model.Event;
import es.achraf.deventer.model.Message;

public interface IView {

    // Fields
    // Comienzo de la consulta para construir urls que buscan un lugar por su nombre en Google Maps
    String MAPS_QUERY = "https://www.google.com/maps/search/?api=1&query=";
    // Espacio que debe ser codificado
    String MAPS_SPACE = " ";
    // Codificación del espacio para uris construidas en Google Maps
    String MAPS_SPACE_ENCODED = "+";
    // Coma que debe ser codificada
    String MAPS_COMMA = ",";
    // Codificación de la coma para uris construidas en Google Maps
    String MAPS_COMMA_ENCODED = "%2C";
    // Clave para el paso del id del evento entre actividades
    String K_EVENT_ID = "event_id";
    // Clave para el paso de un Event entre actividades
    String K_EVENT = "event";
    // Tipo para los Intent de galería
    String GALLERY_INTENT_TYPE = "image/jpeg";
    // Título para los Intent de galería
    String GALLERY_INTENT_TITLE = "Seleccione una imagen";

    // Interfaces

    interface SignUpCompleteListener {
        /**
         * Handler que ejecuta la acción requerida según el resultado de intentar crear un user.
         *
         * @param signedUp es el resultado del intento de creación de un user.
         *                 <p>
         *                 - True -> Usuario creado con éxito
         *                 - False -> Usuario no creado
         */
        void onSignUpComplete(boolean signedUp);
    }

    interface SignInCompleteListener {
        /**
         * Handler que ejecuta la acción requerida según el resultado del intento de inicio de sesión.
         *
         * @param signedIn es el resultado del intento de inicio de sesión.
         *                 <p>
         *                 - True -> Inicio de sesión con éxito
         *                 - False -> Inicio de sesión fracasado
         */
        void onSignInComplete(boolean signedIn);
    }

    interface GetPreferencesListener {
        /**
         * Obtiene las SharedPreferences de la Activity.
         *
         * @param mode es el modo de acceso a las SharedPreferences.
         * @return las SharedPreferences según mode.
         */
        SharedPreferences getPreferences(int mode);
    }

    interface GetEventsListener {
        /**
         * Handler que ejecuta la acción requerida cuando se han leído los eventos de la base
         * de datos.
         *
         * @param alKeys  es el ArrayList de claves en el JSON de la base de datos.
         * @param alEvent es el ArrayList de valores en el JSON de la base de datos.
         */
        void onEventsRead(ArrayList<String> alKeys, ArrayList<Event> alEvent);
    }

    interface GetProfileListener {
        /**
         * Handler que ejecuta la acción requerida cuando se han obtenido los datos del usuario
         * procedentes de la base de datos.
         */
        void onGetProfileComplete();
    }

    interface GetImageListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha obtenido la imagel de la base de
         * datos
         *
         * @param cloudUri es la Uri de la imagen en la base de datos.
         * @param isChange indica si se ha actualizado o no la imagen:
         *                 <p>
         *                 True -> Se ha actualizado
         *                 False -> No se ha actualizado
         */
        void onImageUploaded(Uri cloudUri, boolean isChange);
    }

    interface GetNameListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha obtenido el nombre del usuario
         * de la base de datos.
         *
         * @param name es el nombre del usuario.
         */
        void onNameReaded(String name);
    }

    interface JoinListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha solicitado información sobre si el
         * usuario está apuntado al evento.
         *
         * @param hasJoined indica si el usuario está o no apuntado al evento.
         *                  <p>
         *                  True -> Está apuntado.
         *                  False -> No está apuntado.
         */
        void checkJoinedCompleted(boolean hasJoined);

        /**
         * Handler que ejecuta la acción requerida cuando se ha solicitado la inscripción al evento.
         */
        void joinCompleted();

        /**
         * Handler que ejecuta la acción requerida cuando se ha solicitado abandonar el evento.
         */
        void leaveCompleted();
    }

    interface ChatListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha recibido un nuevo mensaje.
         *
         * @param message es el nuevo mensaje recibido.
         */
        void onNewMessage(Message message);
    }

    interface NotificationListener {
        /**
         * Handler que ejecuta la acción requerida cuando se ha recibido un evento de la base de
         * datos.
         *
         * @param event es el evento recibido.
         */
        void onEventRead(Event event);
    }

    interface SignOutListener {
        /**
         * Handler que ejecuta la acción requerida cuando el user cierra la sesión.
         */
        void onSignOutComplete();
    }
}
