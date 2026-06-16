package com.opheliachi.fokusku;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        List<HistoryItem> list = new ArrayList<>();
        
        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             Cursor cursor = dbHelper.getAllHistory()) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(new HistoryItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SESSION))
                    ));
                } while (cursor.moveToNext());
            }
        }

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            rvHistory.setAdapter(new HistoryAdapter(list));
        }
    }

    // Model Class
    static class HistoryItem {
        String date;
        int duration, session;
        HistoryItem(String date, int duration, int session) {
            this.date = date;
            this.duration = duration;
            this.session = session;
        }
    }

    // Adapter Class
    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<HistoryItem> items;
        HistoryAdapter(List<HistoryItem> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            holder.tvDate.setText(item.date);
            holder.tvDetail.setText(holder.itemView.getContext().getString(R.string.session_detail_template, item.session, item.duration));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvDetail;
            ViewHolder(View v) {
                super(v);
                tvDate = v.findViewById(R.id.tvDate);
                tvDetail = v.findViewById(R.id.tvDetail);
            }
        }
    }
}