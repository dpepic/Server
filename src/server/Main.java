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
	public BufferedReader bCitac;
	static Vector<Konekcija> sveNiti = new Vector<Konekcija>();


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
			this.bCitac = new BufferedReader(citac);

			String ulaz ="";
			this.posaljiPorukuKlijentu("Dobrdosli na server :)");
			/*while (!serverConf.shutdown && this.koJe == null)
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
			}*/
			boolean zavrsetak = false;
			//Ovde zvanicno pocinje chat :D
			while (!zavrsetak && !serverConf.shutdown && ulaz != null)
			{
				if (this.koJe != null)
					ulaz = bCitac.readLine();
				//Ovo nam je petlja iz koje ne pustamo
				//korisnika dok se ne uloguje
				while (!zavrsetak && !serverConf.shutdown && this.koJe == null && ulaz != null)
				{ 
					ulaz = bCitac.readLine();
					switch (ulaz.split(" ")[0])
					{
					case "/login":
						if (ulaz.split(" ").length != 3)
							this.posaljiPorukuKlijentu("Koristite format /login nick sifra");
						else
						{
							String nick = ulaz.split(" ")[1];
							String pass = ulaz.split(" ")[2];
							boolean korisnikPostoji = false;
							for (Korisnik k: Korisnik.sviKorisnici)
							{
								if (k.userName.equals(nick))
								{
									korisnikPostoji = true;
									boolean ulogovan = false;
									for (Konekcija nit : sveNiti)
									{
										if (nit.koJe == k)
											ulogovan = true;
									}
									if (ulogovan)
										this.posaljiPorukuKlijentu("Nalog vec ulogovan! FBI je informisan!!");
									else
									{
										if (pass.equals(k.pass))
										{
											this.koJe = k;
											this.posaljiPorukuKlijentu("Uspesno ste ulogovani :)");
											break;
										} else
										{
											this.posaljiPorukuKlijentu("Nalog ili sifra nije u redu!");
										}
									}
								}
							}
							if (!korisnikPostoji)
								this.posaljiPorukuKlijentu("Nalog ili sifra nije u redu!;) ;)");
						}
						break;
					case "/napraviNalog":
						if (ulaz.split(" ").length != 4)
							this.posaljiPorukuKlijentu("Koristite format /napraviNalog user pass mail!");
						else
						{  
							String user = ulaz.split(" ")[1];
							String pass = ulaz.split(" ")[2];
							String mail = ulaz.split(" ")[3];
							
							boolean kPostoji = false;
							for (Korisnik k: Korisnik.sviKorisnici)
							{
								if (k.userName.equals(user))
								{
									kPostoji = true;
									this.posaljiPorukuKlijentu("Nalog vec postoji!");
									break;
								}
							}
							if (!kPostoji)
							{
								boolean mPostoji = false;
								for(Korisnik k: Korisnik.sviKorisnici)
								{
									if (k.getEmail().equals(mail))
									{
										mPostoji = true;
										this.posaljiPorukuKlijentu("E-mail se vec koristi!");
										break;
									}
								}
								if (!mPostoji)
								{
									this.posaljiPorukuKlijentu("Potvrdite sifru.");
									ulaz = bCitac.readLine();
									if (pass.equals(ulaz))
									{
										Korisnik novi = new Korisnik(user);
										novi.pass = pass;
										novi.email = mail;
										novi.sobe.add(Soba.sveSobe.get(0));
										Korisnik.sviKorisnici.add(novi);
										this.posaljiPorukuKlijentu("Uspesno ste napravili nalog!");
									} else
									{
										this.posaljiPorukuKlijentu("Sifra se ne slaze!");
										
									}
								}
							}
						}
						break;
					case "/logout":
						zavrsetak = true;
						break;
					default:
						this.posaljiPorukuKlijentu("Niste ulogovani! " 
								+ "Koristite /login user pass ili napravite " 
								+ "nalog sa /napraviNalog user pass mail");
					}
				}
				
				if(ulaz.startsWith("/login"))
				{
					this.posaljiPorukuKlijentu("Dobrodosli :)");
					continue;
				}
				
				if (ulaz.startsWith("/"))
				{
					
					switch (ulaz.split(" ")[0])
					{
					case "/napravi":
						if (ulaz.split(" ").length == 1)
							this.posaljiPorukuKlijentu("Niste naveli sobu!");
						else
						{
							if (ulaz.split(" ").length == 3)
							{
								//Proci foreach petljom kroz sobe i videti
								//imali li vec alias i naziv
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

						//ZA DOMACI -- REFAKTORISATI DA RADI U JEDNOJ LINIJI
						//NA PRIMER, /nadimak noviNadimak
					case "/nadimak":
						this.posaljiPorukuKlijentu("Unesite novo korisnicko ime: ");
						ulaz = bCitac.readLine();
						boolean postoji = false;
						for(Korisnik k: Korisnik.sviKorisnici) {
							if(k.toSamJa(ulaz)) {
								postoji = true;
							}
						}
						if(postoji) {
							this.posaljiPorukuKlijentu("Korisnicko ime vec postoji! ");

						}
						else {
							this.koJe.userName = ulaz;
							this.posaljiPorukuKlijentu("Nadimak je uspesno promenjen! ");
						}
						break;
						//					case "/kick":
						//						this.posaljiPorukuKlijentu("Unesite korisnicko ime koje zelite da izbacite");
						//						ulaz = bCitac.readLine();
						//						postoji =  false;
						//						Korisnik izbacen = null;
						//						for(Korisnik k: Korisnik.sviKorisnici) {
						//							if(k.toSamJa(ulaz)) {
						//								postoji = true;
						//								izbacen = k;
						//							}
						//						}
						//						if(postoji) {
						//							Korisnik.sviKorisnici.remove(izbacen);
						//							this.posaljiPorukuKlijentu("Korisnik " + ulaz + " je izbacen");
						//						}
						//						else {
						//							this.posaljiPorukuKlijentu("Odabrali ste nepostojece korisnicko ime");
						//						}
						//						break;
					case "/gdeSam":
						this.posaljiPorukuKlijentu("Nalazite se u:");
						for(Soba s: this.koJe.sobe)
							if (s.naziv != null)
								this.posaljiPorukuKlijentu("Alias: " + s.alias + " Naziv: "+ s.naziv + " + Clanova: " + s.brojKorisnika);
						this.posaljiPorukuKlijentu("To e to!");
						break;
					case "/izlistajSobe":
						this.posaljiPorukuKlijentu("Postoje sobe:");
						for (Soba s: Soba.sveSobe)
							if (s.naziv != null)
								this.posaljiPorukuKlijentu("Alias: " + s.alias + " Naziv: "+ s.naziv + " + Clanova: " + s.brojKorisnika);
						this.posaljiPorukuKlijentu("Konec!");
					break;
					case "/logout":
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
					
					for(Konekcija nit: sveNiti)
					{ 
						if (nit.koJe != null && nit.koJe.sobe.contains(this.koJe.aktivnaSoba))
							nit.posaljiPorukuKlijentu("[" + this.koJe.aktivnaSoba.alias + "]" + "[" + LocalTime.now().toString().split("\\.")[0] + "] " + this.koJe.getUserName() + " kaze: " + ulaz);
					}
				}
			}
			
			if (serverConf.shutdown)
				this.posaljiPorukuKlijentu("Server se gasi!");
			else
				this.posaljiPorukuKlijentu("Hvala na poseti :)");
			
			System.out.println("Gasimo konekciju");
			this.koJe = null;
			this.bCitac.close();
			this.bUpisivac.close();
			this.klijent.close();
			Thread.yield();
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
