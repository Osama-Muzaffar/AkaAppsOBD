/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.akapps.obd2carscannerapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akapps.ecu.EcuCodeItem;
import com.akapps.ecu.prot.obd.ObdProt;
import com.akapps.pvs.IndexedProcessVar;
import com.akapps.pvs.PvList;

import java.util.Collection;
import java.util.Objects;

/**
 * Adapter to display OBD DFCs
 *
 * @author erwin
 */
public class DfcItemAdapter extends ObdItemAdapter
{
	public DfcItemAdapter(Context context, int resource, PvList pvs)
	{
		super(context, resource, pvs);
	}

	@Override
	public Collection<Object> getPreferredItems(PvList pvs)
	{
		return pvs.values();
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.ecu.gui.androbd.ObdItemAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View v, ViewGroup parent)
	{
		// get data PV
		IndexedProcessVar currPv = (IndexedProcessVar) getItem(position);
		Log.d("performFiltering", "item dfc= "+currPv);

		if (v == null)
		{
			v = mInflater.inflate(R.layout.obd_item, parent, false);
		}

		// Show icon
		ImageView ivIcon = v.findViewById(R.id.obd_icon);
		ivIcon.setImageResource(android.R.drawable.ic_menu_myplaces);
		LinearLayout bottomlinear = v.findViewById(R.id.bottomlinear);
//		bottomlinear.setVisibility(View.GONE);

		try
		{
			// Show icon based on code status (pending/permanent/normal)
			Integer svc = (Integer)currPv.get(EcuCodeItem.FID_STATUS);
			switch(svc)
			{
				case ObdProt.OBD_SVC_PENDINGCODES:
					ivIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
					break;

				case ObdProt.OBD_SVC_PERMACODES:
					ivIcon.setImageResource(android.R.drawable.ic_dialog_alert);
					break;
			}
		}
		catch(Exception ex) { /* ignore */ }

		ivIcon.setVisibility(View.VISIBLE);

		TextView tvDescr = v.findViewById(R.id.obd_label);
		TextView tvValue = v.findViewById(R.id.obd_units);

		tvValue.setText(String.valueOf(Objects.requireNonNull(currPv).get(EcuCodeItem.FID_CODE)));
		tvDescr.setText(String.valueOf(currPv.get(EcuCodeItem.FID_DESCRIPT)));

		Log.d("performFiltering", "desciption dfc "+currPv.get(EcuCodeItem.FID_DESCRIPT));

		return v;
	}

	/* (non-Javadoc)
	 * @see com.fr3ts0n.ecu.gui.androbd.ObdItemAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View v, ViewGroup parent)
	{
		return getView(position, v, parent);
	}
}
