package es.achraf.deventer.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.mensaje.Message;

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

        private TextView tvName;
        private TextView tvText;
        private TextView tvDate;
        private CircleImageView civProfile;
        private ImageView ivImage;

        public HolderMessage(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvText = itemView.findViewById(R.id.tvText);
            tvDate = itemView.findViewById(R.id.tvDate);
            civProfile = itemView.findViewById(R.id.civProfile);
            ivImage = itemView.findViewById(R.id.ivMessage);
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
     * Añada un mensaje a la lista.
     *
     * @param message
     */
    public void addMessage(Message message) {
        lMessage.add(message);
        notifyItemInserted(lMessage.size());
    }

    @Override
    public HolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        cvMessage = view.findViewById(R.id.cvMessage);
        return new HolderMessage(view);
    }

    private int chooseRandomColor() {
        int aleColor = new Random().nextInt(aIntColor.length);
        return aIntColor[aleColor];
    }

    private int setColor(String name) {
        if (!hmColor.containsKey(name))
            hmColor.put(name, chooseRandomColor());
        return hmColor.get(name);
    }

    @Override
    public void onBindViewHolder(HolderMessage holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lMessage.size();
    }

}
