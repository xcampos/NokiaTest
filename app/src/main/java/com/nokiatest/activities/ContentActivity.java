package com.nokiatest.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.nokiatest.R;
import com.nokiatest.controller.AppController;
import com.nokiatest.model.DownloadInfo;
import com.nokiatest.model.UeAppData;
import com.nokiatest.util.Constant;
import com.nokiatest.util.Util;
import com.nokiatest.util.XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private static final String TAG = "ContentActivity";
    private ListView lv_apps;
    private String acsURL, title;
    private static String NODE_CONTENT_FILE = "content_file";
    private static String NODE_EDGE_URL = "edge_url";
    private static String NODE_INTERNET_URL = "internet_url";
    private static String NODE_CLOUD_APP_NAME = "cloud_application_name";
    private static String NODE_APP_TYPE = "application_type";
    private static String NODE_ECA_IP = "edge_cloud_server_ip";
    private static String NODE_CDN_IP = "acs_cloud_server_ip";
    private ProgressDialog pDialog;
    private Util util;
    private String tag_string_req = "string_req";
    private List<UeAppData.ListContentFile> listContentFile;
    private ListContentAdapter adapter;
    private boolean showRationale = false;
    private Snackbar snackbar;
    private View mLayout;
    private UeAppData appData;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        lv_apps = (ListView) findViewById(R.id.lv_app_sync);
        mLayout = findViewById(R.id.rl_app_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        util = new Util();
        acsURL = getIntent().getStringExtra(Constant.ACS_URL);
        title = getIntent().getStringExtra(Constant.APP_NAME);
        ab = getSupportActionBar();
        getContentData();

    }

    private void getContentData() {
        pDialog = util.getProgressDialog(ContentActivity.this);
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                acsURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                parseXMLData(response.toString());
                util.hideProgressDialog(pDialog);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(ContentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                util.hideProgressDialog(pDialog);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void parseXMLData(String response) {


        XmlParser parser = new XmlParser();
        Document doc = parser.getDomElement(response);
        appData = new UeAppData();
        // Extract edge_cloud_server_ip
        appData.setIpECA(parser.getElementValue(doc.getElementsByTagName(NODE_ECA_IP).item(0)));
        // Extract acs_cloud_server_ip
        appData.setIpCDN(parser.getElementValue(doc.getElementsByTagName(NODE_ECA_IP).item(0)));
        //Extract nodeAppType
        appData.setApplicationType(parser.getElementValue(doc.getElementsByTagName(NODE_APP_TYPE).item(0)));

        //Extract content files
        NodeList nodeList = doc.getElementsByTagName(NODE_CONTENT_FILE);
        listContentFile = new ArrayList<>();
        String activeUrl = util.isString(appData.getIpECA()) ?  "http://"+appData.getIpECA()+"/"+title +"/" :  "http://"+appData.getIpCDN()+"/"+title +"/";
        ab.setTitle("ECA: " + (util.isString(appData.getIpECA()) ? "yes" : "no"));
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            UeAppData.ListContentFile contentFileItem = appData.new ListContentFile();
            contentFileItem.setEdgeUrl(activeUrl + parser.getValue(e, NODE_EDGE_URL));
            contentFileItem.setInternetUrl(activeUrl + parser.getValue(e, NODE_INTERNET_URL));
            listContentFile.add(contentFileItem);
        }

        if (listContentFile.size() > 0) {
            checkStoragePermission();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(ContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission has not been granted yet. Request it directly.
                Util.requestPermission(ContentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Util.REQUEST_STORAGE_PERMISSION);
                //isRequestingPermission=true;
            } else {
                adapter = new ListContentAdapter();
                lv_apps.setAdapter(adapter);
            }
        } else {
            adapter = new ListContentAdapter();
            lv_apps.setAdapter(adapter);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == Util.REQUEST_STORAGE_PERMISSION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for read sdcard state permission.
            Log.i(TAG, "Received res for sdcard permission req.");

            // Check if the only required permission has been granted
            if (Util.verifyPermissions(grantResults)) {
                // sdcard permission has been granted, preview can be displayed
                adapter = new ListContentAdapter();
                lv_apps.setAdapter(adapter);


            } else {

                showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale) {

                    Log.i(TAG, "sdcard permissions were NOT granted.");
                    snackbar = Snackbar.make(mLayout, R.string.permission_storage_are_needed,
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.app_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    snackbar.show();
                }

                Log.i(TAG, "sdcard permission was NOT granted.");
            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                //  startSync();

                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                finish();
                return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (adapter != null) {
            adapter.cancelDownloadAsyncTask();
        }

    }

    private DownloadInfo info;
    int lenghtOfFile = 0;
    float TTFB = 0, timeForDownload = 0;

    class ListContentAdapter extends BaseAdapter {

        private LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        private DownloadFileFromURL currentAsyncTask;

        @Override
        public int getCount() {
            return listContentFile.size();
        }

        @Override
        public Object getItem(int position) {
            return listContentFile.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_app_sync, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.rowSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        finalViewHolder.rowSync.setBackgroundResource(R.color.row_selected_grey);
                        startSync(finalViewHolder, position);

                }
            });

            viewHolder.tvFileName.setText(String.format(getResources().getString(R.string.txt_file_name), listContentFile.get(position).getInternetUrl()));

            return convertView;
        }

        private void cancelDownloadAsyncTask() {
            if (currentAsyncTask != null) {
                currentAsyncTask.cancel(true);
            }

        }

        private void startSync(ViewHolder viewHolder, int position) {
            if (util.isInternetConnected(ContentActivity.this)) {
                currentAsyncTask = new DownloadFileFromURL(viewHolder);
                currentAsyncTask.execute(listContentFile.get(position).getInternetUrl());
            } else {
                Toast.makeText(ContentActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }

        }

        class ViewHolder {

            TextView tvFileName, tvTimer, tvTTFB, tvMD5, tvProgress;
            ProgressBar progressBar;
            LinearLayout rowSync;

            public ViewHolder(View vi) {
                tvFileName = (TextView) vi.findViewById(R.id.tv_file_name);
                tvTimer = (TextView) vi.findViewById(R.id.tv_timer);
                tvTTFB = (TextView) vi.findViewById(R.id.tv_ttfb);
                tvMD5 = (TextView) vi.findViewById(R.id.tv_md5);
                rowSync = (LinearLayout) vi.findViewById(R.id.row_sync);
                // tvSync = (Button) vi.findViewById(R.id.tv_sync);
                tvProgress = (TextView) vi.findViewById(R.id.tv_progress);
                progressBar = (ProgressBar) vi.findViewById(R.id.progress_bar);
            }
        }

        class DownloadFileFromURL extends AsyncTask<String, String, Boolean> {
            ViewHolder vh;
            private File saveFolder;
            private File outputFile;

            public DownloadFileFromURL(ViewHolder holder) {
                this.vh = holder;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                vh.progressBar.setProgress(0);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                int count;
                Calendar before, afterDownload;
                if (util.isInternetConnected(ContentActivity.this)) {
                    try {
                        before = Calendar.getInstance();
                        Calendar afterTtfb = null;
                        URL url = new URL(params[0]);
                        URLConnection conection = url.openConnection();
                        conection.connect();
                        lenghtOfFile = conection.getContentLength();
                        afterTtfb = (lenghtOfFile > 0) ? Calendar.getInstance() : null;
                        if (afterTtfb != null) {
                            TTFB = afterTtfb.getTimeInMillis() - before.getTimeInMillis();
                            if (TTFB > 0) {
                                publishProgress(new String[]{"0", (TTFB / 1000) + ""});

                            }
                        }
                        saveFolder = new File(Environment.getExternalStorageDirectory(), "nokiatest");
                        System.out.print(saveFolder.getPath().toString());


                        if (!saveFolder.exists()) {
                            saveFolder.mkdir();

                        }

                        outputFile = new File(saveFolder, params[0].substring(params[0].lastIndexOf("/")));
                        InputStream input = new BufferedInputStream(url.openStream());

                        OutputStream output = new FileOutputStream(outputFile);

                        byte data[] = new byte[1024];

                        long total = 0;


                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress(new String[]{"" + (int) ((total * 100) / lenghtOfFile), count + "", total + "", lenghtOfFile + ""});
                            output.write(data, 0, count);
                        }

                        afterDownload = (lenghtOfFile > 0) ? Calendar.getInstance() : null;
                        if (afterDownload != null) {
                            timeForDownload = afterTtfb.getTimeInMillis() - before.getTimeInMillis();

                        }
                        output.flush();
                        output.close();
                        input.close();


                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                        return false;
                    }
                } else {
                    return false;
                }
                return true;
            }


            @Override
            protected void onProgressUpdate(String... progress) {

                Log.d("ANDRO_ASYNC", progress[0]);
                vh.progressBar.setProgress(Integer.parseInt(progress[0]));


                if (progress.length == 2) {
                    vh.tvTTFB.setText(progress[1]);
                } else {
                    String text = "Received: " + progress[1] + " bytes " + "(Downloaded: " + progress[2] + " bytes) Expected: " + progress[3] + " bytes.";
                    vh.tvProgress.setText(String.format(getResources().getString(R.string.txt_file_name), text));
                }
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                if (b) {
                    vh.tvTimer.setText(timeForDownload + "");
                    if(!appData.getApplicationType().equalsIgnoreCase("demo")) {
                        Intent i = new Intent(ContentActivity.this, ContentDetailsActivity.class);
                        startActivity(i);
                    }
                } else {
                    vh.tvFileName.setTextColor(Color.RED);
                }
            }
        }
    }
}
