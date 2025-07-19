package com.example.mytodo;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.*;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskChangedListener {

    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private SharedPreferences prefs;

    private EditText titleInput, descInput, dueDateInput;
    private Spinner prioritySpinner;
    private Button addBtn;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hook up UI elements
        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        addBtn = findViewById(R.id.addBtn);
        recyclerView = findViewById(R.id.recyclerView);

        // Spinner data (should match your strings.xml priorities array)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.priorities, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);

        // Date picker for dueDate
        dueDateInput.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                    (v, year, month, dayOfMonth) ->
                            dueDateInput.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // RecyclerView setup
        adapter = new TaskAdapter(tasks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // SharedPreferences
        prefs = getSharedPreferences("tasks_prefs", MODE_PRIVATE);
        loadTasks();

        // Add button click
        addBtn.setOnClickListener(view -> addTask());
    }

    private void addTask() {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        String dueDate = dueDateInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Task task = new Task(title, desc, priority, dueDate, false);
        tasks.add(task);
        adapter.notifyItemInserted(tasks.size() - 1);
        saveTasks();
        titleInput.setText("");
        descInput.setText("");
        dueDateInput.setText("");
        prioritySpinner.setSelection(0);
    }

    private void saveTasks() {
        JSONArray array = new JSONArray();
        for (Task task : tasks) {
            try {
                array.put(task.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString("tasks_list", array.toString()).apply();
    }

    private void loadTasks() {
        tasks.clear();
        String json = prefs.getString("tasks_list", "");
        if (!json.isEmpty()) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    tasks.add(Task.fromJson(array.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskChanged() {
        saveTasks();
        adapter.notifyDataSetChanged();
    }
}