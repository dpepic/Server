package klijent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public static String korisnicko, sifra, email;
	private JTextField txtNick;
	private JPasswordField txtPass;

	public Login() 
	{
		setResizable(false);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		txtNick = new JTextField();
		txtNick.setBounds(113, 82, 150, 20);
		contentPanel.add(txtNick);
		txtNick.setColumns(10);

		JLabel lblNalog = new JLabel("Nalog:");
		lblNalog.setBounds(57, 85, 46, 14);
		contentPanel.add(lblNalog);

		txtPass = new JPasswordField();
		txtPass.setEchoChar('*');
		txtPass.setBounds(113, 130, 150, 20);
		contentPanel.add(txtPass);

		JLabel lblSifra = new JLabel("Sifra:");
		lblSifra.setBounds(57, 133, 46, 14);
		contentPanel.add(lblSifra);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				
				if (txtNick.getText().length() != 0 && txtPass.getPassword().length != 0)
				{	
					korisnicko = txtNick.getText();
					sifra = txtPass.getPassword().toString();
					dispose();
				} else
				{
					JOptionPane.showMessageDialog(contentPanel, "Niste uneli korisnicko ili sifru!", "Greska!", JOptionPane.ERROR_MESSAGE);
				}
			
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
}
