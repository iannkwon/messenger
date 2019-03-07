package deo.com.signaldemo.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.Inflater;

import deo.com.signaldemo.DeviceListActivity;
import deo.com.signaldemo.MainActivity;
import deo.com.signaldemo.SessionNow;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static deo.com.signaldemo.DeviceListActivity.EXTRA_BLE_ADDRESS;
import static deo.com.signaldemo.DeviceListActivity.EXTRA_DEVICE_ADDRESS;


public class BluetoothService extends Service{

    private static final String TAG = "BluetoothService";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private int mState;

    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    private BluetoothAdapter bluetoothAdapter;

    private Activity activity;
    private Handler handler;
    public static Context context;

    public BluetoothService(){ }




    public BluetoothService(Activity activity, Handler handler){
        this.activity = activity;
        this.handler = handler;

        // BluetoothAdapter 얻기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
     }

    public BluetoothService(Activity activity){
        this.activity = activity;

        // BluetoothAdapter 얻기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Service");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // BluetoothAdapter 얻기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context = this;

        Log.d(TAG, "getState:"+getState());

        getDeviceInfo(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean getDeviceState(){
        Log.d(TAG, "Check the Bluetooth support");

        if (bluetoothAdapter == null){
            Log.d(TAG, "Bluetooth is not available");

            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");

            return true;
        }
    }

    public void enableBluetooth(){

        Log.i(TAG, "Check the enable Bluetooth");

        if (bluetoothAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth Enable Now");
            scanDevice();

        } else {
            Log.d(TAG, "Bluetooth Enable Request");

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    public void scanDevice(){
        Log.d(TAG, "Scan Device");

        Intent intent = new Intent(activity, DeviceListActivity.class);
        activity.startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }

    public void getDeviceInfo(Intent data){
        String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
        Log.d(TAG, "Get Device Info "+"Address:"+address);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        connect(device);
    }

    // bluetooth state set
    private synchronized void setState(int state){
        Log.d(TAG, "setState()"+mState+"->"+state);
        mState = state;
    }
    // bluetooth state get
    public synchronized int getState(){
        return mState;
    }

    public synchronized void start(){
        Log.d(TAG, "start");

        // cancel any thread attempting to make a connection
        if (connectThread == null){

        } else {
            connectThread.cancel();
            connectThread = null;
        }

        // cancel any thread currently running a connetion
        if (connectedThread == null){

        } else {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    // connectThread init device all connection delete
    public synchronized void connect(BluetoothDevice device){
        Log.d(TAG, "connect to:"+device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (connectThread == null) {

            } else {
                connectThread.cancel();
                connectThread = null;
            }
        }
        // cancel any thread currently running a connection
        if (connectedThread == null){

        } else {
            connectedThread.cancel();
            connectedThread = null;
        }
            // start the thread to connect with the given device
            connectThread = new ConnectThread(device);

            connectThread.start();
            setState(STATE_CONNECTING);
    }

    // connectedThread 초기화
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device){
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (connectThread == null){

        } else {
            connectThread.cancel();
            connectThread = null;
        }

        // cancel any thread currently running a connection
        if (connectedThread == null){

        } else {
            connectedThread.cancel();
            connectedThread = null;
        }

        // start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        setState(STATE_CONNECTED);
    }

    // 모든 스레드 정지
    public synchronized void stop(){
        Log.d(TAG, "stop");

        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_NONE);
    }

    // 값을 쓰는 부분(보내는 부분)
    public void write(byte[] out){ // Create temporary object
                                    // Synchronize a copy of the ConnectedThread
        ConnectedThread r;
        synchronized (this){
            if (mState != STATE_CONNECTED) return;
                r = connectedThread;
        } // Perform the write unsynchronized r.write(out); }
        r.write(out);
    }

    // When connection failed
    private void connectionFailed(){
        setState(STATE_LISTEN);
    }
    // When connection losted
    private void connectionLost(){
        setState(STATE_LISTEN);
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

         public ConnectThread(BluetoothDevice device){
             // Use a temporary object that is later assigned to mmSocket,
             // because mmSocket is final
             BluetoothSocket tmp = null;
             mDevice = device;

             // Get a BluetoothSocket to connect with the given BluetoothDevice
             try{
                 // MY_UUID is the app's UUID string, also used by the server code
                 tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
             } catch (IOException e){
//                 Log.e(TAG, "create() failed", e);
             }
             mSocket = tmp;

//             SessionNow.setSession(activity.getApplicationContext(), "device", mDevice.toString());
//             SessionNow.setSession(activity.getApplicationContext(), "socket", mSocket.toString());
//             Log.d("Connected device", mDevice.toString());
//             Log.d("Connected socket", mSocket.toString());
         }

         public void run(){
             Log.i(TAG, "BEGIN mConnectThread");
             setName("ConnectThread");
             bluetoothAdapter.cancelDiscovery();
             // 연결 시도 전 항상 기기 검색 중지 계속 시 연결속도 느려짐
             try {
                 mSocket.connect();
                 Log.d(TAG, "Connect Successed");
             } catch (IOException e){
                 connectionFailed();
                 Log.d(TAG, "Connect Failed");

                 try{
                     mSocket.close();
                     Log.d(TAG, "Connect close");
                 }catch (IOException e2){
                     Log.e(TAG, "unble to close() socket during connection failure", e2);
                 }
                 // 연결 중 혹은 연결 대기 상태인 메소드 호출
                 BluetoothService.this.start();
                 return;
             }
             // 클래스 리셋
             synchronized (BluetoothService.this){
                 connectThread = null;
             }

             // connectedThread 클래스 시작
             connected(mSocket, mDevice);
         }

         public void cancel(){
             try{
                 mSocket.close();
             }catch (IOException e){
                 Log.e(TAG, "close() of connect socket failed", e);
             }
         }

     }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "create ConnectedThread");
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e){
                Log.e(TAG, "temp socket not create", e);
            }
            mInputStream = tmpIn;
            mOutputStream = tmpOut;
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

//            SessionNow.setSession(getApplicationContext(), "bluetoothStatus", "true");
//            Log.d("mInputStream", mInputStream.toString());
//            Log.d("mOutputStream", mOutputStream.toString());
//            BluetoothService.this.write("Test".getBytes());

            // keep listening to the InputStream while connected
            while (true){
                try{
                    bytes = mInputStream.read(buffer);
//                    mhandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                } catch (IOException e){
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
        public void write(byte[] buffer){
                try{
                mOutputStream.write(buffer);
//                mhandler.obtainMessage(1, -1, -1, buffer).sendToTarget();
            } catch (IOException e){
                Log.e(TAG, "Exception during write", e);
            }
        }
        public void cancel(){
            try{
                mSocket.close();
            } catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}


