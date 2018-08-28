package grails.plugin.springsecurity.ui

import groovy.transform.CompileStatic

@CompileStatic
class TabTypes {

    private TabTypes() {}

    public getInstance() {
        if (!this.t || this.t.equals(null)) {
            t = new TabTypes();
        }
        return this.t;
    }
    TabTypes t
    HashMap tabTypes = [(MainTabTypes.User):['userinfo', 'roles', 'secQuestions'], (MainTabTypes.Role):['roleinfo', 'users']]
}