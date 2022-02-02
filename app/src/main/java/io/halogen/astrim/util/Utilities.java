package io.halogen.astrim.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import io.halogen.astrim.R;

public class Utilities {

    static{
        System.loadLibrary("native-lib");
        //Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    //Locale troubles?
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

    public boolean isNavBarPresent(Context context){
        boolean method1, method2, method3;
        Resources resources = context.getResources();
        //method 1(most weighted)
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        method1 = id > 0 && resources.getBoolean(id);
        //method 2(meh)
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        method2 = (!(hasBackKey && hasHomeKey));
        //method 3(least reliable)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        display.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        method3 = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        //aggregator
        return method1 ? (method2 || method3) : (method2 && method3);
    }

    public int getNavBarHeight(Context context){
        if(isNavBarPresent(context)){
            //The device has a navigation bar
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0){
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public boolean isOnline(Context context){
        ConnectivityManager conncheck = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conncheck.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showToastInfo(Context context, String text){
        Toast informer = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        informer.show();
    }

    public void showSnackbar(Context context, RelativeLayout layout, String text){
        final Snackbar notifier = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE);
        notifier.setAction("OK", new View.OnClickListener(){
            @Override public void onClick(View view){
                notifier.dismiss();
            }
        });
        notifier.setActionTextColor(ContextCompat.getColor(context, R.color.fauxBlueD));
        View snackbarView = notifier.getView();
        TextView snackbarText = snackbarView.findViewById(R.id.snackbar_text);
        snackbarText.setTextColor(ContextCompat.getColor(context, R.color.mildRed));
        notifier.show();
    }

    public String getCurrentTime(){
        return timeFormatter.format(new Date());
    }

    public void playTune(Context context, int tune){//1-incoming, 2-outgoing, 3-miscellaneous
        MediaPlayer mediaPlayer;
        switch(tune){
            case 1:
                mediaPlayer = MediaPlayer.create(context, R.raw.incoming);
                mediaPlayer.start();
                break;
            case 2:
                mediaPlayer = MediaPlayer.create(context, R.raw.outgoing);
                mediaPlayer.start();
                break;
            case 3:
                mediaPlayer = MediaPlayer.create(context, R.raw.miscellaneous);
                mediaPlayer.start();
                break;
            default:
                break;
        }
    }

    private String byte2hex(byte[] bytes){
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for(int i = 0; i < bytes.length; i++){
            int x = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[x >>> 4];
            hexChars[i * 2 + 1] = hexArray[x & 0x0F];
        }
        return new String(hexChars);
    }

    public String genKey() throws Exception{
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        return byte2hex(key.getEncoded());
    }

    public byte[] encryptText(String key, String cleartext) throws Exception{
        byte[] iv = new byte[16];
        byte[] data = cleartext.getBytes();
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(iv);
        GCMBlockCipher aesCipher = new GCMBlockCipher(new AESEngine());
        aesCipher.init(true, new ParametersWithIV(new KeyParameter(key.getBytes()), iv));
        byte[] buffer = new byte[aesCipher.getOutputSize(data.length)];
        int processed = aesCipher.processBytes(data, 0, data.length, buffer, 0);
        processed += aesCipher.doFinal(buffer, processed);
        byte[] buffer2 = new byte[processed + 16];
        System.arraycopy(iv, 0, buffer2, 0, 16);
        System.arraycopy(buffer, 0, buffer2, 16, processed);
        return buffer2;
    }

    public String decryptText(String key, byte[] ciphertext) throws Exception{
        byte[] iv = Arrays.copyOfRange(ciphertext, 0, 16);
        byte[] cipherBytes = Arrays.copyOfRange(ciphertext, 16, ciphertext.length);
        GCMBlockCipher aesCipher = new GCMBlockCipher(new AESEngine());
        aesCipher.init(false, new ParametersWithIV(new KeyParameter(key.getBytes()), iv));
        byte[] cleartext = new byte[aesCipher.getOutputSize(cipherBytes.length)];
        int processed = aesCipher.processBytes(cipherBytes, 0, cipherBytes.length, cleartext,0);
        aesCipher.doFinal(cleartext, processed);
        return new String(cleartext, StandardCharsets.UTF_8);
    }

    public String toJson(Map<String, String> map){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public Map fromJson(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Map.class);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
