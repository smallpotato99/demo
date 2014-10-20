package com.example.android.navigationdrawerexample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	
	// --- Google Map coding start ---
	private static final int GPS_ERRORDAILOG_REQUEST = 9001;
	GoogleMap mMap;
	
	Boolean _isInternetPresent = false;
	chkConnectivity _cd;	
	
	private static final float DEFAULTZOOM = 15;
	
	LocationClient mLocationClient;
	Marker marker;
	Marker marker1, marker2;
	Polyline line;
	ArrayList<Marker> markers = new ArrayList<Marker>();
	static final int POLYGON_POINTS = 3;
//	Polygon shape;
	Circle shape;
	
	protected android.location.LocationListener MainActivity;
	// --- Google Map coding end ---
	
	// --- Menu Drawer start ---
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
 // --- Menu Drawer end ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _cd = new chkConnectivity(getApplicationContext());
        _isInternetPresent = _cd.isConnectingToInternet();
        if (_isInternetPresent == false) {
        	Toast.makeText(this, "No Internet available", Toast.LENGTH_LONG).show();
        	this.finish();
        }
        if (servicesOK()) {        	   	
        	setContentView(R.layout.activity_main);
        	       	
        	if (initMap()) {
        		Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
        		mLocationClient = new LocationClient(this, this, this);
        		mLocationClient.connect();

		        mTitle = mDrawerTitle = getTitle();
		        mPlanetTitles = getResources().getStringArray(R.array.menu_array);
		        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		        // set a custom shadow that overlays the main content when the drawer opens
		        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		        // set up the drawer's list view with items and click listener
		        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
		        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		        // enable ActionBar app icon to behave as action to toggle nav drawer
		        getActionBar().setDisplayHomeAsUpEnabled(true);
		        getActionBar().setHomeButtonEnabled(true);
		
		        // ActionBarDrawerToggle ties together the the proper interactions
		        // between the sliding drawer and the action bar app icon
		        mDrawerToggle = new ActionBarDrawerToggle(
		                this,                  /* host Activity */
		                mDrawerLayout,         /* DrawerLayout object */
		                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
		                R.string.drawer_open,  /* "open drawer" description for accessibility */
		                R.string.drawer_close  /* "close drawer" description for accessibility */
		                ) {
		            public void onDrawerClosed(View view) {
		                getActionBar().setTitle(mTitle);
		                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		            }
		
		            public void onDrawerOpened(View drawerView) {
		                getActionBar().setTitle(mDrawerTitle);
		                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		            }
		        };
		        mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		        if (savedInstanceState == null) {
		            selectItem(0);
		        }
        
        	}
        	else {
        		Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
        	}
        }
        else {
        	setContentView(R.layout.activity_main);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.actionView);
        SearchView searchView = (SearchView) item.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:        	
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	//Return menu drawer click
        	switch (position) {
        	case 1:
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
        		Toast.makeText(MainActivity.this, "11111", Toast.LENGTH_SHORT).show();
        		break;
        	case 0:
        		mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
        		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        		startActivity(intent);
        		break;
    		default:
    			break;
        	}
