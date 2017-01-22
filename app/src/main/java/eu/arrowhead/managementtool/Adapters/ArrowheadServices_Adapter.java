package eu.arrowhead.managementtool.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.activities.ArrowheadService_Detail;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.utility.Utility;

public class ArrowheadServices_Adapter extends RecyclerView.Adapter<ArrowheadServices_Adapter.ServiceViewHolder>{

    private List<ArrowheadService> serviceList;
    private Context context;

    public ArrowheadServices_Adapter(List<ArrowheadService> serviceList) {
        this.serviceList = serviceList;
    }

    public void setServiceList(List<ArrowheadService> serviceList){
        this.serviceList = serviceList;
        notifyDataSetChanged();
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_arrowhead_services, parent, false);
        context = parent.getContext();
        return new ServiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        final ArrowheadService service = serviceList.get(position);
        holder.vServiceName.setText(service.getServiceDefinition());
        holder.vServiceGroup.setText(service.getServiceGroup());

        holder.vCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadService_Detail.class);
                intent.putExtra("arrowhead_service", service);
                context.startActivity(intent);
            }
        });

        holder.vServiceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadService_Detail.class);
                intent.putExtra("arrowhead_service", service);
                context.startActivity(intent);
            }
        });

        holder.vServiceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadService_Detail.class);
                intent.putExtra("arrowhead_service", service);
                context.startActivity(intent);
            }
        });

        holder.vServiceName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utility.showHelperToast(context, "Service name");
                return true;
            }
        });

        holder.vServiceGroup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utility.showHelperToast(context, "Service group");
                return true;
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

        ServiceViewHolder(View v){
            super(v);
            vCardView = (CardView) v.findViewById(R.id.service_card_view);
            vServiceName = (TextView) v.findViewById(R.id.service_name);
            vServiceGroup = (TextView) v.findViewById(R.id.service_group);
        }
    }
}
