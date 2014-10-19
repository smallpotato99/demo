package com.example.android.navigationdrawerexample;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;

//@SuppressWarnings("unused")
public class MapStateManager {

	private static final String LONGTITUDE = "longtitude";
	private static final String LATITUDE = "latitude";
	private static final String ZOOM = "zoom";	
	private static final String BEARING = "bearing";
	private static final String TILT = "tilt";
	private static final String MAPTYPE = "MAPTYPE";
	
	private static final String PREFS_NAME = "mapCameraState";
	
	private SharedPreferences mapStatePrefs;
	
	/**
	 * @param args
	 */
	public MapStateManager(Context context) {
		// TODO Auto-generated method stub
		mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveMapState(GoogleMap map) {
		SharedPreferences.Editor editor = mapStatePrefs.edit();
		CameraPosition position = map.getCameraPosition();
		
		editor.putFloat(LATITUDE, (float) position.target.latitude);
		editor.putFloat(LONGTITUDE, (float) position.target.longitude);
		editor.putFloat(ZOOM, position.zoom);
		editor.putFloat(TILT, position.tilt);
		editor.putFloat(BEARING, position.bearing);
		editor.putInt(MAPTYPE, map.getMapType());
		
		editor.commit();
	}
	
	public CameraPosition getSavedCameraPosition() {
		double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
		
		if (latitude == 0) {
			return null;
		}
		double longtitude = mapStatePrefs.getFloat(LONGTITUDE, 0);
		LatLng target = new LatLng(latitude, longtitude);
		
		float zoom = mapStatePrefs.getFloat(ZOOM, 0);
		float bearing = mapStatePrefs.getFloat(BEARING, 0);
		float tilt = mapStatePrefs.getFloat(TILT, 0);
		
		CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
		return position;
	}

}
