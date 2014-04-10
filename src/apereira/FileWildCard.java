package apereira;

import java.io.File;
import java.io.FilenameFilter;

public class FileWildCard implements FilenameFilter {
	
	private String onlyThisExt ;
	
	public FileWildCard(String extension)
	{
		this.onlyThisExt = "." + extension ;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(this.onlyThisExt);
	}

}
