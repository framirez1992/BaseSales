package com.far.basesales;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainReports extends AppCompatActivity {

    Fragment lastFragment;
    ReportsSales reportsSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reports);
        reportsSales = new ReportsSales();
        reportsSales.setParentActivity(this);

        changeFragment(reportsSales);
    }

    public void changeFragment(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
        lastFragment = f;
    }

}
