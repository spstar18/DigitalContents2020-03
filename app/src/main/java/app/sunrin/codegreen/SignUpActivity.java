package app.sunrin.codegreen;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    String[][] finalValue;
    String[] newValue;
    public static String yy,mm,dd;
    FirebaseDatabase database;
    Boolean isDateChanged = false;
    DatabaseReference myRef;
    int age;
    RadioGroup radioGroup;
    int totalLength = 0;
    public static SignUpActivity signUpActivity;
    Boolean radioChecked = false;

    boolean childCalled = false;
    Boolean sex;
    SharedPreferences preferencesBirth, preferencesSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);




        preferencesSex = getSharedPreferences("sex", 0);
        SharedPreferences.Editor editorSex = preferencesSex.edit();

        final TextInputLayout id_Layout = findViewById(R.id.textProductName);
        final TextInputLayout pw_Layout = findViewById(R.id.TextInputChangePassword);
        final TextInputEditText id_ET = findViewById(R.id.text_ID);
        final TextInputEditText pw_ET = findViewById(R.id.text_PW);

        MaterialButton makeBtn = findViewById(R.id.makeBtn);

        signUpActivity = SignUpActivity.this;

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton)
                {
                    editorSex.putBoolean("sex", true);
                    sex = true;
                    radioChecked = true;
                }
                else if(checkedId == R.id.radioButton2)
                {
                    editorSex.putBoolean("sex", false);
                    sex = false;
                    radioChecked = true;
                }
                else
                {
                    radioChecked = false;
                }

            }
        });


        DatePicker datepicker = findViewById(R.id.signupDatePicker);

        datepicker.init(datepicker.getYear(), datepicker.getMonth(), datepicker.getDayOfMonth(),

                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        isDateChanged = true;
                        age = getAge(year, monthOfYear, dayOfMonth);
                        yy=Integer.toString(year);
                        if(monthOfYear+1<10)
                        {
                            mm=Integer.toString(monthOfYear+1);
                            mm = "0" + mm;
                        }
                        else
                        {
                            mm=Integer.toString(monthOfYear+1);
                        }

                        if(dayOfMonth<10)
                        {
                            dd=Integer.toString(dayOfMonth);
                            dd = "0"+dd;
                        }
                        else
                        {
                            dd=Integer.toString(dayOfMonth);
                        }


                    }
                });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        int maxVal = 8;

        finalValue = new String[10000][maxVal];


        myRef = database.getReference("user");


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(childCalled)
                {

                }
                else
                {
                    childCalled = true;
                    String valueAll = snapshot.getValue().toString();
                    valueAll = valueAll.replace("{", "").replace("}", "")
                            .replace("userSex=", "")
                            .replace("userYear=", "")
                            .replace("userMonth=", "")
                            .replace("userPW=", "")
                            .replace("userDay=", "")
                            .replace("userID=", "")
                            .replace("value=", "")
                            .replace("userAge=", "");
                    String[] newValue = valueAll.split(", ");
                    System.out.println(valueAll + "★");
                    System.out.println(newValue[0] + newValue[1] + newValue[2]);

                    for(int j = 0; j<newValue.length; j++)
                    {
                        finalValue[totalLength][j] = newValue[j];
                        System.out.println(newValue[j]);
                    }
                    totalLength++;
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String value = dataSnapshot.getValue().toString();
//                System.out.println(value);
//                value = value.substring(0, value.length()-2);
//                value = value.replace("}, ", "☆");
//                //굳이 별로 바꾼 이유는 split에서 정규식을 사용하기 때문에 이렇게 안 하면 에러가 발생하기 때문
//                //자세한 것은 이 링크 참고 : https://mytory.net/archives/285
//                System.out.println(value);
//
//
//                newValue = value.split("☆");
//                System.out.println(newValue.length);
//
//
//                finalValue = new String[newValue.length][3];
//
//                for(int i = 0; i<newValue.length; i++)
//                {
//                    finalValue[i][0] = newValue[i].substring(newValue[i].indexOf("D=")+2, newValue[i].lastIndexOf(", "));
//                    finalValue[i][1] = newValue[i].substring(newValue[i].indexOf("W=")+2, newValue[i].indexOf(", "));
//                    finalValue[i][2] = newValue[i].substring(newValue[i].indexOf("e=")+2);
//
//                    System.out.println("가" + finalValue[i][0]);
//                    System.out.println("나" + finalValue[i][1]);
//                    System.out.println("다" + finalValue[i][2]);
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error)
//            {
//                System.out.println("ERROR");
//
//            }
//        });



        makeBtn.setOnClickListener(v -> {
            String input_id = id_ET.getText().toString().replace(" ", "");
            String input_pw = pw_ET.getText().toString().replace(" ", "");

            int id = 5;
            int pw = 3;

            boolean passwordExist = false;
            for(int i = 0; i<totalLength; i++)
            {
                if(finalValue[i][id].equals(input_id))
                {
                    passwordExist = true;
                    break;
                }

            }
            // 입력한 ID에 따른 비밀번호 존재 여부 확인
            if (!passwordExist) { // 없을 때
                id_Layout.setErrorEnabled(false);
                if (input_pw.length() >= 8) {


                    if(radioChecked)
                    {
                        if(isDateChanged)
                        {
                            preferencesBirth = getSharedPreferences("birth", 0);
                            System.out.println(yy);
                            preferencesBirth.edit().putInt("year", Integer.parseInt(yy));
                            preferencesBirth.edit().putInt("month", Integer.parseInt(mm));
                            System.out.println(Integer.parseInt(mm));
                            preferencesBirth.edit().putInt("day", Integer.parseInt(dd));
                            preferencesBirth.edit().putInt("age", age);
                            preferencesBirth.edit().commit();





                            myRef = database.getReference("user/" + input_id + "/userID");
                            myRef.setValue(input_id);

                            myRef = database.getReference("user/" + input_id + "/userPW");
                            myRef.setValue(input_pw);

                            myRef = database.getReference("user/" + input_id + "/value");
                            myRef.setValue(" ");

                            myRef = database.getReference("user/" + input_id + "/userSex");
                            myRef.setValue(sex);

                            myRef = database.getReference("user/" + input_id + "/userYear");
                            myRef.setValue(Integer.parseInt(yy));

                            myRef = database.getReference("user/" + input_id + "/userMonth");
                            myRef.setValue(Integer.parseInt(mm));

                            myRef = database.getReference("user/" + input_id + "/userDay");
                            myRef.setValue(Integer.parseInt(dd));

                            myRef = database.getReference("user/" + input_id + "/userAge");
                            myRef.setValue(age);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference("/userNum").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int value = snapshot.getValue(Integer.class);
                                    database.getReference("/userNum").setValue(value + 1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(SignUpActivity.this, "새 아이디를 생성했습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(this, "생년월일을 설정해주세요.", Toast.LENGTH_LONG).show();
                        }


                    }
                    else
                    {
                        Toast.makeText(this, "성별을 입력해주세요.", Toast.LENGTH_LONG).show();
                    }



                }
            }
            else
            {
                id_Layout.setError("이미 존재하는 아이디입니다.");
            }
            // 에러 처리
            if (input_id.length() <= 0)
                id_Layout.setError("아이디를 입력해주세요.");
            if (input_pw.length() <= 0)
                pw_Layout.setError("비밀번호를 입력해주세요.");
            else if (input_pw.length() < 8)
                pw_Layout.setError("비밀번호는 8자 이상입니다.");
            else
                pw_Layout.setErrorEnabled(false);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public int getAge(int birthYear, int birthMonth, int birthDay)
    {
        Calendar current = Calendar.getInstance();
        int currentYear  = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay   = current.get(Calendar.DAY_OF_MONTH);

        int age = currentYear - birthYear;
        // 생일 안 지난 경우 -1
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
            age--;

        return age;
    }
}