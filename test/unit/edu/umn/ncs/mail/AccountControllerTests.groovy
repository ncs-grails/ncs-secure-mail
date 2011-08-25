package edu.umn.ncs.mail

import grails.test.*

class AccountControllerTests extends ControllerUnitTestCase {
	
	def springSecurityService
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testChangePassword() {
		def ac = new AccountController()
		ac.springSecurityService = springSecurityService
		ac.changePassword()
    }
	
    void testUpdatePassword() {
		def ac = new AccountController()
		ac.springSecurityService = springSecurityService
		ac.updatePassword()
    }
	
	void testNewUser() {
		def ac = new AccountController()
		ac.springSecurityService = springSecurityService
		ac.newUser()
	}
	
}
