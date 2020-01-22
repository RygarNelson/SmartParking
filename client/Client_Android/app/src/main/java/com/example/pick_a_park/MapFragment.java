package com.example.pick_a_park;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.pick_a_park.R;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements ConnessioneListener{

    private MapView mapView;
    private MapboxMap map;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.map_box_key));
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setContentView(R.layout.fragment_map);

        mapView = (MapView) getActivity().findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        map = mapboxMap;
                    }
                });

            }
        });
        ParkList();
        // Inflate the layout for this fragment
        return view;
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
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(parcheggio.getString("lat")), Double.parseDouble(parcheggio.getString("long")))));

                    x++;
                }
                x++;

            }catch (Exception e){

            }

        }
    }
}
