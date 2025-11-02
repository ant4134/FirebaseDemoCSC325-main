package aydin.firebasedemo;

import javafx.fxml.FXML;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WelcomeController {
    @FXML
    private Button loginButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button registerButton;

    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }

    public void registerUser() {
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Users").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

            Map<String, Object> data = new HashMap<>();
            data.put("Email", emailTextField.getText());
            data.put("Phone Number", passwordTextField.getText());

            //asynchronously write data
            ApiFuture<WriteResult> result = docRef.set(data);

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    public boolean loginUser(ActionEvent actionEvent) {
        boolean key = false;
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Users").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Getting (reading) data from firebase database....");
                Map<String, Object> data = new HashMap<>();
                for (QueryDocumentSnapshot document : documents)
                {
                    if(emailTextField.getText().equals(document.getData().get("Email")) ||
                            passwordTextField.getText().equals(document.getData().get("Password")))
                    {
                        System.out.println("That is a valid username and password. Logging in now.");
                        key = true;
                        switchToPrimary();
                    }
                    else {
                        System.out.println("That is not a valid username and password.");
                    }
                }
            }
            else
            {
                System.out.println("No data.");
            }

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

}
