import java.net.*;
import java.util.Vector;
import java.io.*;

public class Main 
{
	public static void main(String[] args) 
	{
		try
		{
			ServerSocket soket = new ServerSocket(1234);
			(new serverGovori()).start();
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

class serverGovori extends Thread
{
	public void run()
	{
		while (true)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Unesi nesto: ");
			try
			{
				Konekcija.test = br.readLine();
			} catch (IOException joj)
			{
				joj.printStackTrace();
			}
		}
	}
}

class Konekcija extends Thread
{
	Socket klijent; 
	public BufferedWriter bUpisivac;
	static String test = "Ovo je kao poruka";
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
				ulaz = ulaz.toUpperCase(); 
				
				for(Thread nit: sveNiti)
				{
					((Konekcija)nit).posaljiPorukuKlijentu(ulaz);
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
