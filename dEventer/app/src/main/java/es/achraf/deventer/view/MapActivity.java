package es.achraf.deventer.view;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

import es.achraf.deventer.R;
import es.achraf.deventer.view.fragments.EventsFragment;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Fields
    private GoogleMap googleMap;

    // Methods

    /**
     * Primer método ejecutado por la actividad. Inicializa los elmentos de la actividad.
     *
     * @param savedInstanceState es el bundle que almacena los datos del estado de la actividad
     *                           cuando se produce un cambio como rotaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // https://developers.google.com/maps/documentation/android-sdk/start#the_maps_activity_java_file
        // https://developers.google.com/maps/documentation/android-sdk/map-with-marker#add_a_map
        // https://developers.google.com/maps/documentation/android-sdk/map#add_map_code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextInputEditText tietSearch = findViewById(R.id.tietSearch);
        findViewById(R.id.ibtnSearch).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(tietSearch.getText())) {
                String place = tietSearch.getText().toString();

                List<Address> lAddress;
                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    lAddress = geocoder.getFromLocationName(place, 1);

                    if ((lAddress != null) && !(lAddress.isEmpty())) {
                        Address address = lAddress.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(),
                                address.getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng).title(place);

                        googleMap.clear();
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                        EventsFragment.tietLocation.setText(place);
                    } else
                        Snackbar.make(getWindow().getDecorView().getRootView(),
                                R.string.no_location, Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            } else
                Snackbar.make(getWindow().getDecorView().getRootView(),
                        R.string.enter_location, Snackbar.LENGTH_SHORT).show();
        });
    }

    /**
     * Handler que ejecutará la acción necesaria cuando el mapa esté listo.
     * <p>
     * https://developers.google.com/maps/documentation/android-sdk/start#the_maps_activity_java_file
     * https://developers.google.com/maps/documentation/android-sdk/map#add_map_code
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng latLng = new LatLng(40.4165000, -3.7025600);

        MarkerOptions markerOptions = new MarkerOptions().
                position(latLng).title(getString(R.string.madrid));

        this.googleMap.addMarker(markerOptions);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
