package app.sunrin.codegreen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AfterScanActivity extends AppCompatActivity {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();
    String[] check_how;
    TextView productName, productCategory;
    String url, Shorturl, db_category, KANcode, result, link, stringEco, shareString = "", saveData = "", currentDate = format.format(calendar.getTime());
    Boolean prodName;
    Bitmap bitmap;
    ImageView img;
    RecyclerView recyclerView;
    ArrayList<Item> data = new ArrayList<>(); //분리배출 방법의 recyclerView에 넣는 arraylist
    Button buttonEco;
    boolean goShopping;
    SharedPreferences preferencesSaveAll, preferencesBarcodeResult;
    boolean isCategory = true;


    DatabaseReference myRef;
    ProgressDialog progressDialog;




//    public void shareKakao()
//    {
//        try{
//            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
//            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//            /*메시지 추가*/
//            kakaoBuilder.addText("카카오링크 테스트입니다.");
//
//            /*이미지 가로/세로 사이즈는 80px 보다 커야하며, 이미지 용량은 500kb 이하로 제한된다.*/
//            String url = "https://lh3.googleusercontent.com/4FMghyiNYU73ECn5bHOKG0X1Nv_A5J7z2eRjHGIGxtQtK7L-fyNVuqcvyq6C1vIUxgPP=w300-rw";
//            kakaoBuilder.addImage(url, 160, 160);
//
//            /*앱 실행버튼 추가*/
//            kakaoBuilder.addAppButton("앱 실행 혹은 다운로드");
//
//            /*메시지 발송*/
//            kakaoLink.sendMessage(kakaoBuilder, this);
//
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }



    // data arraylist에 데이터를 넣는 함수
    //플라스틱 1,종이 2,비닐 3,캔 4,스티로폼 5,페트병 6,유리 7,일반쓰레기 8, 전자제품 9
    public void addPlastic() {
        data.add(addItem(getResources().getDrawable(R.drawable.pp), "플라스틱", "플라스틱의 이물질을 제거 후 버려주세요."));
        shareString = shareString + "\n플라스틱 - 플라스틱의 이물질을 제거 후 버려주세요.";
        saveData = saveData + "1-";
    }

    public void addPaper() {
        data.add(addItem(getResources().getDrawable(R.drawable.paper), "종이", "종이에 붙어 있는 테이프 등 종이가 아닌 재질을 제거 후 버려주세요."));
        shareString = shareString + "\n종이 - 종이에 붙어 있는 테이프 등 종이가 아닌 재질을 제거 후 버려주세요.";
        saveData = saveData + "2-";
    }

    public void addVinyl() {
        data.add(addItem(getResources().getDrawable(R.drawable.vinyl), "비닐", "비닐의 이물질을 제거 후 버려주세요"));
        shareString = shareString + "\n비닐 - 비닐의 이물질을 제거 후 버려주세요.";
        saveData = saveData + "3-";
    }

    public void addCan() {
        data.add(addItem(getResources().getDrawable(R.drawable.can), "캔", "캔의 내용물을 제거 후 버려주세요"));
        shareString = shareString + "\n캔 - 캔의 내용물을 제거 후 버려주세요.";
        saveData = saveData + "4-";
    }

    public void addStrph() {
        data.add(addItem(getResources().getDrawable(R.drawable.ps), "스티로폼", "스티로폼의 이물질을 제거후 이물질이 없는 상태로 버려주세요"));
        shareString = shareString + "\n스티로폼 - 스티로폼의 이물질을 제거후 이물질이 없는 상태로 버려주세요.";
        saveData = saveData + "5-";
    }

    public void addPet() {
        data.add(addItem(getResources().getDrawable(R.drawable.pete), "페트병", "페트병 안의 내용물을 제거한 뒤 페트병에 붙어 있는 비닐과 뚜껑을 제거 후 분리수거 합니다."));
        shareString = shareString + "\n페트병 - 페트병 안의 내용물을 제거한 뒤 페트병에 붙어 있는 비닐과 뚜껑을 제거 후 분리수거 합니다.";
        saveData = saveData + "6-";
    }

    public void addTrash() {
        data.add(addItem(getResources().getDrawable(R.drawable.normal_black), "일반쓰레기", "종량제 봉투에 담아 버려주세요."));
        shareString = shareString + "\n일반쓰레기 - 종량제 봉투에 담아 버려주세요.";
        saveData = saveData + "8-";
    }

    public void addGlass() {
        data.add(addItem(getResources().getDrawable(R.drawable.glass), "유리", "공병의 경우 이물질을 제거 후 버려주세요."));
        shareString = shareString + "\n유리 - 공병의 경우 이물질을 제거 후 버려주세요.";
        saveData = saveData + "7-";
    }

    public void addElec() {
        data.add(addItem(getResources().getDrawable(R.drawable.bolt_black), "전자제품", "한 면의 길이가 1m 미만인 소형가전은 재활용품 배출시 함께 배출, 1m 이상인 대형가전은 대형폐가전 무상방문수거 서비스를 이용해 배출"));
        shareString = shareString + "\n전자제품 - 한 면의 길이가 1m 미만인 소형가전은 재활용품 배출시 함께 배출, 1m 이상인 대형가전은 대형폐가전 무상방문수거 서비스를 이용해 배출";
        saveData = saveData + "9 - ";
    }

    private class ProgressDialog extends AlertDialog {

        public ProgressDialog(@NonNull Context context) {
            super(context);
        }

        public ProgressDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        protected ProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("검색 중...");
        progressDialog.setMessage("검색 중입니다. 잠시만 기다려 주세요.");
        progressDialog.setCancelable(false);
        progressDialog.show();


        preferencesBarcodeResult = getSharedPreferences("BarcodeResult", 0);
        result = preferencesBarcodeResult.getString("result", "");
        Shorturl = "http://gs1.koreannet.or.kr";
        url = Shorturl + "/pr/" + result; //바코드를 통한 url 가져오기


        buttonEco = findViewById(R.id.buttonEco);

        // 네트워크 연결상태 체크
        if (NetworkConnection() == false) {
            NotConnected_showAlert();
        } else {
            initView();
        }


    }

    public Item addItem(Drawable img, String title, String way) {
        Item item = new Item();
        item.setRecycle_img(img);
        item.setRecycle_class(title);
        item.setRecycle_how(way);
        return item;
    }

    private void initView() {

        img = findViewById(R.id.product_img);
        productName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.pn);

        if (result.equals("")) {
            Toast.makeText(getApplicationContext(), "상품 정보가 없습니다.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            finish();
        } else {
            new getAll().execute();//상품명 가져오기
        }


    }

    private class getAll extends AsyncTask<String, Void, String> //상품명 가져오기
    {

        @Override
        protected String doInBackground(String... params) {
            try {
                //기본 코드
                Connection.Response response = Jsoup.connect(url).method(Connection.Method.GET).execute();
                Document document = response.parse();


                //상품명 가져오기 => nameNew에 저장
                Element name = document.select("h3").first();
                System.out.println(name);
                String nameNew = name.toString();
                nameNew = nameNew.replace("<h3>", "").replace("</h3>", "");


                //KAN코드 가져오기 => kanNew에 저장
                Element kan = document.select("tr").first();
                String kanNew = kan.toString();
                kanNew = kanNew.replace("<tr>", "").replace("<th>KAN 상품분류코드</th>", "").replace("&gt;", ">").replace("</tr>", "").replace("<td>", "").replace("</td>", "");


                //상품 카테고리 가져오기 => categoryNew에 저장
                Element category = document.select("tr[class=b_line]").first();
                String categoryNew = category.toString();
                categoryNew = categoryNew.replace("<tr class=\"b_line\">", "").replace("<th>KAN 상품분류</th>", "").replace("&gt;", ">").replace("</tr>", "").replace("<td>", "").replace("</td>", "");


                //사진 가져오기 => photoNew에 저장
                Element photo = document.select("ul[class=btn_img]").first();
                String photoNew = photo.toString();

                String target = "/pr/";
                int target_num = photoNew.indexOf(target);
                photoNew = Shorturl + photoNew.substring(target_num, (photoNew.substring(target_num).indexOf("\',\'") + target_num));
                photoNew = photoNew.replace("&amp;", "&");
                return nameNew + "★" + kanNew + "★" + categoryNew + "★" + photoNew;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 화면에 표시함.
            int name = 0;
            int kan = 1;
            int category = 2;
            int photo = 3;

            String[] results = result.split("★");

            //이름 설정
            if (results[name].equals("") || results[name].equals(null)) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                 myRef = database.getReference("product/" + preferencesBarcodeResult.getString("result", "") + "/name"); //name을 기준으로 있는지 없는지 판단
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        if(value==null)
                        {
                            prodName = false;
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "상품 정보가 없습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
                            startActivity(intent);
                        }
                        else
                        {

                            productName.setText(value);

                               myRef = database.getReference("product/" + preferencesBarcodeResult.getString("result", "") + "/category");
                               myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       productCategory.setText(snapshot.getValue(String.class));
                                       saveData = "★" + "@" + value + "@" + snapshot.getValue(String.class) + "@" + currentDate + "@" + "@";
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                   }
                               });

                            myRef = database.getReference("product/" + preferencesBarcodeResult.getString("result", "") + "/recycle");
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String recycle = snapshot.getValue(String.class);

                                    //플라스틱 1,종이 2,비닐 3,캔 4,스티로폼 5,페트병 6,유리 7,일반쓰레기 8, 전자제품 9

                                    if(recycle.contains("1")) {
                                        addPlastic();
                                    }
                                    if(recycle.contains("2")) {
                                        addPaper();
                                    }
                                    if(recycle.contains("3")) {
                                        addVinyl();
                                    }
                                    if(recycle.contains("4")) {
                                        addCan();
                                    }
                                    if(recycle.contains("5")) {
                                        addStrph();
                                    }
                                    if(recycle.contains("6")) {
                                        addPet();
                                    }
                                    if(recycle.contains("7")) {
                                        addGlass();
                                    }
                                    if(recycle.contains("8")) {
                                        addTrash();
                                    }
                                    if(recycle.contains("9")) {
                                        addElec();
                                    }

                                    prodName = true;
                                    recyclerView = findViewById(R.id.recycling);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    ReAdapter reAdapter = new ReAdapter(data);
                                    recyclerView.setAdapter(reAdapter);
                                    loadEco();

                                    Button button = findViewById(R.id.button);
                                    button.setVisibility(View.VISIBLE);
                                    button.setOnClickListener(v -> {
                                        link = "https://search.shopping.naver.com/search/all?query=" + productName.getText().toString();
                                        goShopping = false;
                                        new getLink().execute();


                                    });
                                    progressDialog.dismiss();


                                    Button buttonShopping = findViewById(R.id.buttonShopping);
                                    buttonShopping.setVisibility(View.VISIBLE);

                                    buttonShopping.setOnClickListener(v -> {
                                        link = "https://search.shopping.naver.com/search/all?query=" + productName.getText().toString();
                                        goShopping = true;
                                        new getLink().execute();
                                    });


                                    Button buttonSave = findViewById(R.id.buttonSave);
                                    buttonSave.setOnClickListener(v -> {
                                        preferencesSaveAll = getSharedPreferences("saveAll", 0);
                                        String result = preferencesSaveAll.getString("saveAll", "");
                                        saveData = result + saveData;


                                        SharedPreferences.Editor editor = preferencesSaveAll.edit();
                                        editor.putString("saveAll", saveData);
                                        editor.commit();

                                        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);

                                        String user_id = sharedPreferences.getString("id", "");

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("user/" + user_id + "/value");
                                        myRef.setValue(saveData);

                                        System.out.println(saveData);

                                        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                                        buttonSave.setVisibility(View.INVISIBLE);


                                    });

                                    progressDialog.dismiss();







                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });








                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            } else {
                prodName = true;
                productName.setText(results[0]);


                KANcode = results[kan].substring(7, 14);
                loadDb();
                loadEco();


                productCategory.setText(results[category]);


                TextView textView = findViewById(R.id.debug);
                textView.setText(results[photo]);
                new LoadImage().execute(results[photo]);
                saveData = "★" + results[photo] + "@" + results[name] + "@" + results[category] + "@" + currentDate + "@" + results[kan] + "@";
                saveData = saveData.replaceAll("  ", "");

            }


        }
    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                img.setImageBitmap(image);

            }


        }
    }


    private void NotConnected_showAlert() //네트워크 연결 오류 시 어플리케이션 종료
    {
        Toast.makeText(getApplicationContext(), "네트워크 연결 오류", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        finish();


    }

    private boolean NetworkConnection() { //네트워크 연결 확인하는 코드
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.getType() == networkType) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
        startActivity(intent);
    }

    public void loadDb() { //파베에서 KAN코드에 대한 카테고리 가져옴
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        System.out.println(KANcode);
        DatabaseReference db_KANcode = database.getReference(KANcode);

        // Read from the database
        db_KANcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //saveData = "";


                Button buttonSave2 = findViewById(R.id.buttonSave2);

                System.out.println(KANcode);
                if(KANcode==null)
                {
                    //Toast.makeText(getApplicationContext(), "재활용 정보를 등록할 수 없는 상품입니다.", Toast.LENGTH_SHORT).show();
                    buttonSave2.setVisibility(View.INVISIBLE);
                }
                else if(KANcode.equals("") || KANcode.equals("0000000"))
                {
                    buttonSave2.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getApplicationContext(), "재활용 정보를 등록할 수 없는 상품입니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    buttonSave2.setVisibility(View.VISIBLE);
                    buttonSave2.setOnClickListener(v -> {
                        SaveDialog saveDialog = new SaveDialog(AfterScanActivity.this);
                        saveDialog.callFunction(KANcode);
                    });

                }





                db_category = dataSnapshot.getValue(String.class);
                System.out.println("dbcat : " + db_category);
                if (db_category == null) {
                    //분리수거에 관한 모든 정보를 띄워준다.
                    addCan();
                    addPaper();
                    addPet();
                    addPlastic();
                    addStrph();
                    addTrash();
                    addGlass();
                    addVinyl();
                    addElec();
                    isCategory = false;


                } else {
                    check_how = db_category.split(",");

                    for (int i = 0; i < check_how.length; i++) {
                        System.out.println(check_how[i]);
                        switch (check_how[i]) {

                            //플라스틱 1,종이 2,비닐 3,캔 4,스티로폼 5,페트병 6,유리 7,일반쓰레기 8, 가전제품 9
                            case "1":
                                addPlastic();
                                break;
                            case "2":
                                addPaper();
                                break;
                            case "3":
                                addVinyl();
                                break;
                            case "4":
                                addCan();
                                break;
                            case "5":
                                addStrph();
                                break;
                            case "6":
                                addPet();
                                break;
                            case "7":
                                addGlass();
                                break;
                            case "8":
                                addTrash();
                                break;
                            case "9":
                                addElec();
                                break;


                        }
                    }
                }


                recyclerView = findViewById(R.id.recycling);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()){
                    @Override
                    public boolean canScrollHorizontally() {
                        return false;

                    }

                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                ReAdapter reAdapter = new ReAdapter(data);

                recyclerView.setAdapter(reAdapter);


                saveData = saveData.replace("\n", "");


                Button buttonSave = findViewById(R.id.buttonSave);
                buttonSave.setOnClickListener(v -> {

                    if(isCategory)
                    {
                        preferencesSaveAll = getSharedPreferences("saveAll", 0);
                        String result = preferencesSaveAll.getString("saveAll", "");
                        saveData = result + saveData;


                        SharedPreferences.Editor editor = preferencesSaveAll.edit();
                        editor.putString("saveAll", saveData);
                        editor.commit();

                        SharedPreferences sharedPreferences = getSharedPreferences("ID", 0);

                        String user_id = sharedPreferences.getString("id", "");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("user/" + user_id + "/value");
                        myRef.setValue(saveData);

                        System.out.println(saveData);

                        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                        buttonSave.setVisibility(View.INVISIBLE);
                    }
                    else
                    {

                    }



                });




                Button button = findViewById(R.id.button);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> {
                    link = "https://search.shopping.naver.com/search/all?query=" + productName.getText().toString();
                    goShopping = false;
                    new getLink().execute();


                });
                progressDialog.dismiss();


                Button buttonShopping = findViewById(R.id.buttonShopping);
                buttonShopping.setVisibility(View.VISIBLE);

                buttonShopping.setOnClickListener(v -> {
                    link = "https://search.shopping.naver.com/search/all?query=" + productName.getText().toString();
                    goShopping = true;
                    new getLink().execute();
                });

