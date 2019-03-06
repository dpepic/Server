package server;
import java.net.*;
import java.util.Vector;
import java.io.*;
import java.time.*;

public class Main 
{
	public static void main(String[] argumenti)  
	{
		try
		{
			if (argumenti.length == 0)
				argumenti = new String[] {"1234"};

			System.out.println("Otvaram socket na portu: " + argumenti[0]);
			(new serverConf()).start(); 
			ServerSocket soket = new ServerSocket(Integer.parseInt(argumenti[0]));
			while(!serverConf.shutdown)
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

class serverConf extends Thread
{
	public static boolean shutdown = false;

	public void run()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			while (!shutdown)
			{
				String ulaz = br.readLine();
				if (ulaz.equals("q"))
				{
					serverConf.shutdown = true;

				}
			}
			//SNIMIMO KORISNIKE I OSTALE PODATKE

		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}

class Konekcija extends Thread
{
	Socket klijent;
	public Korisnik koJe = null;
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

			while (!serverConf.shutdown && this.koJe == null)
			{
				this.posaljiPorukuKlijentu("Napravite novi nalog? (d/n)");
				ulaz = bCitac.readLine();

				if (ulaz.equals("d"))
				{

					this.posaljiPorukuKlijentu("Unesite zeljeno korisnicko ime: ");
					ulaz = bCitac.readLine();
					boolean postoji = false;
					for (Korisnik k: Korisnik.sviKorisnici)
					{
						if (k.toSamJa(ulaz))
							postoji = true;
					}

					if (postoji)
					{
						this.posaljiPorukuKlijentu("Nalog vec postoji!");
						continue;
					}

					Korisnik novi = new Korisnik(ulaz);
					boolean postojiMejl;
					do
					{
						this.posaljiPorukuKlijentu("Unesite mejl: ");
						ulaz = bCitac.readLine();
						postojiMejl = false;
						for (Korisnik k: Korisnik.sviKorisnici)
						{
							if (k.getEmail().toLowerCase().equals((ulaz.toLowerCase())))
								postojiMejl = true;
						}
					} while (!serverConf.shutdown && postojiMejl && !novi.promeniMejl(ulaz));

					do
					{
						this.posaljiPorukuKlijentu("Unesite sifru: ");
						ulaz = bCitac.readLine();
					}while(!serverConf.shutdown && !novi.promeniSifru(ulaz));

					do
					{
						this.posaljiPorukuKlijentu("Unesite vasu izabranu sifru za proveru: ");
						ulaz = bCitac.readLine();
					}while(!serverConf.shutdown && !ulaz.equals(novi.getPass()));
					this.posaljiPorukuKlijentu("Nalog uspesno kreiran!");
					Korisnik.sviKorisnici.add(this.koJe);
					continue;
				} else
				{
					this.posaljiPorukuKlijentu("Unesite Vase korisnicko ime:");
					ulaz = bCitac.readLine();
					
					boolean postoji = false;
					for (Korisnik k: Korisnik.sviKorisnici)
					{
						if (k.toSamJa(ulaz))
							postoji = true;
					}
					
					if (postoji)
					{
						boolean ulogovan = false;
						for (Thread nit: sveNiti)
						{                                    
							if (((Konekcija)nit).koJe != null && ((Konekcija)nit).koJe.toSamJa(ulaz))
								ulogovan = true;
						}
						if (ulogovan)
						{
							this.posaljiPorukuKlijentu("Nalog je vec aktivan!");
							continue;
						} else
						{
							//Proverimo sifru
						}
							
					} else
					{
						this.posaljiPorukuKlijentu("Nalog ne postoji!");
						continue;
					}
				}	
			}
				
			//Ovde zvanicno pocinje chat :D
			while (!serverConf.shutdown && (ulaz = bCitac.readLine()) != null)
			{
				if (this.koJe == null)
				{

					boolean korisnikPostoji = false;
					for (Thread nit: sveNiti)
					{                                    
						if (((Konekcija)nit).koJe != null && ((Konekcija)nit).koJe.toSamJa(ulaz))
							korisnikPostoji = true;
					}

					if (korisnikPostoji) //REFAKTORING GOLEMI OVDE!!!!
					{
						this.posaljiPorukuKlijentu("Korisnicki nalog je vec ulogovan!!");
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

			this.posaljiPorukuKlijentu("Server se gasi!");

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
