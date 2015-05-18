package us.dexon.dexonbpm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by androide on 18/05/15.
 */
public class NetUtils {

    public static boolean isConnectedToNetwork(Context context){

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null /*&& networkInfo.isConnectedOrConnecting()*/
                && networkInfo.isConnected() && networkInfo.isAvailable()){
            connected = true;
        }else{
            connected = false;
        }

        return connected;
    }

    public static boolean isServerAvailable(String protocol, String host, int port, String file, int timeout ){

        boolean available = true;
        URL uri = null;

        try {
            uri = new URL(protocol, host, port, file);
            available = InetAddress.getByName(uri.getHost()).isReachable(timeout);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (UnknownHostException exe){
            exe.printStackTrace();
            available = false;
        }catch (IOException exe){
            exe.printStackTrace();
            available = false;
        }
        return available;
    }

}
