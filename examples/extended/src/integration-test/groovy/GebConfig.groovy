// See: http://www.gebish.org/manual/current/configuration.html
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeDriver

environments {

	chrome {
		driver = { new ChromeDriver() }
	}

	chromeHeadless {
		driver = {
			ChromeOptions o = new ChromeOptions()
			o.addArguments('headless')
			new ChromeDriver(o)
		}
	}
}
