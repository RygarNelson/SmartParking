package com.example.pick_a_park;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;



import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.MapboxNavigationActivity;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationConstants;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.turf.TurfMeasurement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.pick_a_park.Parametri.destination;
import static com.example.pick_a_park.Parametri.originPoint;
import static com.example.pick_a_park.Parametri.parks;
import static com.example.pick_a_park.Parametri.selected_park;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

//++++++++++++++++++++++++++++++++++++VARIABILI++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public class FragmentMap extends FragmentWithOnBack implements ConnessioneListener {
    //Progress Dialog del caricamento
    private ProgressDialog caricamento = null;




    //View contenente la mappa
    private MapView mapView;
    //Mappa
    private MapboxMap map;
    //Manager utilizzato per gestire le icone nella mappa
    private SymbolManager symbolManager;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    //NAVIGATOR VARIABLES
    // variables for adding location layer
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    MapboxNavigation navigation ;
    NavigationLauncher navigationLauncher;
    public FragmentMap() {

        // Required empty public constructor
    }
    //++++++++++++++++++++++++++++++++++++ON CREATE VIEW++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.map_box_key));
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
        navigation = new MapboxNavigation(getContext(), getString(R.string.map_box_key));
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        Parametri.nearest_park = sharedPreferences.getBoolean("nearest_park", true);
        selected_park = 0;
        parks = new ArrayList<>();

        if (mapView == null) {
            mapView = (MapView) view.findViewById(R.id.mapView);
        }
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

                        /*
                        // Add symbol at specified lat/lon DA CANCELLARE
                        Symbol symbol = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(43.139928, 13.0658985))
                                .withIconImage("parking-15")
                                .withIconSize(2.0f));
                        Symbol symbol2 = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(42.139928, 14.0658985))
                                .withIconImage("parking-15")
                                .withIconSize(2.0f));

                         */
                                            //Inizializzo la barra di ricerca
                                            initSearchFab();
                                            //Setto lo style della map
                                            setUpSource(style);
                                            // Set up a new symbol layer for displaying the searched location's feature coordinates
                                            setupLayer(style);
                                            enableLocationComponent(style);
                                        }
                                    });
                                }

                            }
        );
        /*
        //Dato che sto lavorando offline aggiungo il parcheggio a mano
        double parkinglong = 14.0658985;
        double parkinglat = 42.139928;

        parks.add(Point.fromLngLat(parkinglong, parkinglat));
        parkinglong = 13.0658985;
        parkinglat = 43.139928;
        parks.add(Point.fromLngLat(parkinglong, parkinglat));

         */


        ParkList();

        // Inflate the layout for this fragment
        return view;
    }
    //++++++++++++++++++++++++++++++++++++STYLE & INITIALIZATIONS++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Setta lo style della mappa
    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }
    //Setta i simboli nella mappa
    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    //controlla che i permessi del gps siano attivi, altrimenti li richiede all' utente
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
    //++++++++++++++++++++++++++++++++++++SEARCHBOX++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Inizializza la searchbox
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
    //Muovere la telecamera nella location selezionata nella searchbox
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
                                    GetSelectedPark(lat,longit);
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
    //++++++++++++++++++++++++++++++++++++ROUTE++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void Ordina(Point destination){
        for(int i = 0; i < parks.size(); i++) {
            boolean flag = false;
           for(int k = 0; k < parks.size()-1;k++) {
               //Se l' elemento j e maggiore del successivo allora
               //scambiamo i valori
               double distance1 = TurfMeasurement.distance(
                       destination, parks.get(k));
               double distance2 = TurfMeasurement.distance(
                       destination, parks.get(k+1));
               if(distance1>distance2) {
                   Point app = parks.get(k);
                   parks.set(k,parks.get(k+1));
                   parks.set(k+1,app);
                   flag=true; //Lo setto a true per indicare che é avvenuto uno scambio
               }

           }
            if(!flag) break;
        }
    }
    //Trova il parcheggio più vicino alla destinazione e salva l'indice
    public void GetSelectedPark(double lat, double longit){
        if(parks.isEmpty()){
            Toast.makeText(getContext(),"No parks avaiable", Toast.LENGTH_LONG).show();
            return;
        }
        destination = Point.fromLngLat(longit, lat);
        if (selected_park == parks.size() || selected_park == 6)
            selected_park = 0;
        if(selected_park == 0)
            Ordina(destination);
        else
            Parametri.nearest_park=false;
        //My location
        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        //Distance beetween my destination and the selected park
        double distanceBetweenLastAndSecondToLastClickPoint = TurfMeasurement.distance(
                destination, parks.get(selected_park));
        Parametri.distance = distanceBetweenLastAndSecondToLastClickPoint;
        String distance = Double.toString(distanceBetweenLastAndSecondToLastClickPoint);

        //Track the route beetween my location and the selected park
        getRoute(originPoint,parks.get(selected_park));
        selected_park++;
        getActivity().findViewById(R.id.startButton).setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(),"DISTANCE: "+distance,Toast.LENGTH_SHORT).show();
        getActivity().findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prenota();
                //Se l'utente ha l'opzione del parcheggio più vicino in tempo reale selezionato




                // Create a NavigationLauncherOptions object to package everything together
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .build();
                StartNavigation(getActivity(),options);





            }
        });

    }

    //Calcola e aggiunge alla mappa la route dal punto di origine fino al parcheggio correntemente selezionato
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
                        Toast.makeText(getApplicationContext(),"Error: route undefined", Toast.LENGTH_LONG).show();
                    }
                });
    }


