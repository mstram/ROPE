/**
 * <p>Title: RopeHelper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: NASA Ames Research Center</p>
 * @author Ronald Mak & Luca Severini <lucaseverini@mac.com>
 * @version 2.0
 */

package rope1401;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.net.URLDecoder;
import java.util.Scanner;

// RopeHelper -----------------------------------------------------
public class RopeHelper 
{
	public static Boolean isWindows = false;
	public static Boolean isMac = false;
	public static Boolean isUnix = false;
	public static Boolean isSolaris = false;
	public static int modifierMaks = 0;
	public static String ropeFolder;
	
	static 
	{
        init();
    }
	
	public static void init()
	{
		// Get the folder containing the executing ROPE
		try
		{
			String path = ROPE.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			File file = new File(path);
			ropeFolder = file.getParentFile().getAbsolutePath();
		}
		catch(Exception ex)
        {
            ex.printStackTrace();
        }		

		String property = System.getProperty("os.name").toLowerCase();
		if(property.contains("win"))
		{
			isWindows = true;
		}
		else if(property.contains("mac"))
		{
			isMac = true;
		}
		else if(property.contains("nix") || property.contains("nux") || property.contains("aix"))
		{
			isUnix = true;
		}
		else if(property.contains("sunos"))
		{
			isSolaris = true;
		}
		
		modifierMaks = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}
	
	@SuppressWarnings("empty-statement")
	public static Point parsePoint(String str)
    {
		String cleanStr = str.replaceAll("[a-z]|[A-Z]|=|\\.|\\]|\\[", "");
     	Scanner scan = new Scanner(cleanStr).useDelimiter(",");
     	
		int x = scan.nextInt();
		int y = scan.nextInt();
		
		return new Point(x, y);
    }
	
	public static Dimension parseDimension(String str)
    {
     	String cleanStr = str.replaceAll("[a-z]|[A-Z]|=|\\.|\\]|\\[", "");
     	Scanner scan = new Scanner(cleanStr).useDelimiter(",");
    
		while(!scan.hasNextInt()) 
		{
			scan.next();
		}
		int x = scan.nextInt();
		
		while(!scan.hasNextInt())
		{
			scan.next();
		}
		int y = scan.nextInt();
		
		return new Dimension(x, y);
    }
}
