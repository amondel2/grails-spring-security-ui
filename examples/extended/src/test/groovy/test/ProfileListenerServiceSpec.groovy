package test


import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

class ProfileListenerServiceSpec extends Specification implements ServiceUnitTest<ProfileListenerService>, DataTest {

    def setupSpec() {
        mockDomain Profile
        mockDomain User
    }

    Closure doWithSpring() {{ -> // <1>
        springSecurityService(grails.plugin.springsecurity.SpringSecurityService)

    }}

    @Rollback
    void "test my answer is encoded on insert"() {
        given:
        User u = new User(username: "adminTest",password: "pass",email: "test@user.com",accountLocked: false,accountExpired: false)
        Profile prof = new Profile(myQuestion2: 'Practical Grails 3', myQuestion1: 'Eric Helgeson', myAnswer2: "12345", myAnswer1: "1234", user: u)

        when:
        service.springSecurityService = Stub(grails.plugin.springsecurity.SpringSecurityService) {
            encodePassword(_ as String) >> 'XXXX-5125'
        }

        service.onProfilePreInsert(new PreInsertEvent(dataStore, prof))

        then:
        prof.myAnswer1 == 'XXXX-5125'
        prof.myAnswer2 == 'XXXX-5125'
    }

    @Rollback
    void "test my answer is encoded on update"() {
        given:
        User u = new User(username: "adminTest",password: "pass",email: "test@user.com",accountLocked: false,accountExpired: false)
        Profile prof = new Profile(myQuestion2: 'Practical Grails 3', myQuestion1: 'Eric Helgeson', myAnswer2: "12345", myAnswer1: "1234", user: u)

        when:

        service.springSecurityService = Stub(grails.plugin.springsecurity.SpringSecurityService) {
            encodePassword(_ as String) >> {
                args -> args[0] + 'XXXX-5125'
            }
        }

        prof.save(flush:true)
        service.onProfilePreInsert(new PreInsertEvent(dataStore, prof))

        prof.myAnswer2 = "1232321344"
        service.onProfilePreUpdate(new PreUpdateEvent(dataStore, prof))

        then:
        prof.myAnswer2 == '1232321344XXXX-5125'
        prof.myAnswer1 == '1234XXXX-5125'

    }
}
