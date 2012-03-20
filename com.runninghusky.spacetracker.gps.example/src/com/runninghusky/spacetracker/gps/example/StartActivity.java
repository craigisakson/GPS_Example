package com.runninghusky.spacetracker.gps.example;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class StartActivity.
 */
public class StartActivity extends Activity {

	/** The m loc manager. */
	private LocationManager mLocManager;

	/** The m loc listener. */
	private LocationListener mLocListener;

	/** The m distance. */
	private TextView mLongitude, mLatitude, mAltitude, mSpeed, mTime,
			mAccuracy, mBearing, mProvider, mDistance;

	/** The m execute. */
	private Button mExecute;

	/** The m update interval. */
	private EditText mUpdateInterval;

	/** The is logging. */
	private Boolean isLogging = false;

	/** The ctx. */
	private Context ctx = this;

	/** The distance. */
	private float distance = 0;

	/** The old loc. */
	private Location oldLoc;

	/** The first run. */
	private Boolean firstRun = true;

	/**
	 * Allows the orientation to change without disrupting the activity.
	 * 
	 * @param newConfig
	 *            the new config
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mExecute = (Button) findViewById(R.id.ButtonStartGPS);
		mExecute.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isLogging) {
					stopLogging();
				} else {
					startLogging();
				}
			}
		});

		mLongitude = (TextView) findViewById(R.id.TextViewLongitude);
		mLatitude = (TextView) findViewById(R.id.TextViewLatitude);
		mAltitude = (TextView) findViewById(R.id.TextViewAltitude);
		mSpeed = (TextView) findViewById(R.id.TextViewSpeed);
		mTime = (TextView) findViewById(R.id.TextViewTime);
		mAccuracy = (TextView) findViewById(R.id.TextViewAccuracy);
		mBearing = (TextView) findViewById(R.id.TextViewBearing);
		mProvider = (TextView) findViewById(R.id.TextViewProvider);
		mDistance = (TextView) findViewById(R.id.TextViewDistance);
		mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mUpdateInterval = (EditText) findViewById(R.id.EditTextUpdateInterval);

	}

	/**
	 * Start logging.
	 */
	private void startLogging() {
		try {
			mLocListener = new OurLocationListener();
			if (String.valueOf(mUpdateInterval.getText()).equals("")) {
				mUpdateInterval.setText("0");
			}
			mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					Integer.valueOf(String.valueOf(mUpdateInterval.getText())),
					0, mLocListener);
			mExecute.setText("Stop");
			isLogging = true;
		} catch (Exception e) {
			Toast.makeText(ctx, "Error occurred...", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Stop logging.
	 */
	private void stopLogging() {
		try {
			if (mLocManager != null) {
				mLocManager.removeUpdates(mLocListener);
				mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}
			mExecute.setText("Start");
			isLogging = false;
		} catch (Exception e) {
			Toast.makeText(ctx, "Error occurred...", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Performs operations on destroy of activity
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocManager != null) {
			if (mLocListener != null) {
				mLocManager.removeUpdates(mLocListener);
			}
			mLocManager = null;
		}
	}

	/**
	 * The listener interface for receiving ourLocation events. The class that
	 * is interested in processing a ourLocation event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addOurLocationListener<code> method. When
	 * the ourLocation event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see OurLocationEvent
	 */
	public class OurLocationListener implements LocationListener {

		/**
		 * onLocationChanged fires when the GPS/provider updates its loc
		 */
		@Override
		public void onLocationChanged(Location loc) {
			if (firstRun) {
				oldLoc = loc;
				firstRun = false;
			}

			distance += loc.distanceTo(oldLoc);

			mLongitude.setText("Longitude:  "
					+ String.valueOf(loc.getLongitude()));
			mLatitude
					.setText("Latitude:  " + String.valueOf(loc.getLatitude()));
			mAltitude
					.setText("Altitude:  "
							+ String.valueOf(roundTwoDecimals(loc.getAltitude() * 3.2808399))
							+ "ft above sea level");
			mSpeed.setText("Speed:  "
					+ String.valueOf(roundTwoDecimals(loc.getSpeed() * 2.23693629))
					+ " mph");

			Date d = new Date(loc.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			mTime.setText("Time at update:  " + sdf.format(d));

			mAccuracy
					.setText("Accuracy:  " + String.valueOf(loc.getAccuracy()));
			mBearing.setText("Bearing:  " + String.valueOf(loc.getBearing()));
			mProvider
					.setText("Provider:  " + String.valueOf(loc.getProvider()));
			mDistance
					.setText("Distance:  "
							+ String.valueOf(roundTwoDecimals(distance * 0.000621371192)));
			oldLoc = loc;
		}

		/**
		 * onProviderDisabled fires when the GPS/provider gets disabled
		 */
		@Override
		public void onProviderDisabled(String arg0) {
			Toast.makeText(ctx, "Provider Disabled", Toast.LENGTH_SHORT).show();
		}

		/**
		 * onProviderEnabled fires when the GPS/provider is enabled
		 */
		@Override
		public void onProviderEnabled(String arg0) {
			Toast.makeText(ctx, "Provider Enabled", Toast.LENGTH_SHORT).show();
		}

		/**
		 * onStatusChanged fires when the GPS/provider status changes
		 */
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			Toast.makeText(ctx, "Status Changed", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Round two decimals.
	 * 
	 * @param d
	 *            the d
	 * @return the double
	 */
	private double roundTwoDecimals(double d) {
		try {
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(d));
		} catch (NumberFormatException nfe) {
			Log.d("nfe", nfe.toString());
			try {
				int i = (int) (d * 100);
				d = (i / 100);
				return d;
			} catch (NumberFormatException ne) {
				return d;
			}
		}
	}
}