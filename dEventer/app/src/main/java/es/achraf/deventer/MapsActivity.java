package es.achraf.deventer;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

import es.achraf.deventer.view.fragments.EventsFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextInputEditText txtBuscarMap = findViewById(R.id.txtBuscaMap);
        ImageButton imgBuscar = findViewById(R.id.imgBuscar);

        imgBuscar.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(txtBuscarMap.getText())) {

                String lugar = txtBuscarMap.getText().toString();

                List<Address> listaLugares = null;
                MarkerOptions markerOptions = new MarkerOptions();

                Geocoder geocoder = new Geocoder(getApplicationContext());


                try {
                    listaLugares = geocoder.getFromLocationName(lugar, 1);

                    if (listaLugares != null) {
                        for (Address address : listaLugares) {
                            LatLng coordenadas = new LatLng(address.getLatitude(), address.getLongitude());
                            markerOptions.position(coordenadas);
                            markerOptions.title("localización del plan (" + lugar + ")");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }

                        EventsFragment.tietLocation.setText(lugar);

                    } else
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No se ha encontrado la localización proporcionada", Snackbar.LENGTH_SHORT).show();


                } catch (IOException e) {
                    Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else
                Snackbar.make(getWindow().getDecorView().getRootView(), "Por favor introduzca una localización", Snackbar.LENGTH_SHORT).show();
        });

        // cargarPlaces();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng coordenadas = new LatLng(40.4636188, -3.7491199);
        mMap.addMarker(new MarkerOptions().position(coordenadas).title("Madrid"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
