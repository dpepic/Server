package klijent;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.SwingConstants;

public class KlijentApp {

	private JFrame frame;
	JTextArea txtChat = new JTextArea();
	private JTextField txtUnos;

	public static void main(String[] args) 
	{	
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					KlijentApp window = new KlijentApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public KlijentApp() 
	{
		initialize();
	}

	private void initialize() 
	{
		Klijent.pokreniKonekciju();
		SwingWorker<Void, String> radnik = new SwingWorker<Void, String>()
		{
			protected Void doInBackground()
			{
				while (true)
				{
					try
					{
						Thread.sleep(1000);
						publish(Klijent.primiPoruku());
					} catch (Exception e)
					{
						
					}
				}
			}
			
			protected void process(List<String> saServera)
			{
				String rez = saServera.get(saServera.size() - 1);
				if (!rez.equals(""))
					txtChat.append(rez);
			}

		};
		radnik.execute();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnPosalji = new JButton("Posalji");
		btnPosalji.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (!txtUnos.getText().equals(""))
				{
					Klijent.posaljiPoruku(txtUnos.getText());
					txtUnos.setText("");
				}
			}
		});

		txtUnos = new JTextField();
		txtUnos.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(txtUnos);
		txtUnos.setColumns(40);
		panel.add(btnPosalji);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.EAST);

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		txtChat.setFont(new Font("Monospaced", Font.PLAIN, 19));


		scrollPane.setViewportView(txtChat);

	}

}
