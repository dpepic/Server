package server;

public class Soba 
{
	String naziv;
	int brojKorisnika;
	boolean privatna;
	
	public Soba(String kakoSeZove)
	{
		this.naziv = kakoSeZove;
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
