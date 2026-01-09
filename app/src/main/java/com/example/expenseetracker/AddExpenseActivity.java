package com.example.expenseetracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    EditText titleInput, amountInput, dateInput, notesInput;
    Spinner categorySpinner;
    Button saveBtn;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Get user ID from intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if(userId == -1){
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = new DBHelper(this);

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        amountInput = findViewById(R.id.amountInput);
        dateInput = findViewById(R.id.dateInput);
        notesInput = findViewById(R.id.notesInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveBtn = findViewById(R.id.saveBtn);

        // Setup category spinner
        String[] categories = {"Food", "Travel", "Bills", "Entertainment", "Shopping", "Health", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Setup date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Save button click
        saveBtn.setOnClickListener(v -> saveExpense());
    }

    private void showDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
            String formattedDate = String.format("%04d-%02d-%02d", y, m+1, d);
            dateInput.setText(formattedDate);
        }, year, month, day);

        datePicker.show();
    }

    private void saveExpense(){
        String title = titleInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if(title.isEmpty() || amountStr.isEmpty() || date.isEmpty()){
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e){
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.addExpense(userId, title, amount, category, date, notes);

        if(inserted){
            Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            // Return to Dashboard
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
        }
    }
}
