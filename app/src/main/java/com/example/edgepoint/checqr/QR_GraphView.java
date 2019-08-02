package com.example.edgepoint.checqr;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.annotation.Target;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.ceil;

public class QR_GraphView extends AppCompatActivity {

    String[] legendNameP ={"Attendance", "Target Count: ", "No. of Precinct: "};
    String[] legendName ={"Attendance", "Target Count: "};
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr__graph_view);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("TargetCount", 0);
        int PrecinctNumber = mIntent.getIntExtra("Precinct", 0);
        String label = mIntent.getStringExtra("Label");
        String graphLabel = mIntent.getStringExtra("GraphLabel");
        //float intValue = mIntent.getFloatExtra("TargetCount",0);
        //Toast.makeText(QR_GraphView.this,String.valueOf(intValue),Toast.LENGTH_LONG).show();

        TextView label_tv = (TextView) findViewById(R.id.QRtextView);
        label_tv.setText(label+" Bar Graph");

        backQRGraphViewButton();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<BarEntry> barEntries = databaseAccess.getQRBarEntries(String.valueOf(1));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Count");
        databaseAccess.close();

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueFormatter(new MyValueFormatter());

        DatabaseAccess datacountM = DatabaseAccess.getInstance(QR_GraphView.this); //count the names
        datacountM.open();
        List<Integer> quaters2 = datacountM.getEntriesQRY();
        datacountM.close();

        final Integer[] temp = quaters2.toArray(new Integer[quaters2.size()]);

        final List<String> quarters1 = new ArrayList<>();

        int attend = temp[0];
        float attendPercent = ((float) attend / (float) intValue) * 100;
        DecimalFormat df = new DecimalFormat("###,###,###.#");
        String dft = df.format(attendPercent);

        if (dft.equals("NaN")) {
            quarters1.add("0%");
        }
        else quarters1.add(dft + "%");

        final String[] quarters = quarters1.toArray(new String[quarters1.size()]);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };

        barChart = (BarChart) findViewById(R.id.QRbarChart);

        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextSize(15);
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(15);
        legend.setXEntrySpace(1);

//        int lenghtLegend;
//        if (graphLabel.equals("barangay")){
//            lenghtLegend = legendNameP.length;
//        }else{
//            lenghtLegend = legendName.length;
//        }

        if (graphLabel.equals("barangay")){
            LegendEntry[] legendEntries = new LegendEntry[legendNameP.length];

            for (int i=0; i<legendEntries.length; i++)
            {
                LegendEntry entry = new LegendEntry();
                if (i==0)
                {
                    entry.label =  String.valueOf(legendNameP[0]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[2] = entry;
                }
                else if (i==1){
                    //int floatValue = Math.round(intValue);
                    entry.label =  String.valueOf(legendNameP[1] + intValue);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[0] = entry;

                }
                else {
                    entry.label =  String.valueOf(legendNameP[2] + PrecinctNumber);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[1] = entry;
                }

            }
            legend.setCustom(legendEntries);
        }
        else{
            LegendEntry[] legendEntries = new LegendEntry[legendName.length];

            for (int i=0; i<legendEntries.length; i++)
            {
                LegendEntry entry = new LegendEntry();
                if (i==0)
                {
                    entry.label =  String.valueOf(legendName[0]);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    legendEntries[1] = entry;
                }
                else if (i==1){
                    //int floatValue = Math.round(intValue);
                    entry.label =  String.valueOf(legendName[1] + intValue);
                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
                    entry.form = Legend.LegendForm.NONE;
                    legendEntries[0] = entry;

                }
//                else {
//                    entry.label =  String.valueOf(legendName[2] + PrecinctNumber);
//                    entry.formColor = (ColorTemplate.COLORFUL_COLORS[i]);
//                    entry.form = Legend.LegendForm.NONE;
//                    legendEntries[1] = entry;
//                }
            }
            legend.setCustom(legendEntries);
        }



        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setTextSize(15);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);

        YAxis RyAxis = barChart.getAxisRight();
        RyAxis.setEnabled(false);
        RyAxis.setAxisMinimum(0f);
        RyAxis.setTextColor(Color.WHITE);
        RyAxis.setAxisMaximum(temp[2]);

        YAxis LyAxis = barChart.getAxisLeft();
        LyAxis.setEnabled(true);
        LyAxis.setAxisMinimum(0f);
        LyAxis.setTextColor(Color.WHITE);
        LyAxis.setAxisMaximum(temp[2]);
        LyAxis.setTextSize(15);
        LyAxis.setAxisMaximum(intValue);
        LyAxis.setValueFormatter(new LargeValueFormatter());

        BarData theData = new BarData(barDataSet);
        theData.setValueTextSize(15);
        theData.setValueTextColor(Color.WHITE);
        theData.setBarWidth(0.5f);

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
        Button prev2button = (Button) findViewById(R.id.QRbutton);
        prev2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}