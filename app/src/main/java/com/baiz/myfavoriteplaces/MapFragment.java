package com.baiz.myfavoriteplaces;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mgoogleMap;
    private MapView mapView;
    private View myView;
    private LocationManager locationManager;
    private Criteria criteria ;
    private Location location ;
    private String provider ;
    double latitude , longitude ;
    private  LatLng my_position ;
    private Toolbar toolbar ;

    public MapFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setTitle("Map");
        // init mon locationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // demande la pemission de localisation si n'exist pas
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99
            );
        }

        // recupere la localisation du telephone pour la donnee au map
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
         latitude = location.getLatitude();
         longitude = location.getLongitude();
         my_position = new LatLng(latitude, longitude);


        return  myView;
    }

  //#################################################

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // lier MapView et creer mon map si n pas ceer
        mapView = (MapView) myView.findViewById(R.id.google_map);
        if (mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }

    }

    // ##################################
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mgoogleMap = googleMap ;
        mgoogleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition ;

        // si l utilisateur vien de allplace il envoi un place objet
        if (getArguments().get("from")=="allPlaces")
        {
            // je recuperer l objet place
            Place place = (Place) getArguments().getSerializable("place");
            // je affiche la localisation de mon place
            mgoogleMap.addMarker(new MarkerOptions().position(getLocationFromAddress(getContext(),place.getAdresse())));

             cameraPosition = CameraPosition.builder().target(getLocationFromAddress(getContext(),place.getAdresse())).zoom(16).bearing(0)
                    .tilt(45).build();
            mgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition) );

        }else {
            // si l utilasteur vien de menu j affiche sa localisation que j ai deja recupere dans onCreateView de fragment
            mgoogleMap.addMarker(new MarkerOptions().position(my_position));
            cameraPosition = CameraPosition.builder().target(my_position).zoom(16).bearing(0)
                    .tilt(45).build();
            mgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition) );
        }
    }

    //#######################################""

    // dans cette function je returne un objet LatLng a partire d une adresse
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

}
