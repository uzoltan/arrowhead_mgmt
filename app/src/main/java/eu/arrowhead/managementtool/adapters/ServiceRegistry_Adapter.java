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
import eu.arrowhead.managementtool.model.ServiceRegChild_ListEntry;
import eu.arrowhead.managementtool.model.ServiceRegParent_ListEntry;

public class ServiceRegistry_Adapter extends
        ExpandableRecyclerAdapter<ServiceRegistry_Adapter.ProviderViewHolder, ServiceRegistry_Adapter.ServiceViewHolder> {

    private LayoutInflater mInflator;

    public ServiceRegistry_Adapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public ProviderViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View providerView = mInflator.inflate(R.layout.list_row_service_registry_parent, parentViewGroup, false);
        return new ServiceRegistry_Adapter.ProviderViewHolder(providerView);
    }

    @Override
    public ServiceViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View serviceView = mInflator.inflate(R.layout.list_row_service_registry_child, childViewGroup, false);
        return new ServiceRegistry_Adapter.ServiceViewHolder(serviceView);
    }

    @Override
    public void onBindParentViewHolder(ProviderViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ServiceRegParent_ListEntry entry = (ServiceRegParent_ListEntry) parentListItem;
        parentViewHolder.bind(entry);
    }

    @Override
    public void onBindChildViewHolder(ServiceViewHolder childViewHolder, int position, Object childListItem) {
        ServiceRegChild_ListEntry entry = (ServiceRegChild_ListEntry) childListItem;
        childViewHolder.bind(entry);
    }

    static class ProviderViewHolder extends ParentViewHolder {

        TextView vSystemName;
        TextView vSystemGroup;

        ProviderViewHolder(View v){
            super(v);
            vSystemName = (TextView) v.findViewById(R.id.system_name);
            vSystemGroup = (TextView) v.findViewById(R.id.system_group);
        }

        void bind(ServiceRegParent_ListEntry entry) {
            vSystemName.setText(entry.getSystemName());
            vSystemGroup.setText(entry.getSystemGroup());
        }
    }

    class ServiceViewHolder extends ChildViewHolder {

        TextView vServiceName;
        TextView vServiceGroup;
        TextView vServiceUri;

        ServiceViewHolder(View v){
            super(v);
            vServiceName = (TextView) v.findViewById(R.id.service_name);
            vServiceGroup = (TextView) v.findViewById(R.id.service_group);
            vServiceUri = (TextView) v.findViewById(R.id.service_uri);
        }

        void bind(ServiceRegChild_ListEntry entry) {
            vServiceName.setText(entry.getServiceDefinition());
            vServiceGroup.setText(entry.getServiceGroup());
            vServiceUri.setText(entry.getServiceUri());
        }
    }
}
