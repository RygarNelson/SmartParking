package com.example.pick_a_park;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.pick_a_park.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.turf.TurfMeasurement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment.TAG;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements ConnessioneListener{

    private MapView mapView;
    private MapboxMap map;
    private SymbolManager symbolManager;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.map_box_key));
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setContentView(R.layout.fragment_map);

        mapView = (MapView) getActivity().findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        map = mapboxMap;

                        // Use a layer manager here
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);

                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconIgnorePlacement(true);
                        //ParkList();
                        // Add symbol at specified lat/lon
                        Symbol symbol = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(43.139928, 13.0658985))
                                .withIconImage("parking-15")
                                .withIconSize(2.0f));
                        initSearchFab();
                        // Create an empty GeoJSON source using the empty feature collection
                        setUpSource(style);

                        // Set up a new symbol layer for displaying the searched location's feature coordinates
                        setupLayer(style);

                    }
                });
            }

            }
        );
        //ParkList();

        // Inflate the layout for this fragment
        return view;
    }
    private void initSearchFab() {
        getActivity().findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.map_box_key))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                startActivityForResult(intent, 1);
            }
        });
    }
    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    public void ParkList(){



        Connessione conn = new Connessione(null, "GET");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parkings");
    }
    @Override
    public void ResultResponse(String responseCode, String result) {
        int x=0;
        x++;
        if(responseCode.equals("200"))
        {
            try {

                JSONObject risposta = new JSONObject(result);
                JSONArray parcheggi = new JSONArray(risposta.getString("parcheggi"));
                for(int i=0; i<parcheggi.length(); i++){
                    JSONObject parcheggio = (JSONObject) parcheggi.get(i);
                    /*
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(parcheggio.getString("lat")), Double.parseDouble(parcheggio.getString("long")))));

                     */
                    Symbol symbol = symbolManager.create(new SymbolOptions()
                            .withLatLng(new LatLng(Double.parseDouble(parcheggio.getString("lat")), Double.parseDouble(parcheggio.getString("long"))))
                            .withIconImage("car-15")
                            .withIconSize(2.0f));
                    x++;
                }
                x++;

            }catch (Exception e){

            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == 1) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (map != null) {
                Style style = map.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

            // Move map camera to the selected location
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
            final double lat = ((Point) selectedCarmenFeature.geometry()).latitude();
            final double longit = ((Point) selectedCarmenFeature.geometry()).longitude();
            Symbol symbol = symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(lat, longit))
                    .withIconImage("marker-15")
                    .withIconSize(3.0f));
            FloatingActionButton parking = getActivity().findViewById(R.id.find_park);
            parking.setVisibility(View.VISIBLE);
            parking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm")
                            .setMessage("Want to find nearest park to your destination?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FindNearestPark(lat,longit);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_map)
                            .show();
                }
            });
        }
    }
    public void FindNearestPark(double lat, double longit){
        double parkinglat = 43.139928;
        double parkinglong = 13.0658985;
        List<Point> pointList = new ArrayList<>();
        pointList.add(Point.fromLngLat(parkinglat, parkinglong));
        pointList.add(Point.fromLngLat(lat, longit));
        double distanceBetweenLastAndSecondToLastClickPoint = TurfMeasurement.distance(
                pointList.get(0), pointList.get(1));
        String distance = Double.toString(distanceBetweenLastAndSecondToLastClickPoint);
        Toast.makeText(getActivity(),distance,Toast.LENGTH_SHORT).show();
    }
}
