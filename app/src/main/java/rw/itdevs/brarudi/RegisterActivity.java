package rw.itdevs.brarudi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends BaseActivity {

    EditText names,name2,dob,address,phone;
    Button send;
    RadioGroup radioGroup;
    ProgressDialog progress;
    CheckBox box;
    TextView link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }

            });
        }

        setTitle(R.string.inscription);
        names=findViewById(R.id.name);
        name2=findViewById(R.id.name2);
        dob=findViewById(R.id.dob);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phone);
        radioGroup=findViewById(R.id.radio);

        send=findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               validate();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        box=findViewById(R.id.box);
        link=findViewById(R.id.link);

        link.setText(Html.fromHtml(getString(R.string.terms)));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        //done();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_settings){
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }
        return true;
    }

    void validate(){

        if(names.getText().toString().equals("")){
            Toast.makeText(this, "Entrez le nom", Toast.LENGTH_SHORT).show();
        } if(name2.getText().toString().equals("")){
            Toast.makeText(this, "Entrez le prenom", Toast.LENGTH_SHORT).show();
        }else if(dob.getText().toString().equals("")){
            Toast.makeText(this, "Entrer la date de Naissance", Toast.LENGTH_SHORT).show();

        }else if(address.getText().toString().equals("")){
            Toast.makeText(this, "Entrer l'addresse", Toast.LENGTH_SHORT).show();

        }else if(phone.getText().toString().equals("")){
            Toast.makeText(this, "Entrer Numéro de Tél.", Toast.LENGTH_SHORT).show();

        }else if(!box.isChecked()){
            Toast.makeText(this, "Agree to the terms and conditions", Toast.LENGTH_SHORT).show();

        }else{

            String sex;
            if(radioGroup.getCheckedRadioButtonId()==R.id.radio1){
                sex="Male";
            }else{
                sex="Female";
            }
            register(sex);
        }
    }

    public void register(String sex) {

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("user_firstname", names.getText().toString())
                .appendQueryParameter("user_lastname", name2.getText().toString())
                .appendQueryParameter("gender", sex)
                .appendQueryParameter("user_date", dob.getText().toString())
                .appendQueryParameter("user_address", address.getText().toString())
                .appendQueryParameter("user_phone", phone.getText().toString());

        String query = builder.build().getEncodedQuery();
        register reg = new register();
       reg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
    }

    public void pickDate() {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                dob.setText(sdf.format(myCalendar.getTime()));
                // dat = sdf.format(myCalendar.getTime());

            }
        };


        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public class register extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {

            progress=ProgressDialog.show(RegisterActivity.this,"",getString(R.string.wait));
            // TODO: Implement this method
            super.onPreExecute();
        }


        protected String doInBackground(String... params) {

            String address = "http://primusicbi.com/api/register.php";

            try {

                url = new URL(address);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                String query = params[0];

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            progress.dismiss();
           if (!result.equals("exception")) {
                try {
                    JSONObject object=new JSONObject(result);
                    String status=object.getString("status");
                    if(status.equals("true")){
                        done();
                    }else{
                        Toast.makeText(RegisterActivity.this, "il y a an exception", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
               Toast.makeText(RegisterActivity.this, "Problemme de resoux", Toast.LENGTH_SHORT).show();
           }
        }
    }

    public void done(){

        final Dialog dialog=new Dialog(RegisterActivity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.done,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image=dialog.findViewById(R.id.image);

        image.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.ic_done_black_24dp,getTheme()));

        Button done=dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });

        dialog.show();
    }

}


