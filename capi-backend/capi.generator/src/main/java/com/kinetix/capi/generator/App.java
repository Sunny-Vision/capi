package com.kinetix.capi.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try{
    		System.out.print("Input the string to be encrypt: ");
    	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    	    String input = bufferRead.readLine();
    	    input =input.trim();
    	      
    	    StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
            enc.setPassword("kinetix");
            enc.setAlgorithm("PBEWITHMD5ANDDES");
            String cihper = enc.encrypt(input);
            System.out.println(cihper);
    	    
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	
    	
    	
    }
}
