package com.androidcourse.moyan.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;

import java.util.List;

public class HomeUnifiedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TREND_HEADER = 0;
    private static final int TYPE_NEWS_ITEM = 1;

    private Context context;
    private List<TrendCard> trendCards;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onTrendCardClick(TrendCard trendCard);
        void onNewsItemClick(NewsItem newsItem);
    }

    public HomeUnifiedAdapter(Context context, List<TrendCard> trendCards, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.trendCards = trendCards;
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TREND_HEADER;
        } else {
            return TYPE_NEWS_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TREND_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_trend_section, parent, false);
            return new TrendHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
            return new NewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TrendHeaderViewHolder) {
            ((TrendHeaderViewHolder) holder).bind(trendCards);
        } else if (holder instanceof NewsViewHolder) {
            int newsPosition = position - 1;
            if (newsList != null && newsPosition >= 0 && newsPosition < newsList.size()) {
                ((NewsViewHolder) holder).bind(newsList.get(newsPosition), listener);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (newsList != null) {
            count += newsList.size();
        }
        return count;
    }

    public void updateData(List<TrendCard> newTrendCards, List<NewsItem> newNewsList) {
        this.trendCards = newTrendCards;
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }

    public void updateNewsList(List<NewsItem> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }

    class TrendHeaderViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvTrendCards;
        private LinearLayout dotIndicator;
        private boolean isInitialized = false;
        private LinearLayoutManager layoutManager;
        private int currentScrollPosition = 0;
        private Handler autoScrollHandler;
        private Runnable autoScrollRunnable;
        private boolean isUserScrolling = false;
        private static final long AUTO_SCROLL_DELAY = 3000;

        public TrendHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            rvTrendCards = itemView.findViewById(R.id.rv_trend_cards);
            dotIndicator = itemView.findViewById(R.id.dot_indicator);
            autoScrollHandler = new Handler(Looper.getMainLooper());
        }

        public void bind(List<TrendCard> cards) {
            if (cards == null || cards.isEmpty()) {
                return;
            }

            if (!isInitialized) {
                layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                rvTrendCards.setLayoutManager(layoutManager);

                androidx.recyclerview.widget.LinearSnapHelper snapHelper = new androidx.recyclerview.widget.LinearSnapHelper();
                snapHelper.attachToRecyclerView(rvTrendCards);

                rvTrendCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            isUserScrolling = true;
                            stopAutoScroll();
                        } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            isUserScrolling = false;
                            updateCurrentPositionByCenter();
                            updateDotByScroll();
                            startAutoScroll(cards);
                        }
                    }
                });

                isInitialized = true;
            }

            TrendCardAdapter trendCardAdapter = new TrendCardAdapter(context, cards, trendCard -> {
                if (listener != null) {
                    listener.onTrendCardClick(trendCard);
                }
            });

            rvTrendCards.setAdapter(trendCardAdapter);

            currentScrollPosition = 0;
            rvTrendCards.scrollToPosition(currentScrollPosition);

            int count = cards.size();
            rvTrendCards.post(() -> createDotIndicators(count));

            stopAutoScroll();
            startAutoScroll(cards);
        }

        private void updateCurrentPositionByCenter() {
            if (layoutManager == null || trendCards == null || trendCards.isEmpty()) return;

            int recyclerViewCenterX = rvTrendCards.getWidth() / 2;
            int minDistance = Integer.MAX_VALUE;
            int closestPosition = 0;

            for (int i = 0; i < trendCards.size(); i++) {
                View itemView = layoutManager.findViewByPosition(i);
                if (itemView == null) continue;

                int itemCenterX = (itemView.getLeft() + itemView.getRight()) / 2;
                int distance = Math.abs(recyclerViewCenterX - itemCenterX);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestPosition = i;
                }
            }

            if (currentScrollPosition != closestPosition) {
                currentScrollPosition = closestPosition;
            }
        }

        private void updateDotByScroll() {
            if (trendCards == null || trendCards.size() <= 1) return;
            selectDot(currentScrollPosition);
        }

        private void startAutoScroll(List<TrendCard> cards) {
            if (cards == null || cards.size() <= 1) {
                return;
            }

            stopAutoScroll();

            autoScrollRunnable = () -> {
                if (isUserScrolling) {
                    return;
                }

                int itemCount = cards.size();
                int nextPosition = currentScrollPosition + 1;

                if (nextPosition >= itemCount) {
                    nextPosition = 0;
                    rvTrendCards.smoothScrollToPosition(nextPosition);
                    currentScrollPosition = nextPosition;
                    updateDotByScroll();
                } else {
                    rvTrendCards.smoothScrollToPosition(nextPosition);
                    currentScrollPosition = nextPosition;
                }

                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
            };

            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }

        private void stopAutoScroll() {
            if (autoScrollRunnable != null) {
                autoScrollHandler.removeCallbacks(autoScrollRunnable);
            }
        }

        private void createDotIndicators(int count) {
            if (count <= 1) {
                dotIndicator.setVisibility(View.GONE);
                return;
            }
            dotIndicator.removeAllViews();
            for (int i = 0; i < count; i++) {
                View dot = new View(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(8), dpToPx(8));
                params.setMarginEnd(dpToPx(8));
                params.setMarginStart(dpToPx(8));
                dot.setLayoutParams(params);
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
                dotIndicator.addView(dot);
            }
            dotIndicator.setVisibility(View.VISIBLE);
            selectDot(0);
        }

        private void selectDot(int position) {
            if (dotIndicator == null || dotIndicator.getChildCount() == 0) return;

            for (int i = 0; i < dotIndicator.getChildCount(); i++) {
                View dot = dotIndicator.getChildAt(i);
                if (i == position) {
                    dot.setBackgroundResource(R.drawable.bg_dot_active);
                } else {
                    dot.setBackgroundResource(R.drawable.bg_dot_inactive);
                }
            }
        }

        private int dpToPx(int dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivNewsImage;
        android.widget.TextView tvTitle;
        android.widget.TextView tvAuthor;
        android.widget.TextView tvTime;
        android.widget.TextView tvCommentCount;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(NewsItem news, OnItemClickListener listener) {
            if (news == null) {
                return;
            }

            if (tvTitle != null) {
                String title = news.getTitle();
                tvTitle.setText(title != null ? title : "");
            }

            if (tvAuthor != null) {
                String author = news.getAuthor();
                tvAuthor.setText(author != null ? author : "");
            }

            if (tvTime != null) {
                long publishTime = news.getPublishTime();
                String timeStr = com.androidcourse.moyan.utils.TimeUtils.formatRelativeTime(publishTime);
                tvTime.setText(timeStr);
            }

            if (tvCommentCount != null) {
                tvCommentCount.setText(String.valueOf(news.getCommentCount()));
            }

            if (ivNewsImage != null && news.getImageResId() != 0) {
                ivNewsImage.setImageResource(news.getImageResId());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNewsItemClick(news);
                }
            });
        }
    }
}
