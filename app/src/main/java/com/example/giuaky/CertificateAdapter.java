package com.example.giuaky;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {
    private List<Certificate> certificates; // Original list of certificates
    private List<Certificate> selectedCertificates; // List to keep track of selected certificates

    public CertificateAdapter(List<Certificate> certificates) {
        this.certificates = certificates;
        this.selectedCertificates = new ArrayList<>(); // Initialize the ArrayList
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Certificate certificate = certificates.get(position);
        holder.bind(certificate);

        // Set the checkbox state
        holder.selectedCertificate.setOnCheckedChangeListener(null); // Prevent triggering listener
        holder.selectedCertificate.setChecked(selectedCertificates.contains(certificate));

        holder.selectedCertificate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedCertificates.contains(certificate)) {
                    selectedCertificates.add(certificate);
                }
            } else {
                selectedCertificates.remove(certificate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    public List<Certificate> getSelectedCertificates() {
        return selectedCertificates; // Return the list of selected certificates
    }

    public Certificate getSelectedCertificate() {
        // Return the first selected certificate, or null if none is selected
        return selectedCertificates.isEmpty() ? null : selectedCertificates.get(0);
    }

    public void clearSelections() {
        selectedCertificates.clear(); // Clear the selected certificates
        notifyDataSetChanged(); // Refresh the adapter
    }

    // Method to update the certificate list
    public void updateCertificateList(List<Certificate> newCertificateList) {
        this.certificates = newCertificateList;
        clearSelections();
        notifyDataSetChanged();
    }

    static class CertificateViewHolder extends RecyclerView.ViewHolder {
        private TextView certificateTitle;
        private TextView certificateIssuedBy;
        private TextView certificateDateIssued;
        private TextView certificateScore;
        private TextView remark;
        private CheckBox selectedCertificate;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            certificateTitle = itemView.findViewById(R.id.certificateName);
            certificateIssuedBy = itemView.findViewById(R.id.issuedBy); // Updated ID
            certificateDateIssued = itemView.findViewById(R.id.dateIssued); // Updated ID
            certificateScore = itemView.findViewById(R.id.score); // Updated ID
            remark = itemView.findViewById(R.id.remarks); // Updated ID
            selectedCertificate = itemView.findViewById(R.id.certificateCheckBox);
        }

        public void bind(Certificate certificate) {
            // Set the text with formatted strings
            certificateTitle.setText("Certificate Name: " + certificate.getName());
            certificateIssuedBy.setText("Issued By: " + certificate.getIssuedBy());
            certificateDateIssued.setText("Date Issued: " + certificate.getDateIssued());
            certificateScore.setText("Score: " + certificate.getScore());

            // Handle null remarks
            String remarksText = certificate.getRemarks() != null ? certificate.getRemarks() : "No remarks available";
            remark.setText("Remarks: " + remarksText);
        }
    }
}