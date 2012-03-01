grails.config.locations = [ 
	"file:/etc/grails/logging-config.groovy",
	"file:/etc/grails/mail-config.groovy",
	"file:/etc/grails/${appName}-config.groovy" ]

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
                      multipartForm: 'multipart/form-data' ]

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

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'edu.umn.ncs.mail.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'edu.umn.ncs.mail.UserRole'
grails.plugins.springsecurity.authority.className = 'edu.umn.ncs.mail.Role'
grails.plugins.springsecurity.auth.forceHttps = true
grails.plugins.springsecurity.secureChannel.definition = [ '/**':'REQUIRES_SECURE_CHANNEL' ]
// "Remember Me" is disabled on the login form
grails.plugins.springsecurity.rememberMe.useSecureCookie = true
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

// use jquery
grails.views.javascript.library="jquery"
