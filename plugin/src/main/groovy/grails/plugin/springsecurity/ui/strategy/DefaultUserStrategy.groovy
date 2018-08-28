/* Copyright 2015-2016 the original author or authors.
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
package grails.plugin.springsecurity.ui.strategy

import grails.plugin.springsecurity.ui.SpringSecurityUiService
import groovy.transform.CompileStatic

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@CompileStatic
class DefaultUserStrategy implements UserStrategy {

	SpringSecurityUiService springSecurityUiService

	def saveUser(Map properties, List<String> roleNames, String password) {
		springSecurityUiService.saveUser properties, roleNames, password
	}

	void updateUser(Map properties, user, List<String> roleNames) {
		springSecurityUiService.updateUser properties, user, roleNames
	}

	void deleteUser(user) {
		springSecurityUiService.deleteUser user
	}

	def getSecurityQuestionsForUser(user,Map params) {
		springSecurityUiService.getSecurityQuestionsForUser user, params
	}
}
