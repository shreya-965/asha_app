package com.example.asha_app;

import android.Manifest;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.os.*;
import android.widget.*;
import android.content.Intent;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic dashCharacteristic;
    private TextView tvStatus;

    // Replace with correct UUIDs using nRF Connect
    private final UUID SERVICE_UUID = UUID.fromString("af230006-879d-6186-1f49-deca0e85d9c1");
    private final UUID CHARACTERISTIC_UUID = UUID.fromString("af230006-879d-6186-1f49-deca0e85d9c1");

    private final byte[] SAY_HI_COMMAND = hexStringToByteArray("53595354444153485f48495f564f0b00c900");

    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ✅ Must be called before findViewById()

        tvStatus = findViewById(R.id.tvStatus);
        Button btnScan = findViewById(R.id.btnScanAndConnect);
        Button btnSend = findViewById(R.id.btnSendCommand);
        Button btnStart = findViewById(R.id.btnStart); // ✅ Safe now

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();

        btnScan.setOnClickListener(v -> {
            updateStatus("Scanning...");
            startScan();
        });

        btnSend.setOnClickListener(v -> {
            if (dashCharacteristic != null && bluetoothGatt != null) {
                dashCharacteristic.setValue(SAY_HI_COMMAND);
                boolean result = bluetoothGatt.writeCharacteristic(dashCharacteristic);
                updateStatus(result ? "Sent Say Hi!" : "Failed to send.");
            } else {
                updateStatus("Not connected or characteristic missing.");
            }
        });

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LessonPlanActivity.class);
            startActivity(intent);
        });

        requestPermissions();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private void startScan() {
        bleScanner.startScan(scanCallback);
        new Handler().postDelayed(() -> {
            bleScanner.stopScan(scanCallback);
            if (bluetoothGatt == null) {
                updateStatus(getString(R.string.dash_not_found));
            }
        }, 10000);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN})
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device.getName() != null && device.getName().toLowerCase().contains("dash")) {
                bleScanner.stopScan(this);
                updateStatus("Connecting to " + device.getName());
                device.connectGatt(MainActivity.this, false, gattCallback);
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt;
                updateStatus("Connected! Discovering services...");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                updateStatus("Disconnected.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if (service != null) {
                dashCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                updateStatus(dashCharacteristic != null ? "Ready to send command!" : "Characteristic not found.");
            } else {
                updateStatus("Service not found.");
            }
        }
    };

    private void updateStatus(String msg) {
        runOnUiThread(() -> tvStatus.setText(msg));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        return data;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) bluetoothGatt.close();
    }
}
