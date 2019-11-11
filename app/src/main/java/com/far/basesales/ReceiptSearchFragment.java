package com.far.basesales;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.ReceiptAdapter;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptSearchFragment extends Fragment {

    Activity parentActivity;
    Spinner spnStatus;
    RecyclerView rvList;
    ProgressBar pb;
    ImageView imgSearch, imgHideSearch;
    LinearLayout llSearch;
    EditText etSearch;

    public ReceiptSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spnStatus = view.findViewById(R.id.spnStatus);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        imgSearch = view.findViewById(R.id.imgSearch);
        imgHideSearch = view.findViewById(R.id.imgHideSearch);
        llSearch = view.findViewById(R.id.llSearch);
        etSearch = view.findViewById(R.id.etSearch);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(etSearch.getText().toString().trim().equals("")){
                        return false;
                    }
                    imgHideSearch.performClick();
                    search(etSearch.getText().toString());

                    return true;

                }
                return false;
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                etSearch.setText("");
                Funciones.showKeyBoard(etSearch, parentActivity);
            }
        });

        imgHideSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones.hideKeyBoard(etSearch, parentActivity);
                llSearch.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.imgMenu).setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        search("");
    }

    public void setParentActivity(Activity activity){
        this.parentActivity = activity;
    }



    public void search(String text){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<ReceiptRowModel>> a = new AsyncTask<String, Void, ArrayList<ReceiptRowModel>>() {
            @Override
            protected ArrayList<ReceiptRowModel> doInBackground(String... strings) {
                //String value = strings[0];
                //String where = ClientsController.DOCUMENT+" like ? OR "+ ClientsController.NAME+" like ? OR "+ClientsController.PHONE+" like ? ";
                return ReceiptController.getInstance(parentActivity).getReceiptsRM("", new String[]{});
            }

            @Override
            protected void onPostExecute(ArrayList<ReceiptRowModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                pb.setVisibility(View.GONE);
                ReceiptAdapter adapter = new ReceiptAdapter(parentActivity, (ListableActivity) parentActivity, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute(text);

    }

}
