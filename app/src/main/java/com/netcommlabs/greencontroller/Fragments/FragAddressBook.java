package com.netcommlabs.greencontroller.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netcommlabs.greencontroller.R;
import com.netcommlabs.greencontroller.activities.MainActivity;
import com.netcommlabs.greencontroller.adapters.AdapterAddressBook;
import com.netcommlabs.greencontroller.model.ModalAddressModule;
import com.netcommlabs.greencontroller.sqlite_db.DatabaseHandler;
import com.netcommlabs.greencontroller.utilities.Constant;

import java.util.List;

/**
 * Created by Android on 12/7/2017.
 */

public class FragAddressBook extends Fragment {

    private MainActivity mContext;
    private RecyclerView gridRecyclerView;
    private AdapterAddressBook adapterAddressBook;
    private TextView tvNoAddressAvailable;
    private List<ModalAddressModule> listModalAddressModule;
    private DatabaseHandler databaseHandler;
    private LinearLayout llAddNewAddress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_address_book, null);

        findViews(view);
        initViewsAndListeners();
        return view;
    }

    private void findViews(View view) {
        this.llAddNewAddress = mContext.llAddNewAddress;
        gridRecyclerView = view.findViewById(R.id.gridRecyclerView);
        tvNoAddressAvailable = view.findViewById(R.id.tvNoAddressAvailable);
    }

    private void initViewsAndListeners() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridRecyclerView.setLayoutManager(gridLayoutManager);

        databaseHandler = DatabaseHandler.getInstance(mContext);
        listModalAddressModule = databaseHandler.getAddressListFormData(0);
        if (listModalAddressModule.size() > 0) {
            tvNoAddressAvailable.setVisibility(View.GONE);
            gridRecyclerView.setVisibility(View.VISIBLE);
            adapterAddressBook = new AdapterAddressBook(mContext, listModalAddressModule);
            gridRecyclerView.setAdapter(adapterAddressBook);
        } else {
            //In case of no address available
            tvNoAddressAvailable.setVisibility(View.VISIBLE);
            gridRecyclerView.setVisibility(View.GONE);
        }

        llAddNewAddress.setVisibility(View.VISIBLE);
        llAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFragmentTransactions.replaceFragment(mContext, new FragAddEditAddress(), Constant.ADD_ADDRESS, mContext.frm_lyt_container_int, true);
            }
        });
    }
}