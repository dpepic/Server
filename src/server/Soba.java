package server;

import java.util.Vector;

public class Soba 
{
	String naziv;
	String alias;
	int brojKorisnika = 1;
	boolean privatna;
	
	public static Vector<Soba> sveSobe = new Vector<Soba>();
	
	public static Soba dajSobu(String naziv)
	{
		for (Soba s: Soba.sveSobe)
			if (s.alias.equals(naziv))
				return s;
		return null;		
	}
	
	public Soba(String kakoSeZove, String alias)
	{
		this.naziv = kakoSeZove;
		this.alias = alias;
		Soba.sveSobe.add(this);
	}
	
	public boolean dodajKorisnika()
	{
		if (this.privatna && this.brojKorisnika > 1)
			return false;
		else
		{
			this.brojKorisnika++;
			return true;
		}
	}
}
