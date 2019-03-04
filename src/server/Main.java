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
	public Korisnik koJe;
	public BufferedWriter bUpisivac;
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
				if (this.koJe == null)
				{
					this.posaljiPorukuKlijentu("Unesite Vase korisnicko ime:");
					
					boolean korisnikPostoji = false;
					for (Thread nit: sveNiti)
					{                                    
						if (((Konekcija)nit).koJe != null && ((Konekcija)nit).koJe.toSamJa(ulaz))
							korisnikPostoji = true;
					}
					
					if (korisnikPostoji) //REFAKTORING GOLEMI OVDE!!!!
					{
						this.posaljiPorukuKlijentu("Nik zauzet, izaberite drugi :(");
						continue;
					}
						
					
					this.koJe = new Korisnik(ulaz);
					
					this.posaljiPorukuKlijentu("Sada ste poznati kao: " + this.koJe.getUserName());
					continue;
				} 
				
				for(Thread nit: sveNiti)
				{                                                         //"11:31:48.565"     {"11:31:48", "565"}  
					((Konekcija)nit).posaljiPorukuKlijentu("[" + LocalTime.now().toString().split("\\.")[0] + "] " + this.koJe.getUserName() + " kaze: " + ulaz);
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
