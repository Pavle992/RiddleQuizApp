/*
 *  ARSimple.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package com.example.pavle92.riddlequizapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.assets.AssetHelper;
import org.artoolkit.ar.base.rendering.ARRenderer;

/**
 * A very simple example of extending ARActivity to create a new AR application.
 */
public class ARSimple extends ARActivity {


	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;
	public static final String PREFS_NAME = "LoginPrefs";

	private Context kontekst;
    /**
     * A custom renderer is used to produce a new visual experience.
     */



    private SimpleRenderer simpleRenderer = new SimpleRenderer();

    /**
     * The FrameLayout where the AR view is displayed.
     */
    private FrameLayout mainLayout;
	private double lat;
	private double log;
	private String userName;
	private String ridle="";
	private String hint="";
	private String solution="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.activity_model3d);


		kontekst=getApplicationContext();
		Bundle bnd=getIntent().getExtras();
		lat=bnd.getDouble("lat");
		log=bnd.getDouble("log");

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userName=settings.getString("UserName", "");


		//assets
		AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getApplicationContext(),"Data");

		mainLayout = (FrameLayout)this.findViewById(R.id.mainLayout);

		if (!checkCameraPermission()) {
			//if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) { //ASK EVERY TIME - it's essential!
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.CAMERA},
					MY_PERMISSIONS_REQUEST_CAMERA);
		}

		// When the screen is tapped, inform the renderer and vibrate the phone
		mainLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {




                simpleRenderer.click();


                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(40);



//                    Toast.makeText(this,marker.getTitle(),Toast.LENGTH_SHORT).show();



				Thread t1=new Thread(new Runnable() {
					@Override
					public void run() {

						Intent in=new Intent(ARSimple.this,AnswerBox.class);

						Place nearest=MyPlacesHTTPHelper.getNearestPlace(lat,log);
						in.putExtra("lat", Double.valueOf(nearest.getLatitude()));
						in.putExtra("log",Double.valueOf(nearest.getLongitude()));
						in.putExtra("riddleQuestionAnsw",nearest.getRidle() + "&" + nearest.getHint() + "&" + nearest.getSolution());
						in.putExtra("userName",userName);
						in.putExtra("userNameQ","RiddleQuizTeam");
						Log.e("QQQQ", "RiddleQuizTeam");
						startActivityForResult(in, 9890);
					}
				});
				t1.start();

				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}




            }

        });
	}

	/**
	 * Provide our own SimpleRenderer.
	 */
	@Override
	protected ARRenderer supplyRenderer() {
		if (!checkCameraPermission()) {
			Toast.makeText(this, "No camera permission - restart the app", Toast.LENGTH_LONG).show();
			return null;
		}

		return new SimpleRenderer();
	}
	
	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);    	
	}

	private boolean checkCameraPermission() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

//	@Override
//	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//		switch (requestCode) {
//			case MY_PERMISSIONS_REQUEST_CAMERA: {
//				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//					Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//				else
//					Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//				return;
//			}
//		}
//	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==9890) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				b.putString("ridle",ridle);
				b.putString("hint",hint);
				b.putString("solution",solution);

				Intent result=new Intent();
				result.putExtras(b);
				setResult(Activity.RESULT_OK, result);

				finish();

			}


		}

	}

	public Context getInstance(){
		return kontekst;
	}
}