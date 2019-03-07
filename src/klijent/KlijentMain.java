package klijent;
import java.net.*;
import java.io.*;

public class KlijentMain 
{

	public static void main(String[] args) 
	{
		try
		{
			//Socket ----> ServerSocket
			Socket konekcija = new Socket("192.168.2.207", 1234);

			InputStream saServera = konekcija.getInputStream();
			InputStreamReader citac = new InputStreamReader(saServera);
			BufferedReader bCitac = new BufferedReader(citac);
			//Ovo nam je ulazni tok podataka sa servera
			//BufferedReader primer = new InputStreamReader(konekcija.GetInputStream());
			//izlazniTok.read(b) //Ovde su bajti :( :( :(
			//citac.read(cbuf) //Ovde su karakteri :(
			//bCitac.readLine() //Ovde fino dobijem string :) 
			
			//Pravimo nit u kojoj slusamo sta nam server javlja
			//Kao argument joj dajemo nas citac
			Slusam osluskivac = new Slusam(bCitac);
			osluskivac.start(); //Postaje nezavisna nit u memoriji

			InputStream konzola = System.in;
			InputStreamReader citacKonzole = new InputStreamReader(konzola);
			BufferedReader odKorisnika = new BufferedReader(citacKonzole);
			//Spremamo se da procitamo sta nam korisnik pise
			//na konzoli
			
			//Ovo nam je izlaz preko soketa kojim saljemo
			//podatke ka serveru
			OutputStream kaServeru = konekcija.getOutputStream();
			OutputStreamWriter upisivac = new OutputStreamWriter(kaServeru);
			BufferedWriter bUpisivac = new BufferedWriter(upisivac);

			while (true) 
			{	
				String poruka = odKorisnika.readLine();
				bUpisivac.write(poruka); //pripremanje podataka
				bUpisivac.newLine();  //dodajemo oznaku za novi red
				bUpisivac.flush(); //Stvarno izvrsava upis
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}

	}
}

class Slusam extends Thread
{
	BufferedReader saServera;
	public Slusam(BufferedReader odakle)
	{
		this.saServera = odakle;
	}

	public void run() //Kada kazemo nit.start() izvrsavanje krece
	{                 //u run() :) 
		while(true)
		{
			try
			{
				Thread.sleep(1000); //Da ne bi opteretili
				//racunar hiljadama zahteva po milisekundi
				//stojimo malo izmedju svakoga
				while (saServera.ready())
				{
					System.out.println(saServera.readLine());
				}
			} catch (IOException | InterruptedException joj)
			{
				joj.printStackTrace();
			}
		}
	}
}
