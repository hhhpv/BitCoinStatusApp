package com.example.ilovezappos;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.firebase.jobdispatcher.*;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


public class MainActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "MainActivity";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private LineChart myChart;
    session s;
    private static final String Job_Tag="alert_job";
    private FirebaseJobDispatcher jobDispatcher;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<TableRow> myDataset=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temporary);
        final GetDataService service = GetRetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<GetDataFromBits>> call = service.getLineGraph();
        s=new session(this);
//        if(s.getUserDetails()>=0.0f){
//            Toast.makeText(MainActivity.this,Float.toString(s.getUserDetails()),Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(MainActivity.this,"No Alert Set",Toast.LENGTH_SHORT).show();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFY_PRICE_DROP";
            String description = "NOTIFY_PRICE_DROP";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NOTIFY_PRICE_DROP", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        stopJob();
        startJob();

        call.enqueue(new Callback<List<GetDataFromBits>>() {
            @Override
            public void onResponse(Call<List<GetDataFromBits>> call, Response<List<GetDataFromBits>> response) {
                setContentView(R.layout.activity_main);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(layoutManager);

                mySwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
                System.out.println(response.body().size()+" response");
                myChart = (LineChart) findViewById(R.id.linechart);
                myChart.setOnChartGestureListener(MainActivity.this);
                myChart.setOnChartValueSelectedListener(MainActivity.this);
                myChart.setDragEnabled(true);
                myChart.setScaleEnabled(true);
                ArrayList<Entry> yAxis = new ArrayList<>();
                float reference_date=Float.parseFloat(response.body().get(response.body().size()-1).getDate());
                for (GetDataFromBits i:response.body()) {
                    System.out.println((Float.parseFloat(i.getDate())-reference_date)+" "+Float.parseFloat(i.getPrice()));
                    yAxis.add((new Entry((Float.parseFloat(i.getDate())-reference_date), Float.parseFloat(i.getPrice()))));
                }
                LineDataSet set = new LineDataSet(yAxis, "BTCUSD");
                set.setCircleColor(Color.GREEN);
                set.setCircleHoleColor(Color.GREEN);
                set.setFillAlpha(110);
                set.setColor(Color.GREEN);
                set.setLineWidth(1f);
                set.setValueTextSize(15f);
                set.setValueTextColor(Color.GRAY);
                set.setDrawValues(false);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set);
                LineData data = new LineData(dataSets);
                myChart.getXAxis().setEnabled(false);
                myChart.getAxisLeft().setEnabled(false);
                myChart.getAxisLeft().setDrawAxisLine(false);
                myChart.getAxisRight().setDrawAxisLine(false);
                myChart.getAxisRight().setEnabled(false);
                myChart.setData(data);
                myChart.getDescription().setText("Tranaction trend in the last hour");
                myChart.getDescription().setTextColor(Color.WHITE);
                myChart.setTouchEnabled(true);
                myChart.setPinchZoom(true);
                myChart.getLegend().setTextColor(Color.WHITE);
                myChart.animateX(3000);
                mySwipeRefreshLayout.setOnRefreshListener(MainActivity.this);
                GetDataService service_order_book = GetOrderBook.getOrderBook().create(GetDataService.class);
                Call<JsonObject> call_order_book = service_order_book.getOrderBook();
                call_order_book.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        System.out.println(response.body().get("bids"));
                        JsonArray bidArray=new JsonArray();
                        bidArray.addAll((JsonArray) response.body().get("bids"));
                        for(int i=0;i<bidArray.size();i++){
                            JsonArray bid=new JsonArray();
                            bid= (JsonArray) bidArray.get(i);
                            System.out.println(bid.get(0));
                            System.out.println(bid.get(0).toString());
                            TableRow temp=new TableRow(bid.get(0).toString().replace("\"",""),bid.get(1).toString().replace("\"",""));
                            myDataset.add(temp);
                        }
                        mAdapter = new MyAdapter(myDataset);
                        recyclerView.setAdapter(mAdapter);
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println(t);
                        Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }});
            }
            @Override
            public void onFailure(Call<List<GetDataFromBits>> call, Throwable t) {
                System.out.println(t);
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        System.out.println("wait");
    }
    @Override
    public void onRefresh(){
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        final GetDataService service = GetRetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<GetDataFromBits>> call = service.getLineGraph();
        call.enqueue(new Callback<List<GetDataFromBits>>() {
            @Override
            public void onResponse(Call<List<GetDataFromBits>> call, Response<List<GetDataFromBits>> response) {
                ArrayList<Entry> yAxis = new ArrayList<>();
                float reference_date=Float.parseFloat(response.body().get(response.body().size()-1).getDate());
                for (GetDataFromBits i:response.body()) {
                    System.out.println((Float.parseFloat(i.getDate())-reference_date)+" "+Float.parseFloat(i.getPrice()));
                    yAxis.add((new Entry((Float.parseFloat(i.getDate())-reference_date), Float.parseFloat(i.getPrice()))));
                }
                LineDataSet set = new LineDataSet(yAxis, "BTCUSD");
                set.setFillAlpha(110);
                set.setColor(Color.GREEN);
                set.setCircleColor(Color.GREEN);
                set.setCircleHoleColor(Color.GREEN);
                set.setLineWidth(1f);
                set.setValueTextSize(15f);
                set.setValueTextColor(Color.GRAY);
                myChart = (LineChart) findViewById(R.id.linechart);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set);
                LineData data = new LineData(dataSets);
                set.setFillAlpha(110);
                set.setColor(Color.GREEN);
                set.setLineWidth(1f);
                set.setValueTextSize(15f);
                set.setValueTextColor(Color.GRAY);
                set.setDrawValues(false);
                set.setCircleRadius(0.0f);
                myChart.clear();
                myChart.setData(data);
                myChart.getDescription().setTextColor(Color.WHITE);
                myChart.getLegend().setTextColor(Color.WHITE);
                myChart.animateX(3000);
                mySwipeRefreshLayout.setRefreshing(false);
                GetDataService service_order_book = GetOrderBook.getOrderBook().create(GetDataService.class);
                Call<JsonObject> call_order_book = service_order_book.getOrderBook();
                call_order_book.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        System.out.println(response.body().get("bids"));
                        myDataset.clear();
                        JsonArray bidArray=new JsonArray();
                        bidArray.addAll((JsonArray) response.body().get("bids"));
                        for(int i=0;i<bidArray.size();i++){
                            JsonArray bid=new JsonArray();
                            bid= (JsonArray) bidArray.get(i);
                            System.out.println(bid.get(0));
                            System.out.println(bid.get(0).toString());
                            TableRow temp=new TableRow(bid.get(0).toString().replace("\"",""),bid.get(1).toString().replace("\"",""));
                            myDataset.add(temp);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println(t);
                        Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }});
            }
            @Override
            public void onFailure(Call<List<GetDataFromBits>> call, Throwable t) {
                System.out.println(t);
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");
                onRefresh();
                return true;
            case R.id.enter_price:
                Intent in=new Intent(MainActivity.this,EnterPrice.class);
                startActivity(in);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public void startJob(){
        Job job=jobDispatcher.newJobBuilder()
                .setService(MyService.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag(Job_Tag)
                .setTrigger(Trigger.executionWindow(10,15))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        jobDispatcher.mustSchedule(job);
//        Toast.makeText(this,"Job Scheduled",Toast.LENGTH_SHORT).show();
    }
    public void stopJob(){
        jobDispatcher.cancel(Job_Tag);
//        Toast.makeText(this,"Job Cancelled",Toast.LENGTH_SHORT).show();
    }
}

