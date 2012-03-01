hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	username = "ncs-secure-mail"
	password = "ied5AiBoo6vaok6OosuwieCing4ahph1"
	dialect = org.hibernate.dialect.MySQLInnoDBDialect
}
/*
	// Development
	CREATE DATABASE secure_mail;
	CREATE USER 'ncs-secure-mail'@'localhost' IDENTIFIED BY 'eu9phe4eigh5eighee6faich2Ahphei0';
	GRANT ALL ON secure_mail.* TO 'ncs-secure-mail'@'localhost';
	FLUSH PRIVILEGES;

	// Production
	CREATE DATABASE secure_mail;
	CREATE DATABASE secure_mail_test;
	
	CREATE USER 'ncs-secure-mail'@'%' IDENTIFIED BY 'a different production password';
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
			url = "jdbc:mysql://sql.ncs.umn.edu/secure_mail_test?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
			// jndiName = "java:comp/env/SecureMailTest"
			dbCreate = "update"		
		}
	}
	production {
		dataSource {
			url = "jdbc:mysql://sql.ncs.umn.edu/secure_mail?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
			// jndiName = "java:comp/env/SecureMail"
			dbCreate = "update"		
		}
	}
}
