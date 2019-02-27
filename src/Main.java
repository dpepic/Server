import java.net.*;
import java.io.*;

public class Main 
{
	public static void main(String[] args) 
	{
		try
		{
			ServerSocket soket = new ServerSocket(1234);
			(new serverGovori()).start();
			while(true)
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

class serverGovori extends Thread
{
	public void run()
	{
		while (true)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Unesi nesto: ");
			try
			{
				Konekcija.test = br.readLine();
			} catch (IOException joj)
			{
				joj.printStackTrace();
			}
		}
	}
}

class Konekcija extends Thread
{
	Socket klijent; 
	static String test = "Ovo je kao poruka";

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
				bUpisivac.write("Server kaze: " + ulaz + " ---- " + test);
				bUpisivac.newLine();
				bUpisivac.flush();
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}
