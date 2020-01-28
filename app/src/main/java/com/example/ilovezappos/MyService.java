package com.example.ilovezappos;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyService extends JobService {
    public static final int[] flag = {0};
    BackGroundTask backGroundTask;
    session s;

    @Override
    public boolean onStartJob(final @NonNull com.firebase.jobdispatcher.JobParameters job) {
        backGroundTask=new BackGroundTask(){
            @Override
            protected void onPostExecute(Boolean aFloat) {
                super.onPostExecute(aFloat);
                if(aFloat && flag[0]==1) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "NOTIFY_PRICE_DROP")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Alert")
                            .setContentText("The price has dropped below your expectation!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                    flag[0]=0;
//                    Toast.makeText(MyService.this, "Done", Toast.LENGTH_SHORT).show();
                }
                jobFinished(job,false);
            }
        };
        s=new session(this);
        float threshold_price=s.getUserDetails();
        backGroundTask.execute(threshold_price);
        return false;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false;
    }

    public static class BackGroundTask extends AsyncTask<Float,Float,Boolean>{
        @Override
        protected Boolean doInBackground(final Float... floats){

            System.out.println(floats[0]);
            GetDataService service_order_book = GetPeriodicAlerts.getPeriodicAlerts().create(GetDataService.class);
            Call<JsonObject> call_Alert = service_order_book.getAlert();
            call_Alert.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    System.out.println(response.body().get("high"));
                    float high= (float) Float.parseFloat(response.body().get("high").toString().replace("\"",""));
                    if(high>floats[0]){
                        flag[0] =0;
                    }
                    else {
                        flag[0]=1;
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println(t);

                }});

            return true;
        }

        public boolean updateAlert(int i){
            if(i==1){
                return true;
            }
            else {
                return false;
            }
        }
    }
}
