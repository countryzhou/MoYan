package com.androidcourse.moyan.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UriUtils {

    public static String getPath(Context context, Uri uri) {
        if (uri == null) return null;

        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return uri.getPath();
        }

        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            // 尝试MediaStore
            try (Cursor cursor = context.getContentResolver().query(
                    uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(0);
                    if (path != null) return path;
                }
            } catch (Exception e) {
                // ignore
            }

            // 降级方案：复制到缓存
            try {
                String fileName = getFileName(context, uri);
                if (fileName == null) fileName = System.currentTimeMillis() + ".tmp";

                File cacheFile = new File(context.getCacheDir(), fileName);
                try (InputStream is = context.getContentResolver().openInputStream(uri);
                     FileOutputStream os = new FileOutputStream(cacheFile)) {
                    if (is == null) return null;
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    return cacheFile.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String getFileName(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    return cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}