package com.adedo;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.adedo.Constants.ProfileUrl;

public class Chofer extends Activity {

    public final static String MY_PREFS_NAME = "AdedoPref";

    private ImageButton im_btn;
    private EditText mailc, nombrec, telefonoc, direccionc, autoc;
    private Context contetx;
    private JSONArray ja;
    private String data;
    private CheckBox cb;
    private Date c = new Date();
    private int año = 0;
    private int mes = 0;
    private int dia = 0;
    private Spinner lista, lista2, lista3;
    String facebook;
    private TextView select_date;
    private String[] datos = {"Día", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private String[] datos2 = {"Mes", "ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};
    private String[] datos3 = {"Año", "2000", "1999", "1998", "1997", "1996", "1995", "1994", "1993", "1992", "1991", "1990", "1989", "1988",
            "1987", "1986", "1985", "1984", "1983", "1982", "1981", "1980", "1979", "1978", "1977", "1976", "1975", "1974", "1973", "1972", "1971",
            "1970", "1969", "1968", "1967", "1966", "1965", "1964", "1963", "1962", "1961", "1960", "1959", "1958", "1957", "1956", "1955", "1954",
            "1953", "1952", "1951", "1950", "1949", "1948", "1947", "1946", "1945", "1944", "1943", "1942", "1941", "1940", "1939", "1938", "1937",
            "1936", "1935", "1934", "1933", "1932", "1931", "1930"};

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int cantidad = 0;
            cantidad = ja.length();
            if (cantidad == 0)
                try {
                    verificaciones();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                siguientePantalla();
        }
    };

    Handler h1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpGetData(Utilities.getUrl(getApplicationContext()) + "/registro_calificaciones.php?mailc=" + mailc.getText() +
                            "&promedioca=0&comentariosca=" + "comentarios");
                }
            }).start();
            Toast.makeText(getApplicationContext(), "Resgistró correctamente la información", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        contetx = this;
        mailc = (EditText) findViewById(R.id.editText);
        nombrec = (EditText) findViewById(R.id.editText2);
        telefonoc = (EditText) findViewById(R.id.editText4);
        direccionc = (EditText) findViewById(R.id.editText3);
        autoc = (EditText) findViewById(R.id.editText5);
        cb = (CheckBox) findViewById(R.id.checkBox1);
        //facebook = (EditText) findViewById(R.id.editText16);
        select_date = (TextView) findViewById(R.id.select_date);

        String mail = getIntent().hasExtra("email") ? getIntent().getExtras().getString("email") : "";
        mailc.setText(mail);
        String name = getIntent().hasExtra("name") ? getIntent().getExtras().getString("name") : "";
        nombrec.setText(getIntent().getExtras().getString("name"));

        SharedPreferences prefs = getSharedPreferences(Chofer.MY_PREFS_NAME, MODE_PRIVATE);
        String first_name = prefs.getString("first_name", "");
        String last_name = prefs.getString("last_name", "");


        facebook = prefs.getString(ProfileUrl,"");
        facebook = facebook.trim().toLowerCase();

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(Chofer.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {

                                año = year;
                                mes = monthOfYear;
                                dia = dayOfMonth;
                                select_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        Toast.makeText(getApplicationContext(), "Si ya esta registrado solo ingrese su mail !", Toast.LENGTH_SHORT).show();
    }

    public void TerminosCondiciones(View view) {
        Intent i = new Intent(Chofer.this, Terminos.class);
        startActivity(i);
    }

    public void estaRegistrado(String m) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ja = null;
                data = httpGetData(Utilities.getUrl(getApplicationContext()) + "/consulta_chofer.php?mailc=" + mailc.getText());
                if (data.length() > 0) {
                    try {
                        ja = new JSONArray(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    h.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    public void atras(View view) {
        Intent i = new Intent(Chofer.this, ADedo.class);
        i.putExtra("parametro1", mailc.getText().toString());
        startActivity(i);
    }

    public void siguiente(View view) {
        try {
            verificaciones();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void siguientePantalla() {
        Intent i = new Intent(Chofer.this, Chofer_viaje.class);
        i.putExtra("parametro1", mailc.getText().toString());
        startActivity(i);
    }

    public void verificaciones() throws IOException {
        if (nombrec.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "Debe ingresar el nombre", Toast.LENGTH_SHORT).show();
        else if (direccionc.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "Debe ingresar la dirección", Toast.LENGTH_SHORT).show();
        else if (telefonoc.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "Debe ingresar el teléfono", Toast.LENGTH_SHORT).show();
        else if (select_date.getText().toString().isEmpty() || select_date.getText().toString().equals(getString(R.string.select_date)))
            Toast.makeText(getApplicationContext(), "Debe ingresar la fecha de nacimiento", Toast.LENGTH_SHORT).show();
        else {
            if (((c.getYear() + 1900) - año) < 18)
                Toast.makeText(getApplicationContext(), "Debe ser mayor de 18 años para poder registrarse", Toast.LENGTH_SHORT).show();
            else if (autoc.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(), "Debe ingresar el modelo del auto", Toast.LENGTH_SHORT).show();
            else if (!cb.isChecked())
                Toast.makeText(getApplicationContext(), "Debe aceptar los Términos y Condiciones para seguir, antes debe leerlo", Toast.LENGTH_SHORT)
                     .show();
            else {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        httpGetData(Utilities.getUrl(getApplicationContext()) + "/registro_chofer.php?mailc=" + mailc.getText()
                                                                                                                     .toString()
                                                                                                                     .replace("\"", "") +
                                "&nombrec=" + nombrec
                                .getText() + "&telefonoc=" + telefonoc.getText() + "&direccionc=" + direccionc.getText() + "&autoc=" + autoc
                                .getText() + "&nacimientoc=" + dia
                                + "/" + mes + "/" + año
                                + "&facebookc=" + facebook);
                        h1.sendEmptyMessage(1);
                    }
                }).start();
                Intent i = new Intent(Chofer.this, Principal.class);
                i.putExtra("email", mailc.getText().toString());
                startActivity(i);
                finish();
            }
        }
    }

    public void insertInSharePref() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(mailc.getText().toString() + "-Inserted", true);
        editor.commit();
    }

    public String httpGetData(String mURL) {
        String response = "";
        mURL = mURL.replace(" ", "%20");
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(mURL);
        ResponseHandler<String> responsehandler = new BasicResponseHandler();
        try {
            response = httpclient.execute(httpget, responsehandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        insertInSharePref();
        return response;
    }
}