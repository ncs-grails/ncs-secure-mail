// NCS Web Site / List Management Specific Configuration Settings

// Database Connection Strings go here
dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	username = "production-mysql-app-account"
	password = "YOURPRODUCTIONMYSQLAPPLICATIONPASSWORD"
	dialect = org.hibernate.dialect.MySQLInnoDBDialect
	dbCreate = null
	url = "jdbc:mysql://sql.production.example.org/secure_mail?useSSL=true&requireSSL=true&verifyServerCertificate=false&noAccessToProcedureBodies=true"
}

// Grails App Settings
grails.serverURL = "https://www.securemail.example.org/${appName}"
grails.exceptionresolver.params.exclude = ['password']
grails.exceptionresolver.params.exclude = ['password2']
grails.exceptionresolver.params.exclude = ['passwordConfirm']

// File Uploader Plugin
fileuploader.files.path = "/var/spool/secure-mail"
fileuploader {
	files {
		maxSize = 1000 * 1024 * 25 //25 mbytes
		allowedExtensions = ["*"]
	}
}

