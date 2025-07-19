package com.example.mytodo;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    private String title;
    private String description;
    private String priority;
    private String dueDate;
    private boolean completed;

    public Task(String title, String description, String priority, String dueDate, boolean completed) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // For saving/loading from SharedPreferences as JSON
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("title", title);
        obj.put("description", description);
        obj.put("priority", priority);
        obj.put("dueDate", dueDate);
        obj.put("completed", completed);
        return obj;
    }

    public static Task fromJson(JSONObject obj) throws JSONException {
        return new Task(
                obj.getString("title"),
                obj.getString("description"),
                obj.getString("priority"),
                obj.getString("dueDate"),
                obj.getBoolean("completed")
        );
    }
}