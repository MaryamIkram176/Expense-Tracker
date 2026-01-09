package com.example.expenseetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    ListView expenseList;
    Button addExpenseBtn;
    DBHelper db;
    int userId;

    ArrayList<String> expenses;      // Display text
    ArrayList<Integer> expenseIds;   // DB IDs
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        expenseList = findViewById(R.id.expenseList);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        db = new DBHelper(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        expenses = new ArrayList<>();
        expenseIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, expenses);
        expenseList.setAdapter(adapter);

        loadExpenses();

        // ➕ ADD EXPENSE
        addExpenseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivityForResult(intent, 100);
        });

        // ❌ DELETE ON LONG PRESS
        expenseList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position >= expenseIds.size()) return true;

            new AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean deleted = db.deleteExpense(expenseIds.get(position));
                        if (deleted) {
                            Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                            loadExpenses();
                        } else {
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    // 🔄 LOAD EXPENSES
    private void loadExpenses() {
        expenses.clear();
        expenseIds.clear();

        Cursor cursor = db.getExpenses(userId);

        if (cursor.getCount() == 0) {
            expenses.add("No expenses added yet");
        } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                expenseIds.add(id);
                expenses.add(title + " - " + amount + " USD (" + category + ")");
            }
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // 🔄 REFRESH AFTER ADD
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadExpenses();
        }
    }
}
