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
import eu.arrowhead.managementtool.activities.CoreSystem_Detail;
import eu.arrowhead.managementtool.model.CoreSystem;

public class CoreSystems_Adapter extends RecyclerView.Adapter<CoreSystems_Adapter.SystemViewHolder>{

    private List<CoreSystem> systemList;
    private Context context;

    public CoreSystems_Adapter(List<CoreSystem> systemList) {
        this.systemList = systemList;
    }

    public void setSystemList(List<CoreSystem> systemList){
        this.systemList = systemList;
        notifyDataSetChanged();
    }

    @Override
    public SystemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_core_systems, parent, false);
        context = parent.getContext();
        return new CoreSystems_Adapter.SystemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SystemViewHolder holder, int position) {
        final CoreSystem system = systemList.get(position);
        holder.vSystemName.setText(system.getSystemName());
        holder.vAddress.setText(system.getAddress());

        holder.vCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CoreSystem_Detail.class);
                intent.putExtra("core_system", system);
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
        TextView vAddress;

        SystemViewHolder(View v){
            super(v);
            vCardView = (CardView) v.findViewById(R.id.system_card_view);
            vSystemName = (TextView) v.findViewById(R.id.system_name);
            vAddress = (TextView) v.findViewById(R.id.address);
        }
    }
}
