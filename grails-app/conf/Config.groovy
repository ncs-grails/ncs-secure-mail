// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']
grails.exceptionresolver.params.exclude = ['password2']
grails.exceptionresolver.params.exclude = ['passwordConfirm']


// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "https://secure.ncs.umn.edu/secure-mail"
		fileuploader.files.path = "/var/spool/secure-mail"
	}
    development {
        grails.serverURL = "https://localhost:8443/${appName}"
		fileuploader.files.path = "/tmp/secure-mail/"
		
		grails.naming.entries = [
			"jdbc/SecureMail": [
				type: "javax.sql.DataSource", //required
				auth: "Container", // optional
				description: "Data source for SecureMail", //optional
				driverClassName: "com.mysql.jdbc.Driver",
				url: "jdbc:mysql://localhost/secure_mail?noAccessToProcedureBodies=true",
				username: "ncs-secure-mail",
				password: "ied5AiBoo6vaok6OosuwieCing4ahph1",
				maxActive: "8",
				maxIdle: "4"
			],
		]
    }
    test {
        grails.serverURL = "https://localhost:8443/${appName}"
		fileuploader.files.path = "/tmp/secure-mail/"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'edu.umn.ncs.mail.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'edu.umn.ncs.mail.UserRole'
grails.plugins.springsecurity.authority.className = 'edu.umn.ncs.mail.Role'
grails.plugins.springsecurity.auth.forceHttps = true
grails.plugins.springsecurity.secureChannel.definition = [ '/**':'REQUIRES_SECURE_CHANNEL' ]
// "Remember Me" is disabled on the login form
grails.plugins.springsecurity.rememberMe.key = 'aehahfughei5zeeboo8ahMaiChah9pai'
grails.plugins.springsecurity.rememberMe.useSecureCookie = true
grails.plugins.springsecurity.logout.afterLogoutUrl = "http://www.ncs.umn.edu/"
// this saves password hashes to a shorter length
grails.plugins.springsecurity.password.encodeHashAsBase64 = true

grails.plugins.springsecurity.controllerAnnotations.staticRules = [
	'/console/**': ['ROLE_ADMIN'],
	'/user/**':['ROLE_ADMIN'],
	'/role/**':['ROLE_ADMIN'],
	'/securityInfo/**':['ROLE_ADMIN'],
	'/requestmap/**':['ROLE_ADMIN'],
	'/registrationCode/**':['ROLE_ADMIN'],
	'/register/**':['ROLE_ADMIN'],
	'/persistentLogin/**':['ROLE_ADMIN'],
	'/aclSid/**':['ROLE_ADMIN'],
	'/aclObjectIdentity/**':['ROLE_ADMIN'],
	'/aclEntry/**':['ROLE_ADMIN'],
	'/aclClass/**':['ROLE_ADMIN'],
	'/auditLogEvent/**':['ROLE_ADMIN'],
	'/download/**':['ROLE_NOBODY'],
	'/fileUploader/**':['ROLE_USER']
	]

// for sending email
grails.mail.host = "mail.cccs.umn.edu"
// disable for debugging
grails.mail.disabled = false

// Used by the file uploader
fileuploader {
	files {
		maxSize = 1000 * 1024 * 25 //25 mbytes
		allowedExtensions = ["*"]
		path = "/var/spool/secure-mail"
	}
}

// use jquery
grails.views.javascript.library="jquery"
