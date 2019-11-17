package com.harshsharma.musify;

import android.app.Application;
import android.content.Context;

import com.harshsharma.musify.interfaces.MusifyApiInterface;
import com.harshsharma.musify.models.MyObjectBox;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.concurrent.TimeUnit;

import io.objectbox.BoxStore;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Custom Application Class for Initializing some libraries(Retrofit, ObjectBox etc. at app start)
public class MusifyApplication extends Application {
    private static String TAG = MusifyApplication.class.getName();
    private static MusifyApplication musifyApplication;
    private MusifyApiInterface musifyApiInterface;
    private BoxStore boxStore;

    public MusifyApplication() {
        musifyApplication = this;
    }

    public static MusifyApplication getInstance() {
        if (musifyApplication == null) {
            musifyApplication = new MusifyApplication();
        }
        return musifyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
        initObjectBox();
        initImageLoader(musifyApplication);
    }

    private void initRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MusifyApiInterface.MUSIFY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        musifyApiInterface = retrofit.create(MusifyApiInterface.class);
    }

    private void initObjectBox() {
        boxStore = MyObjectBox.builder().androidContext(MusifyApplication.this).build();
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    public MusifyApiInterface getMusifyApiInterface() {
        return musifyApiInterface;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
