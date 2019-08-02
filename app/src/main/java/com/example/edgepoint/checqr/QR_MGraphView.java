package com.example.edgepoint.checqr;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class QR_MGraphView extends AppCompatActivity {

    String[] legendNameP ={"Attendance", "Target", "Missing Count: ", "No. of Precinct:"};
    String[] legendName ={"Attendance", "Target", "Missing Count ", "No. of Voters"};
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr__mgraph_view);

        Intent mIntent = getIntent();
        String label = mIntent.getStringExtra("Label");
        int PrecinctNumber = mIntent.getIntExtra("Precinct", 0);
        String graphLabel = mIntent.getStringExtra("GraphLabel");
        int NumVoters = mIntent.getIntExtra("VoterBrgy", 0);
        int yAxis=0;

        if(graphLabel.equals("city"))
            yAxis = 105366;
        else if (graphLabel.equals("barangay"))
            yAxis = NumVoters;
        else if (graphLabel.equals("precinct"))
            yAxis = 210;

        TextView label_tv = (TextView) findViewById(R.id.QRtextView2);
        label_tv.setText(label+" Bar Graph");

        backQRGraphViewButton();

        barChart = (BarChart) findViewById(R.id.QRbarChart2);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<BarEntry> barEntries = databaseAccess.getQRBarEntries(0 + "," + 3);
        BarDataSet barDataSet = new BarDataSet(barEntries, "Count");
        databaseAccess.close();

        DatabaseAccess datacountM = DatabaseAccess.getInstance(QR_MGraphView.this); //count the names
        datacountM.open();
        List<Integer> quaters2 = datacountM.getEntriesQRY();
        datacountM.close();

        final Integer[] temp = quaters2.toArray(new Integer[quaters2.size()]);

        final List<String> quarters1 = new ArrayList<>();

        for(int i=0; i<temp.length;i++) {
            float attendPercent=0;
            if (i==0) {
                int attend = temp[0];
                int divTarget = temp[1];
                attendPercent = ((float) attend / (float) divTarget) * 100;

            }
            else if (i==1){
                int attend = temp[1];
                int divTarget = temp[2];
                attendPercent = ((float) attend / (float) divTarget) * 100;

            }
            else if (i==2){
                int missing = temp[2];
                //int divTarget = temp[2];
                attendPercent = ((float) missing / (float)yAxis) * 100;
            }

            DecimalFormat df = new DecimalFormat("###,###,###.#");
            String dft = df.format(attendPercent);

            if (dft.equals("NaN")) {
                quarters1.add("0%");
            } else quarters1.add(dft + "%");

        }
        final String[] quarters = quarters1.toArray(new String[quarters1.size()]);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };



        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueFormatter(new MyValueFormatter());

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setTextSize(12);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);

        YAxis RyAxis = barChart.getAxisRight();
        RyAxis.setEnabled(false);
        RyAxis.setAxisMinimum(0f);
        RyAxis.setAxisMaximum(temp[2]);
        RyAxis.setTextColor(Color.WHITE);

        YAxis LyAxis = barChart.getAxisLeft();
        LyAxis.setEnabled(true);
        LyAxis.setAxisMinimum(0f);
        //LyAxis.setAxisMaximum(temp[2]);
        if(graphLabel.equals("city"))
            LyAxis.setAxisMaximum(yAxis);
        else if (graphLabel.equals("barangay"))
            LyAxis.setAxisMaximum(yAxis);
        else if (graphLabel.equals("precinct"))
            LyAxis.setAxisMaximum(yAxis);

        LyAxis.setTextColor(Color.WHITE);
        LyAxis.setTextSize(12);
        LyAxis.setValueFormatter(new LargeValueFormatter());

        BarData theData = new BarData(barDataSet);
        theData.setValueTextSize(12);
        theData.setValueTextColor(Color.WHITE);
        theData.setBarWidth(0.9f);

        LegendEntries(graphLabel, temp, PrecinctNumber, yAxis);

        barChart.setData(theData);
        barChart.setEnabled(true);
        barChart.setDescription(null);
        barChart.setDrawValueAboveBar(false);
        barChart.setExtraOffsets(0, 20, 0, 25);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleYEnabled(true);
        barChart.setScaleXEnabled(false);
        barChart.animateY(1000);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.setGridBackgroundColor(Color.WHITE);
        barChart.invalidate();

    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###.#");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    public void backQRGraphViewButton(){
        Button prev2button = (Button) findViewById(R.id.QRbutton2);
        prev2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void LegendEntries(String graphLabel, final Integer[] temp, int PrecinctNumber, int YaxisVoter){
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextSize(13);
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(13);
        legend.setXEntrySpace(1);

        if (graphLabel.equals("barangay")){
            LegendEntry[] legendEntries = new LegendEntry[5];

            for (int i=0; i<legendEntries.length; i++)
            {
                LegendEntry entry = new LegendEntry();
                if (i==0)
                {
                    entry.label =  String.valueOf(legendNameP[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[2] = entry;
                }
                else if(i==1){
                    entry.label =  String.valueOf(legendNameP[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[3] = entry;

                }
                else if (i==2){
                    entry.label =  String.valueOf(legendNameP[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[4] = entry;
                }
                else if (i==3){
                    entry.label =  String.valueOf("No. of Voters:" + YaxisVoter);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[0] = entry;
                }
                else {
                    entry.label = String.valueOf("No. of Precinct:" + PrecinctNumber);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[1] = entry;
                }

            }

            legend.setCustom(legendEntries);
        }else{
            LegendEntry[] legendEntries = new LegendEntry[4];

            for (int i=0; i<legendEntries.length; i++)
            {
                LegendEntry entry = new LegendEntry();
                if (i==0)
                {
                    entry.label =  String.valueOf(legendName[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[1] = entry;
                }
                else if(i==1){
                    entry.label =  String.valueOf(legendName[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[2] = entry;

                }
                else if (i==2){
                    entry.label =  String.valueOf(legendName[i]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    //entry.form = Legend.LegendForm.NONE;
                    legendEntries[3] = entry;
                }
                else if(i==3){
                    entry.label =  String.valueOf("No. of Voters:" +YaxisVoter);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[0] = entry;
                }
//                else {
//                    entry.label =  String.valueOf(YaxisVoter);
//                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
//                    entry.form = Legend.LegendForm.NONE;
//                    legendEntries[1] = entry;
//                }
            }

            legend.setCustom(legendEntries);
        }

    }
}