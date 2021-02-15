package com.example.elbarka.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.elbarka.Prevalent.Prevalent;
import com.example.elbarka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ForgetPasswordActivity extends AppCompatActivity {

    public String check = "" ;
    public TextView pageTitle , titleQuestions;
    public EditText phoneNumber , question_1 , question_2 ;
    public Button verify_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question_1 = findViewById(R.id.question_1);
        question_2 = findViewById(R.id.question_2);
        verify_Btn = findViewById(R.id.verify_btn);


    }

    @Override
    protected void onStart() {
        super.onStart();

        // make it invisable and change if user come from login
        phoneNumber.setVisibility(View.GONE);


        // we will check if user come from login activity oe setting activity
        if(check.equals("settings"))
        {

            pageTitle.setText(R.string.set_questions);

            titleQuestions.setText(R.string.please_set_answers_the_following_security_questions);

            verify_Btn.setText(R.string.set);

            // we create this method to check if customer have pervoius answer or not
            // if yes we will set answers on question edit text to be shown to customer before edit it
            displayPreviousAnswers();

            verify_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswers();


                }
            });

        }else if(check.equals("login")){

            phoneNumber.setVisibility(View.VISIBLE);

            verify_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    verifyUser();
                }
            });


        }
    }

    private void setAnswers(){


        // we add .tolowercase to pass answer and convert it to  small characher in data base and when csutomer try to write it again
        // will not diffrantion between capital adn small charachters
        String answer1 = question_1.getText().toString().toLowerCase();
        String answer2 = question_2.getText().toString().toLowerCase();

        if(question_1.equals("") && question_2.equals("")){

            Toast.makeText(ForgetPasswordActivity.this, R.string.please_answer_all_questions, Toast.LENGTH_SHORT).show();
        }else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(Prevalent.currentOnlineUsers.getPhone());

            // now we can add another child for questions in user data  called " sedcrity questions " and add answers in answer 1 and answer2 fields
            HashMap<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer1" , answer1);
            answerMap.put("answer2" , answer2);

            ref.child("Security Questions").updateChildren(answerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(ForgetPasswordActivity.this, R.string.answers_have_set_security_questions_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this , Home2Activity.class);
                        startActivity(intent);
                    }

                }
            });
        }
    }

    private void displayPreviousAnswers (){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Prevalent.currentOnlineUsers.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    // now we check if customer have perivous answer we will sent then in 2 strings and put then on question 1 and 2
                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    question_1.setText(ans1);
                    question_2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verifyUser() {

        String phone = phoneNumber.getText().toString();
        String answer1 = question_1.getText().toString().toLowerCase();
        String answer2 = question_2.getText().toString().toLowerCase();

        if(!phone.equals("") && !answer1.equals("") && !answer2.equals("")){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // now we will check if phone is exists or not to check after security questions
                    if (snapshot.exists()) {

                        String mPhone = snapshot.child("phone").getValue().toString();

                        if (snapshot.hasChild("Security Questions")) {

                            // now we will compare what customer write in answer question and what was saved in our data base

                            String ans1 = snapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = snapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)) {

                                Toast.makeText(ForgetPasswordActivity.this, R.string.your_1st_answer_wrong, Toast.LENGTH_SHORT).show();
                            } else if (!ans2.equals(answer2)) {

                                Toast.makeText(ForgetPasswordActivity.this, R.string.your_2nd_answer_wrong, Toast.LENGTH_SHORT).show();

                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                builder.setTitle(R.string.new_password);

                                final EditText newPassword = new EditText(ForgetPasswordActivity.this);
                                newPassword.setHint("Write new password here ...");
                                builder.setView(newPassword);
                                builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (!newPassword.getText().toString().equals("")) {

                                            // now will add new password in password field
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                Intent intent = new Intent(ForgetPasswordActivity.this , LoginActivity.class);
                                                                startActivity(intent);
                                                                dialog.dismiss();

                                                                Toast.makeText(ForgetPasswordActivity.this, R.string.password_change_sucssfully, Toast.LENGTH_SHORT).show();
                                                            } else {

                                                                Toast.makeText(ForgetPasswordActivity.this, R.string.please_try_again_later, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });


                                        } else {

                                            Toast.makeText(ForgetPasswordActivity.this, R.string.please_enter_password, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                    }
                                });
                                builder.show();

                            }
                        }else {
                            Toast.makeText(ForgetPasswordActivity.this, R.string.you_have_not_answer_seciurty_questions, Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, R.string.this_phone_not_exist, Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else {

            Toast.makeText(this, R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
        }



    }
}