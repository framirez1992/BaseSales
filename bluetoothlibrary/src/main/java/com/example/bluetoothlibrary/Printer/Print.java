package com.example.bluetoothlibrary.Printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.bluetoothlibrary.Utils.Funciones;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Print {
    BluetoothAdapter myBluetoothAdapter;
    Context context;
    PULGADAS pulgadas;
    ArrayList<byte[]> data =new ArrayList<>();
    int paperLenght=32;//2 pulgadas

    public enum PULGADAS{
        PULGADAS_2,
        PULGADAS_3

    }
    public enum TEXT_ALIGN{
        LEFT,RIGHT,CENTER,
        NO_ALIGN
    }
    public enum PRINTER_ALIGN{
        ALIGN_CENTER,
        ALIGN_RIGHT,
        ALIGN_LEFT
    }
    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    public Print(Context context, PULGADAS pulgadas){
        this.context = context;
        this.pulgadas = pulgadas;
        if(pulgadas == PULGADAS.PULGADAS_2){
          paperLenght=32;
        }
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public  void printText(String mac) throws Exception{
        BluetoothSocket socket;
        try {


            if(myBluetoothAdapter == null) {
                throw new Exception("Bluetooth no disponible");
            }

            BluetoothDevice myParingDevice = myBluetoothAdapter.getRemoteDevice(mac);
            //myParingDevice.setPairingConfirmation(true);
            //esto es para conexiones standar a bluetooh
            Method m = myParingDevice.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) m.invoke(myParingDevice, 1);
            socket.connect();
            Thread.sleep(500);
            if (socket.isConnected()) {

                   // socket.getOutputStream().write(Funciones.decodeBitmap(logo));

               // socket.getOutputStream().write(text.replace("ñ", "n").replace("Ñ", "n").getBytes());
                for(byte[] b: data){
                    socket.getOutputStream().write(b);
                }
                Thread.sleep(500);
                socket.close();
            }
            // el socket debe cerrarse y nulificarse siempre que se acabe de usar ya que si no se hace simpre estara caducado
            // y sera inaccesible.
        }catch(Exception e){
            throw e;

        }
    }

    public void drawText(String text){
        addText(text);
    }
    public void drawText(String txt, TEXT_ALIGN align){
        String result = null;
        if(align == TEXT_ALIGN.RIGHT){
           result=Funciones.reservarCaracteresAlinearDerecha(txt,paperLenght);
        }else if(align == TEXT_ALIGN.CENTER){
           result= Funciones.centrarTexto(txt,paperLenght);
        }else if(align == TEXT_ALIGN.LEFT){
           result=Funciones.reservarCaracteres(txt,paperLenght);
        }
       addText(result);
    }

    public void drawLine(){
        if(pulgadas == PULGADAS.PULGADAS_2){
            addText("--------------------------------");
        }

    }

    public void addText(String text){
        data.add(Funciones.encodeNonAscii(text).getBytes());
        addNewLine();
    }

    public void addAlign(PRINTER_ALIGN align){
        data.add(getAlignBytes(align));
    }

    public void addImage(int resource){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resource);
        Bitmap b = Bitmap.createScaledBitmap(bmp, 250, 250, false);
        data.add(Funciones.decodeBitmap(b));
        addNewLine();
    }

    private byte[] getAlignBytes(PRINTER_ALIGN pa) {
        byte[] d;
        switch (pa) {
            case ALIGN_CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case ALIGN_LEFT:
                d = ESC_ALIGN_LEFT;
                break;
            case ALIGN_RIGHT:
                d = ESC_ALIGN_RIGHT;
                break;
            default:
                d = ESC_ALIGN_LEFT;
                break;
        }

        return d;
    }

    public void addNewLine() {
        data.add(NEW_LINE);
    }
    public void setLineSpacing(int lineSpacing) {
        byte[] cmd = new byte[]{0x1B, 0x33, (byte) lineSpacing};
        data.add(cmd);
    }

    public void setBold(boolean bold) {
        byte[] cmd = new byte[]{0x1B, 0x45, bold ? (byte) 1 : 0};
        data.add(cmd);
    }
}
