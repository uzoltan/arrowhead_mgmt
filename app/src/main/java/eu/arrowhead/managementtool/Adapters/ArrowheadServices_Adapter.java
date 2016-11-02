package eu.arrowhead.managementtool.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.arrowhead.managementtool.Activities.ArrowheadService_Detail;
import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.model.ArrowheadService;

public class ArrowheadServices_Adapter extends RecyclerView.Adapter<ArrowheadServices_Adapter.ServiceViewHolder>{

    private List<ArrowheadService> serviceList;
    private ArrowheadService service;
    private Context context;

    public ArrowheadServices_Adapter(List<ArrowheadService> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_arrowhead_services, parent, false);
        context = parent.getContext();
        return new ServiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        service = serviceList.get(position);
        holder.vServiceGroup.setText(service.getServiceGroup());
        holder.vServiceName.setText(service.getServiceDefinition());

        holder.vCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadService_Detail.class);
                intent.putExtra("arrowhead_service", service);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        CardView vCardView;
        TextView vServiceName;
        TextView vServiceGroup;

        public ServiceViewHolder(View v){
            super(v);
            vCardView = (CardView) v.findViewById(R.id.service_card_view);
            vServiceName = (TextView) v.findViewById(R.id.service_name);
            vServiceGroup = (TextView) v.findViewById(R.id.service_group);
        }
    }
}
