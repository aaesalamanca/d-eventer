package es.achraf.deventer.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.HolderMessage> {

    // Fields
    private List<Message> lMessage = new ArrayList<>();
    private Context context;
    private CardView cvMessage;
    private static HashMap<String, Integer> hmColor = new HashMap<>();
    private static final int[] aIntColor = {Color.parseColor("#C47024"),
            Color.parseColor("#2465C4"), Color.parseColor("#24C43F"),
            Color.parseColor("#1B6E29"), Color.parseColor("#1B466E"),
            Color.parseColor("#4D1B6E"), Color.parseColor("#2DB7D3"),
            Color.parseColor("#D34E2D"), Color.parseColor("#C47024"),
            Color.parseColor("#D32D5A")};

    public class HolderMessage extends RecyclerView.ViewHolder {

        private CircleImageView civProfile;
        private TextView tvName;
        private TextView tvDate;
        private TextView tvText;
        private ImageView ivImage;

        public HolderMessage(View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvText = itemView.findViewById(R.id.tvText);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

    // Constructors

    /**
     * Constructor a partir de los atributos.
     *
     * @param context es el contexto de la aplicación.
     */
    public MessageAdapter(Context context) {
        this.context = context;
    }

    // Methods

    /**
     * Método que se ejecuta al crear el ViewHolder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public HolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent,
                false);
        cvMessage = view.findViewById(R.id.cvMessage);
        return new HolderMessage(view);
    }

    /**
     * Método que se ejecuta al añadir el ViewHolder.
     *
     * @param holder   es el ViewHolder.
     * @param position es la posición.
     */
    @Override
    public void onBindViewHolder(HolderMessage holder, int position) {
        Message message = lMessage.get(position);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(10, 10, 10, 10);
        cvMessage.setLayoutParams(params);

        Glide.with(context).load(Uri.parse(message.getProfileImageUri()))
                .error(R.mipmap.logo).dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(.5f)
                .into(holder.civProfile);
        holder.tvName.setText(message.getName());
        holder.tvName.setTextColor(setColor(message.getName()));

        Date date = new Date(message.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        holder.tvDate.setText(simpleDateFormat.format(date));
        holder.tvText.setText(message.getText());
        if (message.getImageUri() != null) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(message.getImageUri()).into(holder.ivImage);
        }
    }

    /**
     * Obtiene el número de elementos.
     *
     * @return el número de elementos.
     */
    @Override
    public int getItemCount() {
        return lMessage.size();
    }

    /**
     * Añada un mensaje a la lista.
     *
     * @param message es el mensaje que se añade.
     */
    public void addMessage(Message message) {
        lMessage.add(message);
        notifyItemInserted(lMessage.size());
    }

    /**
     * Selecciona un color aleatorio del array de int con los códigos de los colores.
     *
     * @return el entero correspondiende al color aleatorio seleccionado.
     */
    private int chooseRandomColor() {
        int aleColor = new Random().nextInt(aIntColor.length);
        return aIntColor[aleColor];
    }

    /**
     * Establece el color del mensaje.
     *
     * @param key es la clave del usuario que escribe el mensaje.
     * @return el color asociado al usuario.
     */
    private int setColor(String key) {
        if (!hmColor.containsKey(key))
            hmColor.put(key, chooseRandomColor());
        return hmColor.get(key);
    }
}
