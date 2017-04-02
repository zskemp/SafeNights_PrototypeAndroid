package apoorvazachmobileapps.safenights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import apoorvazachmobileapps.safenights.DrinkHistory.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

import static java.security.AccessController.getContext;

public class History extends AppCompatActivity {

    private LineChart mChart;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = (LineChart) findViewById(R.id.chart);
        populatechart();
        View v = this.findViewById(android.R.id.content);
        callHistoryAPI(v);
    }

    public void populatechart() {

        List<Entry> entries = new ArrayList<Entry>();

        Entry c2e1 = new Entry(0.05f, 130000f); // 0 == quarter 1
        entries.add(c2e1);
        Entry c1e1 = new Entry(0.1f, 100000f); // 0 == quarter 1
        entries.add(c1e1);
        Entry c1e2 = new Entry(0.9f, 140000f); // 1 == quarter 2 ...
        entries.add(c1e2);
        Entry c2e2 = new Entry(0.95f, 115000f); // 1 == quarter 2 ...
        entries.add(c2e2);

//        for (YourData data : dataObjects) {
//
//            // turn your data into Entry objects
//            entries.add(new Entry(data.getValueX(), data.getValueY()));
//        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setValueTextColor(R.color.colorPrimaryDark);
        mChart.setData(lineData);
        mChart.invalidate(); // refresh
    }

    public void callHistoryAPI (View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");

        Call<Example> call = apiService.gethistory(username, password);


        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Example table  = response.body();
                //OMG
                Log.i("Help:", table.toString());
                Alcoholtable trial = table.getAlcoholtable().get(0);
                Fields f = trial.getFields();
                String beer = f.getBeer();
                Toast.makeText(getApplicationContext(), "" + beer, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
            }
        });
    }

}
