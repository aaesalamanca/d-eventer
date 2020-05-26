package es.achraf.deventer.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.adapters.MessageAdapter;
import es.achraf.deventer.viewmodel.ViewModelMessage;

public class ChatActivity extends AppCompatActivity {

    // Fields
    private static final int RC_IMAGE = 0;

    private String key;

    private Uri imageSendUri;

    private RecyclerView rcvMessage;
    private MessageAdapter adptMessage;

    private TextInputEditText tietMessage;

    // Methods

    /**
     * Primer método ejecutado por la actividad. Inicializa los elmentos de la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos del estado de la actividad
     *                           cuando se produce un cambio como rotaciones de la pantalla.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }

    /**
     * Inicializa los elementos de la actividad.
     */
    private void init() {
        Bundle bundle = getIntent().getExtras();

        key = bundle.getString(IView.K_EVENT_ID);
        Event event = bundle.getParcelable(IView.K_EVENT);
        imageSendUri = null;

        ViewModelMessage vmm = new ViewModelMessage();
        vmm.setChatListener(message -> adptMessage.addMessage(message));
        vmm.startListening(key);

        Glide.with(ChatActivity.this).load(event.getImageUri())
                .error(R.mipmap.logo)
                .fitCenter()
                .into((CircleImageView) findViewById(R.id.civEvent));

        ((TextView) findViewById(R.id.tvName)).setText(event.getName());

        findViewById(R.id.ibtnPhoto).setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType(IView.GALLERY_INTENT_TYPE);
            galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(galleryIntent, IView.GALLERY_INTENT_TITLE),
                    RC_IMAGE);
        });
        tietMessage = findViewById(R.id.tietMessage);
        findViewById(R.id.fabSend).setOnClickListener(v -> {
            String text = tietMessage.getText().toString();
            if (!(TextUtils.isEmpty(text) && (imageSendUri == null))) {
                vmm.sendMessage(key, text, imageSendUri);
            }
            tietMessage.getText().clear();
            imageSendUri = null;
        });

        rcvMessage = findViewById(R.id.rcvMessage);
        rcvMessage.setLayoutManager(new LinearLayoutManager(this));
        adptMessage = new MessageAdapter(this);
        rcvMessage.setAdapter(adptMessage);
        adptMessage.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                scrollDown();
            }
        });
    }

    /**
     * Hace scroll hacia abajo hasta el último elemento.
     */
    private void scrollDown() {
        rcvMessage.scrollToPosition(adptMessage.getItemCount() - 1);
    }

    /**
     * Se ejecuta cuando termina la actividad iniciada por esta y espera un resultado de vuelta.
     *
     * @param requestCode es el código que identifica a la actividad invocada.
     * @param resultCode  es el código del resultado.
     * @param data        es el Intent con los datos devueltos.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageSendUri = data.getData();
            }
        }
    }

    /**
     * Método llamado cuando hay un nuevo Intent en la actividad porque se la ha invocado con
     * afinidad single_top.
     *
     * @param intent es el nuevo intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }
}
