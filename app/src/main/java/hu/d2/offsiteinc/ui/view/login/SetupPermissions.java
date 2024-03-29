package hu.d2.offsiteinc.ui.view.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import hu.d2.offsiteinc.R;



public class SetupPermissions extends AbsRuntimePermission {

    public static Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = getApplicationContext();

        requestAppPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE}, R.string.app_permissionMsg, 10);

    }

    /**
     * @param requestCode
     * If the user grant all permission - login activity is loading
     */
    @Override
    public void onPermissionsGranted(int requestCode) {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);

    }
}
