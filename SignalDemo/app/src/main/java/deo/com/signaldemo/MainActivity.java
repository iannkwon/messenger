package deo.com.signaldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import deo.com.signaldemo.adapter.ChatListAdapter;
import deo.com.signaldemo.adapter.MainFragmentAdapter;
import deo.com.signaldemo.databinding.ActivityMainBinding;
import deo.com.signaldemo.item.Device;
import deo.com.signaldemo.service.BluetoothService;
import deo.com.signaldemo.viewmodel.MainViewModel;

import static deo.com.signaldemo.DeviceListActivity.EXTRA_BLE_ADDRESS;
import static deo.com.signaldemo.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String[] myProfiles = new String[4];

    private static final String TAG = "BluetoothService Main";
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static Context context;

    private Timer timer;
    private TimerTask timerTask;
    private BluetoothService bluetoothService;
    private BluetoothAdapter bluetoothAdapter;
    public boolean bluetoothStatus = false;
    String macAddress;
    String deviceAddress;
    String bleAddress;
    String status;
    int requestCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE); // delete title bar
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        int position = getIntent().getIntExtra("position", 0);

        myProfiles[0] = SessionNow.getSession(this, "email");
        myProfiles[1] = SessionNow.getSession(this, "nickname");
        myProfiles[2] = SessionNow.getSession(this, "uid");
//        Log.d("email", myProfiles[0]);
//        Log.d("nickname", myProfiles[1]);
        MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager(), myProfiles);
        MainViewModel mainViewModel = new MainViewModel(this, binding.addFab, binding.tabLayout, binding.viewPager, adapter, position);
        mainViewModel.onCreate();
        binding.setMainModel(mainViewModel);

        bluetoothService();
        menuTimer();
    }

    public void bluetoothService(){

        Boolean serviceCheck = isServiceRunning();

        macAddress = android.provider.Settings.Secure
                .getString(getApplicationContext().getContentResolver(), EXTRA_BLE_ADDRESS);
        deviceAddress = SessionNow.getSession(getApplicationContext(), EXTRA_DEVICE_ADDRESS);
        bleAddress = SessionNow.getSession(getApplicationContext(), EXTRA_BLE_ADDRESS);

        if (macAddress.equals(bleAddress) && !serviceCheck) {
            Intent intent = new Intent(MainActivity.this, BluetoothService.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
            startService(intent);
            Toast.makeText(this, "Connecting bluetooth", Toast.LENGTH_LONG).show();
        } else if (serviceCheck) {
//            Toast.makeText(this, "Connected bluetooth", Toast.LENGTH_LONG).show();
            if (requestCount < 5){
                requestBluetooth();
            }
        } else {
            Toast.makeText(this, "Turn on bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (BluetoothService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    int timerCount = 1;
    private void requestBluetooth(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (((BluetoothService)BluetoothService.context).getState() < 3){
                            if (timerCount%2 == 0 ){
                                Toast.makeText(getApplicationContext(), "Check bluetooth connection status", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, BluetoothService.class);
                                stopService(intent);
                            }else{
                                bluetoothService();
                            }
                            if (timerCount > 6){
                                timerTask.cancel();
                                timer.cancel();
                                timerCount = 1;
                            }
                            timerCount++;
                            requestCount++;
                        } else{
                            timerTask.cancel();
                            timer.cancel();
                            timerCount = 1;
                            requestCount = 0;
                            bluetoothStatus = true;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 10000);    // 3초 후부터 10초 마다
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        if (isServiceRunning()){
            if (((BluetoothService)BluetoothService.context).getState() < 3){
                menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device_disconnected);
            }else{
                menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device);
            }
        }else{
            menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device_disconnected);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void menuTimer(){

        final Timer  timer2 = new Timer();
        final TimerTask timerTask2 = new TimerTask() {
            int count=0;
            @Override
            public void run() {
                invalidateOptionsMenu();
                if (count > 2){
                    timer2.cancel();
                }
                count++;
            }
        };
        timer2.schedule(timerTask2, 3000,5000);
    }


//    private final Handler mhandler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 1:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    String writeMessage = new String(writeBuf);
//                    Log.d("handler writeMessage", writeMessage);
//                    break;
//
//                case 2:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Log.d("handler readMessage", readMessage);
//                    break;
//            }
//        }
//    };

    public void bluetoothConnect(){
        if(bluetoothService == null) {
            bluetoothService = new BluetoothService(this);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if(bluetoothService.getDeviceState()){
            bluetoothService.enableBluetooth();
        }else{
            finish();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK){
                    bluetoothService.getDeviceInfo(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK){
                    // Next step
                    bluetoothService.scanDevice();
                } else{
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
