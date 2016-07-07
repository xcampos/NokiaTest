package com.nokiatest.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.nokiatest.R;
import com.nokiatest.adapter.ListConfigAdapter;
import com.nokiatest.controller.AppController;
import com.nokiatest.model.ListConfig;
import com.nokiatest.util.Constant;
import com.nokiatest.util.Util;
import com.nokiatest.util.XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ActionBar actionBar;
    private ListView lvApps;
    private TextView tvNumberOfApps, tvURLConfig;
    private String tag_string_req = "string_req";
    private ProgressDialog pDialog;
    private static String NODE_ACS = "acs";
    private static String NODE_ACS_NAME = "acs_name";
    private static String NODE_ACS_ID = "acsid";
    private static String NODE_ACS_URL = "acs_url";
    private static String NODE_ACS_SYNC_FREQUENCY = "acs_sync_frecuency";
    private static String NODE_ECA_CM = "eca-cm";
    private List<ListConfig> listConfig;
    private ListConfigAdapter adapter;
    private Util util;
    private LinearLayout llSettings;
    private EditText etUrl;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_main_activity));
        lvApps = (ListView) findViewById(R.id.lv_app_names);
        tvNumberOfApps = (TextView) findViewById(R.id.tv_number_of_apps);
        tvURLConfig = (TextView) findViewById(R.id.tv_url_config);

        llSettings = (LinearLayout) findViewById(R.id.layout_settings);
        etUrl = (EditText) findViewById(R.id.et_url);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        util = new Util();
        makeStringReq("");

    }


    /**
     * Making json object request
     */
    private void makeStringReq(String url) {
        if (!util.isString(url)) {
            url = Constant.CONFIG_URL;
        }
        pDialog = util.getProgressDialog(MainActivity.this);
        Log.e(TAG, "makeStringReq: " + url);
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
        StringRequest strReq = new StringRequest(Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                xmlDataParsing(response.toString());
                util.hideProgressDialog(pDialog);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                tvNumberOfApps.setText(String.format(getResources().getString(R.string.txt_number_of_apps), "0"));
                util.hideProgressDialog(pDialog);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void xmlDataParsing(String response) {
        listConfig = new ArrayList<>();

        XmlParser parser = new XmlParser();
        Document doc = parser.getDomElement(response);
        NodeList nodeList = doc.getElementsByTagName(NODE_ACS);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            ListConfig configItem = new ListConfig();
            configItem.setAcsId(e.getAttribute(NODE_ACS_ID));
            configItem.setAcsName(e.getAttribute(NODE_ACS_NAME));
            configItem.setAcsUrl(parser.getValue(e, NODE_ACS_URL));
            configItem.setAcsSyncFrecuency(Integer.parseInt(parser.getValue(e, NODE_ACS_SYNC_FREQUENCY)));
            listConfig.add(configItem);
        }

        if (listConfig.size() > 0) {
            adapter = new ListConfigAdapter(MainActivity.this, listConfig);
            lvApps.setAdapter(adapter);
            lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(MainActivity.this, ContentActivity.class);
                    Bundle b = new Bundle();
                    b.putString(Constant.ACS_URL, listConfig.get(position).getAcsUrl());
                    b.putString(Constant.APP_NAME, listConfig.get(position).getAcsName());
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }

        tvNumberOfApps.setText(String.format(getResources().getString(R.string.txt_number_of_apps), listConfig.size()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.menu_reset:
                makeStringReq("");
                return true;*/
            case R.id.menu_settings:
                openSettings();
                return true;
            default:
                finish();
                return true;
        }

    }

    private void openSettings() {
        if ((llSettings.getVisibility() == View.VISIBLE)) {
            llSettings.setVisibility(View.GONE);
        } else {
            llSettings.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            if (util.isString(etUrl.getText().toString())) {
                String url = etUrl.getText().toString().contains("/config.xml") ? etUrl.getText().toString() : etUrl.getText().toString() + "/config.xml";
                makeStringReq(url.replace("//config", "/config"));
                llSettings.setVisibility(View.GONE);
                hideKeyboard();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getApplicationWindowToken(), 0);
            }

        } catch (Exception e) {

        }
    }
}
