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
import eu.arrowhead.managementtool.activities.ArrowheadCloud_Detail;
import eu.arrowhead.managementtool.model.ArrowheadCloud;

public class ArrowheadClouds_Adapter extends RecyclerView.Adapter<ArrowheadClouds_Adapter.CloudViewHolder>{

    private List<ArrowheadCloud> cloudList;
    private Context context;

    public ArrowheadClouds_Adapter(List<ArrowheadCloud> cloudList) {
        this.cloudList = cloudList;
    }

    public void setCloudList(List<ArrowheadCloud> cloudList){
        this.cloudList = cloudList;
        notifyDataSetChanged();
    }

    @Override
    public CloudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_arrowhead_clouds, parent, false);
        context = parent.getContext();
        return new ArrowheadClouds_Adapter.CloudViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CloudViewHolder holder, int position) {
        final ArrowheadCloud cloud = cloudList.get(position);
        holder.vOperator.setText(cloud.getOperator());
        holder.vCloudName.setText(cloud.getCloudName());

        holder.vCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArrowheadCloud_Detail.class);
                intent.putExtra("arrowhead_cloud", cloud);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cloudList.size();
    }

    static class CloudViewHolder extends RecyclerView.ViewHolder {

        CardView vCardView;
        TextView vCloudName;
        TextView vOperator;

        CloudViewHolder(View v){
            super(v);
            vCardView = (CardView) v.findViewById(R.id.cloud_card_view);
            vCloudName = (TextView) v.findViewById(R.id.cloud_name);
            vOperator = (TextView) v.findViewById(R.id.operator);
        }
    }
}
