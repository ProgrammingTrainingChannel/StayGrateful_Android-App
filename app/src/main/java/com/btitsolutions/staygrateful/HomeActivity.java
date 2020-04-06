package com.btitsolutions.staygrateful;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.btitsolutions.staygrateful.Adapters.GratitudeAdapter;
import com.btitsolutions.staygrateful.Models.GratitudeModel;
import com.btitsolutions.staygrateful.Utilities.DBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    ListView lstViewGreetings;

    List<GratitudeModel> gratitudeModels;
    int SelectedDateRange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, "ca-app-pub-8168171128315421~2382206408");
        AdView mAdView;
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenAddNewDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DBHelper dbHelper = new DBHelper(context);
        gratitudeModels = dbHelper.getAllGratitudes();

        lstViewGreetings = (ListView)findViewById(R.id.lstViewGreetings);
        lstViewGreetings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Snackbar.make(view, "Item long click detected.", Snackbar.LENGTH_SHORT)
                  //      .setAction("Action", null).show();

                return false;
            }
        });

        OpenFilterGratitudesListDialog();
    }
//
//    private void LoadGratitudes(){
//        DBHelper dbHelper = new DBHelper(context);
//        gratitudeModels = dbHelper.getAllGratitudes();
//        GratitudeAdapter gratitudeAdapter = new GratitudeAdapter(this, gratitudeModels);
//        lstViewGreetings = (ListView)findViewById(R.id.lstViewGreetings);
//        lstViewGreetings.setAdapter(gratitudeAdapter);
//    }

    public void OpenAddNewDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.add_new_dialog);
        dialog.setTitle(R.string.add_new_dialog_title);
        dialog.setCancelable(false);
        dialog.show();

        final EditText txtContent = (EditText) dialog.findViewById(R.id.txtContent);
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);

        final Activity appContext = this;
        Button btnDialogSave = (Button) dialog.findViewById(R.id.btnDialogSave);
        btnDialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtContent.getText().toString().equals(""))
                {
                    Snackbar.make(view, "Say something first.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else
                {
                    try{
                        DBHelper dbHelper = new DBHelper(context);
                        GratitudeModel gratitudeModel = new GratitudeModel();
                        gratitudeModel.setCode(String.valueOf(dbHelper.getGratitudesCount() + 1));

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        gratitudeModel.setCreated_date(formattedDate);
                        gratitudeModel.setContent(txtContent.getText().toString());

                        dbHelper.addGratitude(gratitudeModel);

                        txtContent.setText("");
                        RefreshList(appContext);

                        Snackbar.make(view, "Saved Successfully.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                    catch(Exception ex){
                        Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void OpenSetReminderDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.set_reminder_dialog);
        dialog.setTitle(R.string.set_reminder_dialog_title);
        dialog.setCancelable(false);
        dialog.show();

        Button btnDialogSet = (Button) dialog.findViewById(R.id.btnDialogSet);
        btnDialogSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox chkTiming = (CheckBox) dialog.findViewById(R.id.chkTiming);
                SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = SystemSettings.edit();
                editor.putBoolean("ReminderSetForTomorrow", chkTiming.isChecked());
                editor.apply();
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void OpenFilterGratitudesListDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.filter_gratitude_list_dialog);
        dialog.setTitle(R.string.filter_gratitude_list_dialog_title);
        dialog.setCancelable(false);
        dialog.show();

        Spinner spinnerDateRange = (Spinner) dialog.findViewById(R.id.spinnerDateRange);
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.FilterOptions_all));
        list.add(getString(R.string.FilterOptions_today));
        list.add(getString(R.string.FilterOptions_yesterday));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateRange.setAdapter(dataAdapter);

        final List<String> frozenList = list;
        spinnerDateRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedDateRange = i;

                SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = SystemSettings.edit();
                editor.putInt("SelectedDateRange", SelectedDateRange);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Activity appContext = this;
        Button btnDialogFilter = (Button) dialog.findViewById(R.id.btnDialogFilter);
        btnDialogFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((gratitudeModels != null) && (gratitudeModels.size() > 0)){
                    RefreshList(appContext);
                }

                dialog.dismiss();
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void RefreshList(Activity appContext){
        String formattedDate = "";
        List<GratitudeModel> gratitudeModelList = new ArrayList<GratitudeModel>();
        SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
        SelectedDateRange = SystemSettings.getInt("SelectedDateRange", 0);

        DBHelper dbHelper = new DBHelper(context);
        gratitudeModels = dbHelper.getAllGratitudes();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());

        if(SelectedDateRange == 1){
            for (int i=0; i < gratitudeModels.size(); i++){
                if(gratitudeModels.get(i).getCreated_date().equals(formattedDate)){
                    gratitudeModelList.add(gratitudeModels.get(i));
                }
            }
        }
        else if(SelectedDateRange == 2){
            int todayDay = Integer.parseInt(formattedDate.substring(0, 2));
            String yesterdayDay = String.valueOf((Integer.parseInt(formattedDate.substring(0, 2))-1));

            formattedDate = formattedDate.replaceFirst(String.valueOf(todayDay), yesterdayDay);

            for (int i=0; i < gratitudeModels.size(); i++){
                if(gratitudeModels.get(i).getCreated_date().equals(formattedDate)){
                    gratitudeModelList.add(gratitudeModels.get(i));
                }
            }
        }
        else if(SelectedDateRange == 3){
            int todayDay = Integer.parseInt(formattedDate.substring(0, 2));
            String yesterdayDay = String.valueOf((Integer.parseInt(formattedDate.substring(0, 2))-2));

            formattedDate = formattedDate.replaceFirst(String.valueOf(todayDay), yesterdayDay);

            for (int i=0; i < gratitudeModels.size(); i++){
                if(gratitudeModels.get(i).getCreated_date().equals(formattedDate)){
                    gratitudeModelList.add(gratitudeModels.get(i));
                }
            }
        }
        else if(SelectedDateRange == 4){
            int todayDay = Integer.parseInt(formattedDate.substring(0, 2));
            String yesterdayDay = String.valueOf((Integer.parseInt(formattedDate.substring(0, 2))-3));

            formattedDate = formattedDate.replaceFirst(String.valueOf(todayDay), yesterdayDay);

            for (int i=0; i < gratitudeModels.size(); i++){
                if(gratitudeModels.get(i).getCreated_date().equals(formattedDate)){
                    gratitudeModelList.add(gratitudeModels.get(i));
                }
            }
        }
        else if(SelectedDateRange == 5){
            int todayDay = Integer.parseInt(formattedDate.substring(0, 2));
            String yesterdayDay = String.valueOf((Integer.parseInt(formattedDate.substring(0, 2))-4));

            formattedDate = formattedDate.replaceFirst(String.valueOf(todayDay), yesterdayDay);

            for (int i=0; i < gratitudeModels.size(); i++){
                if(gratitudeModels.get(i).getCreated_date().equals(formattedDate)){
                    gratitudeModelList.add(gratitudeModels.get(i));
                }
            }
        }
        else{
            gratitudeModelList = gratitudeModels;
        }

        GratitudeAdapter gratitudeAdapter = new GratitudeAdapter(appContext, gratitudeModelList);
        lstViewGreetings = (ListView)findViewById(R.id.lstViewGreetings);
        lstViewGreetings.setAdapter(gratitudeAdapter);
    }

    public void OpenSettingDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.setting_dialog);
        dialog.setTitle(R.string.setting_dialog_title);
        dialog.setCancelable(false);
        dialog.show();

        final RadioButton rdbtnAmharic = (RadioButton)dialog.findViewById(R.id.rdbtnAmharic);
        final RadioButton rdbtnEnglish = (RadioButton)dialog.findViewById(R.id.rdbtnEnglish);
        final RadioButton rdbtnOromiffa = (RadioButton)dialog.findViewById(R.id.rdbtnOromiffa);
        final RadioButton rdbtnTigrigna = (RadioButton)dialog.findViewById(R.id.rdbtnTigrigna);

        SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
        String LanguageSetting = SystemSettings.getString("LanguageSetting", "en-US");

        if(LanguageSetting.equals("am")){
            rdbtnAmharic.setChecked(true);
            rdbtnEnglish.setChecked(false);
            rdbtnOromiffa.setChecked(false);
            rdbtnTigrigna.setChecked(false);
        }
        else if(LanguageSetting.equals("en-US")){
            rdbtnAmharic.setChecked(false);
            rdbtnEnglish.setChecked(true);
            rdbtnOromiffa.setChecked(false);
            rdbtnTigrigna.setChecked(false);
        }
        else if(LanguageSetting.equals("or")){
            rdbtnAmharic.setChecked(false);
            rdbtnEnglish.setChecked(false);
            rdbtnOromiffa.setChecked(true);
            rdbtnTigrigna.setChecked(false);
        }
        else if(LanguageSetting.equals("tg")){
            rdbtnAmharic.setChecked(false);
            rdbtnEnglish.setChecked(false);
            rdbtnOromiffa.setChecked(false);
            rdbtnTigrigna.setChecked(true);
        }

        Button btnDialogSave = (Button) dialog.findViewById(R.id.btnDialogSave);
        btnDialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String language = "en-US";
                if(rdbtnEnglish.isChecked()){
                    language = "en-US";

                    Resources res = getApplicationContext().getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
                    res.updateConfiguration(conf, dm);
                }
                else if(rdbtnAmharic.isChecked()){
                    language = "am";

                    Resources res = getApplicationContext().getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
                    res.updateConfiguration(conf, dm);
                }
                else if(rdbtnOromiffa.isChecked()){
                    language = "or";

                    Resources res = getApplicationContext().getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
                    res.updateConfiguration(conf, dm);
                }
                else if(rdbtnTigrigna.isChecked()){
                    language = "tg";

                    Resources res = getApplicationContext().getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
                    res.updateConfiguration(conf, dm);
                }

                SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = SystemSettings.edit();
                editor.putString("LanguageSetting", language);
                editor.apply();

                Snackbar.make(view, "Setting Saved.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                finish();
                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            }
        });

        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void OpenAboutDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.about_dialog);
        dialog.setTitle(R.string.about_lblheader);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Zilz8psIcj0"));
            startActivity(youtubeIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_new) {
            OpenAddNewDialog();
        }
        else if (id == R.id.nav_view) {
            OpenFilterGratitudesListDialog();
        }
        else if (id == R.id.nav_set_reminder) {
            //OpenSetReminderDialog();
        }
        else if (id == R.id.nav_setting) {
            OpenSettingDialog();
        }
        else if (id == R.id.nav_set_help) {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Zilz8psIcj0"));
            startActivity(youtubeIntent);
        }
        else if (id == R.id.nav_set_about) {
            OpenAboutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
