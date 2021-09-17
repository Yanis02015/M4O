package com.l3.m4o;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        this.configureToolbar();

        Button buttonSend = findViewById(R.id.activity_contact_send_btn);
        TextView name = findViewById(R.id.activity_contact_name_txt);
        TextView subject = findViewById(R.id.activity_contact_subject_txt);
        TextView email = findViewById(R.id.activity_contact_email_txt);
        TextView message = findViewById(R.id.activity_contact_message_txt);

        buttonSend.setOnClickListener(view -> {
            String txtName = name.getText().toString();
            String txtSubject = subject.getText().toString();
            String txtEmail = email.getText().toString();
            String txtMessage = message.getText().toString();

            if (txtName.length() == 0 || txtEmail.length() == 0 || txtMessage.length() == 0 || txtSubject.length() == 0)
                this.showToast("Tous les champs obligatoires* doivent être saisis");
            else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"m4o.feedback@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, txtSubject);
                intent.putExtra(Intent.EXTRA_TEXT, txtMessage);
                //this.startActivity(Intent.createChooser(intent, "Choose an Email Client : "));
                String sendMailTo = "mailto:m4o.feedback@gmail.com" +
                        "?subject=" +
                        txtSubject +
                        "&body=Nom : " +
                        txtName +
                        "%0A" +
                        "E-mail : " +
                        txtEmail +
                        "%0A%0A" +
                        "Contenu : " +
                        txtMessage;
                Uri uri = Uri.parse(sendMailTo);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                this.startActivity(intent1);
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_contact_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }
}