package klijent;
import java.net.*;
import java.io.*;

public class Klijent 
{
	static Socket konekcija;
	static BufferedWriter bUpisivac;
	static BufferedReader bCitac;
	
	public static void pokreniKonekciju() 
	{
		try
		{
			konekcija = new Socket("192.168.2.207", 1234);
			
			OutputStream kaServeru = konekcija.getOutputStream();
			OutputStreamWriter upisivac = new OutputStreamWriter(kaServeru);
			bUpisivac = new BufferedWriter(upisivac);
			
			InputStream saServera = konekcija.getInputStream();
			InputStreamReader citac = new InputStreamReader(saServera);
			bCitac = new BufferedReader(citac);

		} catch (IOException joj)
		{
			joj.printStackTrace();
		}

	}

	public static void posaljiPoruku(String poruka)
	{	
		//Ovo nam je izlaz preko soketa kojim saljemo
		//podatke ka serveru
		try
		{	
			bUpisivac.write(poruka); //pripremanje podataka
			bUpisivac.newLine();  //dodajemo oznaku za novi red
			bUpisivac.flush(); //Stvarno izvrsava upis
		} catch (Exception e)
		{

		}
	}
	
	public static String primiPoruku()
	{
		String izlaz = "";
		try
		{
			while (bCitac.ready())
			{
				izlaz += bCitac.readLine() + "\n";
			}
			return izlaz;
		} catch (IOException joj)
		{
			joj.printStackTrace();
			return izlaz;
		}
	}
}
