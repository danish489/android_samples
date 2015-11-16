/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.basicpermissions;

import com.example.android.basicpermissions.camera.CameraPreviewActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Launcher Activity that demonstrates the use of runtime permissions for Android M.
 * This Activity requests permissions to access the camera
 * ({@link android.Manifest.permission#CAMERA})
 * when the 'Show Camera Preview' button is clicked to start  {@link CameraPreviewActivity} once
 * the permission has been granted.
 * <p>
 * First, the status of the Camera permission is checked using {@link
 * Activity#checkSelfPermission(String)}.
 * If it has not been granted ({@link PackageManager#PERMISSION_GRANTED}), it is requested by
 * calling
 * {@link Activity#requestPermissions(String[], int)}. The result of the request is returned in
 * {@link Activity#onRequestPermissionsResult(int, String[], int[])}, which starts {@link
 * CameraPreviewActivity}
 * if the permission has been granted.
 */
public class MainActivity extends Activity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register a listener for the 'Show Camera Preview' button.
        Button b = (Button) findViewById(R.id.button_open_camera);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraPreview();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(this, "Camera permission was granted. Starting preview.",
                        Toast.LENGTH_SHORT)
                        .show();
                startCamera();
            } else {
                // Permission request was denied.
                Toast.makeText(this, "Camera permission request was denied.", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    private void showCameraPreview() {

        if (!isMNC()) {
            // Below Android M there is no need to check for runtime permissions
            Toast.makeText(this,
                    "Requested permissions are granted at install time below M and are always "
                            + "available at runtime.",
                    Toast.LENGTH_SHORT).show();
            startCamera();
            return;
        }

        // Check if the Camera permission has been granted
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted and must be requested.

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                Toast.makeText(this, "Camera access is required to display a camera preview.",
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this,
                    "Permission is not available. Requesting camera permission.",
                    Toast.LENGTH_SHORT).show();

            // Request the permission. The result will be received in onRequestPermissionResult()
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            // Permission is already available, start camera preview
            startCamera();
            Toast.makeText(this,
                    "Camera permission is available. Starting preview.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void startCamera() {
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);
    }

    public static boolean isMNC() {
        /*
         TODO: In the Android M Preview release, checking if the platform is M is done through
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
        // return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        return "MNC".equals(Build.VERSION.CODENAME);
    }
}
