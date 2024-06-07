package com.example.mirhatlising.mainPages.callsHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mirhatlising.R;

import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {

    private Context context;
    private List<Call> items;

    public CallAdapter(Context context, List<Call> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallViewHolder(LayoutInflater.from(context).inflate(R.layout.call_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {

        Call currentCall = items.get(position);
        holder.callNumber.setText(currentCall.getNumberOfCaller());
        holder.dateOfCall.setText(currentCall.getDateOfCall());
        holder.typeOfTruck.setText("Тип: " + currentCall.getTypeOfTruck());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CallViewHolder extends RecyclerView.ViewHolder {

        TextView callNumber, dateOfCall, typeOfTruck;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);

            callNumber = itemView.findViewById(R.id.callItemNumber);
            dateOfCall = itemView.findViewById(R.id.callItemDateOfCall);
            typeOfTruck = itemView.findViewById(R.id.callItemTypeOfTruck);

        }
    }

}
