package server;

import java.util.Date;
import java.util.Vector;

public class Poruka 
{
	String sadrzaj;
	String koSalje; //REFACTOR: korisnici kao objekti??
	Date vremeSlanja;
	Vector<String> primaoci = new Vector<String>();
	
	public Poruka(Vector<String> kome, String odKoga, String sta)
	{
		this.primaoci = kome;
		this.koSalje = odKoga;
		this.sadrzaj = sta;
		vremeSlanja = new Date();
	}
	
}
