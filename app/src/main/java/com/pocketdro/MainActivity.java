package com.pocketdro;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText editTextXRev;
    EditText editTextYRev;
    EditText editTextXDistance;
    EditText editTextYDistance;
    EditText editTextEdgeFinder;
    Button buttonRevUnit;
    Button buttonFinderUnit;
    Button buttonDistanceUnit;
    Button buttonConvert;
    Button buttonRemainderUnit;
    TextView textViewXTurnsValue;
    TextView textViewYTurnsValue;
    TextView textViewXRemainderValue;
    TextView textViewYRemainderValue;
    TextView textViewXTurnsLabel;
    TextView textViewYTurnsLabel;
    TextView textViewXRemainderLabel;
    TextView textViewYRemainderLabel;

    boolean remianderUnitIsMetric, revUnitIsMetric, finderUnitIsMetric, distanceUnitIsMetric = false;
    double remainderXConvertedValue, remainderYConvertedValue = 0;
    UserSettings userSettingsCache = new UserSettings();

    public static final String USER_SETTINGS_FILE = "userSettings.ser";

    Typeface digitalTypeface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        digitalTypeface = Typeface.createFromAsset(getAssets(),
                "fonts/digital-7.ttf");

        initEditTextViews();
        initButtonViews();
        initTextViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            readUserSettings(USER_SETTINGS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        updateUserInputs(this.userSettingsCache);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            updateUserSettings(userSettingsCache);
            saveUserSettings(this.userSettingsCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initEditTextViews()
    {
        this.editTextXDistance = (EditText)findViewById(R.id.editTextXDistance);
        this.editTextXDistance.setTypeface(digitalTypeface);
        this.editTextYDistance = (EditText)findViewById(R.id.editTextYDistance);
        this.editTextYDistance.setTypeface(digitalTypeface);
        this.editTextXRev = (EditText)findViewById(R.id.editTextXRev);
        this.editTextXRev.setTypeface(digitalTypeface);
        this.editTextYRev = (EditText)findViewById(R.id.editTextYRev);
        this.editTextYRev.setTypeface(digitalTypeface);
        this.editTextEdgeFinder = (EditText)findViewById(R.id.editTextEdgeFinderDia);
        this.editTextEdgeFinder.setTypeface(digitalTypeface);
    }

    private void initButtonViews()
    {
        this.buttonRevUnit = (Button) findViewById(R.id.buttonRevUnit);
        this.buttonRevUnit.setTypeface(digitalTypeface);
        this.buttonRevUnit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                revUnitIsMetric = !revUnitIsMetric;
                updateUnitButton(revUnitIsMetric, buttonRevUnit);
            }
        });

        this.buttonFinderUnit = (Button) findViewById(R.id.buttonFinderUnit);
        this.buttonFinderUnit.setTypeface(digitalTypeface);
        this.buttonFinderUnit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finderUnitIsMetric = !finderUnitIsMetric;
                updateUnitButton(finderUnitIsMetric, buttonFinderUnit);
            }
        });

        this.buttonDistanceUnit = (Button) findViewById(R.id.buttonDistanceUnit);
        this.buttonDistanceUnit.setTypeface(digitalTypeface);
        this.buttonDistanceUnit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                distanceUnitIsMetric = !distanceUnitIsMetric;
                updateUnitButton(distanceUnitIsMetric, buttonDistanceUnit);
            }
        });

        this.buttonRemainderUnit = (Button) findViewById(R.id.buttonRemainderUnit);
        this.buttonRemainderUnit.setTypeface(digitalTypeface);
        this.buttonRemainderUnit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                remianderUnitIsMetric = !remianderUnitIsMetric;
                double xRemainder, yRemainder;
                if (remianderUnitIsMetric)
                {
                    buttonRemainderUnit.setText("mm");
                    xRemainder = convertToMetric(remainderXConvertedValue);
                    yRemainder = convertToMetric(remainderYConvertedValue);
                }
                else
                {
                    buttonRemainderUnit.setText("inch");
                    xRemainder = remainderXConvertedValue;
                    yRemainder = remainderYConvertedValue;
                }
                updateRemainders(xRemainder, yRemainder);
            }
        });

        this.buttonConvert = (Button) findViewById(R.id.buttonConvert);
        this.buttonConvert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    double xRev = getRevolutions(Double.parseDouble(editTextXRev.getText().toString()));
                    double yRev = getRevolutions(Double.parseDouble(editTextYRev.getText().toString()));
                    double edgeFinderRadius = getEdgeFinderRadius(Double.parseDouble(editTextEdgeFinder.getText().toString()), finderUnitIsMetric);
                    double xDistance = getDistance(Double.parseDouble(editTextXDistance.getText().toString()), distanceUnitIsMetric, edgeFinderRadius);
                    double yDistance = getDistance(Double.parseDouble(editTextYDistance.getText().toString()), distanceUnitIsMetric, edgeFinderRadius);
                    double xTurns = calculateTurns(xRev, xDistance);
                    double yTurns = calculateTurns(yRev, yDistance);
                    double xInchRemainder = calculateInchRemander(xTurns, xRev, xDistance);
                    double yInchRemainder = calculateInchRemander(yTurns, yRev, yDistance);
                    remainderXConvertedValue = xInchRemainder;
                    remainderYConvertedValue = yInchRemainder;
                    updateResults(xTurns, xInchRemainder, yTurns, yInchRemainder);

                    updateUserSettings(userSettingsCache);
                    saveUserSettings(userSettingsCache);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private double getEdgeFinderRadius(double diameter, boolean finderUnitIsMetric) {
        double retVal = diameter;
        if (finderUnitIsMetric)
        {
            retVal = convertToSae(diameter);
        }
        //return the radius
        return retVal/2;
    }

    private void initTextViews()
    {
        this.textViewXRemainderValue = (TextView) findViewById(R.id.textViewXRemainderValue);
        this.textViewXRemainderValue.setTypeface(digitalTypeface);
        this.textViewYRemainderValue = (TextView) findViewById(R.id.textViewYRemainderValue);
        this.textViewYRemainderValue.setTypeface(digitalTypeface);
        this.textViewXTurnsValue = (TextView) findViewById(R.id.textViewXTurnsValue);
        this.textViewXTurnsValue.setTypeface(digitalTypeface);
        this.textViewYTurnsValue = (TextView) findViewById(R.id.textViewYTurnsValue);
        this.textViewYTurnsValue.setTypeface(digitalTypeface);
        this.textViewXTurnsLabel = (TextView) findViewById(R.id.textViewXTurnsLabel);
        this.textViewXTurnsLabel.setTypeface(digitalTypeface);
        this.textViewYTurnsLabel = (TextView) findViewById(R.id.textViewYTurnsLabel);
        this.textViewYTurnsLabel.setTypeface(digitalTypeface);
        this.textViewXRemainderLabel = (TextView) findViewById(R.id.textViewXRemainderLabel);
        this.textViewXRemainderLabel.setTypeface(digitalTypeface);
        this.textViewYRemainderLabel = (TextView) findViewById(R.id.textViewYRemainderLabel);
        this.textViewYRemainderLabel.setTypeface(digitalTypeface);
    }

    private double getDistance(double distance, boolean isMetric, double edgeFinderRadius) throws Exception
    {
        double retVal = distance;
        if (isMetric) {
            retVal = convertToSae(distance);
        }
        retVal += edgeFinderRadius;
        if (retVal > 0) {
            return retVal;
        }
        else {
            throw new IllegalArgumentException (distance + " is not a valid distance entry");
        }
    }

    private double getRevolutions(double revs)
    {
        double retVal = revs;
        if (revUnitIsMetric)
        {
            retVal = convertToSae(revs);
        }
        return retVal;
    }

    private double calculateTurns(double rev, double distance)
    {
        return Math.floor(distance/rev);
    }

    private double calculateInchRemander(double turns, double revs, double distance)
    {
        double inchTurns = revs * turns;
        return distance - inchTurns;
    }

    private double convertToSae(double metricValue)
    {
        return metricValue * 0.03937008;
    }
    private double convertToMetric(double standardValue)
    {
        return standardValue * 25.4;
    }

    private void updateUnitButton(boolean isMetric, Button button)
    {
        if (isMetric)
        {
            button.setText("mm");
        }
        else
        {
            button.setText("inch");
        }
    }

    private void updateResults(double xTurn, double xRemainder, double yTurn, double yRemainder)
    {
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        this.textViewXTurnsValue.setText(String.valueOf(xTurn));
        this.textViewYTurnsValue.setText(String.valueOf(yTurn));
        this.textViewXRemainderValue.setText(String.valueOf(numberFormat.format(xRemainder)));
        this.textViewYRemainderValue.setText(String.valueOf(numberFormat.format(yRemainder)));
    }

    private void updateRemainders(double xRemainder, double yRemainder)
    {
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        this.textViewXRemainderValue.setText(String.valueOf(numberFormat.format(xRemainder)));
        this.textViewYRemainderValue.setText(String.valueOf(numberFormat.format(yRemainder)));
    }

    private void updateUserSettings(UserSettings userSettings)
    {
        userSettings.setxRev(Double.parseDouble(editTextXRev.getText().toString()));
        userSettings.setyRev(Double.parseDouble(editTextYRev.getText().toString()));
        userSettings.setRevIsMetric(revUnitIsMetric);
        userSettings.setEdgeFinderDiam(Double.parseDouble(editTextEdgeFinder.getText().toString()));
        userSettings.setEdgeIsMetric(finderUnitIsMetric);
        userSettings.setxDist(Double.parseDouble(editTextXDistance.getText().toString()));
        userSettings.setyDist(Double.parseDouble(editTextYDistance.getText().toString()));
        userSettings.setDistIsMetric(distanceUnitIsMetric);
    }

    private void updateUserInputs(UserSettings userSettings)
    {
        this.editTextXRev.setText(String.valueOf(userSettings.getxRev()));
        this.editTextYRev.setText(String.valueOf(userSettings.getyRev()));
        this.revUnitIsMetric = userSettings.isRevIsMetric();
        updateUnitButton(this.remianderUnitIsMetric, buttonRevUnit);

        this.editTextEdgeFinder.setText(String.valueOf(userSettings.getEdgeFinderDiam()));
        this.finderUnitIsMetric = userSettings.isEdgeIsMetric();
        updateUnitButton(this.finderUnitIsMetric, buttonFinderUnit);

        this.editTextXDistance.setText(String.valueOf(userSettings.getxDist()));
        this.editTextYDistance.setText(String.valueOf(userSettings.getyDist()));
        this.distanceUnitIsMetric = userSettings.isDistIsMetric();
        updateUnitButton(this.distanceUnitIsMetric, buttonDistanceUnit);
    }

    private void saveUserSettings(UserSettings userSettingsCache) throws IOException {
        FileOutputStream fos = this.openFileOutput(USER_SETTINGS_FILE, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(userSettingsCache);
        os.close();
        fos.close();
    }

    private void readUserSettings(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = this.openFileInput(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        this.userSettingsCache = (UserSettings) is.readObject();
        is.close();
        fis.close();
    }
}
