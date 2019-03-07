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

			System.out.println("Ucitavam podatke o korisnicma...");
			FileInputStream fi = new FileInputStream("korisnici.obj");
			ObjectInputStream oi = null;
			try
			{
				oi = new ObjectInputStream(fi);
				while (true)
				{
					Korisnik.sviKorisnici.add((Korisnik)oi.readObject());
				}
			} catch (IOException | ClassNotFoundException coolSmo)
			{} finally
			{
				if (oi != null)
					oi.close();
			}

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

			FileOutputStream fo = new FileOutputStream("korisnici.obj", false);
			ObjectOutputStream oo = new ObjectOutputStream(fo);
			for (Korisnik k: Korisnik.sviKorisnici)
				oo.writeObject(k);
			oo.close();
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
						if (postojiMejl)
						{
							this.posaljiPorukuKlijentu("Mejl adresa je vec zauzeta!");
							continue;
						}

					} while (!serverConf.shutdown && !novi.promeniMejl(ulaz));

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
					Korisnik.sviKorisnici.add(novi); 
					continue;
				} else
				{
					Korisnik nalog = null;
					this.posaljiPorukuKlijentu("Unesite Vase korisnicko ime:");
					ulaz = bCitac.readLine();

					boolean postoji = false;
					for (Korisnik k: Korisnik.sviKorisnici)
					{
						if (k.toSamJa(ulaz))
						{
							postoji = true;
							nalog = k;
						}
					}

					if (postoji)
					{
						boolean ulogovan = false;
						for (Thread nit: sveNiti)
						{                                    
							if (((Konekcija)nit).koJe != null && ((Konekcija)nit).koJe.toSamJa(nalog.getUserName()))
								ulogovan = true;
						}
						if (ulogovan)
						{
							this.posaljiPorukuKlijentu("Nalog je vec aktivan!");
							continue;
						} else
						{
							this.posaljiPorukuKlijentu("Unesite Vasu sifru: ");
							ulaz = bCitac.readLine();
							if (nalog.getPass().equals(ulaz))
							{
								this.koJe = nalog;
								this.posaljiPorukuKlijentu("Dobrodosli :)");
							} else
							{
								this.posaljiPorukuKlijentu("Sifra nija tacna!");
								continue;
							}	
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

				if (ulaz.startsWith("/"))
				{
					switch (ulaz.split(" ")[0])
					{
						case "/test":
							this.posaljiPorukuKlijentu("Cujemo se ;)");
							if (ulaz.split(" ").length > 1)
								this.posaljiPorukuKlijentu("Imamo i parametar: " + 
							                               ulaz.split(" ")[1]);
							break;
					}
				} else
				{
					for(Thread nit: sveNiti)
					{ 
						if (((Konekcija)nit).koJe != null)
							((Konekcija)nit).posaljiPorukuKlijentu("[" + LocalTime.now().toString().split("\\.")[0] + "] " + this.koJe.getUserName() + " kaze: " + ulaz);
					}
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
