// NCS Web Site / List Management Specific Configuration Settings

// Grails App Settings
grails.serverURL = "http://localhost:8080/${appName}"
grails.exceptionresolver.params.exclude = ['password']
grails.exceptionresolver.params.exclude = ['password2']
grails.exceptionresolver.params.exclude = ['passwordConfirm']

// File Uploader Plugin
fileuploader.files.path = "/tmp/secure-mail"
fileuploader {
	files {
		maxSize = 1000 * 1024 * 25 //25 mbytes
		allowedExtensions = ["*"]
	}
}

// Environment specific variables
environments {
	development {
		// Database Connection Strings go here
		dataSource {
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "ncs-secure-mail-user"
			password = "YOURSECUREDATABASEPASSWORD"
			dialect = org.hibernate.dialect.MySQLInnoDBDialect
			dbCreate = "update"
			url = "jdbc:mysql://localhost/secure_mail?noAccessToProcedureBodies=true&autoReconnect=true"
		}
	}
	test {
		// Database Connection Strings go here
		dataSource {
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "ncs-secure-mail-user"
			password = "YOURSECUREDATABASEPASSWORD"
			dialect = org.hibernate.dialect.MySQLInnoDBDialect
			dbCreate = "update"
			url = "jdbc:mysql://sql.staging.example.org/secure_mail_test?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
		}
	}
	production {
		// Database Connection Strings go here
		dataSource {
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			username = "ncs-secure-mail-user"
			password = "YOURSECUREDATABASEPASSWORD"
			dialect = org.hibernate.dialect.MySQLInnoDBDialect
			dbCreate = "update"
			url = "jdbc:mysql://sql.production.example.org/secure_mail?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
		}
	}
}
