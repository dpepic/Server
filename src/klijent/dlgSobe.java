package klijent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class dlgSobe extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;


	/**
	 * Create the dialog.
	 */
	public dlgSobe() 
	{
		Klijent.posaljiPoruku("/izlistajSobe");
		SwingWorker<Void, String> listaSobe = new SwingWorker<Void,String>()
				{

					@Override
					protected Void doInBackground() throws Exception 
					{
						String stari = KlijentApp.rez;
						while (!KlijentApp.rez.equals("Konec!"))
						{
							if (!KlijentApp.rez.equals(stari))
							{
								System.out.println(stari);
								stari = KlijentApp.rez;
								publish(stari);
							}
							Thread.sleep(500);
							
						}
						return null;
					}
					
					protected void process(List<String> rezultati)
					{
						String linija = rezultati.get(rezultati.size() - 1);
						//Alias: gc Naziv:General chat + Clanova: 1
						String[] red = linija.split(": ");
						if (!linija.equals("Postoje sobe:") || !linija.equals("Konec!"))
							((DefaultTableModel)table.getModel()).addRow(red);
					}
			
				};
				
				listaSobe.execute();

		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.EAST);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"Alias", "Naziv", "Broj ucesnika", "U sobi"
					}
				) {
					Class[] columnTypes = new Class[] {
						String.class, String.class, String.class, String.class
					};
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
					boolean[] columnEditables = new boolean[] {
						false, false, false, false
					};
					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scrollPane.setViewportView(table);
			}
		}
	}

}
