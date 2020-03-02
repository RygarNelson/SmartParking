package com.example.pick_a_park;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignUpActivity extends AppCompatActivity implements ConnessioneListener {
    private ProgressDialog caricamento = null;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }
    /**
     * This method will send user credentials for registration.
     */
    public void TakePicture(View view){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {



                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }



                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView imageview = findViewById(R.id.img);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle extras = imageReturnedIntent.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    imageview.setImageBitmap(bitmap);
                    Toast.makeText(this, Parametri.path, Toast.LENGTH_LONG).show();
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageview.setImageURI(selectedImage);
                    BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
                    bitmap = drawable.getBitmap();
                    Toast.makeText(this, Parametri.path, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        if(bitmapImage == null)
            return "no image";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile" + Parametri.id+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    public void sendDataForSignUp(View view){
        //Prendo i dati dalla form:
        EditText nome = findViewById(R.id.nome);
        EditText cognome = findViewById(R.id.cognome);
        EditText dataDinascita = findViewById(R.id.data_nascita);
        EditText telefono = findViewById(R.id.telefono);
        EditText fiscal_code = findViewById(R.id.CF);
        EditText mail = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText passwordr = findViewById(R.id.repPass);

        //Converto i dati in stringa per inviarli al server
        String nomes = nome.getText().toString();
        String cognomes = cognome.getText().toString();
        String dataDinascitas = dataDinascita.getText().toString();
        String telefonos = telefono.getText().toString();
        String fiscal_codes = fiscal_code.getText().toString();
        String mails = mail.getText().toString();
        String passwords = password.getText().toString();
        String passwordrs = passwordr.getText().toString();

        if (!passwords.equals(passwordrs))
        {
            Toast.makeText(this, "ERROR:\nPasswords are not equals.", Toast.LENGTH_LONG).show();
            return;
        }
        else
        if (nomes.length() < 1 || cognomes.length() < 1 || dataDinascitas.length() < 1 || telefonos.length() < 1 || mails.length() < 1)
        {
            Toast.makeText(this, "ERROR:\nFields can not be empty.", Toast.LENGTH_LONG).show();
            return;
        }else
        if ( dataDinascitas.indexOf(" ") != -1 || telefonos.indexOf(" ") != -1 || mails.indexOf(" ") != -1 ||passwords.indexOf(" ") != -1 || passwordrs.indexOf(" ") != -1)
        {
            Toast.makeText(this, "ERROR:\nYou can not use space in password, mail, date or phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            passwords = SHA1(passwords);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "ERRORE:\nimpossibile hashare la password da inviare.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject postData = new JSONObject();
        JSONObject autista = new JSONObject();

        try {
            autista.put("password", passwords);
            autista.put("email", mails);
            autista.put("nome", nomes);
            autista.put("CF", fiscal_codes);
            autista.put("cognome", cognomes);
            autista.put("dataDiNascita", dataDinascitas);
            autista.put("telefono", telefonos);
            autista.put("foto", Parametri.profile_image );
            postData.put("autista", autista);

        }catch (Exception e){
            Toast.makeText(this, "ERRORE:\nimpossibile leggere i campi appena compilati.", Toast.LENGTH_LONG).show();
            return;
        }


        // Avverto l'utente del tentativo di invio dei dati di login al server
        caricamento = ProgressDialog.show(SignUpActivity.this, "",
                "Connessione con il server in corso...", true);
        Connessione conn = new Connessione(postData, "POST");
        conn.addListener(this);
        conn.execute(Parametri.IP + "/api/auth/register");
        Parametri.profile_image=null;
    }
    public void returnToLogin(View view){
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
    @Override
    public void ResultResponse(String responseCode, String result) {
        if (responseCode == null) {
            caricamento.dismiss();
            Toast.makeText(getApplicationContext(), "ERRORE:\nConnessione Assente o server offline.", Toast.LENGTH_LONG).show();
            return;
        }

        if (responseCode.equals("400")) {
            String message = Connessione.estraiErrore(result);
            caricamento.dismiss();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            return;
        }

        if (responseCode.equals("200")) {
            String message;

            // Estraggo i miei dati restituiti dal server
            try {
                JSONObject token = new JSONObject(result);
                JSONObject autistajs = new JSONObject(token.getString("autista"));
                Parametri.id = autistajs.getString("id");
                Parametri.Token = autistajs.getString("token");
                Parametri.nome = autistajs.getString("nome");
                Parametri.cognome = autistajs.getString("cognome");
                Parametri.data_nascita = autistajs.getString("dataDiNascita");
                Parametri.email = autistajs.getString("email");
                Parametri.telefono = autistajs.getString("telefono");

                message = "Welcome " + Parametri.nome + ".";
                /*
                // Tento l'estrazione dei dati della carta di credito
                if (autistajs.has("carta_di_credito")) {
                    carta = new JSONObject(autistajs.getString("carta_di_credito"));

                    if (carta.has("numero_carta"))
                        Parametri.numero_carta = carta.getString("numero_carta");
                    if (carta.has("dataDiScadenza"))
                        Parametri.data_di_scadenza = carta.getString("dataDiScadenza");
                    if (carta.has("pin"))
                        Parametri.pin = carta.getString("pin");
                }
                */
                Parametri.path = saveToInternalStorage(bitmap);
                int x = 0;
            } catch (Exception e) {
                message = "Response Error.";

                caricamento.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return;
            }
            // Salvo i dati di login corretti
            saveData(Parametri.username, Parametri.password);

            caricamento.dismiss();
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
    //Criptazione SHA1
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    //Funzione per criptazione SHA1
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                }
                else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
    private void saveData(String username, String password) {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(Parametri.login_file.getAbsolutePath()));
            fos.write(username + "\n");
            fos.write(password + "\n");
            fos.write(Parametri.TEMPO_EXTRA + "\n");
            fos.write(Parametri.TEMPO_AVVISO + "\n");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