//++++++++++++++++++++++++++++++++++++CONNESSIONE E INTERAZIONE CON IL SERVER++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //Richiede al server la lista dei parcheggi
    public void ParkList(){
        caricamento = ProgressDialog.show(getContext(), "Connection",
                "Looking for parks...", true);
        caricamento.show();
        Connessione conn = new Connessione(null, "GET");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parkings");
    }
    //Prenota il parcheggio selezionato inviandolo al server
    public void Prenota(){
        double lat = parks.get(selected_park-1).latitude();
        double longi = parks.get(selected_park-1).longitude();
        caricamento = ProgressDialog.show(getContext(), "Connection",
                "Prenotation...", true);
        caricamento.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);
            postData.put("lat",lat);
            postData.put("long",longi);
        } catch (Exception e) {
            caricamento.dismiss();
            return;
        }

        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parking/book");
    }
    //Metodo utilizzato per leggere la risposta dal server
    @Override
    public void ResultResponse(String responseCode, String result) {
        if (responseCode == null) {
            caricamento.dismiss();
            Toast.makeText(getApplicationContext(), "ERRORE:\nNo connection or server offline.", Toast.LENGTH_LONG).show();
            return;
        }
        if(responseCode.equals("200"))
        {
            try {
                caricamento.dismiss();
                JSONObject risposta = new JSONObject(result);
                JSONArray parcheggi = new JSONArray(risposta.getString("parcheggi"));
                for(int i=0; i<parcheggi.length(); i++){
                    JSONObject parcheggio = (JSONObject) parcheggi.get(i);

                    /*
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(parcheggio.getString("lat")), Double.parseDouble(parcheggio.getString("long")))));

                     */

                    if(parcheggio.getInt("code")==0) {
                        Symbol symbol = symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(Double.parseDouble(parcheggio.getString("lat")), Double.parseDouble(parcheggio.getString("long"))))
                                .withIconImage("car-15")
                                .withIconSize(2.0f));
                        parks.add(Point.fromLngLat(Double.parseDouble(parcheggio.getString("long")), Double.parseDouble(parcheggio.getString("lat"))));

                    }
                }


            }catch (Exception e){
                caricamento.dismiss();
            }

        }
        if(responseCode.equals("201"))
        {
            caricamento.dismiss();
            Toast.makeText(getApplicationContext(), "Penotation Done", Toast.LENGTH_LONG).show();
            return;
        }


    }


    //++++++++++++++++++++++++++++++++++++++++++++++Other Methods+++++++++++++++++++++++++++++++++++++++++++++++
    private static void storeDirectionsRouteValue(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putString(NavigationConstants.NAVIGATION_VIEW_ROUTE_KEY, options.directionsRoute().toJson());
    }
    public void StartNavigation(Activity activity, NavigationLauncherOptions options){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        storeDirectionsRouteValue(options, editor);
        storeConfiguration(options, editor);

        storeThemePreferences(options, editor);
        storeOfflinePath(options, editor);
        storeOfflineVersion(options, editor);
        if (options.offlineMapOptions() != null) {
            storeOfflineMapDatabasePath(options, editor);
            storeOfflineMapStyleUrl(options, editor);
        }

        editor.apply();

        Intent navigationActivity = new Intent(activity, NavigationActivity.class);
        storeInitialMapPosition(options, navigationActivity);
        activity.startActivity(navigationActivity);
    }


    private static void storeConfiguration(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putBoolean(NavigationConstants.NAVIGATION_VIEW_SIMULATE_ROUTE, options.shouldSimulateRoute());
    }

    private static void storeThemePreferences(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        boolean preferenceThemeSet = options.lightThemeResId() != null || options.darkThemeResId() != null;
        editor.putBoolean(NavigationConstants.NAVIGATION_VIEW_PREFERENCE_SET_THEME, preferenceThemeSet);

        if (preferenceThemeSet) {
            if (options.lightThemeResId() != null) {
                editor.putInt(NavigationConstants.NAVIGATION_VIEW_LIGHT_THEME, options.lightThemeResId());
            }
            if (options.darkThemeResId() != null) {
                editor.putInt(NavigationConstants.NAVIGATION_VIEW_DARK_THEME, options.darkThemeResId());
            }
        }
    }

    private static void storeInitialMapPosition(NavigationLauncherOptions options, Intent navigationActivity) {
        if (options.initialMapCameraPosition() != null) {
            navigationActivity.putExtra(
                    NavigationConstants.NAVIGATION_VIEW_INITIAL_MAP_POSITION, options.initialMapCameraPosition()
            );
        }
    }

    private static void storeOfflinePath(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putString(NavigationConstants.OFFLINE_PATH_KEY, options.offlineRoutingTilesPath());
    }

    private static void storeOfflineVersion(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putString(NavigationConstants.OFFLINE_VERSION_KEY, options.offlineRoutingTilesVersion());
    }

    private static void storeOfflineMapDatabasePath(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putString(NavigationConstants.MAP_DATABASE_PATH_KEY, options.offlineMapOptions().getDatabasePath());
    }

    private static void storeOfflineMapStyleUrl(NavigationLauncherOptions options, SharedPreferences.Editor editor) {
        editor.putString(NavigationConstants.MAP_STYLE_URL_KEY, options.offlineMapOptions().getStyleUrl());
    }
    static void cleanUpPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .remove(NavigationConstants.NAVIGATION_VIEW_ROUTE_KEY)
                .remove(NavigationConstants.NAVIGATION_VIEW_SIMULATE_ROUTE)
                .remove(NavigationConstants.NAVIGATION_VIEW_PREFERENCE_SET_THEME)
                .remove(NavigationConstants.NAVIGATION_VIEW_PREFERENCE_SET_THEME)
                .remove(NavigationConstants.NAVIGATION_VIEW_LIGHT_THEME)
                .remove(NavigationConstants.NAVIGATION_VIEW_DARK_THEME)
                .remove(NavigationConstants.OFFLINE_PATH_KEY)
                .remove(NavigationConstants.OFFLINE_VERSION_KEY)
                .remove(NavigationConstants.MAP_DATABASE_PATH_KEY)
                .remove(NavigationConstants.MAP_STYLE_URL_KEY)
                .apply();
    }
    static DirectionsRoute extractRoute(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String directionsRouteJson = preferences.getString(NavigationConstants.NAVIGATION_VIEW_ROUTE_KEY, "");
        return DirectionsRoute.fromJson(directionsRouteJson);
    }

}




