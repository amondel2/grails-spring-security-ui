/* Copyright 2009-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springsecurity.ui

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.ui.strategy.UserStrategy
import groovy.transform.CompileStatic

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class UserController extends AbstractS2UiDomainController {

	/** Dependency injection for the 'uiUserStrategy' bean. */
	UserStrategy uiUserStrategy

	def create() {
		super.create()
	}

	def save() {
		withForm {
			doSave uiUserStrategy.saveUser(params, roleNamesFromParams(), params.password)
		}.invalidToken {
			doSaveWithInvalidToken(params.username)
		}
	}

	def edit() {
		super.edit()
	}

	def update() {
		withForm {
			doUpdate { user ->
				uiUserStrategy.updateUser params, user, roleNamesFromParams()
			}
		}.invalidToken {
			doUpdateWithInvalidToken(params.username)
		}
	}

	def delete() {
		withForm{
			tryDelete { user ->
				uiUserStrategy.deleteUser user
			}
		}.invalidToken {
			doDeleteWithInvalidToken()
		}
	}

	def search() {
		if (!isSearch()) {
			// show the form
			return
		}

		def results = doSearch { ->
			like 'username', delegate
			eqBoolean 'accountExpired', delegate
			eqBoolean 'accountLocked', delegate
			eqBoolean 'enabled', delegate
			eqBoolean 'passwordExpired', delegate
		}

		renderSearch results: results, totalCount: results.totalCount,
		            'accountExpired', 'accountLocked', 'enabled', 'passwordExpired', 'username'
	}

	protected lookupFromParams() {
		findUserByUsername(params.username) ?: byId()
	}

	protected List<String> roleNamesFromParams() {
		params.keySet().findAll { it.contains('ROLE_') && params[it] == 'on' } as List
	}

	protected Map buildUserModel(user) {

		Set userRoleNames = user[authoritiesPropertyName].collect { it[authorityNameField] }
		Map roleMap = buildRoleMap(userRoleNames, sortedRoles())

		[tabErrors:buildTabErrorMap(user.hasProperty('tabErrors')?user.tabErrors:null) ,roleMap: roleMap, tabData: tabData, user: user, secQuestions:getSecQuections(user)]
	}

	protected buildTabErrorMap(tabError) {
		def rtn = [:]
		tabError.collect {key, val ->
			rtn[key] = [:]
			val?.each{ k, v ->
				rtn[key][k] = message(error: v.error)
			}
		}
		rtn
	}

	@CompileStatic
	protected Map buildRoleMap(Set userRoleNames, List sortedRoles) {
		Map granted = [:]
		Map notGranted = [:]
		for (role in sortedRoles) {
			String authority = role[authorityNameField]
			if (userRoleNames?.contains(authority)) {
				granted[(role)] = true
			} else {
				notGranted[(role)] = false
			}
		}
		granted + notGranted
	}

	protected List sortedRoles() {
		Role.list().sort { it[authorityNameField] }
	}

	protected getTabData() {
		def rtn = [
			[name: 'userinfo', icon: 'icon_user', message: message(code: 'spring.security.ui.user.info')],
			[name: 'roles', icon: 'icon_role', message: message(code: 'spring.security.ui.user.roles')]]
		if (SpringSecurityUtils.securityConfig.ui.forgotPassword?.forgotPasswordExtraValidation?.size() > 0) {
			return	rtn + [name: 'secQuestions', icon: 'icon_role', message: message(code: 'spring.security.ui.menu.' + SpringSecurityUtils.securityConfig.ui.forgotPassword.forgotPasswordExtraValidationDomainClassName.substring(SpringSecurityUtils.securityConfig.ui.forgotPassword.forgotPasswordExtraValidationDomainClassName.lastIndexOf('.') + 1))]
		}
		rtn
	}

	protected getSecQuections(user) {
		uiUserStrategy.getSecurityQuestionsForUser(user, params)
	}

	protected Class<?> getClazz() { User }
	protected String getClassLabelCode() { 'user.label' }

	protected Map model(user, String action) {
		if (action == 'edit' || action == 'update') {
			buildUserModel user
		}
		else {
			[tabErrors:buildTabErrorMap(user.hasProperty('tabErrors')?user.tabErrors:null), user: user, authorityList: sortedRoles(), tabData: tabData,secQuestions: ( action == 'create' || action == 'save') ?  getSecQuections(null)  : getSecQuections(user)]
		}
	}

	protected String authoritiesPropertyName

	void afterPropertiesSet() {
		super.afterPropertiesSet()

		if (!conf.userLookup.userDomainClassName) {
			return
		}

		authoritiesPropertyName = conf.userLookup.authoritiesPropertyName ?: ''
	}
}
