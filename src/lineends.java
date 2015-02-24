import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.filefilter.WildcardFileFilter;


public class lineends {
	
	final static String version = "lineends v1.00, 24.02.2015";

	final static int cLF = 10;
	final static int cCR = 13;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.err.println(version);
//		System.out.println(version);

		try {
			if (args.length == 2) {
				if (args[1].equals("CRLF")) {
					// convert file named by args[0] to CRLF
					File f_original = new File(args[0]);
					File f_tmp = new File(args[0]+".tmp");
					lineends l = new lineends();
					String s = l.parsefile (f_original);
					if (!s.equals("CRLF")) {
						f_original.renameTo(f_tmp);
						l.copyfileCRLF(f_tmp, f_original);
						l.parsefile(f_original);
						f_tmp.delete();
					}
				}
			}
			else if (args.length == 0) {
				File files [] = wildcardResolution(new File ("U:\\TGMT_WCU_SW\\TRA\\SRC\\*.cpp"));
				for (File f : files) {
					lineends l = new lineends();
					l.parsefile (f);
				}
			}
			else if (args.length == 1) {
				lineends l = new lineends();
				l.parsefile (new File(args[0]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	protected static File[] wildcardResolution(File f) {
	    File dir = f.getParentFile();
	    FileFilter fileFilter = new WildcardFileFilter(f.getName());
	    return dir.listFiles(fileFilter);
	}
	
	
	String parsefile (File file) throws IOException
	{
		FileInputStream f = new FileInputStream(file);
		
		int b = 0;
		boolean cr_was_last_char = false;
		
		int anztotal = 0;
		int anzcrlf = 0;
		int anzlf = 0;
		int anzcr = 0;
		
		while ((b = f.read()) != -1) {
			anztotal++;
			
			if (cr_was_last_char) {
				if (b == cLF) {
					anzcr--;
					anzcrlf++;
				}
			}
			else {
				if (b == cLF) {
					anzlf++;
				}
			}
			
			if (b == cCR) {
				anzcr++;
			}
			cr_was_last_char = (b == cCR);
		}
		
		f.close();

		String res = lineEndStatus(anzcrlf, anzcr, anzlf);
		System.out.println(res + "  fname=" + file.getName() + " total=" + anztotal + " crlf=" + anzcrlf + " lf=" + anzlf + " cr=" + anzcr);
		
		return res;
	}
	
	
	static String lineEndStatus (int anzcrlf, int anzcr, int anzlf)
	{
		if ((anzcrlf != 0) && (anzcr == 0) && (anzlf == 0)) {
			return "CRLF";
		}
		if ((anzcrlf == 0) && (anzcr == 0) && (anzlf != 0)) {
			return "-LF-";
		}
		return "*EE*";
	}
	
	
	void copyfileCRLF (File fIn, File fOut) throws IOException
	{
		FileInputStream f = new FileInputStream(fIn);
		FileOutputStream outf = new FileOutputStream(fOut);
		
		int b = 0;
		boolean cr_was_last_char = false;
		
		while ((b = f.read()) != -1) {

			if (b == cCR) {
				outf.write(cCR);
				outf.write(cLF);
				cr_was_last_char = true;
			}
			else if (b == cLF) {
				if (cr_was_last_char == false) {
					outf.write(cCR);
					outf.write(cLF);
				}
				cr_was_last_char = false;
			}
			else {
				outf.write(b);
				cr_was_last_char = false;
			}
		}
			
		f.close();
		outf.close();
	}
	
}
