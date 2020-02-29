

package com.example.pick_a_park;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.turf.TurfMeasurement;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.pick_a_park.Parametri.destination;
import static com.example.pick_a_park.Parametri.originPoint;
import static com.example.pick_a_park.Parametri.parks;
import static com.example.pick_a_park.Parametri.selected_park;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class NavigationActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, RouteListener, ProgressChangeListener,ConnessioneListener {

    private Runnable runnable;
    private Handler handler;
    private NavigationView navigationView;
    private boolean dropoffDialogShown;
    private Location lastKnownLocation;
    //Lista dei parcheggi liberi




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        if(Parametri.nearest_park)
            LaunchThread();
        navigationView.initialize(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchRoute(originPoint, parks.get(selected_park-1));
    }

    @Override
    public void onCancelNavigation() {
        // Navigation canceled, finish the activity
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future longRunningTaskFuture = executorService.submit(runnable);
        longRunningTaskFuture.cancel(true);
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    @Override
    public void onNavigationFinished() {
        // Intentionally empty
    }

    @Override
    public void onNavigationRunning() {
        // Intentionally empty
    }

    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        return true;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {
        if (!dropoffDialogShown && !parks.isEmpty()) {

            dropoffDialogShown = true; // Accounts for multiple arrival events
            Toast.makeText(this, "You have arrived!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        lastKnownLocation = location;
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions navigationViewOptions = setupOptions(directionsRoute);
        navigationView.startNavigation(navigationViewOptions);
    }



    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .alternatives(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                        DirectionsResponse directionsResponse = response.body();
                        if (directionsResponse != null && !directionsResponse.routes().isEmpty()) {
                            startNavigation(directionsResponse.routes().get(0));
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Toast.makeText(getApplicationContext(),"Error: route undefined", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private NavigationViewOptions setupOptions(DirectionsRoute directionsRoute) {
        dropoffDialogShown = false;

        NavigationViewOptions.Builder options = NavigationViewOptions.builder();
        options.directionsRoute(directionsRoute)
                .navigationListener(this)
                .progressChangeListener(this)
                .routeListener(this);
        return options.build();
    }

    private Point getLastKnownLocation() {
        return Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
    }
    //++++++++++++++++++++++++++++++++++++++++Thread++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Metodo che lancia un thread indipendente e che avvierà un timer:
    //Quando il timer scade, richiede nuovamente i parcheggi al server e poi richiama se stesso
    public void LaunchThread(){
        final int interval = 10000; // 1 Second
        handler = new Handler();

        runnable = new Runnable(){
            public void run() {
                ParkList();
                //parks.add(Point.fromLngLat(12.901900,43.332550 ));
                FindNearestPark();
                Toast.makeText(getApplicationContext(),"Il timer è scaduto", Toast.LENGTH_LONG).show();
                LaunchThread();
            }
        };

        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }
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
    public void FindNearestPark(){
        Point selected = parks.get(selected_park-1);
        Ordina(destination);
        if(parks.get(0) != selected) {
            Prenota();
            fetchRoute(getLastKnownLocation(), parks.get(0));
        }
        selected_park = 1;


    }





    //Richiede al server la lista dei parcheggi
    public void ParkList(){

        Connessione conn = new Connessione(null, "GET");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/data/parkings");
    }
    //Prenota il parcheggio selezionato inviandolo al server
    public void Prenota(){
        double lat = parks.get(selected_park-1).latitude();
        double longi = parks.get(selected_park-1).longitude();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Parametri.email);
            postData.put("lat",lat);
            postData.put("long",longi);
        } catch (Exception e) {
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
            Toast.makeText(getApplicationContext(), "ERRORE:\nNo connection or server offline.", Toast.LENGTH_LONG).show();
            return;
        }
        if(responseCode.equals("200"))
        {
            try {

                JSONObject risposta = new JSONObject(result);
                JSONArray parcheggi = new JSONArray(risposta.getString("parcheggi"));
                for(int i=0; i<parcheggi.length(); i++){
                    JSONObject parcheggio = (JSONObject) parcheggi.get(i);
                    parks.clear();

                    if(parcheggio.getInt("code")==0) {

                        parks.add(Point.fromLngLat(Double.parseDouble(parcheggio.getString("long")), Double.parseDouble(parcheggio.getString("lat"))));

                    }
                }
                if(responseCode.equals("201"))
                {
                    Toast.makeText(getApplicationContext(), "Penotation Done", Toast.LENGTH_LONG).show();
                    return;
                }

            }catch (Exception e){

            }

        }



    }

}

