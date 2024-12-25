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

package com.akapps.ecu.gui.application;

import javax.swing.Icon;

/**
 * @author erwin
 */
public class AboutPanel extends javax.swing.JPanel
{

	/** Serial version UID */
	private static final long serialVersionUID = -7733037825291372813L;

	/** Creates new form AboutPanel */
	public AboutPanel()
	{
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		lblImage = new javax.swing.JLabel();
		// Variables declaration - do not modify//GEN-BEGIN:variables
		javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
		lblName = new javax.swing.JLabel();
		lblVersion = new javax.swing.JLabel();
		lblCopyright = new javax.swing.JLabel();

		setLayout(new java.awt.BorderLayout());

		lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/akapps/ecu/gui/res/JaVAG_Logo.png"))); // NOI18N
		add(lblImage, java.awt.BorderLayout.CENTER);

		jPanel1.setLayout(new java.awt.GridLayout(0, 1));

		lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jPanel1.add(lblName);

		lblVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jPanel1.add(lblVersion);

		add(jPanel1, java.awt.BorderLayout.PAGE_START);

		lblCopyright.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(lblCopyright, java.awt.BorderLayout.PAGE_END);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * set icon for application about dialog
	 *
	 * @param icon icon to show
	 */
	public void setIcon(Icon icon)
	{
		lblImage.setIcon(icon);
	}

	/**
	 * set application name
	 *
	 * @param appName application name
	 */
	public void setApplicationName(String appName)
	{
		lblName.setText(appName);
	}

	/**
	 * set application version
	 *
	 * @param appVersion application version string
	 */
	public void setApplicationVersion(String appVersion)
	{
		lblVersion.setText(appVersion);
	}

	/**
	 * set copyright string
	 *
	 * @param cRightString copyright string
	 */
	public void setCopyrightString(String cRightString)
	{
		lblCopyright.setText(cRightString);
	}
	
	private javax.swing.JLabel lblCopyright;
	private javax.swing.JLabel lblImage;
	private javax.swing.JLabel lblName;
	private javax.swing.JLabel lblVersion;
	// End of variables declaration//GEN-END:variables

}
