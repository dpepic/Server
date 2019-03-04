package klijent;
import java.net.*;
import java.io.*;

public class KlijentMain 
{
	public static void main(String[] args) 
	{
		try
		{
			Socket konekcija = new Socket("192.168.2.174", 1234);

			InputStream saServera = konekcija.getInputStream();
			InputStreamReader citac = new InputStreamReader(saServera);
			BufferedReader bCitac = new BufferedReader(citac);

			Slusam osluskivac = new Slusam(bCitac);
			osluskivac.start();

			//BufferedReader primer = new InputStreamReader(konekcija.GetInputStream());
			//izlazniTok.read(b) //Ovde su bajti :( :( :(
			//citac.read(cbuf) //Ovde su karakteri :(
			//bCitac.readLine() //Ovde fino dobijem string :) 

			InputStream konzola = System.in;
			InputStreamReader citacKonzole = new InputStreamReader(konzola);
			BufferedReader odKorisnika = new BufferedReader(citacKonzole);

			OutputStream kaServeru = konekcija.getOutputStream();
			OutputStreamWriter upisivac = new OutputStreamWriter(kaServeru);
			BufferedWriter bUpisivac = new BufferedWriter(upisivac);

			while (true) 
			{
				System.out.println("Unesite nesto: ");
				
				String poruka = odKorisnika.readLine();

				bUpisivac.write(poruka);
				bUpisivac.newLine();
				bUpisivac.flush();
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

	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(1000);
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
