package es.achraf.deventer.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.adapters.AdapterMensajes;
import es.achraf.deventer.mensaje.MensajeEnviar;
import es.achraf.deventer.mensaje.MensajeRecibir;
import es.achraf.deventer.restApi.RestApiConstants;
import es.achraf.deventer.restApi.Endpoints;
import es.achraf.deventer.restApi.Respuesta;
import es.achraf.deventer.restApi.RestApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    // Fields
    private static final int PHOTO_SEND = 1;

    private String key;

    // Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();

        key = bundle.getString(IView.K_EVENT_ID);
        Event event = bundle.getParcelable(IView.K_EVENT);

        Glide.with(ChatActivity.this).load(event.getImageUri())
                .error(R.mipmap.logo)
                .fitCenter()
                .into((CircleImageView) findViewById(R.id.civEvent));

        ((TextView) findViewById(R.id.tvName)).setText(event.getName());
    }

    private void scrollDown() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
