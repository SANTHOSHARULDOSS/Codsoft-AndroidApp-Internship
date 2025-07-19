package com.example.mytodo;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskChangedListener listener;

    public interface OnTaskChangedListener {
        void onTaskChanged();
    }

    public TaskAdapter(List<Task> tasks, OnTaskChangedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, description, priority, dueDate;
        ImageButton editBtn, deleteBtn;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.taskCheckBox);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDesc);
            priority = itemView.findViewById(R.id.taskPriority);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.priority.setText("Priority: " + task.getPriority());
        holder.dueDate.setText("Due: " + task.getDueDate());
        holder.checkBox.setChecked(task.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (task.isCompleted() != isChecked) {
                task.setCompleted(isChecked);
                listener.onTaskChanged();
            }
        });

        holder.editBtn.setOnClickListener(view -> {
            Context context = view.getContext();
            showEditDialog(context, task, listener);
        });

        holder.deleteBtn.setOnClickListener(view -> {
            tasks.remove(position);
            notifyItemRemoved(position);
            listener.onTaskChanged();
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> taskList) {
        this.tasks = taskList;
        notifyDataSetChanged();
    }

    private void showEditDialog(Context context, Task task, OnTaskChangedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        EditText titleInput = new EditText(context);
        titleInput.setText(task.getTitle());
        titleInput.setHint("Task title");

        EditText descInput = new EditText(context);
        descInput.setText(task.getDescription());
        descInput.setHint("Description");

        Spinner prioritySpinner = new Spinner(context);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        int priorityIndex = adapter.getPosition(task.getPriority());
        prioritySpinner.setSelection(priorityIndex);

        EditText dueDateInput = new EditText(context);
        dueDateInput.setInputType(InputType.TYPE_CLASS_DATETIME);
        dueDateInput.setText(task.getDueDate());
        dueDateInput.setHint("Due date (YYYY-MM-DD)");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);
        layout.addView(titleInput);
        layout.addView(descInput);
        layout.addView(prioritySpinner);
        layout.addView(dueDateInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            task.setTitle(titleInput.getText().toString());
            task.setDescription(descInput.getText().toString());
            task.setPriority(prioritySpinner.getSelectedItem().toString());
            task.setDueDate(dueDateInput.getText().toString());
            listener.onTaskChanged();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}