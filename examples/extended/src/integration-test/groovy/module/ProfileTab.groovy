package module
import geb.Module

class ProfileTab extends Module {

    static content = {
        tab { $("a", href: "#tab-secQuestions") }

    }

    void select() {
        tab.click()
        waitFor { $("#tab-secQuestions") }
    }


}
