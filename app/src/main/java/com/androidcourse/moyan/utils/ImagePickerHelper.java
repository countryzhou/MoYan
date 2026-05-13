package com.androidcourse.moyan.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.androidcourse.moyan.BuildConfig;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagePickerHelper {

    private final AppCompatActivity activity;  // 改为 AppCompatActivity
    private ImagePickCallback callback;
    private Uri currentPhotoUri;
    private static final int MAX_IMAGES = 6;

    // 用于多图选择的临时存储
    private List<String> tempImagePaths = new ArrayList<>();

    public interface ImagePickCallback {
        void onImagesPicked(List<String> imagePaths);
        void onError(String error);
    }

    // 构造函数改为接收 AppCompatActivity
    public ImagePickerHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * 注册单选图片（从相册选择单张）
     */
    public ActivityResultLauncher<String> registerGalleryLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        List<String> paths = new ArrayList<>();
                        String path = UriUtils.getPath(activity, uri);
                        if (path != null) {
                            paths.add(path);
                            if (callback != null) {
                                callback.onImagesPicked(paths);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError("获取图片路径失败");
                            }
                        }
                    }
                }
        );
    }

    /**
     * 注册多图选择器（从相册选择多张）
     */
    public ActivityResultLauncher<String> registerMultipleGalleryLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        List<String> paths = new ArrayList<>();
                        // 修复 lambda 中的遍历问题
                        for (Uri uri : uris) {
                            if (paths.size() >= MAX_IMAGES) {
                                break;
                            }
                            String path = UriUtils.getPath(activity, uri);
                            if (path != null && !path.isEmpty()) {
                                paths.add(path);
                            }
                        }
                        if (callback != null && !paths.isEmpty()) {
                            callback.onImagesPicked(paths);
                        } else if (callback != null) {
                            callback.onError("没有获取到有效图片");
                        }
                    }
                }
        );
    }

    /**
     * 注册相机拍照
     */
    public ActivityResultLauncher<Uri> registerCameraLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && currentPhotoUri != null) {
                        List<String> paths = new ArrayList<>();
                        String path = UriUtils.getPath(activity, currentPhotoUri);
                        if (path != null) {
                            paths.add(path);
                            if (callback != null) {
                                callback.onImagesPicked(paths);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError("拍照失败，无法获取图片路径");
                            }
                        }
                    } else if (callback != null) {
                        callback.onError("拍照失败");
                    }
                }
        );
    }

    /**
     * 注册权限请求
     */
    public ActivityResultLauncher<String[]> registerPermissionLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    // 修复 result.values() 的问题
                    for (boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (!allGranted && callback != null) {
                        callback.onError("需要存储权限才能选择图片");
                    }
                }
        );
    }

    /**
     * 创建图片文件（用于相机拍照）
     */
    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(null);
        if (storageDir == null) {
            storageDir = activity.getCacheDir();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * 获取拍照URI
     */
    public Uri getCameraUri(File photoFile) {
        currentPhotoUri = FileProvider.getUriForFile(
                activity,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                photoFile
        );
        return currentPhotoUri;
    }

    /**
     * 压缩图片（避免OOM和上传过大）
     */
    public static String compressImage(String imagePath) {
        // 这里可以使用BitmapFactory压缩
        // 简单返回原路径，实际项目中需要实现压缩逻辑
        return imagePath;
    }

    /**
     * 清除临时图片（释放缓存）
     */
    public void clearTempImages() {
        for (String path : tempImagePaths) {
            try {
                new File(path).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tempImagePaths.clear();
    }

    public void setCallback(ImagePickCallback callback) {
        this.callback = callback;
    }

    public static int getMaxImages() {
        return MAX_IMAGES;
    }
}