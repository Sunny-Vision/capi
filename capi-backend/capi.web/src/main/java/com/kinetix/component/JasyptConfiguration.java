package com.kinetix.component;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("JasyptConfiguration")
public class JasyptConfiguration {

	@Bean(name="pbeEncryptor")
	public StandardPBEStringEncryptor getJasyptEncryptor(){
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWITHMD5ANDDES");
		encryptor.setPassword("kinetix");
		return encryptor;
	}
}
