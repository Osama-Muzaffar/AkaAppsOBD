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
 *
 */

package com.akapps.ecu;

/**
 * Internal int-based conversion for hex display
 */
public class IntConversion
	extends NumericConversion
{
	/** uid */
	private static final long serialVersionUID = -2551205550381391025L;

	@Override
	public Number memToPhys(long value)
	{
		return value;
	}

	@Override
	public Number physToMem(Number value)
	{
		return value;
	}

	@Override
	public String physToPhysFmtString(Number physVal, String format)
	{
		long val = physVal.longValue();
		return String.format(format, val);
	}
}
