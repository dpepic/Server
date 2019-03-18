package klijent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class dlgOpcije extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtIP;
	private JTextField txtPort;
	public String IP;
	public int port;
	public boolean promena;


	/**
	 * Create the dialog.
	 */
	public dlgOpcije() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		txtIP = new JTextField();
		txtIP.setBounds(147, 41, 150, 20);
		contentPanel.add(txtIP);
		txtIP.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setText("1234");
		txtPort.setBounds(147, 109, 86, 20);
		contentPanel.add(txtPort);
		txtPort.setColumns(10);
		
		JLabel lblIpAdresa = new JLabel("IP adresa:");
		lblIpAdresa.setBounds(46, 44, 64, 14);
		contentPanel.add(lblIpAdresa);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(64, 112, 46, 14);
		contentPanel.add(lblPort);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) 
					{
						IP = txtIP.getText();
						port = Integer.parseInt(txtPort.getText());
						promena = true;
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						promena = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
