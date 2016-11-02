package eu.arrowhead.managementtool.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.R;

public class ArrowheadService_Interfaces_Adapter extends RecyclerView.Adapter<ArrowheadService_Interfaces_Adapter.InterfaceViewHolder>{

    private List<String> interfaceList = new ArrayList<>();

    public ArrowheadService_Interfaces_Adapter(List<String> interfaceList) {
        this.interfaceList = interfaceList;
    }

    @Override
    public InterfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_arrowhead_service_interfaces, parent, false);
        return new ArrowheadService_Interfaces_Adapter.InterfaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InterfaceViewHolder holder, int position) {
        String interFace = interfaceList.get(position); //interface is a keyword in java, cant be used as a variable name
        holder.vInterface.setText(interFace);
    }

    @Override
    public int getItemCount() {
        return interfaceList.size();
    }

    static class InterfaceViewHolder extends RecyclerView.ViewHolder {

        TextView vInterface;

        public InterfaceViewHolder(View v){
            super(v);
            vInterface = (TextView) v.findViewById(R.id.interface_name);
        }
    }
}
