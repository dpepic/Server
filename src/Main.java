import java.net.*;
import java.io.*;

public class Main 
{
	public static void main(String[] args) 
	{
		try
		{
			ServerSocket soket = new ServerSocket(1234);
			while(true)
			{
				System.out.println("Cekam konekciju...");
				Konekcija nekaKonekcija = new Konekcija(soket.accept());
				nekaKonekcija.run();
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}

class Konekcija
{
	Socket klijent; 
	
	public Konekcija(Socket koSeKonektuje)
	{
		this.klijent = koSeKonektuje;
	}

	public void run()
	{
		try
		{
			System.out.println("Konekcija sa: " + klijent.getInetAddress());

			OutputStream slanje = klijent.getOutputStream();
			OutputStreamWriter upisivac = new OutputStreamWriter(slanje);
			BufferedWriter bUpisivac = new BufferedWriter(upisivac);

			InputStream primanje = klijent.getInputStream();
			InputStreamReader citac = new InputStreamReader(primanje);
			BufferedReader bCitac = new BufferedReader(citac);

			String ulaz;

			while ((ulaz = bCitac.readLine()) != null)
			{
				ulaz = ulaz.toUpperCase(); 
				bUpisivac.write("Server kaze: " + ulaz);
				bUpisivac.newLine();
				bUpisivac.flush();
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}
