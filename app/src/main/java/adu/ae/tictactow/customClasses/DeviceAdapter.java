package adu.ae.tictactow.customClasses;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import adu.ae.tictactow.R;
import adu.ae.tictactow.utils.Device;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private ArrayList<Device> deviceList;
    private OnitemClickListener onitemClickListener;



    public interface OnitemClickListener{

        void onItemClick(int position);
    }

    public void setOnitemClickListener(OnitemClickListener onitemClickListener){
        this.onitemClickListener = onitemClickListener;
    }

    public DeviceAdapter(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder{
        TextView deviceNameTextView;

        public DeviceViewHolder(@NonNull View itemView, final OnitemClickListener onitemClickListener) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.deviceNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onitemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onitemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_recycler_view,viewGroup,false);
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(view, onitemClickListener);
        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder deviceViewHolder, int i) {
        Device device = deviceList.get(i);
        deviceViewHolder.deviceNameTextView.setText(device.getName());

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
