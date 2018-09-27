package spec

import page.user.UserCreatePage
import page.user.UserEditPage
import page.user.UserSearchPage
import spock.lang.IgnoreIf

@IgnoreIf({
	if (!System.getProperty('geb.env')) {
		return true
	}
	if (System.getProperty('geb.env') == 'phantomjs' && !System.getProperty('phantomjs.binary.path')) {
		return true
	}
	if (System.getProperty('geb.env') == 'chrome' && !System.getProperty('webdriver.chrome.driver')) {
		return true
	}
	false
})
class UserSpec extends AbstractSecuritySpec {

	void testFindAll() {
		when:
		to UserSearchPage

		then:
		assertNotSearched()

		when:
		submit()

		then:
		at UserSearchPage
		assertResults 1, 10, 22
	}

	void testFindByUsername() {
		when:
		to UserSearchPage

		username = 'foo'
		submit()

		then:
		at UserSearchPage
		assertResults 1, 3, 3

		assertContentContains 'foon_2'
		assertContentContains 'foolkiller'
		assertContentContains 'foostra'
	}

	void testUserProfileQuestionTab() {
		when: "find Admin user"
		to UserSearchPage
		username = 'admin'
		submit()

		then:
		at UserSearchPage
		assertContentContains 'admin'

		when: "Edit user"
		$("a", text: "admin").click()

		then:
		 at UserEditPage

		when:
		submit()

		then:
		at UserEditPage

		when:
			profileTab.select()

		then:
			assert  $("#myQuestion1").value() ==  "Count to four"

		when:
		def a1 =  $("#myAnswer1").value()
		def a2 =  $("#myAnswer2").value()
		$("#myQuestion1").value("Count to 1234")
		$("#myQuestion2").value("Count to six")
		$("#myAnswer2").value("123456")
		submit()

		then:
		at UserEditPage
		assertHtmlContains("updated")

		when:
		profileTab.select()

		then:
		$("#myAnswer1").value() ==  a1
		$("#myQuestion1").value() ==  "Count to 1234"
		$("#myQuestion2").value() ==  "Count to six"
		$("#myAnswer2").value() !=  "123456"
		$("#myAnswer1").value() !=   "1234"
		$("#myAnswer1").value() !=   a2

	}

	void testFindByDisabled() {
		when:
		to UserSearchPage

		enabled.checked = '-1'
		submit()

		then:
		at UserSearchPage
		assertResults 1, 1, 1
		assertContentContains 'billy9494'
	}

	void testFindByAccountExpired() {
		when:
		to UserSearchPage

		accountExpired.checked = '1'

		submit()

		then:
		at UserSearchPage
		assertResults 1, 3, 3
		assertContentContains 'maryrose'
		assertContentContains 'ratuig'
		assertContentContains 'rome20c'
	}

	void testFindByAccountLocked() {
		when:
		to UserSearchPage

		accountLocked.checked = '1'

		submit()

		then:
		at UserSearchPage
		assertResults 1, 3, 3
		assertContentContains 'aaaaaasd'
		assertContentContains 'achen'
		assertContentContains 'szhang1999'
	}

	void testFindByPasswordExpired() {
		when:
		to UserSearchPage

		passwordExpired.checked = '1'

		submit()

		then:
		at UserSearchPage
		assertResults 1, 3, 3
		assertContentContains 'hhheeeaaatt'
		assertContentContains 'mscanio'
		assertContentContains 'kittal'
	}

	void testCreateAndEdit() {
		given:
		String newUsername = 'newuser' + UUID.randomUUID()

		// make sure it doesn't exist
		when:
		to UserSearchPage

		username = newUsername
		submit()

		then:
		at UserSearchPage
		assertNoResults()

		// create
		when:
		to UserCreatePage

		username = newUsername
		$('#password') << 'password'
		enabled.check()
		profileTab.select()
		$("#myAnswer2").value("123456")
		$("#myAnswer1").value("1234")
		submit()

		then:
		at UserEditPage
		username == newUsername
		enabled.checked
		!accountExpired.checked
		!accountLocked.checked
		!passwordExpired.checked

		// edit
		when:
		String updatedName = newUsername + '_updated'
		username = updatedName
		enabled.uncheck()
		accountExpired.check()
		accountLocked.check()
		passwordExpired.check()
		submit()

		then:
		at UserEditPage
		username == updatedName
		!enabled.checked
		accountExpired.checked
		accountLocked.checked
		passwordExpired.checked

		// delete
		when:
		delete()

		then:
		at UserSearchPage

		when:
		username = updatedName
		submit()

		then:
		at UserSearchPage
		assertNoResults()
	}
}
