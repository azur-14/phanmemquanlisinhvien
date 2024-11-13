package com.example.giuaky;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    private List<Certificate> certificatesList;  // List to store the certificates

    // Constructor
    public CertificateAdapter(List<Certificate> certificatesList) {
        this.certificatesList = certificatesList;
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the certificate item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Certificate certificate = certificatesList.get(position);

        Log.d("CertificateAdapter", "Binding certificate: " + certificate.getName() + ", Issued by: " + certificate.getIssuedBy());

        holder.certificateTitle.setText(certificate.getName());
        holder.certificateIssuedBy.setText("Issued By: " + certificate.getIssuedBy());
        holder.certificateScore.setText("Score: " + certificate.getScore());
        holder.certificateDateIssued.setText("Date Issued: " + certificate.getDateIssued());
        holder.remark.setText("Remarks: " + certificate.getRemarks());
    }

    @Override
    public int getItemCount() {
        return certificatesList.size();  // Return the size of the list
    }

    // ViewHolder class to hold the views for each certificate item
    public static class CertificateViewHolder extends RecyclerView.ViewHolder {

        TextView certificateTitle;
        TextView certificateIssuedBy;
        TextView certificateScore;
        TextView certificateDateIssued;
        TextView remark;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views with the correct IDs from item_certificate.xml
            certificateTitle = itemView.findViewById(R.id.certificateName);
            certificateIssuedBy = itemView.findViewById(R.id.issuedBy);
            certificateScore = itemView.findViewById(R.id.score);
            certificateDateIssued = itemView.findViewById(R.id.dateIssued);
            remark = itemView.findViewById(R.id.remarks);
        }
    }

    // Method to update the list of certificates and notify the adapter
    public void updateCertificates(List<Certificate> newCertificatesList) {
        certificatesList.clear();
        certificatesList.addAll(newCertificatesList);
        notifyDataSetChanged();  // Notify the adapter to refresh the view
    }
}
