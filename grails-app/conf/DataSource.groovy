hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'

    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "ncs-secure-mail"
    password = "RANDOMLYGENERATEDLONGPASSWORD"
    
}
/*
	// Development
	CREATE DATABASE secure_mail;
	CREATE USER 'ncs-secure-mail'@'localhost' IDENTIFIED BY 'RANDOMLYGENERATEDLONGPASSWORD';
	GRANT ALL ON secure_mail.* TO 'ncs-secure-mail'@'localhost';
	FLUSH PRIVILEGES;

	// Production
	CREATE DATABASE secure_mail;
	CREATE DATABASE secure_mail_test;
	
	CREATE USER 'ncs-secure-mail'@'%' IDENTIFIED BY 'RANDOMLYGENERATEDLONGPASSWORD';
	GRANT ALL ON secure_mail.* TO 'ncs-secure-mail'@'%' REQUIRE SSL;
	GRANT ALL ON secure_mail_test.* TO 'ncs-secure-mail'@'%' REQUIRE SSL;
	
	FLUSH PRIVILEGES;
 */

environments {
	development {
		dataSource {
			/// To run on local MySQL database ///
			//dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			dbCreate = "update"
			url = "jdbc:mysql://localhost/secure_mail?noAccessToProcedureBodies=true"
			
			
			/// To run locally in memory ///
			// dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			//url = "jdbc:hsqldb:mem:devDB"
			//driverClassName = "org.hsqldb.jdbcDriver"
			//username = "sa"
			//password = ""
		}
	}
	test {
		dataSource {
			url = "jdbc:mysql://test.mysql.server.example.com/secure_mail_test?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
			//url = "jdbc:mysql://localhost/secure_mail_test?noAccessToProcedureBodies=true"
			//jndiName = "java:comp/env/SecureMailTest"
			dbCreate = "update"		
		}
	}
	production {
		dataSource {
			url = "jdbc:mysql://production.mysql.server.example.com/secure_mail?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
			//url = "jdbc:mysql://localhost/secure_mail?noAccessToProcedureBodies=true"
			//jndiName = "java:comp/env/SecureMail"
			dbCreate = "update"		
		}
	}
}

