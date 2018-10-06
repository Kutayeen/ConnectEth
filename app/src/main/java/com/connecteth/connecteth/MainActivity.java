package com.connecteth.connecteth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        final EditText passField = (EditText) findViewById(R.id.editText);
        final Web3j web3 = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/b39486461e69426f864a851cf3dc758f"));
        final SharedPreferences myPreferences =  getSharedPreferences("some_prefs_name", MODE_PRIVATE);
        final SharedPreferences.Editor myEditor = myPreferences.edit();
        if (!myPreferences.getBoolean("Created?", false)) {
            Button mButton = findViewById(R.id.button);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String seed = UUID.randomUUID().toString();
                    Credentials credentials = null;
                    try {
                    ECKeyPair exKey = Keys.createEcKeyPair();
                    WalletFile wallet = Wallet.createLight(seed, exKey);
                    credentials = Credentials.create(Wallet.decrypt(seed, wallet));
                    //create unique private key
                    BigInteger private_big = credentials.getEcKeyPair().getPrivateKey();

                    String key_private = private_big + "";
                    String key = passField.getText().toString();
                    // Write
                    myEditor.putString(encrypt(key), encrypt(key_private));
                    myEditor.apply(); // Or commit if targeting old devices
                    myEditor.putBoolean("Created?", true);
                    Log.w("", "Created new account");
                    myEditor.commit();
                    System.out.println("IN: " + credentials.getEcKeyPair().getPrivateKey());

                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (CipherException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    if(myPreferences.getBoolean("Created?", false)){
        final EthGetBalance[] ethGetBalance = {null};
        Button mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            { EthGetBalance ethGetBalance = null;
                try {
                    String key2 = passField.getText().toString();
                    // Read
                    String passEncrypted = myPreferences.getString(encrypt(key2), encrypt("default"));
                    String pass = decrypt(passEncrypted);
                    ECKeyPair exKey2 = ECKeyPair.create(new BigInteger(pass));
                    Credentials credentials2 = Credentials.create(exKey2);
                    String Address2 = credentials2.getAddress();
                    System.out.println("OUT: "+credentials2.getEcKeyPair().getPrivateKey());
                    ethGetBalance = web3.ethGetBalance(Address2, DefaultBlockParameterName.LATEST).sendAsync().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                BigInteger wei = ethGetBalance.getBalance();
                }
            });
        }
    }
    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }
    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}
