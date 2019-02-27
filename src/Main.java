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
				Socket klijent = soket.accept();
				System.out.println("Konekcija sa: " + klijent.getInetAddress());
				
				OutputStream slanje = klijent.getOutputStream();
				OutputStreamWriter upisivac = new OutputStreamWriter(slanje);
				BufferedWriter bUpisivac = new BufferedWriter(upisivac);
				
				bUpisivac.write("Poyy od servera :)");
				bUpisivac.newLine();
				bUpisivac.flush();
				
				InputStream primanje = klijent.getInputStream();
				InputStreamReader citac = new InputStreamReader(primanje);
				BufferedReader bCitac = new BufferedReader(citac);
				
				while (!klijent.isClosed())
				{
					if (bCitac.ready())
					{
						String ulaz = bCitac.readLine();
						if (ulaz.equals("q"))
						{
							klijent.close();
							break;
						}
						ulaz = ulaz.toUpperCase(); 
						bUpisivac.write("Server kaze: " + ulaz);
						bUpisivac.newLine();
						bUpisivac.flush();
					}
				}
				
				
				//klijent.close();
			}
		} catch (IOException joj)
		{
			joj.printStackTrace();
		}
	}
}
