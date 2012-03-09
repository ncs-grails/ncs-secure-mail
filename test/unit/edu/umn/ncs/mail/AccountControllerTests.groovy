package edu.umn.ncs.mail

import grails.test.*

class AccountControllerTests extends ControllerUnitTestCase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testChangePassword() {
		def ac = new AccountController()
		// this is a standard GSP with no model to render
		assert ac.changePassword() == null
    }
	
    void testUpdatePassword() {
		def ac = new AccountController()
		// check our results
		assert ac.updatePassword    }
	
	void testNewUser() {
		def ac = new AccountController()
		assert ac.newUser
	}
	
}
