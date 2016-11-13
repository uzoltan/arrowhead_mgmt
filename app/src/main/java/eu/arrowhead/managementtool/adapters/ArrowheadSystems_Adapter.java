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
import eu.arrowhead.managementtool.activities.ArrowheadSystem_Detail;
import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class ArrowheadSystems_Adapter extends RecyclerView.Adapter<ArrowheadSystems_Adapter.SystemViewHolder>{

    private List<ArrowheadSystem> systemList;
    private Context context;

    public ArrowheadSystems_Adapter(List<ArrowheadSystem> systemList) {
        this.systemList = systemList;
    }

    public void setSystemList(List<ArrowheadSystem> systemList){
        this.systemList = systemList;
    }

    @Override
    public SystemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_arrowhead_systems, parent, false);
        context = parent.getContext();
        return new SystemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SystemViewHolder holder, int position) {
        final ArrowheadSystem system = systemList.get(position);
        holder.vSystemGroup.setText(system.getSystemGroup());
        holder.vSystemName.setText(system.getSystemName());

        holder.vCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadSystem_Detail.class);
                intent.putExtra("arrowhead_system", system);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return systemList.size();
    }

    static class SystemViewHolder extends RecyclerView.ViewHolder {

        CardView vCardView;
        TextView vSystemName;
        TextView vSystemGroup;

        public SystemViewHolder(View v){
            super(v);
            vCardView = (CardView) v.findViewById(R.id.system_card_view);
            vSystemName = (TextView) v.findViewById(R.id.system_name);
            vSystemGroup = (TextView) v.findViewById(R.id.system_group);
        }
    }
}
