package com.example.myapplication.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.myapplication.dialogs.ProgressDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PicassoBitmab {

    public static void gettingUri(Activity activity,String memoryName, Context context, List<String> urlList) {
        progressDialog = new ProgressDialog(activity, false);
        progressDialog.show("getting Photo path");
        gettingPicassoBitMab(activity,memoryName, context, urlList);

    }

    public static void deleteAllOldFiles(String memortName) {
        File mydir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SharedMemory" + "/"
                + memortName);
        if (mydir.exists()) {
            for (File file : mydir.listFiles()) {
                if (file.exists()) {
                    boolean delete = file.delete();
                    Log.d("TAGDelete", "deleteAllOldFiles: " + delete);
                }
            }
        }


    }

    static ProgressDialog progressDialog;

    private static ArrayList<Uri> gettingPicassoBitMab(Activity activity, String memoryName, Context context, List<String> urlList) {
        progressDialog.show();
        ArrayList<Uri> imagesUri = new ArrayList<>();
        for (String url : urlList) {
            String fileName = FileExtensionAndName.get(context).getFileName(url);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {
                        File mydir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SharedMemory" + File.separator + memoryName);
                        if (!mydir.exists()) {
                            mydir.mkdirs();
                        }

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String fileUri = mydir.getAbsolutePath() + File.separator + fileName;
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(fileUri);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Log.d("TAG21", "run: "+e.getMessage());
                                }
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                                try {
                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("TAG21", "run: "+e.getMessage());
                                }
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("TAG21", "run: "+e.getMessage());

                                }
                                Uri parse = Uri.parse(fileUri);
                                imagesUri.add(parse);
                                Log.d("TAG21", "run: "+imagesUri.size()+"  "+urlList.size());
                                if (imagesUri.size()==urlList.size())
                                {

                                    progressDialog.dismiss();
                                    startIntentShare(imagesUri,activity,memoryName);
                                }
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        Log.d("TAG51", "onBitmapLoaded: " + e.getMessage());
                        Log.d("TAG21", "run: "+e.getMessage());
                    }


                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(url).into(target);

        }
        return imagesUri;
    }

    private static void startIntentShare(ArrayList<Uri> imagesUri, Activity activity, String memoryName) {
        Intent sharerIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharerIntent.setType("image/*");
        sharerIntent.putExtra(Intent.EXTRA_STREAM, imagesUri);
        sharerIntent.putExtra(Intent.EXTRA_TEXT, "Memory: " + memoryName);
        activity.startActivity(sharerIntent);



    }

}

