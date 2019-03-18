package klijent;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class KlijentApp {

	private JFrame frame;
	JTextArea txtChat = new JTextArea();
	private JTextField txtUnos;
	private String IP;
	private int port;
	private boolean konektovan;

	public static void main(String[] args) 
	{	
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					KlijentApp window = new KlijentApp();
					//window.frame.setVisible(true);
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


		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		/*Login prozor = new Login();
		prozor.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent zatvoren)
			{
				frame.setVisible(true);
				if (!prozor.signUp)
					Klijent.posaljiPoruku(String.format("/login %s %s", 
											prozor.korisnicko, 
						                    prozor.sifra));
				else
				{
					Klijent.posaljiPoruku(String.format("/napraviNalog %s %s %s", 
							                 prozor.korisnicko,
							                 prozor.sifra,
							                 prozor.email));
					Klijent.posaljiPoruku(prozor.sifra);
				}
			}

		}); 
		prozor.setVisible(true);
		 */
		frame.setVisible(true);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnPosalji = new JButton("Posalji");
		btnPosalji.setToolTipText("Blah blah blah");
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

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnMeni = new JMenu("Meni");
		menuBar.add(mnMeni);

		JMenuItem mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if (konektovan)
				{
					Klijent.posaljiPoruku("/logout");
					konektovan = false;
				}
			}
		});

		JMenuItem mntmOpcije = new JMenuItem("Opcije");
		mntmOpcije.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				dlgOpcije op = new dlgOpcije();
				op.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosed(WindowEvent zatvoren)
					{
						if (op.promena)
						{
							IP = op.IP;
							port = op.port;
						}
					}
				});
				op.setVisible(true);
			}
		});

		JMenuItem mntmKonektujSe = new JMenuItem("Konektuj se");
		mntmKonektujSe.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (!konektovan)
				{
					Klijent.pokreniKonekciju(IP, port);
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
					konektovan = true;
				}
			}
		});
		mnMeni.add(mntmKonektujSe);
		mnMeni.add(mntmOpcije);
		mnMeni.add(mntmLogout);

		JMenuItem mntmSobe = new JMenuItem("Sobe");
		mntmSobe.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (konektovan) 
				{
					dlgSobe sobe = new dlgSobe();
					sobe.setVisible(true);
				}
			}
		});
		menuBar.add(mntmSobe);

	}

}
