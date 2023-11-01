package com.example.olxapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamiltondamasceno
 */

public class Permissions {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> permissionsList = new ArrayList<>();

            /*Verify if permission is already authorized */
            for ( String permission : permissions ){
                Boolean havePermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if ( !havePermission ) permissionsList.add(permission);
            }

            /*if list is empty don't ask for permission*/
            if ( permissionsList.isEmpty() ) return true;
            String[] newPermissions = new String[ permissionsList.size() ];
            permissionsList.toArray( newPermissions );

            //Asks for permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode );


        }

        return true;

    }

}
