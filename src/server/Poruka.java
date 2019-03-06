package server;

import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class Poruka 
{
	String sadrzaj;
	Korisnik koSalje;
	Socket odakleSalje;
	Date vremeSlanja;
	Vector<String> primaoci = new Vector<String>();
	
	public Poruka(Vector<String> kome, Korisnik odKoga, String sta)
	{
		this.primaoci = kome;
		this.koSalje = odKoga;
		this.sadrzaj = sta;
		vremeSlanja = new Date();
	}
	
}
