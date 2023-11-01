package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TransactionInput extends AppCompatActivity {

    String[] nelayan = { "Hasanudin - 3604300806810001", "Burhan - 3604302104730002",
            "Darwis - 3604300606790002", "Abdul Latif - 3604300502850006"};

    String[] ikan = { "lemadang Grade A", "tuna gigi anjing Grade A", "tenggiri Grade A", "marlin loreng Grade A" };

    Integer[] hargaikandata = {15000, 25000, 55000, 23000};

    Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_input);

        Spinner nelayanspin = findViewById(R.id.nelayansel);
        Spinner ikanspin = findViewById(R.id.ikansel);
        TextView hargaikan = findViewById(R.id.harga);

        nelayanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nelayanvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Nelayan : " + nelayanvalue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        ikanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ikanvalue = parent.getItemAtPosition(position).toString();
                hargaikan.setText(ikanvalue + " | Rp " + hargaikandata[position] + " /kg");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        ArrayAdapter adnelayan = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nelayan);
        ArrayAdapter adikan = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ikan);

        adnelayan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adikan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nelayanspin.setAdapter(adnelayan);
        ikanspin.setAdapter(adikan);

        dateButton = findViewById(R.id.tanggalbutton);
        dateButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionInput.this,
                    (view, year1, monthOfYear, dayOfMonth) -> dateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1),
                    year, month, day);
            datePickerDialog.show();
        });
    }
}