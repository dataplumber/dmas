package gov.nasa.podaac.distribute.echo;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ECHOAuth {
	
	static String key = "Bar12345Bar12345"; // 128 bit key
    static String initVector = "RandomInitVector"; // 16 bytes IV
	
	private static String user = null;
	private static String pass = null;
	
	
	
	
    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            
        }

        
        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    
    public static Map<String,String> readFile(String filename){
    	
    	Map<String,String> mp = new HashMap<String,String>();
    	BufferedReader br = null;
    	String line;
		try {
			
			br = new BufferedReader(new FileReader(filename));
			
			int i = 0;
			while ((line = br.readLine()) != null) {
				if( i == 0){
					mp.put("user", decrypt(key, initVector, line));
				}else if(i==1){
					mp.put("pass", decrypt(key, initVector, line));
				}
				i++;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
    	}
    	
		return mp;
    }
    
    private static boolean writeFile(String filename, String content){
    	FileWriter fileWriter = null;
        boolean succeeded = true;
        try {
           fileWriter = new FileWriter(filename);
           fileWriter.write(content);
        } catch(Exception exception) {
           succeeded = false;
        } finally {
           try {
              if(fileWriter != null) {
                 fileWriter.close();
              }
           } catch(Exception exception) {
           }
        }
        
        return succeeded;
    }

    public static void main(String[] args) {
        
    	System.out.println("Generating distribute password tokens.");
    	System.out.println("Please enter your URS username: ");
    	user = System.console().readLine();

    	System.out.println("Please enter your URS password: ");
    	char[] pw = System.console().readPassword();
    	pass = new String(pw);

    	writeFile("echo.tokens", encrypt(key, initVector,user) +"\n" + encrypt(key, initVector,pass));
    }

}
