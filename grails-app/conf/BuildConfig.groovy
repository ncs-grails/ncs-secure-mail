grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
		//grailsRepo "http://svn.cccs.umn.edu/ncs-grails-plugins"

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        // uncomment these to enable remote dependency resolution from public Maven repositories
		mavenRepo "http://artifact.ncs.umn.edu/plugins-release"
		//mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        //runtime 'mysql:mysql-connector-java:5.1.13'
		compile 'org.apache.ant:ant:1.7.1'
		compile 'org.apache.ant:ant-launcher:1.7.1'
    }
	plugins {

		build ":tomcat:${grailsVersion}"
		compile ":hibernate:${grailsVersion}"

		compile ":audit-logging:0.5.4"
		compile ":code-coverage:1.2.5"
		compile ":codenarc:0.16.1"
		compile ":famfamfam:1.0.1"
		compile ":file-uploader:1.2"
		compile ":jquery:1.6.1.1"
		compile ":jquery-ui:1.8.11"
		compile ":mail:1.0"
		compile ":ncs-web-template:0.2"
		compile ":quartz:0.4.2"
		compile ":spring-security-core:1.2.7.2"
		compile ":spring-security-ui:0.1.2"
	}
}
