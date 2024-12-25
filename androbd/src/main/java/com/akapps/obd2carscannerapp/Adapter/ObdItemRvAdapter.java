package com.akapps.obd2carscannerapp.Adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akapps.androbd.plugin.mgr.PluginManager;
import com.akapps.obd2carscannerapp.ColorAdapter;
import com.akapps.obd2carscannerapp.R;
import com.akapps.ecu.EcuDataPv;
import com.akapps.pvs.IndexedProcessVar;
import com.akapps.pvs.PvChangeEvent;
import com.akapps.pvs.PvChangeListener;
import com.akapps.pvs.PvList;

import org.achartengine.model.XYSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObdItemRvAdapter extends RecyclerView.Adapter<ObdItemRvAdapter.MyViewHolder>{
    private PvList pvs;
    private  LayoutInflater mInflater;
    private static final String FID_DATA_SERIES = "SERIES";
    public static boolean allowDataUpdates = true;
    private  SharedPreferences prefs;

    public ObdItemRvAdapter(Context context, PvList pvs) {
        this.pvs = pvs;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.mInflater = LayoutInflater.from(context);
        setPvList(pvs);
    }

    public void setPvList(PvList pvs) {
        this.pvs = pvs;
        Collection<Object> filtered = getPreferredItems(pvs);
        List<Object> pidPvs = new ArrayList<>(filtered);
        Log.d("ObdItemAdapter", "setPvList: size= "+pidPvs.size());
        Collections.sort(pidPvs, pidSorter);
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.obd_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EcuDataPv currPv = (EcuDataPv) pvs.get(position);

        if (currPv!=null) {
            holder.tvDescr.setText(String.valueOf(currPv.get(EcuDataPv.FID_DESCRIPT)));
            holder.tvValue.setText(formatText(currPv));
            holder.tvUnits.setText(currPv.getUnits());

            Number min = (Number) currPv.get(EcuDataPv.FID_MIN);
            Number max = (Number) currPv.get(EcuDataPv.FID_MAX);
            holder.updateProgressBar(currPv, min, max);
        }
    }
    @Override
    public int getItemCount() {
        return pvs.size();
    }

//    @Override
//    public void pvChanged(PvChangeEvent event) {
//        // handle data list updates
//        switch (event.getType())
//        {
//            case PvChangeEvent.PV_ADDED:
//                PvList pvList = (PvList)event.getSource();
//                pvs.clear();
//                pvs= pvList;
//                break;
//
//            case PvChangeEvent.PV_DELETED:
//                pvs.remove(event.getValue());
//                break;
//
//            case PvChangeEvent.PV_CLEARED:
//                pvs.clear();
//                break;
//        }
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescr;
        TextView tvValue;
        TextView tvUnits;
        ProgressBar pb;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescr = itemView.findViewById(R.id.obd_label);
            tvValue = itemView.findViewById(R.id.obd_value);
            tvUnits = itemView.findViewById(R.id.obd_units);
            pb = itemView.findViewById(R.id.bar);
        }
        public void updateProgressBar(EcuDataPv pv, Number min, Number max) {
            Object colVal = pv.get(EcuDataPv.FID_VALUE);
            int pidColor = ColorAdapter.getItemColor(pv);

            if (min != null && max != null && colVal instanceof Number) {
                pb.setVisibility(ProgressBar.VISIBLE);
                pb.getProgressDrawable().setColorFilter(pidColor, PorterDuff.Mode.SRC_IN);
                pb.setProgress((int) (100 * ((((Number) colVal).doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue()))));
            } else {
                pb.setVisibility(ProgressBar.GONE);
            }
        }
    }

    private String formatText(EcuDataPv currPv) {
        // Implement your formatting logic here
        return String.valueOf(currPv.get(EcuDataPv.FID_VALUE));
    }
    private static final Comparator<Object> pidSorter = new Comparator<Object>() {
        @Override
        public int compare(Object lhs, Object rhs) {
            if (lhs instanceof IndexedProcessVar && rhs instanceof IndexedProcessVar) {
                IndexedProcessVar leftPv = (IndexedProcessVar) lhs;
                IndexedProcessVar rightPv = (IndexedProcessVar) rhs;
                // Compare by ID string
                int result = leftPv.toString().compareTo(rightPv.toString());
                if (result == 0) {
                    // If IDs are the same, compare by description
                    result = String.valueOf(leftPv.get(EcuDataPv.FID_DESCRIPT))
                            .compareTo(String.valueOf(rightPv.get(EcuDataPv.FID_DESCRIPT)));
                }
                return result;
            }
            return 0;
        }
    };

    // Get preferred items based on preferences
    /*private Collection<Object> getPreferredItems(PvList pvs) {
        Set<String> pidsToShow = prefs.getStringSet(SettingsActivity.KEY_DATA_ITEMS, (Set<String>) pvs.keySet());
        Log.d("Pdsshow", "pds to shwo: "+pidsToShow);
        return getMatchingItems(pvs, pidsToShow);
    }*/
    Collection getPreferredItems(PvList pvs) {
        // Get set of String keys from preferences
        Set<String> pidsToShow = new HashSet<>();
        for (Object key : pvs.keySet()) {
            pidsToShow.add(String.valueOf(key));
        }
        // Now pidsToShow contains keys as Strings
//        pidsToShow = prefs.getStringSet(SettingsActivity.KEY_DATA_ITEMS, pidsToShow);
        return getMatchingItems(pvs, pidsToShow);
    }

    // Filter items based on preferences
    private Collection<Object> getMatchingItems(PvList pvs, Set<String> pidsToShow) {
        List<Object> filtered = new ArrayList<>();
        for (String key : pidsToShow) {
            IndexedProcessVar pv = (IndexedProcessVar) pvs.get(key);
            if (pv != null) {
                filtered.add(pv);
            }
        }
        return filtered;
    }

    // Filter positions
    public void filterPositions(int[] positions) {
        List<EcuDataPv> filtered = new ArrayList<>();
        for (int pos : positions) {
            filtered.add((EcuDataPv) pvs.get(pos));
        }
        pvs.clear();
        addAllDataSeries();
        notifyDataSetChanged();
    }

    // Handle view holder

    // Get view type count
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    protected synchronized void addAllDataSeries()
    {
        StringBuilder pluginStr = new StringBuilder();
        for (int pos = 0; pos < pvs.size(); pos++)
        {
            IndexedProcessVar pv = (IndexedProcessVar)pvs.get(pos);
            XYSeries series = (XYSeries) pv.get(FID_DATA_SERIES);
            if (series == null)
            {
                series = new XYSeries(String.valueOf(pv.get(EcuDataPv.FID_DESCRIPT)));
                pv.put(FID_DATA_SERIES, series);
                pv.addPvChangeListener(dataChangeHandler, PvChangeEvent.PV_MODIFIED);
            }

            // assemble data items for plugin notification
            pluginStr.append(String.format("%s;%s;%s;%s\n",
                    pv.get(EcuDataPv.FID_MNEMONIC),
                    pv.get(EcuDataPv.FID_DESCRIPT),
                    String.valueOf(pv.get(EcuDataPv.FID_VALUE)),
                    pv.get(EcuDataPv.FID_UNITS)
            ));
        }

        // notify plugins
        if (PluginManager.pluginHandler != null)
        {
            PluginManager.pluginHandler.sendDataList(pluginStr.toString());
        }
    }
    PvChangeListener dataChangeHandler = new PvChangeListener()
    {
        @Override
        public void pvChanged(PvChangeEvent event)
        {
            // handle data item updates
            if (allowDataUpdates)
            {
                IndexedProcessVar pv = (IndexedProcessVar) event.getSource();
                XYSeries series = (XYSeries) pv.get(FID_DATA_SERIES);
                if (series != null)
                {
                    if (event.getValue() instanceof Number)
                    {
                        series.add(event.getTime(),
                                ((Number) event.getValue()).doubleValue());

                    }
                }

                // send update to plugin handler
                if (PluginManager.pluginHandler != null)
                {
                    PluginManager.pluginHandler.sendDataUpdate(
                            pv.get(EcuDataPv.FID_MNEMONIC).toString(),
                            event.getValue().toString());
                }
            }
        }
    };
}
