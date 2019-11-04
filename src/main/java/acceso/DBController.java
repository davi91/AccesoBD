package acceso;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class DBController implements Initializable {

	public DBController() throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBFXML.fxml"));
		loader.setController(this);
		loader.load();
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
