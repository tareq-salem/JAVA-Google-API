/**
 *
 */
package fr.houseofcode.dap.google;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;

/**
 * @author adminHOC
 *
 */
@Service
public class GoogleUserService extends GoogleService {

    /**
     *
     * @return userToken
     */
    public DataStore<StoredCredential> getUserCredential() {

        DataStore<StoredCredential> response = null;
        try {

            response = getFlow().getCredentialDataStore();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return response;

    }

}
