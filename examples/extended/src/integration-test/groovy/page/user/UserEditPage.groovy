package page.user

import geb.module.Checkbox
import geb.module.TextInput
import module.RolesTab
import page.EditPage
import module.ProfileTab

class UserEditPage extends EditPage {

	static url = 'user/edit'

	static typeName = { 'User' }

	static content = {
		username { $('#username').module(TextInput) }
		enabled { $(name: 'enabled').module(Checkbox) }
		accountExpired { $(name: 'accountExpired').module(Checkbox) }
		accountLocked { $(name: 'accountLocked').module(Checkbox) }
		passwordExpired { $(name: 'passwordExpired').module(Checkbox) }
		profileTab { module ProfileTab}
		roleTab { module RolesTab}
	}
}
