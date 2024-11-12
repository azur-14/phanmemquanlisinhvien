package com.example.giuaky;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {
    private List<Certificate> certificates;

    public CertificateAdapter(List<Certificate> certificates) {
        this.certificates = certificates;
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
        holder.certificateName.setText(certificate.getName());
        holder.issuedBy.setText("Issued By: " + certificate.getIssuedBy());
        holder.dateIssued.setText("Date Issued: " + certificate.getDateIssued());
        holder.score.setText("Score: " + certificate.getScore());
        holder.remarks.setText("Remarks: " + certificate.getRemarks());
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    static class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView certificateName;
        TextView issuedBy;
        TextView dateIssued;
        TextView score;
        TextView remarks;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            certificateName = itemView.findViewById(R.id.certificate_name);
            issuedBy = itemView.findViewById(R.id.certificate_issued_by);
            dateIssued = itemView.findViewById(R.id.certificate_date_issued);
            score = itemView.findViewById(R.id.certificate_score);
            remarks = itemView.findViewById(R.id.certificate_remarks);
        }
    }
}