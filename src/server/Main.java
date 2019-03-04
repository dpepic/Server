package server;
import java.net.*;
import java.util.Vector;
import java.io.*;
import java.time.*;

public class Main 
{
	public static void main(String[] args) 
	{
		try
		{
			ServerSocket soket = new ServerSocket(1234);
			while(true)
			{
				System.out.println("Cekam konekciju...");
				(new Konekcija(soket.accept())).start();
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}

class Konekcija extends Thread
{
	Socket klijent; 
	public BufferedWriter bUpisivac;
	public String nik = null;
	static Vector<Thread> sveNiti = new Vector<Thread>();

	public Konekcija(Socket koSeKonektuje)
	{
		this.klijent = koSeKonektuje;
		sveNiti.add(this);
	}

	public void run()
	{
		try
		{
			System.out.println("Konekcija sa: " + klijent.getInetAddress());

			OutputStream slanje = klijent.getOutputStream();
			OutputStreamWriter upisivac = new OutputStreamWriter(slanje);
			this.bUpisivac = new BufferedWriter(upisivac);

			InputStream primanje = klijent.getInputStream();
			InputStreamReader citac = new InputStreamReader(primanje);
			BufferedReader bCitac = new BufferedReader(citac);

			String ulaz;

			while ((ulaz = bCitac.readLine()) != null)
			{
				if (this.nik == null)
				{
					boolean nikPostoji = false;
					for (Thread nit: sveNiti)
					{                                    
						if (((Konekcija)nit).nik != null && ((Konekcija)nit).nik.toLowerCase().equals(ulaz.toLowerCase()))
							nikPostoji = true;
					}
					
					if (nikPostoji)
					{
						this.posaljiPorukuKlijentu("Nik zauzet, izaberite drugi :(");
						continue;
					}
						
					
					this.nik = ulaz;
					this.posaljiPorukuKlijentu("Sada ste poznati kao: " + ulaz);
					continue;
				} 
				
				for(Thread nit: sveNiti)
				{                                                         //"11:31:48.565"     {"11:31:48", "565"}  
					((Konekcija)nit).posaljiPorukuKlijentu("[" + LocalTime.now().toString().split("\\.")[0] + "] " + this.nik + " kaze: " + ulaz);
				}
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
	
	public void posaljiPorukuKlijentu(String sta)
	{
		try
		{
			this.bUpisivac.write(sta);
			this.bUpisivac.newLine();
			this.bUpisivac.flush();
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}
