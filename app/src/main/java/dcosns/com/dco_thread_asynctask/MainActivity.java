package dcosns.com.dco_thread_asynctask;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editText; // 에딧텍스트 레퍼런스
    View.OnClickListener bHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button1:
                    doThreadAction();
                    break;
                case R.id.button2:
                    doAsyncTaskAction();
                    break;
            }
        }
    };

    private void doThreadAction(){
        new MyThread().start();
    }

    private void doAsyncTaskAction(){
        new MyAsyncTask().execute(0, 3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(bHandler);
        findViewById(R.id.button2).setOnClickListener(bHandler);

        editText = (EditText) findViewById(R.id.editText);

    }

    /**
     * JAVA는 프로그램안에서 작은 프로그램을 만들 수 있는 멀티쓰레드를 지원한다
     * (특징이니 알아두도록하자)
     * Thread에서 사용하는 메소드는 run이다.
     * Thread는 프로그램의 Back에서 돌아가기 때문에 UI변경이 불가능하다.
     * 그래서 여러가지 방법이 있다.
     * 1. Handler.sendMessage 사용
     * 2. Handler.post 사용
     * 3. runOnUiHandler 사용
     */
    class MyThread extends Thread{
        @Override// run을 오버라이딩
        public void run() {
            /* 이곳에 반복적으로 적용하고 싶은 내용을 적는다.
            * 예시로 알아보기 쉽도록 반복문으로 작성했다.
            */
            int cnt = 0; // 1초에 1씩 증가하는 변수 선언

            /**
             * 1번 방법 Handler.sendMessage 이용
             * 아래쪽에 Handler를 정의했고, Handler로 보낼 메시지를 만들어서 보내는 방법
             * Message클래스의 구조를 잘 파악 해보자
             * what : int 식별하는데에 쓰는 정수형 변수
             * arg1, arg2 : int 인자 전달시에 사용하는 정수형 변수
             * obj : Object 정보전달 시에 String으로 넘길수도 있도록 클래스자체로 전달이 가능한 변수
             */
            Message uiMessage = uiHandler.obtainMessage();//Handler로 보낼 수 있는 메시지 만들기
                                                // (새로 선언이 아니라 해당 핸들러에서 가져온다.)
            while(cnt < 20){
                cnt++;
                if(cnt < 10) {
                    uiMessage.what = cnt % 3; // what의 값에 따라 다른 효과가 나도록 Handler에서 코딩한다.
                    uiMessage.obj = "sampleString"; // 어떤 객체든 넣을 수 있는데 String 객체를 넣어봤다.
                    uiMessage.arg1 = 1; // arg1, agr2 둘다 그냥 쓰는데, 단순 ui변경시엔 what만 사용한다.
                    uiMessage.arg2 = 2;
                    uiHandler.sendMessage(uiMessage); //Handler로 만든 메시지 보내기
                }
                SystemClock.sleep(1000); // 1초마다 작동하도록 sleep
            }

            /**
             * 2번 방법 Handler.post 사용하기
             * post 메소드에 들어가는 인자가 Runnable인데 Runnable을 오버라이딩하여 바꿀 수 있다.
             */
            while(cnt < 40){
                cnt++;
                final int a = cnt; //익명함수에 쓸 때는 final로 선언해야하기 때문에 새롭게 선언
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(a == 25) editText.setBackgroundColor(Color.RED);
                        String s = editText.getText().toString() + "\n";
                        editText.setText(s);
                    }
                });
                SystemClock.sleep(1000); // 1초마다 작동하도록 sleep
            }

            /**
             * 3번 방법 runOnUiHandler사용하기
             * 2번과 동일하게 Runnable을 오버라이딩해서 사용한다.
             */
            while(cnt < 60){
                cnt++;
                final int a = cnt;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(a == 45) editText.setBackgroundColor(Color.YELLOW);
                        String s = editText.getText().toString() + "\n";
                        editText.setText(s);
                    }
                });
                SystemClock.sleep(1000); // 1초마다 작동하도록 sleep
            }
        }
    }

    /**
     * Thread는 화면의 UI를 바꿀 수 없어서 Handler를 이용해서 UI를 변경한다.
     * Message의 구조상 what을 식별자로 사용하여 경우에 따른 다른 UI변경을 구현할 수 있다.
     */
    Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {//what의 경우에 따라서 분류해서 사용
                //예제에서는 3으로 나눈 나머지가 들어오기 때문에 0, 1, 2 로 구분해서 사용
                case 0:
                    editText.setBackgroundColor(Color.GRAY);
                    break;
                case 1:
                    editText.setBackgroundColor(Color.LTGRAY);
                    break;
                case 2:
                    editText.setBackgroundColor(Color.MAGENTA);
                    break;
            }
            String s = editText.getText().toString() + "\n";
            editText.setText(s);
        }
    };

    /**
     * 쓰레드보다 더 작업이 편한 AsyncTask이다. 하나의 Task안에 UI변경과 데이터처리를 할 수 있다.
     */
    class MyAsyncTask extends AsyncTask<
            Integer/*맨처음에 입력하는 변수의 데이터타입*/,
            Integer/*작동하는 동안 오가는 데이터타입*/,
            String/*최종적으로 호출하는 함수에 넘길 데이터타입 */>{
        /**
         *  시작전에 호출되는 메소드
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editText.setText("AsyncTask Start!!\n");
        }

        /**
         * 작동하는 동안 호출되는 메소드
         * @param value 중간중간에 바뀔 때 넘어오는 값
         */
        @Override
        protected void onProgressUpdate(Integer[] value) {
            super.onProgressUpdate(value);
            int cnt = value[0]; // cnt를 소환 배열로 들어왔으니 배열로 받아서 사용한다.
            editText.setText(editText.getText().toString()+"\n"+"cnt : "+cnt+"  value : "+value[1]);
        }

        /**
         * 모두다 끝나고 나면 호출되는 메소드
         * @param value doInBackground에서 보내주는 데이터를 받는다.
         */
        @Override
        protected void onPostExecute(String value) {
//            super.onPostExecute(value);
            if(value.equals("SUCCESS")){ // SUCCESS의 경우 한줄을 추가함
                editText.setText(editText.getText().toString()+"\n"+value);
            }

        }

        /**
         * 뒤에서 돌아가는 메소드 Thread의 역할을 한다.
         * @param intArr 맨처음에 입력하는 데이터가 들어온다.
         * @return
         */
        @Override
        protected String doInBackground(Integer[] intArr) {
            int cnt = 0;
            int value = intArr[1]; // 배열로 가져와서 사용
            while (true){
                cnt++;
                publishProgress(cnt, value);//작동하는 중간중간에 onProgressUpdate 호출
                // (넘어가는 파라미터는 배열형식으로 넘길 수 있음)
                SystemClock.sleep(1000); //1초에 1씩 증가
                if(cnt == 60) break; // 60초에서 반복문 종료
            }
            return "SUCCESS"; // onPostExecute로 넘길 데이터 String값이다.
        }


    }
}
