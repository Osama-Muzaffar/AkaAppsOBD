package com.akapps.obd2carscannerapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.akapps.pvs.PvChangeEvent;
import com.akapps.pvs.PvList;

/**
 * Data adapter for plugin-provided data
 */
public class PluginDataAdapter extends ObdItemAdapter
{

	public PluginDataAdapter(Context context, int resource, PvList pvs)
	{
		super(context, resource, pvs);
		pvs.addPvChangeListener(this,
								PvChangeEvent.PV_ADDED | PvChangeEvent.PV_DELETED | PvChangeEvent.PV_CLEARED);
	}

	@Override
	public synchronized void setPvList(PvList pvs)
	{
		this.pvs = pvs;
		clear();
		// add all elements
		addAll(pvs.values());
		// add data series for each element
		addAllDataSeries();
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return super.getView(position, convertView, parent);
    }
}
