package com.example.pick_a_park;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.example.pick_a_park.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
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
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfMeasurement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment.TAG;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMap extends Fragment implements ConnessioneListener{
    private int selected_park;
    private List<Point> parks;
    private MapView mapView;
    private MapboxMap map;
    private SymbolManager symbolManager;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    //NAVIGATOR VARIABLES
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button btn_navigation;
    public FragmentMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        selected_park = 0;
        parks = new ArrayList<>();
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
                        // Add symbol at specified lat/lon DA CANCELLARE
                        Symbol symbol = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(43.139928, 13.0658985))
                                .withIconImage("parking-15")
                                .withIconSize(2.0f));
                        Symbol symbol2 = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(42.139928, 14.0658985))
                                .withIconImage("parking-15")
                                .withIconSize(2.0f));
                        initSearchFab();
                        // Create an empty GeoJSON source using the empty feature collection
                        setUpSource(style);

                        // Set up a new symbol layer for displaying the searched location's feature coordinates
                        setupLayer(style);
                        enableLocationComponent(style);
                    }
                });
            }

            }
        );
        //Dato che sto lavorando offline aggiungo il parcheggio a mano
        double parkinglong = 13.0658985;
        double parkinglat = 43.139928;
        parks.add(Point.fromLngLat(parkinglong, parkinglat));
        parkinglong = 14.0658985;
        parkinglat = 42.139928;
        parks.add(Point.fromLngLat(parkinglong, parkinglat));
        //ParkList();

        // Inflate the layout for this fragment
        return view;
    }
    //Navigation
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(getContext(), loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
    }
    private void getRoute(Point origin, Point destination) {
        Toast.makeText(getContext(), destination.toString(), Toast.LENGTH_LONG).show();
        NavigationRoute.builder(getContext())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }




    //Searchbox initialization
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
    //Contact the server for get list of the parks
    public void ParkList(){
        Connessione conn = new Connessione(null, "GET");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parkings");
    }
    //Add park to the map (after contacting the server)
    @Override
    public void ResultResponse(String responseCode, String result) {

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
                    //APPENA AGGIUNTO, RICONTROLLARE
                    parks.add(Point.fromLngLat(Double.parseDouble(parcheggio.getString("lat")),Double.parseDouble(parcheggio.getString("long"))));
                }


            }catch (Exception e){

            }

        }
    }

    //Searchbox
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
                            .setTitle("Find a park?")
                            .setMessage("Remeber that if you don't like the choosen park, you can search again")

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
    //Used for find nearest park to my destination
    public void FindNearestPark(double lat, double longit){



        Point destination = Point.fromLngLat(longit, lat);
        if (selected_park == parks.size())
            selected_park = 0;

        //My location
        final Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        //Distance beetween my destination and the selected park
        double distanceBetweenLastAndSecondToLastClickPoint = TurfMeasurement.distance(
                destination, parks.get(selected_park));
        String distance = Double.toString(distanceBetweenLastAndSecondToLastClickPoint);

        //Track the route beetween my location and the selected park
        getRoute(originPoint,parks.get(selected_park));

        selected_park++;
        getActivity().findViewById(R.id.startButton).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean simulateRoute = true;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();
                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(getActivity(), options);
            }
        });
        Toast.makeText(getActivity(),"DISTANCE: "+distance,Toast.LENGTH_SHORT).show();
    }
}
