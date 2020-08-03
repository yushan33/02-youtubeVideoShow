package com.example.youtubevideoshow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//前面build.gradle(app)裡面implementation他人專案後，andriod studio會自己抓取class
//用ctrl + 左鍵點選下列class都可以看見原始碼
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

/*
*
* Create by yushan 2020/08/03
* 範例參考來源:https://github.com/HaarigerHarald/android-youtubeExtractor/blob/master/sampleApp/src/main/java/at/huber/sampleDownload/SampleDownloadActivity.java
* 此作者弄了一個apk可以玩玩看，比較會知道他在做甚麼 :  https://github.com/HaarigerHarald/android-youtubeExtractor/releases/tag/v2.1.0
* readme裡面可以看一下
*
* */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String youtubeLink , downloadLink ,videoName = null;
    private Button mbtnShowDownloadLink, mbtnDownloadFile , mbtnClear;
    private EditText medYoutubeLink, medDownloadLink;
    private TextView mtvYoutibeVideoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initView();


    }

    private void initView(){
        medYoutubeLink = (EditText)findViewById(R.id.edYoutubeLink);
        medDownloadLink = (EditText)findViewById(R.id.edDownloadLink);
        mtvYoutibeVideoName = (TextView)findViewById(R.id.edVideoName);
        mbtnShowDownloadLink = (Button)findViewById(R.id.btnShowDownloadLink);
        mbtnDownloadFile = (Button)findViewById(R.id.btnDownloadFile);
        mbtnClear = (Button)findViewById(R.id.btnClear);
        mbtnShowDownloadLink.setOnClickListener(this);
        mbtnDownloadFile.setOnClickListener(this);
        mbtnClear.setOnClickListener(this);

    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {        //這是一個github上已經設定好的執行緒，執行影片網站網址解取出影片的動作

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {//監聽轉換完成後的方法，類似監聽videoview播放完成時候，需要做一些事情

                if (ytFiles == null) {  //如果轉換不成功就直接從這裡跳出
                    // Something went wrong we got no urls. Always check this.
                    return;
                }

                int itag = 22;
                // ytFile represents one file with its url and meta data
                YtFile ytFile = ytFiles.get(itag);      //YtFile為作者自訂的class
                downloadLink = ytFile.getUrl();         //轉出的格式應該是串流(沒認真研究)，用電腦打開的話會看到影片慢慢載入，也可以隨便點擊段落開始播放
                videoName = vMeta.getTitle();
                Log.w("Download Link is ",downloadLink);
                Message msg = new Message();
                showDownLoadLink.sendMessage(msg);

            }
        }.extract(youtubeLink, true, false);
    }

    //取得youtube 影片畫質格式 如720p,360p等，動態產生button供使用者設定要下載的影片格式(我沒用到所以就放著)
//    private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
//        // Display some buttons and let the user choose the format
//        String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
//                ytfile.getFormat().getAudioBitrate() + " kbit/s" :
//                ytfile.getFormat().getHeight() + "p";
//        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
//        Button btn = new Button(this);
//        btn.setText(btnText);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
                    //設定下載影片檔名的長度
//                String filename;
//                if (videoTitle.length() > 55) {
//                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
//                } else {
//                    filename = videoTitle + "." + ytfile.getFormat().getExt();
//                }
                    //將裡面有奇怪符號全部移除
//                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//                finish();
//            }
//        });
        //mainLayout.addView(btn);
//    }

    //下載檔案用
    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadTitle);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    Handler showDownLoadLink = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            medDownloadLink.setText(downloadLink);
            mtvYoutibeVideoName.setText(videoName);
            mbtnDownloadFile.setVisibility(View.VISIBLE);
            mbtnClear.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShowDownloadLink:
                Log.w("11111","OnClick : ShowDownloadLink1");
                if(medYoutubeLink.getText().toString().length()!=0){
                    String ytLink = medYoutubeLink.getText().toString();
                    Log.w("11111","OnClick : ShowDownloadLink2");
                    if(ytLink != null && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v=")))  {
                        youtubeLink = ytLink;
                        getYoutubeDownloadUrl(youtubeLink);
                    }else {
                        Toast.makeText(this, R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.btnDownloadFile:
                if(downloadLink != null && videoName != null){
                    downloadFromUrl( downloadLink, videoName);
                }
                break;

            case R.id.btnClear:
                youtubeLink=null;
                downloadLink=null;
                videoName = null;
                medYoutubeLink.setText(null);
                mtvYoutibeVideoName.setText(null);
                medDownloadLink.setText(null);
                mbtnDownloadFile.setVisibility(View.INVISIBLE);
                mbtnClear.setVisibility(View.INVISIBLE);
                break;

        }

    }
}