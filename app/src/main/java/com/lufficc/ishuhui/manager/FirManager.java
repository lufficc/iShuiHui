package com.lufficc.ishuhui.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.lufficc.ishuhui.Config;
import com.lufficc.ishuhui.api.Fir;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by lufficc on 2016/8/26.
 */

public class FirManager {
    private Retrofit retrofit;
    private static final int BUFFER_SIZE = 16 * 1024;
    Handler handler = new Handler(Looper.getMainLooper());

    private static final class Holder {
        private static final FirManager RIR_MANAGER = new FirManager();
    }

    private Fir fir;

    private FirManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new FirInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fir.im/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Fir fir() {
        return instance().fir == null ? instance().fir = get().create(Fir.class) : instance().fir;
    }

    private class FirInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            Request request = oldRequest
                    .newBuilder()
                    .url(oldRequest.url().newBuilder().addQueryParameter("api_token", Config.FIR_API_KEY).build())
                    .build();
            return chain.proceed(request);
        }
    }

    public static FirManager instance() {
        return Holder.RIR_MANAGER;
    }

    public static Retrofit get() {
        return instance().retrofit;
    }

    public interface DownloadListener {
        void onProgress(int percent);

        void onError(Throwable throwable);

        void onSuccess(Uri apk);
    }

    public void downloadApk(final String mUrl, @NonNull final DownloadListener downloadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                BufferedOutputStream outputStream = null;
                File fileDir = Environment.getExternalStorageDirectory();
                File apkFile = null;
                float percent = 0;
                try {
                    URL url = new URL(mUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setInstanceFollowRedirects(true);
                    urlConnection.connect();
                    int contentLength = urlConnection.getContentLength();


                    Log.d(TAG, "Start downloading " + urlConnection.getURL());
                    Log.d(TAG, String.format("File size %.2f kb", (float) contentLength / 1024));

                    String fileName = getFileName(urlConnection);
                    apkFile = new File(fileDir, fileName);
                    outputStream = new BufferedOutputStream(new FileOutputStream(apkFile));
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    int totalLength = 0;
                    InputStream in = urlConnection.getInputStream();
                    while ((length = in.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                        totalLength += length;
                        percent = (totalLength == 0f) ? 0f : (float) totalLength / (float) contentLength;
                        final float finalPercent = percent;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadListener.onProgress((int) (finalPercent * 100));
                            }
                        });
                    }
                } catch (IOException e) {
                    downloadListener.onError(e);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            downloadListener.onError(e);
                        }
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                final File finalApkFile = apkFile;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onSuccess(Uri.fromFile(finalApkFile));
                    }
                });
            }
        }).start();
    }

    private String getFileName(HttpURLConnection urlConnection) {
        Uri uri = Uri.parse(urlConnection.getURL().toString());
        String fileName = uri.getQueryParameter("attname");
        if (TextUtils.isEmpty(fileName)) {
            String attachment = urlConnection.getHeaderField("Content-Disposition");
            if (attachment != null) {
                String delimiter = "filename=\"";
                int index = attachment.indexOf(delimiter);
                if (index != -1) {
                    fileName = attachment.substring(index + delimiter.length(), attachment.length() - 1);
                }
                if (TextUtils.isEmpty(fileName)) {
                    fileName = uri.getLastPathSegment();
                }
            }
        }
        return fileName;
    }

    public static void install(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
