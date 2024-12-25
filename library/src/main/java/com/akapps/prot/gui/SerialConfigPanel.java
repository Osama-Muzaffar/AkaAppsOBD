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

package com.akapps.prot.gui;

import java.awt.event.ItemEvent;
import java.util.Enumeration;

/**
 * Configuration GUI-Panel for RXTX Serial port object
 *
 * @author erwin
 */
@SuppressWarnings("unchecked")
public class SerialConfigPanel
	extends javax.swing.JPanel
{

	private static final long serialVersionUID = -8518189691304679418L;
	/** allow direct port changes from dialog changes? */
	private static final boolean doPortUpdates = true;
	/** ist the dialog initialized? */
	private boolean initialized = false;

	/** Creates new form SerialConfigPanel */
	public SerialConfigPanel()
	{
		initComponents();
		initPortNames();
	}

	/** Creates new form SerialConfigPanel */
	public SerialConfigPanel(SerialPort serPort)
	{
		initComponents();
		initPortNames();
		setPort(serPort);
	}

	/**
	 * initialize list of available port names for combo box
	 */
	private void initPortNames()
	{
		initialized = false;
		cbPort.addItem("");
		// initialize port selection
		CommPortIdentifier currPort;
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements())
		{
			currPort = (CommPortIdentifier) ports.nextElement();
			if (currPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				cbPort.addItem(currPort.getName());
			}
		}
		initialized = true;
	}

	/**
	 * Initialize all port parameters PORT -&gt; DIALOG
	 */
	private void initPortParameters(SerialPort port)
	{
		initialized = false;
		if (port != null)
		{
			cbPort.setSelectedItem(port.getName());
			cbBaudrate.setSelectedItem(String.valueOf(port.getBaudRate()));
			cbParity.setSelectedIndex(port.getParity());
			cbDataBits.setSelectedIndex(port.getDataBits() - 5);
			cbStopBits.setSelectedIndex(port.getStopBits() - 1);
			// flow control combo's
			int prt = port.getFlowControlMode();
			cbProtocolRx.setSelectedIndex((prt & SerialPort.FLOWCONTROL_RTSCTS_IN) != 0 ? 1 : (prt & SerialPort.FLOWCONTROL_XONXOFF_IN) != 0 ? 2 : 0);
			cbProtocolTx.setSelectedIndex((prt & SerialPort.FLOWCONTROL_RTSCTS_OUT) != 0 ? 1 : (prt & SerialPort.FLOWCONTROL_XONXOFF_OUT) != 0 ? 2 : 0);
			btnDSR.setSelected(port.isDSR());
			btnCTS.setSelected(port.isCTS());
			btnCD.setSelected(port.isCD());
			btnDTR.setSelected(port.isDTR());
			btnRTS.setSelected(port.isRTS());
		}
		initialized = true;
	}

	/**
	 * Initialize all port parameters DIALOG -&gt; PORT
	 */
	private void updatePortParameters(SerialPort port)
	{
		if (initialized && port != null)
		{
			try
			{
				port.setSerialPortParams(Integer.valueOf(cbBaudrate.getSelectedItem().toString()).intValue(),
					cbDataBits.getSelectedIndex() + 5,
					cbStopBits.getSelectedIndex() + 1,
					cbParity.getSelectedIndex());
				// flow control parameters
				int mode = 0;
				mode |= (cbProtocolRx.getSelectedIndex() == 1) ? SerialPort.FLOWCONTROL_RTSCTS_IN : (cbProtocolRx.getSelectedIndex() == 2) ? SerialPort.FLOWCONTROL_XONXOFF_IN : 0;
				mode |= (cbProtocolTx.getSelectedIndex() == 1) ? SerialPort.FLOWCONTROL_RTSCTS_OUT : (cbProtocolTx.getSelectedIndex() == 2) ? SerialPort.FLOWCONTROL_XONXOFF_OUT : 0;
				port.setFlowControlMode(mode);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;
		
		javax.swing.JPanel panData = new javax.swing.JPanel();
		javax.swing.JLabel lblPort = new javax.swing.JLabel();
		cbPort = new javax.swing.JComboBox();
		javax.swing.JLabel lblBaudrate = new javax.swing.JLabel();
		cbBaudrate = new javax.swing.JComboBox();
		javax.swing.JLabel lblDataBits = new javax.swing.JLabel();
		cbDataBits = new javax.swing.JComboBox();
		javax.swing.JLabel lblStopBits = new javax.swing.JLabel();
		cbStopBits = new javax.swing.JComboBox();
		javax.swing.JLabel lblParity = new javax.swing.JLabel();
		cbParity = new javax.swing.JComboBox();
		javax.swing.JLabel lblHsRx = new javax.swing.JLabel();
		javax.swing.JLabel lblHsTx = new javax.swing.JLabel();
		cbProtocolRx = new javax.swing.JComboBox();
		cbProtocolTx = new javax.swing.JComboBox();
		javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
		btnDSR = new javax.swing.JToggleButton();
		btnCTS = new javax.swing.JToggleButton();
		btnCD = new javax.swing.JToggleButton();
		btnDTR = new javax.swing.JToggleButton();
		btnRTS = new javax.swing.JToggleButton();

		setLayout(new java.awt.BorderLayout());

		panData.setLayout(new java.awt.GridBagLayout());

		lblPort.setFont(new java.awt.Font("Dialog", 0, 10));
		lblPort.setLabelFor(cbPort);
		lblPort.setText("Port");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblPort, gridBagConstraints);

		cbPort.setFont(new java.awt.Font("Dialog", 0, 10));
		cbPort.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				cbPortItemStateChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		panData.add(cbPort, gridBagConstraints);

		lblBaudrate.setFont(new java.awt.Font("Dialog", 0, 10));
		lblBaudrate.setLabelFor(cbBaudrate);
		lblBaudrate.setText("Baudrate");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblBaudrate, gridBagConstraints);

		cbBaudrate.setEditable(true);
		cbBaudrate.setFont(new java.awt.Font("Dialog", 0, 10));
		cbBaudrate.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"300", "600", "1200", "2400", "4800", "9600", "19200", "38400", "57600", "115200"}));
		cbBaudrate.setSelectedIndex(5);
		cbBaudrate.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		panData.add(cbBaudrate, gridBagConstraints);

		lblDataBits.setFont(new java.awt.Font("Dialog", 0, 10));
		lblDataBits.setLabelFor(cbDataBits);
		lblDataBits.setText("Data Bits");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblDataBits, gridBagConstraints);

		cbDataBits.setFont(new java.awt.Font("Dialog", 0, 10));
		cbDataBits.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"5", "6", "7", "8"}));
		cbDataBits.setSelectedIndex(3);
		cbDataBits.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		panData.add(cbDataBits, gridBagConstraints);

		lblStopBits.setFont(new java.awt.Font("Dialog", 0, 10));
		lblStopBits.setLabelFor(cbStopBits);
		lblStopBits.setText("Stop Bits");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblStopBits, gridBagConstraints);

		cbStopBits.setFont(new java.awt.Font("Dialog", 0, 10));
		cbStopBits.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1", "2", "1.5"}));
		cbStopBits.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		panData.add(cbStopBits, gridBagConstraints);

		lblParity.setFont(new java.awt.Font("Dialog", 0, 10));
		lblParity.setLabelFor(cbParity);
		lblParity.setText("Parity");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblParity, gridBagConstraints);

		cbParity.setFont(new java.awt.Font("Dialog", 0, 10));
		cbParity.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"None", "Odd", "Even", "Mark", "Space"}));
		cbParity.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		panData.add(cbParity, gridBagConstraints);

		lblHsRx.setFont(new java.awt.Font("Dialog", 0, 10));
		lblHsRx.setText("Handshake Rx");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblHsRx, gridBagConstraints);

		lblHsTx.setFont(new java.awt.Font("Dialog", 0, 10));
		lblHsTx.setText("Handshake Tx");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		panData.add(lblHsTx, gridBagConstraints);

		cbProtocolRx.setFont(new java.awt.Font("Dialog", 0, 10));
		cbProtocolRx.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"None", "RTS/CTS", "XON/XOFF"}));
		cbProtocolRx.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		panData.add(cbProtocolRx, gridBagConstraints);

		cbProtocolTx.setFont(new java.awt.Font("Dialog", 0, 10));
		cbProtocolTx.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"None", "RTS/CTS", "XON/XOFF"}));
		cbProtocolTx.addItemListener(new java.awt.event.ItemListener()
		{
			public void itemStateChanged(java.awt.event.ItemEvent evt)
			{
				SelectionChanged(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		panData.add(cbProtocolTx, gridBagConstraints);

		add(panData, java.awt.BorderLayout.CENTER);

		jPanel1.setLayout(new java.awt.GridLayout(1, 0, 1, 0));

		btnDSR.setFont(new java.awt.Font("Dialog", 0, 8));
		btnDSR.setText("DSR");
		btnDSR.setEnabled(false);
		btnDSR.setOpaque(false);
		jPanel1.add(btnDSR);

		btnCTS.setFont(new java.awt.Font("Dialog", 0, 8));
		btnCTS.setText("CTS");
		btnCTS.setEnabled(false);
		btnCTS.setOpaque(false);
		jPanel1.add(btnCTS);

		btnCD.setFont(new java.awt.Font("Dialog", 0, 8));
		btnCD.setText("CD");
		btnCD.setEnabled(false);
		btnCD.setOpaque(false);
		jPanel1.add(btnCD);

		btnDTR.setFont(new java.awt.Font("Dialog", 0, 8));
		btnDTR.setText("DTR");
		btnDTR.setEnabled(false);
		btnDTR.setOpaque(false);
		jPanel1.add(btnDTR);

		btnRTS.setFont(new java.awt.Font("Dialog", 0, 8));
		btnRTS.setText("RTS");
		btnRTS.setEnabled(false);
		btnRTS.setOpaque(false);
		jPanel1.add(btnRTS);

		add(jPanel1, java.awt.BorderLayout.SOUTH);

	}// </editor-fold>//GEN-END:initComponents

	private void SelectionChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_SelectionChanged
	{//GEN-HEADEREND:event_SelectionChanged
		if (doPortUpdates && evt.getStateChange() == ItemEvent.SELECTED)
		{
			updatePortParameters(port);
		}
	}//GEN-LAST:event_SelectionChanged

	private void cbPortItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cbPortItemStateChanged
	{//GEN-HEADEREND:event_cbPortItemStateChanged
		// the port has been changed -> select thew new port
		if (initialized && evt.getStateChange() == ItemEvent.SELECTED)
		{
			String portName = String.valueOf(evt.getItem());
			SerialPort newPort;
			try
			{
				newPort = (SerialPort) CommPortIdentifier.getPortIdentifier(portName).open(portName, 1000);
			} catch (Exception e)
			{
				newPort = null;
				Logger.getLogger(getClass()).warn(e.getMessage());
			}

			setPort(newPort);
		}
	}//GEN-LAST:event_cbPortItemStateChanged

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JToggleButton btnCD;
	private javax.swing.JToggleButton btnCTS;
	private javax.swing.JToggleButton btnDSR;
	private javax.swing.JToggleButton btnDTR;
	private javax.swing.JToggleButton btnRTS;
	private javax.swing.JComboBox cbBaudrate;
	private javax.swing.JComboBox cbDataBits;
	private javax.swing.JComboBox cbParity;
	private javax.swing.JComboBox cbPort;
	private javax.swing.JComboBox cbProtocolRx;
	private javax.swing.JComboBox cbProtocolTx;
	private javax.swing.JComboBox cbStopBits;
	// End of variables declaration//GEN-END:variables
	/**
	 * Holds value of property port.
	 */
	private SerialPort port;

	/**
	 * Getter for property device.
	 *
	 * @return Value of property device.
	 */
	public SerialPort getPort()
	{
		return this.port;
	}

	/**
	 * Setter for serial Port.
	 *
	 * @param port New value of port.
	 */
	private void setPort(SerialPort port)
	{
		if (this.port != null)
		{
			this.port.removeEventListener();
		}
		this.port = port;
		initPortParameters(port);
	}
}
