package com.luck.picture.lib.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.luck.picture.lib.config.PictureMimeType;
import com.yalantis.ucrop.util.BitmapUtils;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q相关处理类
 */
public class AndroidQTransformUtils {

    /**
     * 解析Android Q版本下视频
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @return
     */
    public static String parseVideoPathToAndroidQ(Context ctx, String path) {
        try {
            File filesDir = ctx.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (filesDir != null) {
                String cachedDir = filesDir.getAbsolutePath();
                String newPath = cachedDir + File.separator + System.currentTimeMillis() + ".mp4";
                ParcelFileDescriptor parcelFileDescriptor =
                        ctx.getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                FileInputStream inputStream = new FileInputStream(fileDescriptor);
                boolean copyFile = FileUtils.copyFile(inputStream, newPath);
                if (copyFile) {
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param path
     * @return
     */
    public static String parseImagePathToAndroidQ(Context ctx, String path) {
        String cachedDir = PictureFileUtils.getDiskCacheDir(ctx.getApplicationContext());
        String imgType = PictureMimeType.getLastImgType(path);
        String newPath = cachedDir + File.separator + System.currentTimeMillis() + imgType;
        Bitmap bitmapFromUri = BitmapUtils.getBitmapFromUri(ctx.getApplicationContext(),
                Uri.parse(path));
        BitmapUtils.saveBitmap(bitmapFromUri, newPath);
        return newPath;
    }
}