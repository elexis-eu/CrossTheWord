package it.uniroma1.lcl.crucyservlet.mongodb;


public enum Language
{
	IT("it.txt"), EN("en.txt");
	private String lang;
	Language(String file)
	{
		this.lang = file;
	}

	public String toFile() {
		return lang;
	}
}
