package us.mose.googlemapspermision;

import android.app.Dialog;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(servicesOK()){
            setContentView(R.layout.activity_map);
            if(initMap()){
                Toast.makeText(this, "Klar til at mappe",Toast.LENGTH_SHORT).show();
                gotoLocation(56.440547,10.135221,10);
            }else{
                Toast.makeText(this, "Kortet er ikke til rådighed",Toast.LENGTH_SHORT).show();
            }

        } else{
            setContentView(R.layout.activity_main);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK(){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);

        //int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable== ConnectionResult.SUCCESS){
            return true;

        }else if (googleAPI.isUserResolvableError(isAvailable)){
        //}else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
           Dialog dialog = googleAPI.getErrorDialog(this,isAvailable,ERROR_DIALOG_REQUEST);

         //  Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Kan ikke tilslutte sig mapping Services",Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    private boolean initMap(){
        if(mMap==null){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //mMap =mapFragment.getMapAsync(this);
            mMap =mapFragment.getMap();
        }
        return (mMap != null);

    }

    private void gotoLocation(double lat, double lng,float zoom){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.moveCamera(update);
    }

    private void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);

    }
    public void geoLocate (View v) throws IOException {
        hideSoftKeyboard(v);
        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = null;

        searchString = tv.getText().toString();

        Toast.makeText(this, "Søger efter: " + searchString, Toast.LENGTH_SHORT).show();

        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            android.location.Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Fundet " + locality, Toast.LENGTH_SHORT).show();
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, 15);

        }


    }
}
