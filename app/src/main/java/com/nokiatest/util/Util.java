package com.nokiatest.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.nokiatest.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;


public class Util {
    public static final int REQUEST_STORAGE_PERMISSION = 1;
    public ProgressDialog getProgressDialog(Context context) {
        ProgressDialog pDialog;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        return  pDialog;
    }

    public void hideProgressDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.cancel();
    }

    public boolean isInternetConnected(Context context) {

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean ret = true;
        if (conMgr != null) {
            NetworkInfo i = conMgr.getActiveNetworkInfo();

            if (i != null) {
                if (!i.isConnected()) {
                    ret = false;
                }

                if (!i.isAvailable()) {
                    ret = false;
                }
            }

            if (i == null)
                ret = false;
        } else
            ret = false;
        return ret;
    }

    public static void requestPermission(Activity activity, String permissionName, int requestCode) {
        Log.e(activity.getLocalClassName(), "Permission has NOT been granted. Requesting permission.");

        if (shouldShowRequestPermissionRationale(activity, permissionName)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(activity.getLocalClassName(), "Displaying phone state permission rationale to provide additional context.");

            ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);

        } else {

            // Phone permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[]{permissionName},
                    requestCode);

        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean isString(String str) {

        if (str != null && !str.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String generateMD5(File encTarget) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("calculateMD5", "Exception while getting Digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(encTarget);
        } catch (FileNotFoundException e) {
            Log.e("calculateMD5", "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("calculateMD5", "Exception on closing MD5 input stream", e);
            }
        }
    }

}
