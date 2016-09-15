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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
  
  final String LOG_TAG = "myLogs";
  final String abi = Build.CPU_ABI;
  String arch;

  
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//startservice();
    	setContentView(R.layout.main);
    	
    	arch = System.getProperty("os.arch");
    	Log.d(LOG_TAG, "abi:"+abi); 
    	Log.d(LOG_TAG, "arch:"+arch); 
    	
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
		      	    
		      	install_native("execroot",abi,"execroot");
		      	install_native("busybox-"+arch, "busybox_binaries","busybox");

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
		      		    dos.writeBytes("mount -o rw,remount /system\n");
		      		    dos.writeBytes(cmd);
		      		    dos.writeBytes("chmod 555 "+dest+"\n");
		      		    dos.writeBytes("chattr +i "+dest+"\n");  // in case of already installed busybox
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
    
void install_native(String file,String path,String dest_name){
	
	String system_bin="/system/bin/"+dest_name;
	String datadir = getFilesDir().toString();
	String datadir_file = datadir+"/"+dest_name;
	
              if (!(new File(datadir_file).exists())) {
           try {
               AssetManager assetManager = getAssets();
               InputStream in = assetManager.open(path+"/"+file);
               DataOutputStream outw = new DataOutputStream(new 
            		   FileOutputStream(new File(datadir, dest_name).getAbsolutePath()));
               
               byte[] buf = new byte[1024];
               int len;
               while ((len = in.read(buf)) > 0) {
                   outw.write(buf, 0, len);
               }
               in.close();
               outw.close();
               Log.d(LOG_TAG, file+" copied!");      
               
           } catch (Exception e) {
               Log.d(LOG_TAG,"Error writing "+file+"\n"+e.toString());
               return;
           }
       } 		
     
        try{
        	Process p=Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            String cmd = "cp "+datadir_file+" "+system_bin+"\n"; 
            Log.d(LOG_TAG, cmd);
            dos.writeBytes("mount -o rw,remount /proc /system\n"); // for Android <=4
            dos.writeBytes("mount -o rw,remount /system\n");  // for Android 5 and above
            dos.writeBytes(cmd);
            dos.writeBytes("chmod 6555 "+system_bin+"\n"); // suid works on <4.3
            dos.writeBytes("chattr +i "+system_bin+"\n");  // in case of existing busybox
            dos.writeBytes("busybox chattr +i "+system_bin+"\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();

        File finalfile = new File(system_bin);
        if(finalfile.exists()){
        Log.d(LOG_TAG, file+" installed successfully!");
        	}

        }
        catch (Exception e){Log.d(LOG_TAG, "error installing "+file);}
    }




}