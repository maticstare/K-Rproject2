package fri.sparovcek;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class SceneController {
    private static Stage stage;
    private static Scene scene;

    private static String username;

    private static double domaciRacun;
    private static double sluzbeniRacun;
    private static double neobdavceniRacun;

    private static List<String> history;

    private static List<String> currentUpn;

    private static final Dictionary<String, List<String>> savedUpns = new java.util.Hashtable<>();

    private static List<String> currentTransfer;

    private static final Dictionary<String, List<String>> savedTransactions = new java.util.Hashtable<>();


    //switching scenes functions
    public void switchToMenuScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("menuView.fxml")));
        setStageScene(event, root);
        Label welcomeUser = (Label) scene.lookup("#welcomeUser");
        welcomeUser.setText("Pozdravljeni, " + username + "!");
        welcomeUser.setAlignment(Pos.CENTER);
    }

    public void switchToStanjeScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("stanjeView.fxml")));
        setStageScene(event, root);
        Label domaciRacunLabel = (Label) scene.lookup("#domaciRacun");
        domaciRacunLabel.setText("Domači račun: " + domaciRacun + "€");
        Label sluzbeniRacunLabel = (Label) scene.lookup("#sluzbeniRacun");
        sluzbeniRacunLabel.setText("Službeni račun: " + sluzbeniRacun + "€");
        Label neobdavceniRacunLabel = (Label) scene.lookup("#neobdavceniRacun");
        neobdavceniRacunLabel.setText("Neobdavčeni račun: " + neobdavceniRacun + "€");
        domaciRacunLabel.setAlignment(Pos.CENTER);
        sluzbeniRacunLabel.setAlignment(Pos.CENTER);
        neobdavceniRacunLabel.setAlignment(Pos.CENTER);
        history.add("Pregled stanja.");
    }

    public void switchToConfirmLogoffScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("confirmLogoffView.fxml")));
        setStageScene(event, root);
        Label historyLabel = (Label) scene.lookup("#history");
        StringBuilder setText = new StringBuilder();
        for (String s : history) {
            setText.append("- ").append(s).append("\n");
        }
        historyLabel.setText(setText.toString());
    }



    //login scene functions
    public static void openLoginScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Sparovcek.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
        SceneController.scene = scene;
        SceneController.stage = stage;
    }

    public void displayLoginError() {
        Label loginError = (Label) scene.lookup("#loginError");
        loginError.setVisible(true);
    }

    public boolean checkIfValidLogin() {
        TextField username = (TextField) scene.lookup("#loginUsername");
        PasswordField password = (PasswordField) scene.lookup("#loginPassword");
        return !(username.getText().isEmpty() || password.getText().isEmpty());

    }

    public void onLogin(ActionEvent event) throws IOException {
        if (checkIfValidLogin()) {
            username = ((TextField) scene.lookup("#loginUsername")).getText();
            domaciRacun = 1000;
            sluzbeniRacun = 2000;
            neobdavceniRacun = 3000;
            history = new ArrayList<>();
            switchToMenuScene(event);

        } else {
            displayLoginError();
        }
    }



    //transfer scene functions
    public void switchToTransferScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("transferView.fxml")));
        setStageScene(event, root);
    }

    public void onTransfer(ActionEvent event) throws IOException {
        if (checkIfValidTransfer()) {
            switchToConfirmTransferScene(event);
        } else {
            displayTransferError();
        }
    }

    private boolean checkIfValidTransfer() {
        TextField znesek = ((TextField) scene.lookup("#znesek"));
        TextField iban = ((TextField) scene.lookup("#iban"));
        TextField osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek"));
        String racun = (String) ((ComboBox<?>) scene.lookup("#izbiraRacuna")).getValue();

        currentTransfer = new ArrayList<>();
        currentTransfer.add(znesek.getText());
        currentTransfer.add(iban.getText());
        currentTransfer.add(osebniZaznamek.getText());
        currentTransfer.add(racun);

        return (racun != null &&
                !znesek.getText().isEmpty() &&
                !iban.getText().isEmpty());
    }

    public void displayTransferError() {
        Label transferError = (Label) scene.lookup("#transferError");
        transferError.setVisible(true);
    }

    public void switchToConfirmTransferScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("transferConfirmView.fxml")));
        collectTransfer(event, root);
        ((ComboBox<?>) scene.lookup("#izbiraRacuna")).setPromptText(currentTransfer.get(3));

    }

    private void collectTransfer(ActionEvent event, Parent root) {
        setStageScene(event, root);

        TextField znesek = ((TextField) scene.lookup("#znesek"));
        TextField iban = ((TextField) scene.lookup("#iban"));
        TextField osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek"));

        znesek.setText(currentTransfer.get(0));
        iban.setText(currentTransfer.get(1));
        osebniZaznamek.setText(currentTransfer.get(2));
    }

    public void onConfirmTransfer(ActionEvent actionEvent) throws IOException {
        history.add("Prenos sredstev na drug račun (" + currentTransfer.get(2) + ").");

        String osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek")).getText();
        if (!osebniZaznamek.isEmpty()) {
            savedTransactions.put(osebniZaznamek, currentTransfer);
        }
        double znesek = Double.parseDouble(currentTransfer.get(0));
        String racun = currentTransfer.get(3);
        if (racun.equals("Domači račun"))
            domaciRacun -= znesek;
        else if (racun.equals("Službeni račun"))
            sluzbeniRacun -= znesek;
        else
            neobdavceniRacun -= znesek;
        switchToMenuScene(actionEvent);

    }

    public void returnToTransferScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("transferView.fxml")));
        collectTransfer(event, root);
    }

    public void onSelectTransferTemplate(ActionEvent event) throws IOException {
        String osebniZaznamek = ((ComboBox<String>) scene.lookup("#izbiraTemplata")).getValue();
        if (osebniZaznamek == null){
            displayTransferTemplateError();
            return;
        }
        switchToTransferScene(event);
        List<String> transaction = savedTransactions.get(osebniZaznamek);
        TextField znesek = ((TextField) scene.lookup("#znesek"));
        TextField iban = ((TextField) scene.lookup("#iban"));
        TextField osebniZaznamekField = ((TextField) scene.lookup("#osebniZaznamek"));

        znesek.setText(transaction.get(0));
        iban.setText(transaction.get(1));
        osebniZaznamekField.setText(transaction.get(2));
    }

    private void displayTransferTemplateError() {
        Label transferTemplateError = (Label) scene.lookup("#transferTemplateError");
        transferTemplateError.setVisible(true);
    }

    public void switchToTemplateTransferScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("selectTransferTemplateView.fxml")));
        setStageScene(event, root);
        ComboBox<String> izbiraTemplata = (ComboBox<String>) scene.lookup("#izbiraTemplata");
        izbiraTemplata.setItems(FXCollections.observableArrayList(getKeys(savedTransactions)));

    }




    //upn scene functions
    public void switchToUpnScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("upnView.fxml")));
        setStageScene(event, root);
    }
    public boolean checkIfValidUpn() {
        String znesek = ((TextField) scene.lookup("#znesek")).getText();
        String iban = ((TextField) scene.lookup("#iban")).getText();
        String referenca = ((TextField) scene.lookup("#referenca")).getText();
        String nazivPrejemnika = ((TextField) scene.lookup("#nazivPrejemnika")).getText();
        String naslovPrejemnika = ((TextField) scene.lookup("#naslovPrejemnika")).getText();
        String osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek")).getText();
        String racun = (String) ((ComboBox<?>) scene.lookup("#izbiraRacuna")).getValue();

        currentUpn = new ArrayList<>();
        currentUpn.add(znesek);
        currentUpn.add(iban);
        currentUpn.add(referenca);
        currentUpn.add(nazivPrejemnika);
        currentUpn.add(naslovPrejemnika);
        currentUpn.add(osebniZaznamek);
        currentUpn.add(racun);

        return (!znesek.isEmpty() &&
                !referenca.isEmpty() &&
                !nazivPrejemnika.isEmpty() &&
                !naslovPrejemnika.isEmpty() &&
                !iban.isEmpty());
    }
    public void onPlacajUpn(ActionEvent event) throws IOException {
        if (checkIfValidUpn()) {
            switchToConfirmUpnScene(event);
        } else {
            displayUpnError();
        }
    }


    public void displayUpnError() {
        Label upnError = (Label) scene.lookup("#upnError");
        upnError.setVisible(true);
    }

    public void switchToConfirmUpnScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("upnConfirmView.fxml")));
        collectUpn(event, root);
        ((ComboBox<?>) scene.lookup("#izbiraRacuna1")).setPromptText(currentUpn.get(6));
    }

    private void collectUpn(ActionEvent event, Parent root) {
        setStageScene(event, root);

        TextField znesek = ((TextField) scene.lookup("#znesek"));
        TextField iban = ((TextField) scene.lookup("#iban"));
        TextField referenca = ((TextField) scene.lookup("#referenca"));
        TextField nazivPrejemnika = ((TextField) scene.lookup("#nazivPrejemnika"));
        TextField naslovPrejemnika = ((TextField) scene.lookup("#naslovPrejemnika"));
        TextField osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek"));

        znesek.setText(currentUpn.get(0));
        iban.setText(currentUpn.get(1));
        referenca.setText(currentUpn.get(2));
        nazivPrejemnika.setText(currentUpn.get(3));
        naslovPrejemnika.setText(currentUpn.get(4));
        osebniZaznamek.setText(currentUpn.get(5));
    }

    public void returnToUpnScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("upnView.fxml")));
        collectUpn(event, root);
    }

    public void onConfirmPlacajUpn(ActionEvent actionEvent) throws IOException {
        history.add("Plačilo UPN naloga (" + currentUpn.get(5) + ").");

        String osebniZaznamek = ((TextField) scene.lookup("#osebniZaznamek")).getText();
        if (!osebniZaznamek.isEmpty()) {
            savedUpns.put(osebniZaznamek, currentUpn);
        }
        double znesek = Double.parseDouble(currentUpn.getFirst());
        String racun = currentUpn.getLast();
        if (racun.equals("Domači račun"))
            domaciRacun -= znesek;
        else if (racun.equals("Službeni račun"))
            sluzbeniRacun -= znesek;
        else
            neobdavceniRacun -= znesek;
        switchToMenuScene(actionEvent);
    }

    public void switchToTemplateUpnScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("selectUpnTemplateView.fxml")));
        setStageScene(event, root);
        ComboBox<String> izbiraTemplata = (ComboBox<String>) scene.lookup("#izbiraTemplata");
        izbiraTemplata.setItems(FXCollections.observableArrayList(getKeys(savedUpns)));
    }

    public void onSelectUpnTemplate(ActionEvent event) throws IOException {
        String osebniZaznamek = ((ComboBox<String>) scene.lookup("#izbiraTemplata")).getValue();
        if (osebniZaznamek == null){
            displayUpnTemplateError();
            return;
        }
        switchToUpnScene(event);
        List<String> transaction = savedUpns.get(osebniZaznamek);

        TextField znesek = ((TextField) scene.lookup("#znesek"));
        TextField iban = ((TextField) scene.lookup("#iban"));
        TextField referenca = ((TextField) scene.lookup("#referenca"));
        TextField nazivPrejemnika = ((TextField) scene.lookup("#nazivPrejemnika"));
        TextField naslovPrejemnika = ((TextField) scene.lookup("#naslovPrejemnika"));
        TextField osebniZaznamekField = ((TextField) scene.lookup("#osebniZaznamek"));

        znesek.setText(transaction.get(0));
        iban.setText(transaction.get(1));
        referenca.setText(transaction.get(2));
        nazivPrejemnika.setText(transaction.get(3));
        naslovPrejemnika.setText(transaction.get(4));
        osebniZaznamekField.setText(transaction.get(5));
    }

    private void displayUpnTemplateError() {
        Label upnTemplateError = (Label) scene.lookup("#upnTemplateError");
        upnTemplateError.setVisible(true);
    }

    //utility functions
    public ArrayList<String> getKeys(Dictionary<String, List<String>> dictionary) {
        ArrayList<String> keys = new ArrayList<>();
        Enumeration<String> enumeration = dictionary.keys();
        while (enumeration.hasMoreElements()) {
            keys.add(enumeration.nextElement());
        }
        return keys;
    }

    public void setStageScene(ActionEvent event, Parent root) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void exitApplication() {
        stage.close();
    }
}