//            selectItem(position);
        	mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments    	
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.menu_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		String msg = "Location: " + location.getLatitude() + ", " + location.getLongitude();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

    @Override
    public void onDisconnected() {
    	Toast.makeText(this, "Disconnected to location service", Toast.LENGTH_SHORT).show();
    }
	
	public boolean servicesOK() {
    	int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	
    	if (isAvailable == ConnectionResult.SUCCESS) {
    		return true;
    	}
    	else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDAILOG_REQUEST);
    		dialog.show();    		
    	}
    	else {
    		Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
    	}
    	return false;
    }
    
    private boolean initMap() {
    	if (mMap == null) { 
    		SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    		mMap = mapFrag.getMap();
    		mMap.setMyLocationEnabled(false);
    		mMap.getUiSettings().setCompassEnabled(false);
    		mMap.getUiSettings().setZoomControlsEnabled(false);
    		mMap.getUiSettings().setMyLocationButtonEnabled(false);	
    		
    		if (mMap != null) {    			    		
    			mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
        			
    				@Override
    				public void onMapLongClick(LatLng ll) {
    					Geocoder gc = new Geocoder(MainActivity.this);
    					List<Address> list = null;

    					try {
    						list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
    					}
    					catch (IOException e) {
    						e.printStackTrace();
    						return;    						
    					}
    					
    					Address add = list.get(0);
//    					MainActivity.this.setMarker(add.getLocality(), add.getCountryName(), ll.latitude, ll.longitude);
    					MainActivity.this.setMarker(add.getLocality(), ll.latitude, ll.longitude);
    				}
    			});
    			
    			mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
    				
    				@Override
    				public boolean onMarkerClick(Marker marker) {
    		    		    		    
    					String msg = marker.getTitle() + " (" + marker.getPosition().latitude + "," + marker.getPosition().longitude + ")";
    					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    					
    					return false;
    				}
    			});
    			    			
    			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					public void onInfoWindowClick(Marker marker) {						 
						if (marker.getTitle().equals("Current Location")) {
						}
						else {
							Intent intent = new Intent(MainActivity.this, TourActivity.class);						
							startActivity(intent);
						}
					}    				
    			});
    		}
    	}
    	return (mMap != null);
    }
    
    private void gotoLocation(double lat, double lng, float defZoom) {
    	LatLng ll = new LatLng(lat, lng);
    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,defZoom);
    	mMap.moveCamera(update);    	
    }
    
	public void geoLocate(View v) throws IOException {
    	EditText et = (EditText) findViewById(R.id.editText1);
    	String location = et.getText().toString();
    	if ( location.length() == 0 ) {    	
    		Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	hideSoftKeyboard(v);
    	
    	Geocoder gc = new Geocoder(this);
    	List<Address> list = gc.getFromLocationName(location, 1);
    	Address add = list.get(0);
    	String locality = add.getLocality();
//    	String country = add.getCountryName();
    	
    	Toast.makeText(this, locality, Toast.LENGTH_LONG).show();  
    	
    	double lat = add.getLatitude();
    	double lng = add.getLongitude();    	
    	
    	gotoLocation(lat, lng, DEFAULTZOOM);
    	
//    	setMarker(locality, country, lat, lng);
    	setMarker(locality, lat, lng);
    }
    
    private void hideSoftKeyboard(View v) {
    	InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    
   @Override
    protected void onStop() {
    	super.onStop();
    	MapStateManager mgr = new MapStateManager(this);
    	mgr.saveMapState(mMap);    	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	MapStateManager mgr = new MapStateManager(this);
    	CameraPosition position = mgr.getSavedCameraPosition();    	
    	if (position != null) {
    		CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
    		mMap.moveCamera(update);
    	}
    }
    
public void gotoCurrentLocation(View v) throws IOException {
    	
    	Location currentLocation = mLocationClient.getLastLocation();
    	if (currentLocation == null) {
    		Toast.makeText(this, "Current location isn't available", Toast.LENGTH_LONG).show();    		
    	}
    	else {
    		LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
    		mMap.animateCamera(update);
    		setMarker("Current Location", currentLocation.getLatitude(), currentLocation.getLongitude());
    	}
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
    	Toast.makeText(this, "Failed to connected location service", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onConnected(Bundle arg0) {
    	Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
    	
    	LocationRequest request = LocationRequest.create();
//    	request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    	request.setInterval(60000);
//    	request.setFastestInterval(1000);    	
    	mLocationClient.requestLocationUpdates(request, this);
    }
    
//	private void setMarker(String locality, String country, double lat, double lng) {
	private void setMarker(String locality, double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		
		if (markers.size() == POLYGON_POINTS) {
			removeEverything();
		}
		MarkerOptions options = new MarkerOptions()
		.title(locality)
		.position(ll)
		// center marker with location
//		.anchor(.5f, .5f)
//		 Use it for default marker icon
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//		.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag))
		.draggable(true);		
//		if (country.length() > 0) {
//			options.snippet(country);
//		}
		
		if (marker != null) {
			removeEverything();
		}
		
		marker = mMap.addMarker(options);
		
	}
	
	private void removeEverything() {
		marker.remove();
		marker = null;
	}
}