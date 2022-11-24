package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    ParcelFileDescriptor fileDescriptor;
    UsbManager usbManager;
    private boolean mIsShutdown;
    private boolean mIsAttached;
    private FileOutputStream mOutputStream;
    private FileInputStream mInputStream;
    private ParcelFileDescriptor mParcelFileDescriptor;
    boolean permissionIntent;
    private PendingIntent mPermissionIntent;
    //    private static final String ACTION_USB_PERMISSION =
//            "com.android.example.USB_PERMISSION";
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (accessory != null) {
                            //call method to set up accessory communication
                        }
                    } else {
                        Log.d("TAG", "permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("AOA", "This is test");


//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(usbReceiver, filter);

//        BufferHolder mReadBuffer;

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AOA
                usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                UsbAccessory[] accessories = usbManager.getAccessoryList();
                Log.d("AOA", "This is test2");

                Log.d("AOA", accessories[0] + "accesories");
                if (accessories.length > 1) {
                    Log.w("AOA", "Multiple accessories attached!? Using first one...");
                }

                Log.d("AOA", usbManager.hasPermission(accessories[0]) + " >>>ACCESSORY PERMISSION");
                //        maybeAttachAccessory(accessories[0]);
                Intent intent = new Intent("USB_ACCESSORY_ATTACHED");
//                UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
//                Log.d("AOA",accessory+"  >>>accesory");
                /**f
                 * Attach accessory
                 */
                if (accessories.length >= 1) {
                    usbManager.requestPermission(accessories[0], mPermissionIntent);
                    Log.d("AOA", usbManager.hasPermission(accessories[0]) + " >>>ACCESSORY PERMISSION");

                    Log.w("AOA", "Testing wala");
                    maybeAttachAccessory(accessories[0]);
                }

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void maybeAttachAccessory(final UsbAccessory accessory) {
        final ParcelFileDescriptor parcelFileDescriptor = usbManager.openAccessory(accessory);

        Log.d("AOA", parcelFileDescriptor + " >>>parcelFileDescriptor");

        if (parcelFileDescriptor != null) {
            final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            mIsAttached = true;
            mOutputStream = new FileOutputStream(fileDescriptor);
            mInputStream = new FileInputStream(fileDescriptor);
            mParcelFileDescriptor = parcelFileDescriptor;
//            Thread thread = new Thread(null, this, "AccessoryThread");
//            thread.start();

            Log.d("AOA", mOutputStream + " >>>outputstream");
            Log.d("AOA", mInputStream + " >>>inputstream");


//            mHandler.sendEmptyMessage(MAYBE_READ);
        }
    }


}