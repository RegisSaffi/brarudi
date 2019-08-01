package rw.itdevs.brarudi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rw.itdevs.brarudi.adapters.calendarAdapter;
import rw.itdevs.brarudi.model.cal;

public class CalendarActivity extends BaseActivity {

    RecyclerView recyclerView;
    rw.itdevs.brarudi.model.cal cal;
    List<rw.itdevs.brarudi.model.cal> calList;
    rw.itdevs.brarudi.adapters.calendarAdapter calendarAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.calendrier);
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

        calList=new ArrayList<>();
        recyclerView=findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        calendarAdapter=new calendarAdapter(calList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(calendarAdapter);


addCal();


    }

    public void addCal(){

        cal=new cal();
        cal.setDate("16 May,2019");
        cal.setSession("LANCEMENT OFFICIEL");
        cal.setTime("09:00 AM - 12:00 AM");
        cal.setSpeaker("...");
        cal.setVenue("BRARUDI SALLE");
        calList.add(cal);

        cal=new cal();
        cal.setDate("23 May,2019");
        cal.setSession("MUYINGA");
        cal.setTime("14:00 AM - 12:00 PM");
        cal.setSpeaker("...");
        cal.setVenue("BAR IWACU");
        calList.add(cal);

        cal=new cal();
        cal.setDate("24 May,2019");
        cal.setSession("KIRUNDO");
        cal.setTime("12:00 AM - 18:00 PM");
        cal.setSpeaker("...");
        cal.setVenue("CERCLE DU NORD");
        calList.add(cal);

        cal=new cal();
        cal.setDate("25 May,2019");
        cal.setSession("NGOZI");
        cal.setTime("12:00 AM - 18:00 PM");
        cal.setSpeaker("...");
        cal.setVenue("BAR TAPIS VERT");
        calList.add(cal);

        cal=new cal();
        cal.setDate("25 May,2019");
        cal.setSession("NGOZI");
        cal.setTime("12:00 AM - 18:00 PM");
        cal.setSpeaker("...");
        cal.setVenue("BAR BISSO NA BISSO");
        calList.add(cal);

        calendarAdapter.notifyDataSetChanged();
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
}
