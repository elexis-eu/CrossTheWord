package it.uniroma1.lcl.crucyservlet.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;


import java.net.HttpURLConnection;


public class TesterClass {
	
	public static void main(String[] args) {
		try {
			URL url = new URL("http://2.236.83.85/crucy2/imgStats?name=casaw&value=-11");
			url.openConnection().getContent();

		} catch (IOException e) {
		}
	}
}
