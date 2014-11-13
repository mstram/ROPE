/**
 * <p>Title: Assembler.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: NASA Ames Research Center</p>
 * @author Ronald Mak
 * @version 2.0
 */

package rope1401;

import java.io.*;
import java.util.Vector;

class Assembler
{
	// private static String sourceName;
	// private static String sourcePath;
    private static BufferedReader stdout;
    private static Process process;

    static void setPaths(String sourceName, String sourcePath)
    {
		// Assembler.sourceName = sourceName;
		// Assembler.sourcePath = sourcePath;
    }

    static boolean version()
    {		
		String[] args = new String[2];
		args[0] = AssemblerOptions.assemblerPath;
		args[1] = "-V";

        try 
		{
            process = Runtime.getRuntime().exec(args);
            stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
 
			process = null;
			
			return true;
		}
        catch(IOException ex) 
		{
            ex.printStackTrace();
			
			return false;
        }
    }

    static boolean assemble()
    {
        try 
		{
			Simulator.kill();
						
			if(AssemblerOptions.tape && AssemblerOptions.tapeEncoding && 
							AssemblerOptions.tapeEncodingChoice != AssemblerOptions.deckEncodingChoice)
			{
				// Generate tape first...
				String[] tapeArgs = buildCommand(false).toArray(new String[0]);
				process = Runtime.getRuntime().exec(tapeArgs);
				stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

				// ...then generate deck
				String[] deckArgs = buildCommand(true).toArray(new String[0]);
				process = Runtime.getRuntime().exec(deckArgs);
				stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			}
			else
			{
				String[] args = AssemblerOptions.command.toArray(new String[0]);
				args[0] = AssemblerOptions.assemblerPath;
				process = Runtime.getRuntime().exec(args);
				stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			}
			
			process = null;
			
			if(AssemblerOptions.tape && AssemblerOptions.convertTapeForTapeSimulator)
			{
				convertTape(AssemblerOptions.tapePath);
			}

			return true;
        }
        catch(IOException ex) 
		{
            ex.printStackTrace();
			
			return false;
        }
    }

    static String output()
    {
        try 
		{
            String line = stdout.readLine();
            if (line == null) 
			{
                stdout.close();
            }

            return line;
        }
        catch (IOException ex) 
		{
            ex.printStackTrace();
			
            return null;
        }
    }

	static void kill()
    {
        if (process != null)
		{
            process.destroy();            
            System.out.println("Assembler killed");
        }
    }
	
	static void convertTape(String tapePath)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile(new File(tapePath), "rws");
			
			int len = (int)file.length();
			
			byte[] buffer = new byte[len];
			file.readFully(buffer);
			
			for (int idx = 0; idx < len; idx++) 
			{
				if (buffer[idx] == '(') 
				{
					buffer[idx] = '%';
				}
				else if (buffer[idx] == '=') 
				{
					buffer[idx] = '#';
				}
			}
 
			file.seek(0);
			file.write(buffer);

