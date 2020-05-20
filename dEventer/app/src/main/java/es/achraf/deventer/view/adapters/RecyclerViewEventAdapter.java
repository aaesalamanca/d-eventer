package es.achraf.deventer.view.adapters;

import android.content.Context;
import android.content.Intent;
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
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;
import es.achraf.deventer.view.IView;

public class RecyclerViewEventAdapter extends RecyclerView.Adapter<RecyclerViewEventAdapter.ViewHolder> {

    // Fields
    private ArrayList<Event> alEvent;
    private Context context;
    private ItemClickListener itemClickListener;
    private int resource;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Fields
        private CircleImageView civEvent;

        private TextView tvName;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvLocation;
        private TextView tvPrice;

        // Constructors

        /**
         * Constructor a partir de la vista.
         *
         * @param v es la vista.
         */
        public ViewHolder(View v) {
            super(v);

            civEvent = v.findViewById(R.id.covEvent);

            tvName = v.findViewById(R.id.tvName);
            tvDate = v.findViewById(R.id.tvDate);
            tvTime = v.findViewById(R.id.tvTime);
            tvLocation = v.findViewById(R.id.tvLocation);
            tvLocation.setOnClickListener(v1 -> {
                String encodedPlace = tvLocation.getText().toString();
                encodedPlace = encodedPlace.replace(IView.MAPS_SPACE, IView.MAPS_SPACE_ENCODED);
                encodedPlace = encodedPlace.replace(IView.MAPS_COMMA, IView.MAPS_COMMA_ENCODED);
                Uri uri = Uri.parse(IView.MAPS_QUERY + encodedPlace);
                Intent mapsIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(mapsIntent);
            });
            tvPrice = v.findViewById(R.id.tvPrice);

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
     * @param context
     * @param alEvent
     * @param itemClickListener
     * @param resource
     */
    public RecyclerViewEventAdapter(Context context, ArrayList<Event> alEvent,
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
        ViewHolder viewHolder = new ViewHolder(item);

        return viewHolder;
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
        holder.tvTime.setText(event.getTime());
        holder.tvLocation.setText(event.getLocation());
        holder.tvPrice.setText(event.getPrice());
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