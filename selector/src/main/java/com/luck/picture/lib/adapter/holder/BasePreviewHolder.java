package com.luck.picture.lib.adapter.holder;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.utils.BitmapUtils;
import com.luck.picture.lib.utils.DensityUtil;

/**
 * @author：luck
 * @date：2021/11/20 3:17 下午
 * @describe：BasePreviewHolder
 */
public class BasePreviewHolder extends RecyclerView.ViewHolder {
    /**
     * 图片
     */
    public final static int ADAPTER_TYPE_IMAGE = 1;
    /**
     * 视频
     */
    public final static int ADAPTER_TYPE_VIDEO = 2;

    /**
     * 音频
     */
    public final static int ADAPTER_TYPE_AUDIO = 3;

    protected final int screenWidth;
    protected final int screenHeight;
    protected final int screenAppInHeight;
    protected LocalMedia media;
    protected final PictureSelectionConfig config;
    public PhotoView coverImageView;

    public static BasePreviewHolder generate(ViewGroup parent, int viewType, int resource) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        if (viewType == ADAPTER_TYPE_VIDEO) {
            return new PreviewVideoHolder(itemView);
        } else if (viewType == ADAPTER_TYPE_AUDIO) {
            return new PreviewAudioHolder(itemView);
        } else {
            return new PreviewImageHolder(itemView);
        }
    }

    public BasePreviewHolder(@NonNull View itemView) {
        super(itemView);
        this.config = PictureSelectionConfig.getInstance();
        this.screenWidth = DensityUtil.getRealScreenWidth(itemView.getContext());
        this.screenHeight = DensityUtil.getScreenHeight(itemView.getContext());
        this.screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.getContext());
        findViews(itemView);
    }

    protected void findViews(View itemView) {
        this.coverImageView = itemView.findViewById(R.id.preview_image);
    }

    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    public void bindData(LocalMedia media, int position) {
        this.media = media;
        int[] size = getRealSizeFromMedia(media);
        int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
        loadImage(media, maxImageSize[0], maxImageSize[1]);
        setScaleDisplaySize(media);
        setOnClickEventListener();
        setOnLongClickEventListener();
    }

    /**
     * load image cover
     *
     * @param media
     * @param maxWidth
     * @param maxHeight
     */
    protected void loadImage(final LocalMedia media, int maxWidth, int maxHeight) {
        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), coverImageView,
                    media.getAvailablePath(), maxWidth, maxHeight);
        }
    }

    protected void setOnClickEventListener() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    protected void setOnLongClickEventListener() {
        coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    protected int[] getRealSizeFromMedia(LocalMedia media) {
        if (media.isCut() && media.getCropImageWidth() > 0 && media.getCropImageHeight() > 0) {
            return new int[]{media.getCropImageWidth(), media.getCropImageHeight()};
        } else {
            return new int[]{media.getWidth(), media.getHeight()};
        }
    }

    protected void setScaleDisplaySize(LocalMedia media) {
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.getWidth() > 0 && media.getHeight() > 0) {
                float ratio = (float) media.getWidth() / (float) media.getHeight();
                int displayHeight = (int) (screenWidth / ratio);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) coverImageView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = displayHeight > screenHeight ? screenAppInHeight : screenHeight;
                layoutParams.gravity = Gravity.CENTER;
            }
        }
    }

    /**
     * onViewAttachedToWindow
     */
    public void onViewAttachedToWindow() {

    }

    /**
     * onViewDetachedFromWindow
     */
    public void onViewDetachedFromWindow() {

    }

    protected OnPreviewEventListener mPreviewEventListener;

    public void setOnPreviewEventListener(OnPreviewEventListener listener) {
        this.mPreviewEventListener = listener;
    }

    public interface OnPreviewEventListener {

        void onBackPressed();

        void onPreviewVideoTitle(String videoName);

        void onLongPressDownload(LocalMedia media);
    }
}
