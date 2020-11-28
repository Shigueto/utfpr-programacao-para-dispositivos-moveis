package br.edu.utfpr.gpsmapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int GET_LAST_LOCATION_REQUEST_CODE = 1;
    private static final int GET_LOCATION_UPDATES_REQUEST_CODE = 2;

    private FusedLocationProviderClient client;
    private TextView txtLatitude;
    private TextView txtLongitude;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);

        txtLatitude.setText(getString(R.string.latitude, ""));
        txtLongitude.setText(getString(R.string.longitude, ""));

        client = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLocations().get(0);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    txtLatitude.setText(getString(R.string.latitude, String.valueOf(location.getLatitude())));
                    txtLongitude.setText(getString(R.string.longitude, String.valueOf(location.getLongitude())));
                }
            }
        };

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(15);
    }

    @SuppressLint("MissingPermission")
    private void obterUltimaLocalizacao() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    txtLatitude.setText(getString(R.string.latitude,
                            String.valueOf(location.getLatitude())));
                    txtLongitude.setText(getString(R.string.longitude,
                            String.valueOf(location.getLongitude())));
                }
            }
        });
    }

    public void onObterUltimaLocalizacaoClick(View view) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        checkLocationSettings(locationRequest, GET_LAST_LOCATION_REQUEST_CODE,
                new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (hasLocationPermission(GET_LAST_LOCATION_REQUEST_CODE)) {
                            obterUltimaLocalizacao();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_LAST_LOCATION_REQUEST_CODE:
            case GET_LOCATION_UPDATES_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == GET_LAST_LOCATION_REQUEST_CODE) {
                        obterUltimaLocalizacao();
                    } else {
                        startLocationUpdates();
                    }
                } else {
                    Toast.makeText(this,
                            getString(R.string.get_last_location_request_permisson_denied),
                            Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_LAST_LOCATION_REQUEST_CODE:
            case GET_LOCATION_UPDATES_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (hasLocationPermission(requestCode)) {
                        if (resultCode == GET_LAST_LOCATION_REQUEST_CODE) {
                            obterUltimaLocalizacao();
                        } else {
                            startLocationUpdates();
                        }
                    }
                } else {
                    Toast.makeText(this,
                            getString(R.string.get_last_location_request_permisson_denied),
                            Toast.LENGTH_LONG).show();
                }
        }
    }

    private void checkLocationSettings(LocationRequest locationRequest, final int requestCode,
                                       OnSuccessListener<LocationSettingsResponse> onSuccessListener) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(onSuccessListener);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, requestCode);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean hasLocationPermission(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            return false;
        }
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        client.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationSettings(locationRequest, GET_LOCATION_UPDATES_REQUEST_CODE,
                new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (hasLocationPermission(GET_LOCATION_UPDATES_REQUEST_CODE)) {
                            startLocationUpdates();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void onVisualizarNoMapaClick(View v) {
        if (latitude != null && longitude != null) {
            Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // intent específico para Google Maps
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    public void onVisualizarNoMapaInternoClick(View v) {
        startActivity(new Intent(this, MapsActivity.class)
                .putExtra("latitude", latitude)
                .putExtra("longitude", longitude));
    }
}