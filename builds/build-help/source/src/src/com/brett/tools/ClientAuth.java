package com.brett.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
* @author Brett
* @date Sep 7, 2020
*/

public class ClientAuth {
	
	private static char[] alph = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
								  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
								  '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
	
	public static int check_auth(String username, String password) {
		try {
			URL obj = new URL("http://tpgc.me/totalcrafter/post_auth.php");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	
			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			String postData = "username=" + username + "&" + "password=" + password;
			os.write(postData.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();
			// For POST only - END
	
			int responseCode = con.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);
	
			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine = in.readLine();
				in.close();
				
				try {
					return Integer.parseInt(inputLine);
				} catch (Exception e) {}
			} else {
				System.out.println("POST request not worked");
			}
		} catch (Exception e) {e.printStackTrace();}
		return -1;
	}
	
	public static int check_auth_token(String username, String token) {
		try {
			URL obj = new URL("http://paragonscode.ddns.net/totalcrafter/token_auth.php");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	
			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			String postData = "username=" + username + "&" + "token=" + token;
			os.write(postData.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();
			// For POST only - END
	
			int responseCode = con.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);
	
			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine = in.readLine();
				in.close();
				
				try {
					return Integer.parseInt(inputLine);
				} catch (Exception e) {}
			} else {
				System.out.println("POST request not worked");
			}
		} catch (Exception e) {e.printStackTrace();}
		return -1;
	}
	
	public static String setToken(String username, String password) {
		try {
			URL obj = new URL("http://tpgc.me/totalcrafter/set_token.php");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	
			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			
			String token = "";
			for (int i = 0; i < 64; i++) {
				token += alph[Maths.randomInt(0, alph.length-1)];
			}
			
			String postData = "username=" + username + "&password=" + password + "&token=" + token;
			os.write(postData.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();
			// For POST only - END
	
			int responseCode = con.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);
	
			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				System.out.println("token set");
			} else {
				System.out.println("POST request not worked");
			}
			return token;
		} catch (Exception e) {e.printStackTrace();}
		return "NOTOKEN";
	}
	
}
