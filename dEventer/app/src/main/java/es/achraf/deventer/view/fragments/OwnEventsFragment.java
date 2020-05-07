package es.achraf.deventer.view.fragments;

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

import es.achraf.deventer.R;
import es.achraf.deventer.view.adapters.AdapterRecyclerViewPlanes;
import es.achraf.deventer.interfaces.ItemClickListener;
import es.achraf.deventer.model.Event;

public class OwnEventsFragment extends Fragment implements ItemClickListener {

    private RecyclerView recyclerViewMisPlanes;
    private AdapterRecyclerViewPlanes adapterMisPlanes;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference reference;
    private FirebaseFirestore db;
    private ItemClickListener itemClickListener;

    private ArrayList<Event> planes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mis_planes, container, false);
        this.itemClickListener = this;
        mDatabase = FirebaseDatabase.getInstance();
        reference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        this.recyclerViewMisPlanes = v.findViewById(R.id.recyclerViewMisPlanes);
        leerDatosMisPlanes();
        return v;
    }

    private void leerDatosMisPlanes() {
        //recuperamos el id del user
        FirebaseUser user = mAuth.getCurrentUser();
        String IDusuario = user.getUid();

        DatabaseReference usuario = reference.child(IDusuario);
        usuario.child("planesApuntados").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void leerPlan(String id) {


        DocumentReference ref = db.collection("tabla_planes").document(id);

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

                adapterMisPlanes = new AdapterRecyclerViewPlanes(getContext(), planes, itemClickListener, R.layout.item_planes);
                recyclerViewMisPlanes.setAdapter(adapterMisPlanes);
                adapterMisPlanes.notifyDataSetChanged();
                recyclerViewMisPlanes.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int pos) {

    }
}
