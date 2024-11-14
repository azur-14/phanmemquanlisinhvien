package com.example.giuaky;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class LoginHistoryAdapter extends FirestoreRecyclerAdapter<LoginHistory, LoginHistoryAdapter.ViewHolder> {

    public LoginHistoryAdapter(@NonNull FirestoreRecyclerOptions<LoginHistory> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_one_login_history, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull LoginHistory model) {
        holder.bindData(position, model);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;
        TextView tvLoginDate;
        View viewContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewContainer = itemView;

            tvLocation = itemView.findViewById(R.id.tv_location);
            tvLoginDate = itemView.findViewById(R.id.tv_login_date);
        }

        public void bindData(int position, LoginHistory loginHistory) {
            tvLocation.setText(loginHistory.getLocation());
            tvLoginDate.setText(loginHistory.getLoginDate().toString());
        }
    }
}
