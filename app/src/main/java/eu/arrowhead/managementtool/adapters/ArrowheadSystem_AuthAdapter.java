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
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.model.SystemAuth_ListEntry;
import eu.arrowhead.managementtool.utility.Utility;

public class ArrowheadSystem_AuthAdapter extends
        ExpandableRecyclerAdapter<ArrowheadSystem_AuthAdapter.ServiceViewHolder, ArrowheadSystem_AuthAdapter.SystemViewHolder> {

    private LayoutInflater mInflator;
    private Context ctx;

    public ArrowheadSystem_AuthAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        ctx = context;
    }

    @Override
    public ServiceViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View serviceView = mInflator.inflate(R.layout.list_row_system_auth_parent, parentViewGroup, false);
        return new ServiceViewHolder(serviceView);
    }

    @Override
    public SystemViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View systemView = mInflator.inflate(R.layout.list_row_system_auth_child, childViewGroup, false);
        return new SystemViewHolder(systemView);
    }

    @Override
    public void onBindParentViewHolder(ServiceViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        SystemAuth_ListEntry entry = (SystemAuth_ListEntry) parentListItem;
        parentViewHolder.bind(entry);
    }

    @Override
    public void onBindChildViewHolder(SystemViewHolder childViewHolder, int position, Object childListItem) {
        ArrowheadSystem system = (ArrowheadSystem) childListItem;
        childViewHolder.bind(system, ctx);
    }

    static class ServiceViewHolder extends ParentViewHolder {

        TextView vServiceName;
        TextView vServiceGroup;

        ServiceViewHolder(View v){
            super(v);
            vServiceName = (TextView) v.findViewById(R.id.service_name);
            vServiceGroup = (TextView) v.findViewById(R.id.service_group);
        }

        void bind(SystemAuth_ListEntry entry) {
            vServiceName.setText(entry.getServiceDefition());
            vServiceGroup.setText(entry.getServiceGroup());
        }
    }

    static class SystemViewHolder extends ChildViewHolder {

        TextView vSystemName;
        TextView vSystemGroup;

        SystemViewHolder(View v){
            super(v);
            vSystemName = (TextView) v.findViewById(R.id.system_name);
            vSystemGroup = (TextView) v.findViewById(R.id.system_group);
        }

        void bind(ArrowheadSystem system, final Context ctx) {
            vSystemName.setText(system.getSystemName());
            vSystemGroup.setText(system.getSystemGroup());

            vSystemName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Utility.showHelperToast(ctx, "System name");
                    return true;
                }
            });

            vSystemGroup.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Utility.showHelperToast(ctx, "System group");
                    return true;
                }
            });
        }
    }
}
