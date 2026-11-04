
package com.example.studenttaskmanager;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText taskInput;
    Button addButton;
    TextView taskSummary;
    LinearLayout taskContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.taskInput);
        addButton = findViewById(R.id.addButton);
        taskSummary = findViewById(R.id.taskSummary);
        taskContainer = findViewById(R.id.taskContainer);

        databaseHelper = new DatabaseHelper(this);

        displayTasks();

        addButton.setOnClickListener(v -> {

            String task = taskInput.getText().toString().trim();

            if (task.isEmpty()) {

                Toast.makeText(this,
                        "Please enter a task",
                        Toast.LENGTH_SHORT).show();

            } else {

                boolean inserted = databaseHelper.insertTask(task);

                if (inserted) {

                    taskInput.setText("");
                    displayTasks();

                    Toast.makeText(this,
                            "Task added successfully",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this,
                            "Unable to add task",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayTasks() {

        taskContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllTasks();

        int total = 0;
        int completed = 0;

        if (cursor.getCount() == 0) {

            TextView emptyMessage = new TextView(this);

            emptyMessage.setText(
                    "No tasks yet. Add your first task!");

            emptyMessage.setTextSize(16);
            emptyMessage.setTextColor(Color.GRAY);
            emptyMessage.setGravity(Gravity.CENTER);
            emptyMessage.setPadding(10, 30, 10, 30);

            taskContainer.addView(emptyMessage);
        }

        while (cursor.moveToNext()) {

            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id"));

            String task = cursor.getString(
                    cursor.getColumnIndexOrThrow("task_name"));

            boolean isCompleted = cursor.getInt(
                    cursor.getColumnIndexOrThrow("is_completed")) == 1;

            total++;

            if (isCompleted) {
                completed++;
            }

            LinearLayout row = new LinearLayout(this);

            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(10, 8, 10, 8);
            row.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            rowParams.setMargins(0, 0, 0, 12);

            CheckBox checkBox = new CheckBox(this);

            checkBox.setText(task);
            checkBox.setTextSize(16);
            checkBox.setTextColor(Color.BLACK);

            checkBox.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1));

            // Set checked state before adding listener
            checkBox.setChecked(isCompleted);

            checkBox.setOnCheckedChangeListener(
                    (buttonView, checked) -> {

                        databaseHelper.updateTask(id, checked);

                        displayTasks();
                    });

            Button deleteButton = new Button(this);

            deleteButton.setText("Delete");
            deleteButton.setTextSize(12);
            deleteButton.setTextColor(Color.WHITE);

            deleteButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(220, 53, 69)));

            deleteButton.setOnClickListener(v -> {

                databaseHelper.deleteTask(id);

                Toast.makeText(this,
                        "Task deleted",
                        Toast.LENGTH_SHORT).show();

                displayTasks();
            });

            row.addView(checkBox);
            row.addView(deleteButton);

            taskContainer.addView(row, rowParams);
        }

        cursor.close();

        taskSummary.setText(
                "Total Tasks: " + total
                        + "    Completed: " + completed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
