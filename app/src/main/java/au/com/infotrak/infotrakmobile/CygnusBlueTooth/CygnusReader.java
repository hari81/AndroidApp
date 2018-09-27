package au.com.infotrak.infotrakmobile.CygnusBlueTooth;

/**
 * Created by TinO on 4/12/2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

public class CygnusReader {

    private BluetoothDevice _device=null;
    private BluetoothAdapter _adapter =null;
    private BluetoothSocket _socket =null;
    private UUID _MY_UUID = null;
    private String _status = "UNKNOWN";
    private static CygnusReader _ourInstance = new CygnusReader();

    public static CygnusReader getInstance() {
        if(_ourInstance._status != "CONNECTION_OK" ) _ourInstance = new CygnusReader();
        return _ourInstance;
    }

    private CygnusReader() {
        try {
            _MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            _adapter = BluetoothAdapter.getDefaultAdapter();
            if (!_adapter.isEnabled()) {
                _status = "BLUETOOTH_DISABLED/BLUETOOTH_NOT_SUPPORTED";
                return;
            }

            _device = null;
            Set<BluetoothDevice> bondedDevices = _adapter.getBondedDevices();
            for (BluetoothDevice dev : bondedDevices) {
                if (dev.getName().toLowerCase().contains("cygnus")) {
                    _device = dev;
                    break;
                }
            }

            if (_device == null || _status.trim().length() <= 0) {
                _device = null;
                _status = "NO_CYGNUS_DEVICE_FOUND";
                return;
            }

            createSocket();
        }catch (Exception ex)
        {
            _status = "INITIALIZATION_ERROR : " + ex.getMessage();
        }
    }

    private void createSocket() throws IOException {
        try {
            _socket = _device.createRfcommSocketToServiceRecord(_MY_UUID);
            _socket.connect();
            _status = "CONNECTION_OK";
        }catch (Exception ex)
        {
            try{
                _socket =(BluetoothSocket) _device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(_device,1);
                _socket.connect();
            }catch (Exception innerEx) {
                _status = "CANNOT_COMMUNICATE_DEVICE";
            }
        }
    }
public void setStatus(String _newStatus) {
    _status = _newStatus;
}
    public String getStatus(){
        return _status;
    }
    public void resetSocket(){
        _status =  "UNKNOWN";
        if(_socket == null)
            try{
                createSocket();
            }catch(Exception ex){
                String message = ex.getMessage();
            }
        if(_socket != null){
            try{
                _socket.close();
            }catch(Exception ex){
                String message = ex.getMessage();
            }
        }
    }
    public CygnusData getData() {
        CygnusData _response = new CygnusData();
        try {
            if(!_socket.isConnected()) {createSocket();}
            InputStream _inputStream = _socket.getInputStream();

            BufferedReader _reader = new BufferedReader(new InputStreamReader(_inputStream));
            String line = _reader.readLine();

            //For Debugging purpose, check raw data
            _response.RawData = line;

            if(line !=null && line.length()>=15) {
                //STX 012.50,5920,08 ETX
                line = line.substring(1,line.length());
                String[] data_array = line.split(",");
                if(data_array.length>=3)
                {
                    int strength = Integer.parseInt(data_array[2]);
                    _response.DataStabilityNumber = strength;
                    if(strength <=5)
                        _response.DataStability = "NOT_STABLE";
                    if(strength > 5 && strength <=10)
                        _response.DataStability = "MEDIUM";
                    if(strength >10)
                        _response.DataStability = "STRONG";

                    _response.Status = "DATA_OK";
                    _response.ThicknessInMM = Double.parseDouble(data_array[0]);
                    if(_response.ThicknessInMM==0){
                        _response.Status="DATA_ERROR : CANNOT_GET_THICKNESS";
                    }
                }else
                {
                    _response.Status="DATA_INVALID";
                }
            }else {
                _response.Status = "DATA_NOT_AVAILABLE";
            }
        _socket.close();
        }catch (Exception ex)
        {
            _response.Status = "DATA_READ_ERROR : " + ex.getMessage();
        }
        return _response;
    }

    public CygnusData readData() {
        CygnusData cygdata = new CygnusData();

        if(getStatus()=="CONNECTION_OK")
        {
            for(int i= 0 ; i <= 1 ; i++) {
                cygdata = getData();
                if (cygdata.Status == "DATA_OK") break;
            }
        }else {
            cygdata.Status = _status;
        }
        return cygdata;
    }
}

