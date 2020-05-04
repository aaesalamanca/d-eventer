package es.achraf.deventer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.achraf.deventer.ChatActivity;
import es.achraf.deventer.R;
import es.achraf.deventer.adaptadores.AdapterChatPlan;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;

public class ChatFragment extends Fragment implements ItemClickListener {

    private RecyclerView recyclerViewPlanesChat;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private AdapterChatPlan adapterChatPlan;
    private ArrayList<Event> planes = new ArrayList<>();

    private ItemClickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        db = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reference = db.getReference();

        this.recyclerViewPlanesChat = v.findViewById(R.id.recyclerViewPlanes);
        this.itemClickListener = this;

        leerDatos();

        return v;
    }


    public void leerDatos() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            DatabaseReference dbReferecnce = reference.child(user.getUid());

            dbReferecnce.child("planesApuntados").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String IDplan = ds.getValue().toString();
                        leerPlan(IDplan);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void leerPlan(String id) {

        DocumentReference ref = firestore.collection("tabla_planes").document(id);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful() && task.getResult() != null) {//HAY QUE RECORRER TAMBIEN EL ARRAYLIST QUE DEVUELVE

                    DocumentSnapshot document = task.getResult();

                    String idPlan = document.getId();
                    String fecha = document.getString("fecha");
                    String hora = document.getString("hora");
                    String urlImagen = document.getString("imagen");
                    String titulo = document.getString("titulo");
                    String ubicacion = document.getString("ubicacion");
                    String precio = document.getString("precio");
                    String descripcion = document.getString("descripcion");
                    String dueno = document.getString("dueno");
                    String imgDueno = document.getString("imgDueno");
                    ArrayList<String> usuariosApuntados = (ArrayList<String>) document.get("usuariosApuntados");

                    planes.add(new Event(idPlan, titulo, ubicacion, fecha, hora, precio, urlImagen, descripcion, dueno, imgDueno, usuariosApuntados));


                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                adapterChatPlan = new AdapterChatPlan(getContext(), planes, itemClickListener, R.layout.item_recycler_planes_chat);
                recyclerViewPlanesChat.setAdapter(adapterChatPlan);
                adapterChatPlan.notifyDataSetChanged();
                recyclerViewPlanesChat.setLayoutManager(new LinearLayoutManager(getContext()));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int posicion) {
        Event event = planes.get(posicion);

        Intent intentChat = new Intent(getActivity(), ChatActivity.class);
        intentChat.putExtra("id", event.getId());
        intentChat.putExtra("titulo", event.getNombre());
        intentChat.putExtra("imagen", event.getUrlImagen());
        startActivity(intentChat);
    }
}
