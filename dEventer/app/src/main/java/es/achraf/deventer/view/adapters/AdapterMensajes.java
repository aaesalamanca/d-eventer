package es.achraf.deventer.view.adapters;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import es.achraf.deventer.ChatActivity;
import es.achraf.deventer.R;
import es.achraf.deventer.mensaje.MensajeRecibir;
import es.achraf.deventer.restApi.RestApiConstants;


public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;
    private CardView cardMensaje;
    private static HashMap<String, Integer> burbujas = new HashMap<>();
    private int[] colores = {Color.parseColor("#C47024"), Color.parseColor("#2465C4"),
            Color.parseColor("#24C43F"), Color.parseColor("#1B6E29"), Color.parseColor("#1B466E"),
            Color.parseColor("#4D1B6E"), Color.parseColor("#2DB7D3"), Color.parseColor("#D34E2D"),
            Color.parseColor("#C47024"), Color.parseColor("#D32D5A")};

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private static String nombreId = "";

    public AdapterMensajes(Context c) {
        this.c = c;
        nombreId = user.getDisplayName();

    }

    public void addMensaje(MensajeRecibir m) {
        //aqui creo que es donde debería comparar el token del user con el token que esta en el mensaje en database
        String tokenUsuarioActual = RestApiConstants.TOKEN;
        String titulo = m.getNombre();
        String mensaje = m.getMensaje();

        if (!tokenUsuarioActual.equals(m.getToken()) && !m.isVisto()){
            notificationDialog(titulo,mensaje);
            m.setVisto(true);
        }



          //Toast.makeText(c, "TOKEN USUARIO: "+RestApiConstants.TOKEN+"\n"+"TOKEN MENSAJE: "+m.getToken(), Toast.LENGTH_SHORT).show();
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }


    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_chat, parent, false);
        cardMensaje = v.findViewById(R.id.cardMensaje);
        return new HolderMensaje(v);
    }


    private int escogerColor() {
        Random r = new Random();
        int aleColor = r.nextInt(9 - 0) + 0;
        return colores[aleColor];
    }


    private int establecerColorChat(String nombre) {
        if (!burbujas.containsKey(nombre))
            burbujas.put(nombre, escogerColor());
        return burbujas.get(nombre);
    }

    private void notificationDialog(String titulo, String mensaje) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "id_notificacion";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 0, 200, 200});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(c, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo)
                .setTicker("dEventer")
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setContentInfo(String.valueOf(ServerValue.TIMESTAMP));
        notificationManager.notify(1, notificationBuilder.build());
    }


    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {
        String nombre = listMensaje.get(position).getNombre();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(10, 10, 10, 10);
        cardMensaje.setLayoutParams(params);

        holder.getNombre().setText(nombre);
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());


        if (listMensaje.get(position).getType_mensaje().equals("2")) {//si se ha enviado imagen se muestra
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);

            Glide.with(c).load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensaje());

        } else if (listMensaje.get(position).getType_mensaje().equals("1")) {//si no se ha enviado imagen se oculta
            holder.getFotoMensaje().setVisibility(View.VISIBLE);//a lo mejor debo dejarlo como estaba, en GONE, lo he cambiado para probar
            holder.getMensaje().setVisibility(View.VISIBLE);
        }


        holder.getNombre().setTextColor(establecerColorChat(nombre));


        if (user != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            String ruta = listMensaje.get(position).getFotoPerfil();
            StorageReference sCreaPlan = storageReference.child("ProfileActivity").child(ruta);

            Task<Uri> task = sCreaPlan.getDownloadUrl();

            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(c).load(uri).error(R.mipmap.logo).dontTransform()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//almacene la imagen en cache antes y despues de la carga de la magen, consiguiendo una disminucon del lag
                            .thumbnail(.5f).into(holder.getFotoMensajePerfil());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");//a pm o am
        holder.getHora().setText(sdf.format(d));

    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

}
