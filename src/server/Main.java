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
			new Soba("General chat", "gc"); //Pravimo sobu za sve korisnike
		      //Posto ovo radimo na samom startu imamo
		     //garanciju da je ova soba uvek I u vektoru sveSobe
			
			if (argumenti.length == 0) //Mozemo da podesimo port
				//koristeci java server.Main 1234 preko argumenta
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
								this.koJe.sobe.add(Soba.sveSobe.get(0));
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
					boolean zavrsetak = false;
					switch (ulaz.split(" ")[0])
					{
						case "/test":
							this.posaljiPorukuKlijentu("Cujemo se ;)");
							if (ulaz.split(" ").length > 1)
								this.posaljiPorukuKlijentu("Imamo i parametar: " + 
							                               ulaz.split(" ")[1]);
							break;
						case "/napravi":
							if (ulaz.split(" ").length == 1)
								this.posaljiPorukuKlijentu("Niste naveli sobu!");
							else
							{
								if (ulaz.split(" ").length == 3)
								{
									Soba s = new Soba(ulaz.split(" ")[1], 
											          ulaz.split(" ")[2]);
									this.koJe.sobe.add(s);
								} else
									this.posaljiPorukuKlijentu("Navedite i alias, npr /napravi generalChat gc");
							}
							break;
						case "/udji":
							if (ulaz.split(" ").length == 1)
								this.posaljiPorukuKlijentu("Niste naveli sobu!");
							else
							{
								Soba s = Soba.dajSobu(ulaz.split(" ")[1]);
								if (s == null)
								{
									this.posaljiPorukuKlijentu("Soba ne postoji!");
								} else
								{
									this.koJe.sobe.add(s);
									this.koJe.aktivnaSoba = s;
									this.posaljiPorukuKlijentu("Dobrodosli u " + s.naziv);
									s.dodajKorisnika();
								}
							}
								break;
						case "/izadji":
							if (ulaz.split(" ").length == 1)
								this.posaljiPorukuKlijentu("Niste uneli sobu!");
							else
							{       //ZA DOMACI NAPRAVITI DA SE KORISNIKU JAVI
								    //I KADA NIJE IZASAO IZ SOBE
								for (Soba s: this.koJe.sobe)
									if (s.alias.equals(ulaz.split(" ")[1]))
									{
										this.koJe.sobe.remove(s);
										if (this.koJe.aktivnaSoba == s)
											this.koJe.aktivnaSoba = Soba.sveSobe.get(0);
										this.posaljiPorukuKlijentu("Izasli ste iz sobe.");
										break;
									}
							}
							break;
						case "/gdeSam":
							this.posaljiPorukuKlijentu("Nalazite se u:");
							for(Soba s: this.koJe.sobe)
								if (s.naziv != null)
									this.posaljiPorukuKlijentu("+ " + s.naziv);
							break;
						case "/konec":
							zavrsetak = true;
							break;
							
					}
					if (zavrsetak)
						break;
				} else if(ulaz.startsWith("@"))
				{
					//DOMACI -- PRIJAVITI KORISNIKU DA NIJE USPEO
					//DA ODABERE SOBU
					String alias = ulaz.substring(1).split(" ")[0];
					for (Soba s: this.koJe.sobe)
						if (s.alias.equals(alias))
						{
							this.koJe.aktivnaSoba = s;
							this.posaljiPorukuKlijentu("Pricate na " + s.naziv);
						}
				} else
				{
					for(Thread nit: sveNiti)
					{ 
						if (((Konekcija)nit).koJe != null && ((Konekcija)nit).koJe.sobe.contains(this.koJe.aktivnaSoba))
							((Konekcija)nit).posaljiPorukuKlijentu("[" + this.koJe.aktivnaSoba.alias + "]" + "[" + LocalTime.now().toString().split("\\.")[0] + "] " + this.koJe.getUserName() + " kaze: " + ulaz);
					}
				}
			}
			if (serverConf.shutdown)
				this.posaljiPorukuKlijentu("Server se gasi!");
			else
				this.posaljiPorukuKlijentu("Hvala na poseti :)");
			this.koJe = null;
			this.klijent.close();
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