			file.close();
		}
        catch(Exception ex)
        {
            ex.printStackTrace();
        }		
	}

    static Vector<String> buildCommand(boolean forDeckOrTape)
    {
        Vector<String> command = new Vector<String>();

		command.add(AssemblerOptions.assemblerPath);

		if(forDeckOrTape)
		{
			if (AssemblerOptions.listing) 
			{
				command.add("-l");			
				command.add(AssemblerOptions.listingPath);
			}

			if (AssemblerOptions.object) 
			{
				command.add("-o");
				command.add(AssemblerOptions.objectPath);
			}

			if (AssemblerOptions.diagnostic) 
			{			
				command.add("-d");
				command.add(AssemblerOptions.diagnosticPath);
			}

			if (AssemblerOptions.deckEncoding)
			{
				command.add("-e");

				if(AssemblerOptions.deckEncodingChoice == AssemblerOptions.ENCODING_SIMH)
				{
					command.add("S");
				}
				else if(AssemblerOptions.deckEncodingChoice == AssemblerOptions.ENCODING_A)
				{
					command.add("A");
				}
				else if(AssemblerOptions.deckEncodingChoice == AssemblerOptions.ENCODING_H)
				{
					command.add("H");
				}
				else if(AssemblerOptions.deckEncodingChoice == AssemblerOptions.ENCODING_PRINT)
				{
					command.add("?");
				}
				else
				{
					command.add("");
				}
			}
		}
		else 
		{
			command.add("-t");
			command.add(AssemblerOptions.tapePath);

			if (AssemblerOptions.tapeEncoding)
			{
				command.add("-e");

				if(AssemblerOptions.tapeEncodingChoice == AssemblerOptions.ENCODING_SIMH)
				{
					command.add("S");
				}
				else if(AssemblerOptions.tapeEncodingChoice == AssemblerOptions.ENCODING_A)
				{
					command.add("A");
				}
				else if(AssemblerOptions.tapeEncodingChoice == AssemblerOptions.ENCODING_H)
				{
					command.add("H");
				}
				else
				{
					command.add("");
				}
			}
		}
		
		if (AssemblerOptions.boot) 
		{
            command.add("-b");
			
			if(AssemblerOptions.bootLoader == AssemblerOptions.BOOT_IBM)
			{
				switch(AssemblerOptions.coreSize)
				{
					case AssemblerOptions.SIZE_1400:
						command.add("I1");
						break;

					case AssemblerOptions.SIZE_2000:
						command.add("I2");
						break;

					case AssemblerOptions.SIZE_4000:
						command.add("I4");
						break;
				
					case AssemblerOptions.SIZE_8000:
						command.add("I8");
						break;

					case AssemblerOptions.SIZE_12000:
						command.add("Iv");
						break;

					case AssemblerOptions.SIZE_16000:
						command.add("Ix");
						break;
				}
			}
			else if(AssemblerOptions.bootLoader == AssemblerOptions.BOOT_VAN_1)
			{
				command.add("B");
			}
			else if(AssemblerOptions.bootLoader == AssemblerOptions.BOOT_VAN_2)
			{
				command.add("V");
			}	
        }

        if (AssemblerOptions.macro) 
		{
            for (String macro : AssemblerOptions.macros.split(";")) 
			{
                command.add("-m");
                command.add(macro);
            }
			
            for (String path : AssemblerOptions.macroPath.split(";")) 
			{
                command.add("-I");
                command.add(path);
            }
        }

        if (AssemblerOptions.codeOk) 
		{
            command.add("-a");
        }

        if (AssemblerOptions.interleave) 
		{
            command.add("-i");
        }

        if (AssemblerOptions.store) 
		{
            command.add("-L");
        }

        if (AssemblerOptions.dump) 
		{
            command.add("-s");
        }

        if (AssemblerOptions.page) 
		{
            String pageLength = AssemblerOptions.pageLength;
            if (pageLength.length() > 0) 
			{
                command.add("-p");
			    command.add(pageLength);
            }
        }

        if (AssemblerOptions.trace) 
		{
            StringBuilder letters = new StringBuilder(3);

            if (AssemblerOptions.traceLexer) 
			{
                letters.append('l');
            }
			
            if (AssemblerOptions.traceParser) 
			{
                letters.append('p');
            }
			
            if (AssemblerOptions.traceProcess) 
			{
                letters.append('P');
            }
			
            if (letters.length() > 0) 
			{
                command.add("-T");
                command.add(letters.toString());
            }
        }

        if (AssemblerOptions.extras) 
		{
            int flag = 0;
			
            if (AssemblerOptions.extrasEx) 
			{
                flag += 1;
            }
			
            if (AssemblerOptions.extrasEnd) 
			{
                flag += 2;
            }
			
            if (AssemblerOptions.extrasQueue) 
			{
                flag += 4;
            }
			
            if (AssemblerOptions.extrasReloader) 
			{
                flag += 8;
            }
			
            if (flag > 0) 
			{
                command.add("-X");
                command.add("" + flag);
            }
        }
		
        command.add(AssemblerOptions.sourcePath);
		
		String commandStr = "";
		for(int idx = 0; idx < command.size(); idx++)
		{
			if(idx < command.size() - 1)
			{
				commandStr = commandStr.concat(command.get(idx) + " ");
			}
			else
			{
				commandStr = commandStr.concat(command.get(idx));
			}
		}
		
        return command;
    }}
