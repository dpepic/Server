package server;

import java.io.Serializable;
import java.util.Vector;

public class Korisnik implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	String userName, pass, email;
	Vector<Soba> sobe = new Vector<Soba>();
	
	static Vector<Korisnik> sviKorisnici = new Vector<Korisnik>();
	
	public Korisnik(String userName)
	{
		this.userName = userName;
	}
	
	//Getter
	public String getUserName()
	{
		return this.userName;
	}
	
	//Getter
	public String getEmail()
	{
		if (this.email != null)
			return this.email;
		else
			return "";
	}
	
	public String getPass()
	{
		return this.pass;
	}
	
	
	//Setter
	public boolean promeniMejl(String eMail)
	{
		//DOMACI NAPRAVITI PROVERU MEJLA :) :) 
			this.email = eMail;
			return true;
	}
	
	//Setter
	public boolean promeniSifru(String pass)
	{
		if (pass.length() < 3)
			return false;
		else
		{
			this.pass = pass; //Ovo nikada, nikada, nikada, nikada
			return true;
		}
	}
	
	public boolean auth(String pass)
	{
		if (this.pass.equals(pass))
			return true;
		else
			return false;
	}
	
	public boolean toSamJa(String naziv)
	{
		if (naziv.toLowerCase().equals(this.userName.toLowerCase()))
			return true;
		else
			return false;
	}
}
