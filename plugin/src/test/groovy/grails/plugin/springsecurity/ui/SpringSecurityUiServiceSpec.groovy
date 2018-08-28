package grails.plugin.springsecurity.ui

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.ui.strategy.DefaultPropertiesStrategy
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll


class SpringSecurityUiServiceSpec extends Specification implements ServiceUnitTest<SpringSecurityUiService> {

    void cleanup() {
        SpringSecurityUtils.resetSecurityConfig()
    }

    @Unroll
    void "forgot password email subject is loaded from config if config exists"() {
        given: "the subject text exists in messages.properties"
        addForgotPasswordEmailSubjectToMessageSource()

        and: "the legacy config file exists"
        if (hasConfig) {
            SpringSecurityUtils.securityConfig.ui.forgotPassword.emailSubject = 'This is from the config'
        }

        and: "the properties strategy is set"
        service.uiPropertiesStrategy = new DefaultPropertiesStrategy(springSecurityUiService: service)

        and: "the service is initialized"
        updateFromConfig()

        when: "the value of forgotPasswordEmailSubject is requested"
        String results = service.forgotPasswordEmailSubject

        then: "the value from config is used if it exists, else the value from messages.properties is used"
        results == expectedResults

        where:
        hasConfig | expectedResults
        true      | 'This is from the config'
        false     | 'Password Reset'
    }

    void "getSecurityQuestionsForUser(user)"() {
        given:
        SpringSecurityUtils.securityConfig.ui.forgotPassword
    }

    private void updateFromConfig() {
        service.messageSource = messageSource
        service.initialize()
    }

    protected void addForgotPasswordEmailSubjectToMessageSource() {
        messageSource.addMessage 'spring.security.ui.forgotPassword.email.subject', Locale.US, 'Password Reset'
    }


}