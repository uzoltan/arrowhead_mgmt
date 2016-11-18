package eu.arrowhead.managementtool.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.model.OrchestrationStore;
import eu.arrowhead.managementtool.model.SystemStoreBased_ListEntry;

public class ArrowheadSystem_StoreBasedAdapter extends
        ExpandableRecyclerAdapter<ArrowheadSystem_StoreBasedAdapter.ServiceViewHolder, ArrowheadSystem_StoreBasedAdapter.DetailsViewHolder> {

    private LayoutInflater mInflator;
    private Context context;

    public ArrowheadSystem_StoreBasedAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.context = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public ArrowheadSystem_StoreBasedAdapter.ServiceViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View serviceView = mInflator.inflate(R.layout.list_row_system_store_based_parent, parentViewGroup, false);
        return new ArrowheadSystem_StoreBasedAdapter.ServiceViewHolder(serviceView);
    }

    @Override
    public ArrowheadSystem_StoreBasedAdapter.DetailsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View detailsView = mInflator.inflate(R.layout.list_row_system_store_based_child, childViewGroup, false);
        return new ArrowheadSystem_StoreBasedAdapter.DetailsViewHolder(detailsView);
    }

    //TODO add long click listeners to the views, explaning what is the value
    @Override
    public void onBindParentViewHolder(ArrowheadSystem_StoreBasedAdapter.ServiceViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        SystemStoreBased_ListEntry entry = (SystemStoreBased_ListEntry) parentListItem;
        parentViewHolder.bind(entry);
    }

    @Override
    public void onBindChildViewHolder(ArrowheadSystem_StoreBasedAdapter.DetailsViewHolder childViewHolder, int position, Object childListItem) {
        OrchestrationStore storeEntry = (OrchestrationStore) childListItem;
        childViewHolder.bind(storeEntry);
    }

    static class ServiceViewHolder extends ParentViewHolder {

        TextView vServiceName;
        TextView vServiceGroup;

        ServiceViewHolder(View v){
            super(v);
            vServiceName = (TextView) v.findViewById(R.id.service_name);
            vServiceGroup = (TextView) v.findViewById(R.id.service_group);
        }

        void bind(SystemStoreBased_ListEntry entry) {
            vServiceName.setText(entry.getServiceDefition());
            vServiceGroup.setText(entry.getServiceGroup());
        }
    }

    class DetailsViewHolder extends ChildViewHolder {

        TextView vSystemName;
        TextView vSystemGroup;
        TextView vOrchRule;
        TextView vPriority;
        TextView vCloudName;
        TextView vOperator;

        DetailsViewHolder(View v){
            super(v);
            vSystemName = (TextView) v.findViewById(R.id.system_name);
            vSystemGroup = (TextView) v.findViewById(R.id.system_group);
            vOrchRule = (TextView) v.findViewById(R.id.orchestration_rule);
            vPriority = (TextView) v.findViewById(R.id.priority);
            vCloudName = (TextView) v.findViewById(R.id.cloud_name);
            vOperator = (TextView) v.findViewById(R.id.operator);
        }

        void bind(OrchestrationStore storeEntry) {
            if(storeEntry.isConsumerSide()){
                vSystemName.setText(storeEntry.getProviderSystem().getSystemName());
                vSystemGroup.setText(storeEntry.getProviderSystem().getSystemGroup());
            }
            else{
                vSystemName.setText(storeEntry.getConsumer().getSystemName());
                vSystemGroup.setText(storeEntry.getConsumer().getSystemGroup());
            }
            vOrchRule.setText(storeEntry.getOrchestrationRule());
            vPriority.setText(context.getString(R.string.priority) + " " + storeEntry.getPriority().toString());
            if(storeEntry.getProviderCloud() != null){
                vCloudName.setText(storeEntry.getProviderCloud().getCloudName());
                vOperator.setText(storeEntry.getProviderCloud().getOperator());
            }
            else{
                vCloudName.setVisibility(View.GONE);
                vOperator.setVisibility(View.GONE);
            }
        }
    }
}