//                Button buttonShopping2 = findViewById(R.id.buttonShopping2);
//                buttonShopping2.setVisibility(View.VISIBLE);
//                buttonShopping2.setOnClickListener(v -> {
//                    link = "http://search.danawa.com/dsearch.php?query=" + productName.getText().toString();
//                    new getLink2().execute();
//                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }


    public void loadEco()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference db_eco = database.getReference("eco/" + KANcode);

        db_eco.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stringEco = dataSnapshot.getValue(String.class);

                if(stringEco!=null)
                {
                    buttonEco.setVisibility(View.VISIBLE);
                    buttonEco.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringEco));
                        startActivity(intent);

                    });
                }
                else
                {
                    buttonEco.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private class getLink extends AsyncTask<String, Void, String> //상품명 가져오기
    {

        @Override
        protected String doInBackground(String... params) {
            try {
                //기본 코드
                Connection.Response response = Jsoup.connect(link).method(Connection.Method.GET).execute();
                Document document = (Document) response.parse();


                //상품명 가져오기 => nameNew에 저장
                Element name = (Element) document.select("li[class=basicList_item__2XT81]").first();
                if (name == null)
                {
                    return null;
                }
                else
                    {
                    String nameNew = name.toString();
                    nameNew = nameNew.substring(nameNew.indexOf("<a"), nameNew.indexOf("target=\"_blank\"")).replace("<a href=", "").replace("\"", "");
                    System.out.println(nameNew);
                    //nameNew = nameNew.replace("<h3>", "").replace("</h3>", "");

                    return nameNew;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 화면에 표시함.
            System.out.println(result);
            if (result == null) {
                if(goShopping)
                {
                    Toast.makeText(AfterScanActivity.this, "상품 정보가 없습니다.", Toast.LENGTH_LONG).show();
                }
                else
                {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "[상품명]\n" + productName.getText().toString() + "\n\n[카테고리]\n" + productCategory.getText().toString() + "\n[분리배출 방법]" + shareString );
                        startActivity(sharingIntent);
                }

            } else {

                if(goShopping)
                {


                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        intent.setPackage("com.nhn.android.search");//네이버 앱으로 연결하기
                        startActivity(intent);

                    }catch(ActivityNotFoundException e) //네이버 앱이 없을 경우
                    {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        startActivity(intent2); //아무 브라우저나 연결하기
                    }

                }
                else
                {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                   // sharingIntent.putExtra(Intent.EXTRA_TEXT, result);

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "[상품명]\n" + productName.getText().toString() + "\n\n[카테고리]\n" + productCategory.getText().toString() + "\n[분리배출 방법]" + shareString + "\n\n[구매링크]" + result);
                    startActivity(sharingIntent.createChooser(sharingIntent, "공유하기"));
                }

            }


        }
    }

    private class getLink2 extends AsyncTask<String, Void, String> //상품명 가져오기
    {

        @Override
        protected String doInBackground(String... params) {
            try {
                //기본 코드
                Connection.Response response = Jsoup.connect(link).method(Connection.Method.GET).execute();
                Document document = (Document) response.parse();


                //상품명 가져오기 => nameNew에 저장
                Element name = (Element) document.select("p[class=prod_name]").first();
                if (name == null)
                {
                    return null;
                }
                else
                {
                    String nameNew = name.toString();
                    //nameNew = nameNew.substring(nameNew.indexOf("<a"), nameNew.indexOf("target=\"_blank\"")).replace("<a href=", "").replace("\"", "");
                    System.out.println(nameNew);
                    //nameNew = nameNew.replace("<h3>", "").replace("</h3>", "");

                    return nameNew;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과값을 화면에 표시함.
            System.out.println(result);
            if (result == null) {

                    Toast.makeText(AfterScanActivity.this, "상품 정보가 없습니다.", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(AfterScanActivity.this, result, Toast.LENGTH_LONG).show();

//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
//                    startActivity(intent);

            }


        }
    }
}

