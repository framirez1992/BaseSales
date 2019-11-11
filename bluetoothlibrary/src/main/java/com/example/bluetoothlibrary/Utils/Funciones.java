package com.example.bluetoothlibrary.Utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Funciones {

    public static String reservarCaracteres(String text, int caracteres){
        return  reservarCaracteres(text, caracteres, false);
    }

    public static String reservarCaracteresAlinearDerecha(String text, int caracteres){
        return  reservarCaracteres(text, caracteres, true);
    }
    public static String reservarCaracteres(String text, int caracteres, boolean alinearDerecha){
        String format = "%"+(alinearDerecha?"":"-")+caracteres+"s";
        String data = String.format(format, text);
        return data;
    }

    public static String centrarTexto(String texto, int longitud){
        int lRestante = longitud - texto.length();
        if(lRestante >0){
            int espacio = texto.length()+lRestante/2;
            String data = reservarCaracteres(texto,espacio);
            espacio = data.length()+lRestante/2;
            data = reservarCaracteresAlinearDerecha(data,espacio);
            return data;
        }

        return texto;


    }


    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>();
        StringBuffer sb;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) zeroStr = zeroStr + "0";
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");
            }
            if (zeroCount > 0) sb.append(zeroStr);
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }


    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = strToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }



    public static String strToHexString(String binaryStr) {
         String hexStr = "0123456789ABCDEF";
        String[] binaryArray = {"0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111"};
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        //byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
        //commandList.add(ESC_ALIGN_CENTER);
        for (String hexStr : list) commandList.add(hexStringToBytes(hexStr));
        return sysCopy(commandList);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) return null;
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String encodeNonAscii(String text) {
        String result = text.replace('á', 'a')
                .replace('č', 'c')
                .replace('ď', 'd')
                .replace('é', 'e')
                .replace('ě', 'e')
                .replace('í', 'i')
                .replace('ň', 'n')
                .replace('ó', 'o')
                .replace('ř', 'r')
                .replace('š', 's')
                .replace('ť', 't')
                .replace('ú', 'u')
                .replace('ů', 'u')
                .replace('ý', 'y')
                .replace('ž', 'z')
                .replace('Á', 'A')
                .replace('Č', 'C')
                .replace('Ď', 'D')
                .replace('É', 'E')
                .replace('Ě', 'E')
                .replace('Í', 'I')
                .replace('Ň', 'N')
                .replace('Ó', 'O')
                .replace('Ř', 'R')
                .replace('Š', 'S')
                .replace('Ť', 'T')
                .replace('Ú', 'U')
                .replace('Ů', 'U')
                .replace('Ý', 'Y')
                .replace('Ž', 'Z');
        return result;
    }

}
