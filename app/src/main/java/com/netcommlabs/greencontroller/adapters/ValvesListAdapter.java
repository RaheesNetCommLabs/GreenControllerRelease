package com.netcommlabs.greencontroller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netcommlabs.greencontroller.Fragments.FragDeviceDetails;
import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.model.ModalValveMaster;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;

import java.util.List;

/**
 * Created by Android on 11/1/2017.
 */

public class ValvesListAdapter extends RecyclerView.Adapter<ValvesListAdapter.MyViewHolder> {

    //private List<String> listValves;
    //ArrayList<MdlValveNameStateNdSelect> listModalValveProperties;
    private Context mContext;
    //private List<View> listViewsCollection;
    private DatabaseHandler databaseHandler;
    //private String dvcMacAdd;
    private ModalValveMaster modalBLEValve;
    private FragDeviceDetails fragDeviceDetails;
    //private int posiViewHolder = 0;
    //MdlValveNameStateNdSelect modalVlNameSelect1;
    private List<ModalValveMaster> listModalValveMaster;

    public ValvesListAdapter(Context mContext, FragDeviceDetails fragDeviceDetails, List<ModalValveMaster> listModalValveMaster) {
        this.mContext = mContext;
        //this.dvcMacAdd = dvcMacAdd;
        this.fragDeviceDetails = fragDeviceDetails;
        this.listModalValveMaster = listModalValveMaster;
        databaseHandler = DatabaseHandler.getInstance(mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llValveNameColor;
        TextView tvValveName;
        ImageView ivColorDot;

        public MyViewHolder(View itemView) {
            super(itemView);
            llValveNameColor = itemView.findViewById(R.id.llValveNameColor);
            tvValveName = itemView.findViewById(R.id.tvValveName);
            ivColorDot = itemView.findViewById(R.id.ivColorDot);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_valves_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ModalValveMaster modalValveMaster = listModalValveMaster.get(position);

        String vlvName = modalValveMaster.getValveName();
        int vlvSelected = modalValveMaster.getValveSelectStatus();
        String valveOpTpSPP = modalValveMaster.getValveOpTpSPP();

        holder.tvValveName.setText(vlvName);
        //modalBLEValve = databaseHandler.getValveSessionData(dvcMacAdd, vlvName);

        //final boolean isValveSelected = modalValveMaster.getValveSelected();

        if (vlvSelected == 1) {
            holder.llValveNameColor.setBackgroundResource(R.drawable.volve_bg_shadow_select);
        } else {
            holder.llValveNameColor.setBackgroundResource(R.drawable.volve_bg_shadow);
        }

        if (valveOpTpSPP.equals("PLAY")) {
            holder.ivColorDot.setBackgroundResource(R.drawable.circle_green);
        } else if (valveOpTpSPP.equals("PAUSE")) {
            holder.ivColorDot.setBackgroundResource(R.drawable.circle_yellow);
        } else if (valveOpTpSPP.equals("ERROR")) {
            //holder.ivColorDot.setBackgroundResource(R.drawable.circle_red);
            Toast.makeText(mContext, "Dot color should be red", Toast.LENGTH_SHORT).show();
        } else {
            //Valve State case "STOP"
            holder.ivColorDot.setBackgroundResource(R.drawable.circle_grey);
        }

        holder.llValveNameColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MdlValveNameStateNdSelect modalVlNameSelectClicked = FragDeviceDetails.listModalValveProperties.get(position);
                //String clickedVlvName = modalValveMaster.getValveName();
                //Converting all views FALSE
                for (int i = 0; i < listModalValveMaster.size(); i++) {
                    listModalValveMaster.get(i).setValveSelectStatus(0);
                    databaseHandler.updateValveSelectStatus(listModalValveMaster.get(i).getValveUUID(), 0);
                    //FragDeviceDetails.listModalValveProperties.get(i).setValveSelected(false);
                    //modalVlNameSelectFrEch.setValveSelected(false);
                    holder.llValveNameColor.setBackgroundResource(R.drawable.volve_bg_shadow);
                }
                //Item showing as selected
                //modalVlNameSelect1 = listModalValveProperties.get(position);
                int isValveSelected = modalValveMaster.getValveSelectStatus();
                if (isValveSelected == 0) {
                    listModalValveMaster.get(position).setValveSelectStatus(1);
                    databaseHandler.updateValveSelectStatus(listModalValveMaster.get(position).getValveUUID(), 1);
                }
                holder.llValveNameColor.setBackgroundResource(R.drawable.volve_bg_shadow_select);
                notifyDataSetChanged();
                //DB work for valve selection
                //modalBLEValve = databaseHandler.getValveSessionData(dvcMacAdd, clickedVlvName);
                //fragDeviceDetails.clickedPassDataToParent(modalBLEValve, clickedVlvName);
                fragDeviceDetails.checkValveOPTYAndGOFurther(position);

            }
        });

       /* if (clickedPosition==position){
            //---- First Item selected----
            listViewsCollection.get(clickedPosition).setBackgroundResource(R.drawable.volve_bg_shadow_select);
        }else {
            listViewsCollection.get(position).setBackgroundResource(R.drawable.volve_bg_shadow);
        }*/
    }

    @Override
    public int getItemCount() {
        return listModalValveMaster.size();
    }

}
