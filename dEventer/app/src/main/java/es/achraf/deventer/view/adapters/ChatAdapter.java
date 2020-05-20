package es.achraf.deventer.view.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.ItemClickListener;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    // Fields
    private ArrayList<Event> alEvent;
    private Context context;
    private ItemClickListener itemClickListener;
    private int resource;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Fields
        CircleImageView civEvent;

        TextView tvName;
        TextView tvDate;
        TextView tvJoined;

        // Constructors

        /**
         * Constructor a partir de la vista.
         *
         * @param v es la vista.
         */
        public ViewHolder(View v) {
            super(v);

            civEvent = v.findViewById(R.id.civEvent);

            tvName = v.findViewById(R.id.tvName);
            tvDate = v.findViewById(R.id.tvDate);
            tvJoined = v.findViewById(R.id.tvJoined);

            v.setOnClickListener(this);
        }

        /**
         * Handler que ejecuta la acción requerida cuando se hace click en el elemento.
         *
         * @param v es la vista.
         */
        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(itemView, getAdapterPosition());
        }
    }

    // Constructors

    /**
     * Constructor a partir de todos los atributos.
     *
     * @param context           es el contexto.
     * @param alEvent           es el ArrayList de Event.
     * @param itemClickListener es el listener que escuchará los eventos lanzados por el ítem.
     * @param resource          es el número del recurso.
     */
    public ChatAdapter(Context context, ArrayList<Event> alEvent,
                       ItemClickListener itemClickListener, int resource) {
        this.alEvent = alEvent;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.resource = resource;
    }

    // Methods

    /**
     * Método que se ejecuta al crear el ViewHolder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(resource, parent, false);
        ViewHolder holder = new ViewHolder(item);

        return holder;
    }

    /**
     * Método que se ejecuta al añadir el ViewHolder.
     *
     * @param holder   es el ViewHolder.
     * @param position es la posición.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = alEvent.get(position);

        Glide.with(context).load(Uri.parse(event.getImageUri()))
                .error(R.mipmap.logo)
                .fitCenter()
                .into(holder.civEvent);

        holder.tvName.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvJoined.setText(event.getUsersNum());
    }

    /**
     * Obtiene el número de elementos.
     *
     * @return el número de elementos.
     */
    @Override
    public int getItemCount() {
        return alEvent.size();
    }
}
