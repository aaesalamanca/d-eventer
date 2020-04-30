package es.achraf.deventer.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.achraf.deventer.R;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;

public class AdapterRecyclerViewPlanes extends RecyclerView.Adapter<AdapterRecyclerViewPlanes.ViewHolder> {

    private ArrayList<Event> events;
    private Context context;
    private ItemClickListener itemClickListener;
    private int resource;

    public AdapterRecyclerViewPlanes(Context context, ArrayList<Event> events, ItemClickListener itemClickListener, int resource) {
        this.events = events;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.resource = resource;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(resource, parent, false);
        ViewHolder holder = new ViewHolder(item);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Event event = events.get(position);

        holder.txtTituloPlan.setText(event.getNombre());
        holder.txtFechaPlan.setText(event.getFecha());
        holder.txtHoraPlan.setText(event.getHora());
        holder.txtUbicacionPlan.setText(event.getUbicacion());
        holder.txtPrecioPlan.setText(event.getPrecio());


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        StorageReference sCreaPlan = storageReference.child("FotosPlanes").child(event.getUrlImagen());

        Task<Uri> task = sCreaPlan.getDownloadUrl();

        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).error(R.drawable.logo).fitCenter().into(holder.imgPlan);
            }
        });


    }
    @Override
    public int getItemCount() {
        return events.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView imgPlan;
        TextView txtTituloPlan;
        TextView txtFechaPlan;
        TextView txtHoraPlan;
        TextView txtUbicacionPlan;
        TextView txtPrecioPlan;

        public ViewHolder(View v) {
            super(v);

            imgPlan = v.findViewById(R.id.imgPlan);
            txtTituloPlan = v.findViewById(R.id.txtTituloPlan);
            txtFechaPlan = v.findViewById(R.id.txtFechaPlan);
            txtHoraPlan = v.findViewById(R.id.txtHoraPlan);
            txtUbicacionPlan = v.findViewById(R.id.txtUbicacionPlan);
            txtPrecioPlan = v.findViewById(R.id.txtPrecioPlan);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(itemView, getAdapterPosition());
        }
    }
}